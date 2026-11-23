import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { MobileService } from '../../services/mobile.service';
import { EmployeeService } from '../../services/employee.service';
import { Mobile, MobileAssignment, Employee } from '../../models/models';

@Component({
  selector: 'app-mobiles',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './mobiles.component.html',
  styleUrls: ['./mobiles.component.css']
})
export class MobilesComponent implements OnInit {
  mobiles: Mobile[] = [];
  employees: Employee[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // Filter & Search
  searchQuery: string = '';
  selectedBrand: string = 'ALL';
  selectedStatus: string = 'ALL';
  brands: string[] = ['Samsung', 'Apple', 'Google', 'OnePlus', 'Xiaomi', 'Motorola'];

  // Add / Edit Modal
  showFormModal: boolean = false;
  isEditing: boolean = false;
  mobileForm: FormGroup;
  currentMobileId: string | null = null;

  // Assign Modal
  showAssignModal: boolean = false;
  assignForm: FormGroup;
  selectedMobileForAssign: Mobile | null = null;

  // Return Modal
  showReturnModal: boolean = false;
  returnForm: FormGroup;
  selectedMobileForReturn: Mobile | null = null;
  activeSimWarning: string | null = null;

  // History Modal
  showHistoryModal: boolean = false;
  historyMobile: Mobile | null = null;
  mobileHistory: MobileAssignment[] = [];
  loadingHistory: boolean = false;

  constructor(
    private mobileService: MobileService,
    private employeeService: EmployeeService,
    private fb: FormBuilder
  ) {
    this.mobileForm = this.fb.group({
      mobileId: ['', [Validators.required]],
      name: ['', [Validators.required]],
      brand: ['', [Validators.required]],
      imeiNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{15}$/)]],
      purchaseDate: [''],
      warrantyExpiryDate: [''],
      status: ['Available', [Validators.required]]
    });

    this.assignForm = this.fb.group({
      employeeId: ['', [Validators.required]],
      assignedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    this.returnForm = this.fb.group({
      returnedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      returnCondition: ['Good', [Validators.required]],
      remarks: ['']
    });
  }

  ngOnInit(): void {
    this.loadMobiles();
    this.loadEmployees();
  }

  loadMobiles(): void {
    this.loading = true;
    this.mobileService.getMobiles(this.searchQuery, this.selectedBrand, this.selectedStatus).subscribe({
      next: (data) => {
        this.mobiles = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load mobile phones from server.';
        this.loading = false;
      }
    });
  }

  loadEmployees(): void {
    this.employeeService.getEmployees(undefined, undefined, 'Active').subscribe({
      next: (data) => {
        this.employees = data;
      }
    });
  }

  applyFilter(): void {
    this.loadMobiles();
  }

  openAddModal(): void {
    this.isEditing = false;
    this.currentMobileId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.mobileForm.reset({
      status: 'Available',
      purchaseDate: new Date().toISOString().substring(0, 10)
    });
    this.mobileForm.get('mobileId')?.enable();
    this.showFormModal = true;
  }

  openEditModal(mob: Mobile): void {
    this.isEditing = true;
    this.currentMobileId = mob.id || null;
    this.errorMessage = '';
    this.successMessage = '';
    this.mobileForm.patchValue({
      mobileId: mob.mobileId,
      name: mob.name,
      brand: mob.brand,
      imeiNumber: mob.imeiNumber,
      purchaseDate: mob.purchaseDate || '',
      warrantyExpiryDate: mob.warrantyExpiryDate || '',
      status: mob.status
    });
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.mobileForm.reset();
  }

  saveMobile(): void {
    if (this.mobileForm.invalid) {
      this.mobileForm.markAllAsTouched();
      return;
    }

    const payload = this.mobileForm.getRawValue();

    if (this.isEditing && this.currentMobileId) {
      this.mobileService.updateMobile(this.currentMobileId, payload).subscribe({
        next: () => {
          this.successMessage = 'Mobile phone updated successfully.';
          this.closeFormModal();
          this.loadMobiles();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update mobile.';
        }
      });
    } else {
      this.mobileService.createMobile(payload).subscribe({
        next: () => {
          this.successMessage = 'New mobile phone registered successfully.';
          this.closeFormModal();
          this.loadMobiles();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to register mobile.';
        }
      });
    }
  }

  // Assign
  openAssignModal(mob: Mobile): void {
    this.selectedMobileForAssign = mob;
    this.errorMessage = '';
    this.assignForm.reset({
      employeeId: '',
      assignedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });
    this.showAssignModal = true;
  }

  closeAssignModal(): void {
    this.showAssignModal = false;
    this.selectedMobileForAssign = null;
  }

  submitAssign(): void {
    if (this.assignForm.invalid || !this.selectedMobileForAssign) {
      this.assignForm.markAllAsTouched();
      return;
    }

    this.mobileService.assignMobile(this.selectedMobileForAssign.id!, this.assignForm.value).subscribe({
      next: () => {
        this.successMessage = `Mobile ${this.selectedMobileForAssign?.mobileId} assigned successfully.`;
        this.closeAssignModal();
        this.loadMobiles();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign mobile phone.';
      }
    });
  }

  // Return
  openReturnModal(mob: Mobile): void {
    this.selectedMobileForReturn = mob;
    this.errorMessage = '';
    this.activeSimWarning = null;

    // Check if phone has active SIM assignments
    if (mob.activeSimCount > 0) {
      this.activeSimWarning = `Warning: This mobile phone has ${mob.activeSimCount} active SIM card(s) associated with it. According to business rules, you must return or reassign the SIM cards in the 'SIM Assignments' section before returning this mobile phone.`;
    }

    this.returnForm.reset({
      returnedDate: new Date().toISOString().substring(0, 10),
      returnCondition: 'Good',
      remarks: ''
    });
    this.showReturnModal = true;
  }

  closeReturnModal(): void {
    this.showReturnModal = false;
    this.selectedMobileForReturn = null;
    this.activeSimWarning = null;
  }

  submitReturn(): void {
    if (this.returnForm.invalid || !this.selectedMobileForReturn) {
      this.returnForm.markAllAsTouched();
      return;
    }

    if (this.selectedMobileForReturn.activeSimCount > 0) {
      this.errorMessage = `Cannot return mobile phone: ${this.selectedMobileForReturn.activeSimCount} active SIM card(s) remain attached. Return the SIMs first to prevent orphan assignments.`;
      return;
    }

    this.mobileService.returnMobile(this.selectedMobileForReturn.id!, this.returnForm.value).subscribe({
      next: () => {
        this.successMessage = `Mobile ${this.selectedMobileForReturn?.mobileId} returned successfully.`;
        this.closeReturnModal();
        this.loadMobiles();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to return mobile phone.';
      }
    });
  }

  // History
  viewHistory(mob: Mobile): void {
    this.historyMobile = mob;
    this.loadingHistory = true;
    this.showHistoryModal = true;
    this.mobileService.getMobileHistory(mob.id!).subscribe({
      next: (data) => {
        this.mobileHistory = data;
        this.loadingHistory = false;
      },
      error: () => {
        this.loadingHistory = false;
      }
    });
  }

  closeHistoryModal(): void {
    this.showHistoryModal = false;
    this.historyMobile = null;
    this.mobileHistory = [];
  }
}
