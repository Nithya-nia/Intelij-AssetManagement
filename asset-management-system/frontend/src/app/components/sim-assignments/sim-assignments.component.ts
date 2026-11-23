import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { SimService } from '../../services/sim.service';
import { MobileService } from '../../services/mobile.service';
import { EmployeeService } from '../../services/employee.service';
import { RechargeService } from '../../services/recharge.service';
import { NotificationService } from '../../services/notification.service';
import { SimCard, Mobile, Employee, SimAssignment, Recharge } from '../../models/models';

@Component({
  selector: 'app-sim-assignments',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './sim-assignments.component.html',
  styleUrls: ['./sim-assignments.component.css']
})
export class SimAssignmentsComponent implements OnInit {
  activeTab: 'assignments' | 'recharges' = 'assignments';

  // Data lists
  assignments: SimAssignment[] = [];
  recharges: Recharge[] = [];
  availableSims: SimCard[] = [];
  allSims: SimCard[] = [];
  assignedMobiles: Mobile[] = [];
  activeEmployees: Employee[] = [];

  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // SIM Assign Modal & Form
  showAssignModal: boolean = false;
  assignForm: FormGroup;
  filteredMobilesForEmployee: Mobile[] = [];

  // SIM Return Modal & Form
  showReturnModal: boolean = false;
  returnForm: FormGroup;
  selectedAssignmentForReturn: SimAssignment | null = null;

  // Add Recharge Modal & Form
  showRechargeModal: boolean = false;
  rechargeForm: FormGroup;
  selectedSimForRecharge: SimCard | null = null;

  // Recharge Filters
  rechargeFilterMobile: string = '';
  rechargeFilterProvider: string = 'ALL';
  rechargeFilterEmployee: string = '';

  constructor(
    private simService: SimService,
    private mobileService: MobileService,
    private employeeService: EmployeeService,
    private rechargeService: RechargeService,
    private notificationService: NotificationService,
    private fb: FormBuilder
  ) {
    this.assignForm = this.fb.group({
      employeeId: ['', [Validators.required]],
      mobileId: ['', [Validators.required]],
      simId: ['', [Validators.required]],
      issuedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    this.returnForm = this.fb.group({
      returnedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    this.rechargeForm = this.fb.group({
      simId: ['', [Validators.required]],
      rechargeAmount: ['', [Validators.required, Validators.min(1)]],
      rechargePlan: ['', [Validators.required]],
      rechargeDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      planEndDate: ['', [Validators.required]],
      remarks: ['']
    });
  }

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loading = true;
    this.simService.getAllAssignments().subscribe({
      next: (data) => {
        this.assignments = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.rechargeService.getRecharges().subscribe({
      next: (data) => {
        this.recharges = data;
      }
    });

    this.simService.getSims().subscribe({
      next: (data) => {
        this.allSims = data;
        this.availableSims = data.filter((s) => s.status === 'Available');
      }
    });

    this.mobileService.getMobiles().subscribe({
      next: (data) => {
        this.assignedMobiles = data.filter((m) => m.status === 'Assigned');
      }
    });

    this.employeeService.getEmployees(undefined, undefined, 'Active').subscribe({
      next: (data) => {
        this.activeEmployees = data;
      }
    });
  }

  onEmployeeChange(): void {
    const selectedEmpId = this.assignForm.get('employeeId')?.value;
    if (!selectedEmpId) {
      this.filteredMobilesForEmployee = [];
      this.assignForm.patchValue({ mobileId: '' });
      return;
    }

    // Filter mobiles assigned to this employee
    this.filteredMobilesForEmployee = this.assignedMobiles.filter(
      (m) => m.assignedEmployeeId === selectedEmpId && m.activeSimCount < 2
    );

    if (this.filteredMobilesForEmployee.length === 1) {
      this.assignForm.patchValue({ mobileId: this.filteredMobilesForEmployee[0].mobileId });
    } else {
      this.assignForm.patchValue({ mobileId: '' });
    }
  }

  openAssignModal(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.filteredMobilesForEmployee = [];
    this.assignForm.reset({
      employeeId: '',
      mobileId: '',
      simId: '',
      issuedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });
    this.showAssignModal = true;
  }

  closeAssignModal(): void {
    this.showAssignModal = false;
  }

  submitAssign(): void {
    if (this.assignForm.invalid) {
      this.assignForm.markAllAsTouched();
      return;
    }

    const { simId, ...payload } = this.assignForm.value;

    this.simService.assignSim(simId, payload).subscribe({
      next: () => {
        this.successMessage = 'SIM assigned to employee successfully.';
        this.closeAssignModal();
        this.loadAllData();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign SIM card.';
      }
    });
  }

  openReturnModal(assignment: SimAssignment): void {
    this.selectedAssignmentForReturn = assignment;
    this.errorMessage = '';
    this.returnForm.reset({
      returnedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });
    this.showReturnModal = true;
  }

  closeReturnModal(): void {
    this.showReturnModal = false;
    this.selectedAssignmentForReturn = null;
  }

  submitReturn(): void {
    if (this.returnForm.invalid || !this.selectedAssignmentForReturn) {
      this.returnForm.markAllAsTouched();
      return;
    }

    this.simService.returnSim(this.selectedAssignmentForReturn.simId, this.returnForm.value).subscribe({
      next: () => {
        this.successMessage = `SIM ${this.selectedAssignmentForReturn?.mobileNumber} returned successfully.`;
        this.closeReturnModal();
        this.loadAllData();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to return SIM card.';
      }
    });
  }

  // Recharge Management
  openRechargeModal(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.selectedSimForRecharge = null;
    this.rechargeForm.reset({
      simId: '',
      rechargeAmount: '',
      rechargePlan: '',
      rechargeDate: new Date().toISOString().substring(0, 10),
      planEndDate: '',
      remarks: ''
    });
    this.showRechargeModal = true;
  }

  closeRechargeModal(): void {
    this.showRechargeModal = false;
    this.selectedSimForRecharge = null;
  }

  onRechargeSimChange(): void {
    const simId = this.rechargeForm.get('simId')?.value;
    this.selectedSimForRecharge = this.allSims.find((s) => s.simId === simId) || null;
  }

  submitRecharge(): void {
    if (this.rechargeForm.invalid) {
      this.rechargeForm.markAllAsTouched();
      return;
    }

    const payload = this.rechargeForm.value;

    this.rechargeService.addRecharge(payload).subscribe({
      next: () => {
        this.successMessage = 'Recharge recorded successfully! Expiry notifications updated.';
        this.closeRechargeModal();
        this.loadAllData();
        this.notificationService.getAlertNotifications().subscribe();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to record recharge.';
      }
    });
  }

  applyRechargeFilters(): void {
    this.rechargeService.getRecharges({
      mobileNumber: this.rechargeFilterMobile,
      provider: this.rechargeFilterProvider,
      employee: this.rechargeFilterEmployee
    }).subscribe({
      next: (data) => {
        this.recharges = data;
      }
    });
  }
}
