import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MobileService } from '../../services/mobile.service';
import { SimService } from '../../services/sim.service';
import { EmployeeService } from '../../services/employee.service';
import { RechargeService } from '../../services/recharge.service';
import { NotificationService } from '../../services/notification.service';
import { SimMappingService } from '../../services/sim-mapping.service';
import { Mobile, MobileAssignment, SimCard, SimAssignment, Recharge, Employee, NotificationItem, SimMappingDevice } from '../../models/models';

@Component({
  selector: 'app-mobile-sim',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  templateUrl: './mobile-sim.component.html',
  styleUrls: ['./mobile-sim.component.css']
})
export class MobileSimComponent implements OnInit {
  activeTab: 'mobiles' | 'sims' | 'mapping' | 'assignments' | 'recharges' = 'mobiles';

  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // Common data
  employees: Employee[] = [];
  mobiles: Mobile[] = [];
  sims: SimCard[] = [];
  assignments: SimAssignment[] = [];
  recharges: Recharge[] = [];
  notifications: NotificationItem[] = [];

  // ================= MOBILE PHONE STATE =================
  mobileSearch: string = '';
  mobileBrandFilter: string = 'ALL';
  mobileStatusFilter: string = 'ALL';
  mobileProjectFilter: string = '';
  mobileSlotFilter: string = 'ALL';
  brands: string[] = [
    'Samsung',
    'Apple',
    'Xiaomi',
    'Redmi',
    'POCO',
    'OnePlus',
    'OPPO',
    'Vivo',
    'Realme',
    'Motorola',
    'iQOO',
    'Nothing',
    'Google Pixel',
    'Nokia',
    'Lava',
    'Tecno',
    'Infinix',
    'ASUS',
    'Other'
  ];
  slotConfigs: string[] = ['Single SIM', 'Dual SIM'];

  showMobileFormModal: boolean = false;
  isEditingMobile: boolean = false;
  mobileForm: FormGroup;
  currentMobileId: string | null = null;
  editingMobileActiveSims: number = 0;

  showMobileAssignModal: boolean = false;
  mobileAssignForm: FormGroup;
  selectedMobileForAssign: Mobile | null = null;

  showMobileReturnModal: boolean = false;
  mobileReturnForm: FormGroup;
  selectedMobileForReturn: Mobile | null = null;
  mobileActiveSimWarning: string | null = null;

  showMobileHistoryModal: boolean = false;
  historyMobile: Mobile | null = null;
  mobileHistory: MobileAssignment[] = [];
  loadingMobileHistory: boolean = false;

  // ================= SIM CARD STATE =================
  simSearch: string = '';
  simProviderFilter: string = 'ALL';
  simStatusFilter: string = 'ALL';
  simTypeFilter: string = 'ALL';
  providers: string[] = ['Airtel', 'Jio', 'Vi', 'BSNL', 'Other'];

  showSimFormModal: boolean = false;
  isEditingSim: boolean = false;
  simForm: FormGroup;
  currentSimId: string | null = null;

  showSimDetailsModal: boolean = false;
  selectedSimForDetails: SimCard | null = null;
  simDetailsHistory: SimAssignment[] = [];
  simDetailsRecharges: Recharge[] = [];
  simDetailsTab: 'history' | 'recharges' = 'history';
  loadingSimDetails: boolean = false;

  // ================= SIM ASSIGNMENTS STATE =================
  assignmentStatusFilter: string = 'ALL';
  assignmentSearch: string = '';
  showSimAssignModal: boolean = false;
  simAssignForm: FormGroup;
  filteredMobilesForEmployee: Mobile[] = [];
  availableSimsForIssue: SimCard[] = [];
  allAvailableSims: SimCard[] = [];
  selectedMobileForSimIssue: Mobile | null = null;
  mobileSpecDetails: {
    slotConfig: string;
    maxSlots: number;
    usedSlots: number;
    supportedFormat: string;
    isFull: boolean;
    slot1Occupied: boolean;
    slot1Label: string;
    slot2Occupied: boolean;
    slot2Label: string;
  } | null = null;
  selectedSimForIssue: SimCard | null = null;

  showSimReturnModal: boolean = false;
  simReturnForm: FormGroup;
  selectedSimAssignmentForReturn: SimAssignment | null = null;

  // ================= SIM MAPPING STATE =================
  mappingDevices: SimMappingDevice[] = [];
  availableSimsForMapping: SimCard[] = [];
  loadingMapping: boolean = false;
  mappingSearch: string = '';
  showMappingModal: boolean = false;
  mappingForm: FormGroup;
  selectedMobileForMapping: SimMappingDevice | null = null;
  selectedSimForMapping: SimCard | null = null;

  // ================= RECHARGE STATE =================
  rechargeMobileFilter: string = '';
  rechargeProviderFilter: string = 'ALL';
  rechargeEmployeeFilter: string = '';
  rechargeExpiryOnly: boolean = false;

  showRechargeModal: boolean = false;
  rechargeForm: FormGroup;

  constructor(
    private mobileService: MobileService,
    private simService: SimService,
    private employeeService: EmployeeService,
    private rechargeService: RechargeService,
    private notificationService: NotificationService,
    private simMappingService: SimMappingService,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {
    // 1. Mobile Form
    this.mobileForm = this.fb.group({
      mobileId: [{ value: '', disabled: true }, [Validators.required]],
      name: ['', [Validators.required]],
      brandSelect: ['Samsung', [Validators.required]],
      customBrand: [''],
      model: [''],
      imeiNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{15}$/)]],
      projectName: ['', [Validators.required]],
      simSlotConfig: ['Dual SIM', [Validators.required]],
      purchaseDate: [''],
      warrantyExpiryDate: [''],
      status: ['Available', [Validators.required]]
    });

    this.mappingForm = this.fb.group({
      mobileId: ['', [Validators.required]],
      simId: ['', [Validators.required]],
      slotNumber: [1, [Validators.required]],
      remarks: ['']
    });

    this.mobileAssignForm = this.fb.group({
      employeeId: ['', [Validators.required]],
      assignedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    this.mobileReturnForm = this.fb.group({
      returnedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      returnCondition: ['Good', [Validators.required]],
      remarks: ['']
    });

    // 2. SIM Card Form
    this.simForm = this.fb.group({
      simId: ['', [Validators.required]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      provider: ['Airtel', [Validators.required]],
      simSerialNumber: [''],
      simType: ['Physical SIM', [Validators.required]],
      status: ['Available', [Validators.required]]
    });

    // 3. SIM Assignment Form
    this.simAssignForm = this.fb.group({
      employeeId: ['', [Validators.required]],
      mobileId: ['', [Validators.required]],
      slotNumber: [1, [Validators.required]],
      simId: ['', [Validators.required]],
      issuedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    this.simReturnForm = this.fb.group({
      returnedDate: [new Date().toISOString().substring(0, 10), [Validators.required]],
      remarks: ['']
    });

    // 4. Recharge Form
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
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        const t = params['tab'].toLowerCase();
        if (t === 'mobiles' || t === 'sims' || t === 'mapping' || t === 'assignments' || t === 'recharges') {
          this.activeTab = t as any;
        }
      }
      if (params['status']) {
        if (this.activeTab === 'mobiles') this.mobileStatusFilter = params['status'];
        if (this.activeTab === 'sims') this.simStatusFilter = params['status'];
        if (this.activeTab === 'assignments') this.assignmentStatusFilter = params['status'];
      }
      if (params['filter'] === 'expiring') {
        this.activeTab = 'recharges';
        this.rechargeExpiryOnly = true;
      }
      this.loadAllData();
    });
  }

  setTab(tab: 'mobiles' | 'sims' | 'mapping' | 'assignments' | 'recharges'): void {
    this.activeTab = tab;
    this.errorMessage = '';
    this.successMessage = '';
    if (tab === 'mapping') {
      this.loadMappingData();
    }
  }

  loadAllData(): void {
    this.loading = true;
    this.loadMobiles();
    this.loadSims();
    this.loadMappingData();
    this.loadAssignments();
    this.loadRecharges();
    this.loadEmployees();
    this.loadNotifications();
  }

  loadEmployees(): void {
    this.employeeService.getEmployees(undefined, undefined, 'Active').subscribe({
      next: (data) => (this.employees = data)
    });
  }

  loadNotifications(): void {
    this.notificationService.getAlertNotifications().subscribe({
      next: (data) => (this.notifications = data)
    });
  }

  // ================= MOBILE METHODS =================
  loadMobiles(): void {
    this.mobileService
      .getMobiles(
        this.mobileSearch,
        this.mobileBrandFilter,
        this.mobileStatusFilter,
        this.mobileProjectFilter,
        this.mobileSlotFilter
      )
      .subscribe({
        next: (data) => {
          this.mobiles = data;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Failed to load mobile phone assets.';
          this.loading = false;
        }
      });
  }

  applyMobileFilter(): void {
    this.loadMobiles();
  }

  onBrandChange(): void {
    const val = this.mobileForm.get('brandSelect')?.value;
    const customControl = this.mobileForm.get('customBrand');
    if (val === 'Other') {
      customControl?.setValidators([Validators.required]);
    } else {
      customControl?.clearValidators();
      customControl?.setValue('');
    }
    customControl?.updateValueAndValidity();
  }

  openAddMobileModal(): void {
    this.isEditingMobile = false;
    this.currentMobileId = null;
    this.editingMobileActiveSims = 0;
    this.errorMessage = '';
    this.successMessage = '';
    this.mobileForm.reset({
      mobileId: 'Generating...',
      brandSelect: 'Samsung',
      customBrand: '',
      simSlotConfig: 'Dual SIM',
      status: 'Available',
      purchaseDate: new Date().toISOString().substring(0, 10)
    });
    this.mobileForm.get('mobileId')?.disable();
    this.mobileService.getNextMobileId().subscribe({
      next: (res) => {
        this.mobileForm.patchValue({ mobileId: res.nextId });
      },
      error: () => {
        this.mobileForm.patchValue({ mobileId: 'MOB-0001' });
      }
    });
    this.showMobileFormModal = true;
  }

  openEditMobileModal(m: Mobile): void {
    this.isEditingMobile = true;
    this.currentMobileId = m.id || null;
    this.editingMobileActiveSims = m.activeSimCount || 0;
    this.errorMessage = '';
    this.successMessage = '';

    let brandSel = m.brand || 'Samsung';
    let customB = '';
    if (m.brand && !this.brands.includes(m.brand)) {
      brandSel = 'Other';
      customB = m.brand;
    }

    this.mobileForm.patchValue({
      mobileId: m.mobileId,
      name: m.name,
      brandSelect: brandSel,
      customBrand: customB,
      model: m.model || '',
      imeiNumber: m.imeiNumber,
      projectName: m.projectName || '',
      simSlotConfig: m.simSlotConfig || 'Dual SIM',
      purchaseDate: m.purchaseDate || '',
      warrantyExpiryDate: m.warrantyExpiryDate || '',
      status: m.status
    });
    this.mobileForm.get('mobileId')?.disable();
    this.onBrandChange();
    this.showMobileFormModal = true;
  }

  closeMobileFormModal(): void {
    this.showMobileFormModal = false;
    this.mobileForm.reset();
  }

  saveMobile(): void {
    if (this.mobileForm.invalid) {
      this.mobileForm.markAllAsTouched();
      return;
    }

    const formVal = this.mobileForm.getRawValue();
    const finalBrand = formVal.brandSelect === 'Other' ? formVal.customBrand : formVal.brandSelect;

    // Business rule validation: Admin Editing SIM Slot Configuration
    if (this.isEditingMobile && this.editingMobileActiveSims > 1) {
      if (formVal.simSlotConfig === 'Single SIM') {
        this.errorMessage =
          'Cannot change SIM slot configuration to Single SIM while multiple SIM cards are assigned. Please unassign SIMs first.';
        return;
      }
    }

    const payload: Mobile = {
      ...formVal,
      brand: finalBrand
    };

    if (this.isEditingMobile && this.currentMobileId) {
      this.mobileService.updateMobile(this.currentMobileId, payload).subscribe({
        next: () => {
          this.successMessage = 'Mobile phone asset updated successfully.';
          this.closeMobileFormModal();
          this.loadMobiles();
          this.loadMappingData();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update mobile asset.';
        }
      });
    } else {
      this.mobileService.createMobile(payload).subscribe({
        next: () => {
          this.successMessage = 'New mobile phone asset registered successfully.';
          this.closeMobileFormModal();
          this.loadMobiles();
          this.loadMappingData();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to register mobile phone.';
        }
      });
    }
  }

  openMobileAssignModal(mob: Mobile): void {
    this.selectedMobileForAssign = mob;
    this.errorMessage = '';
    this.mobileAssignForm.reset({
      employeeId: '',
      assignedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });
    this.showMobileAssignModal = true;
  }

  closeMobileAssignModal(): void {
    this.showMobileAssignModal = false;
    this.selectedMobileForAssign = null;
  }

  submitMobileAssign(): void {
    if (this.mobileAssignForm.invalid || !this.selectedMobileForAssign) {
      this.mobileAssignForm.markAllAsTouched();
      return;
    }

    this.mobileService.assignMobile(this.selectedMobileForAssign.id!, this.mobileAssignForm.value).subscribe({
      next: () => {
        this.successMessage = `Mobile ${this.selectedMobileForAssign?.mobileId} assigned successfully.`;
        this.closeMobileAssignModal();
        this.loadMobiles();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign mobile phone.';
      }
    });
  }

  openMobileReturnModal(mob: Mobile): void {
    this.selectedMobileForReturn = mob;
    this.errorMessage = '';
    this.mobileActiveSimWarning = null;

    // Asset Return Safeguard: Check active SIM assignments
    if (mob.activeSimCount > 0) {
      this.mobileActiveSimWarning = `Action Blocked: This mobile phone has ${mob.activeSimCount} active SIM card(s) assigned to it. According to business rules, you must return or reassign the active SIM cards in the 'SIM Assignments' tab first.`;
    }

    this.mobileReturnForm.reset({
      returnedDate: new Date().toISOString().substring(0, 10),
      returnCondition: 'Good',
      remarks: ''
    });
    this.showMobileReturnModal = true;
  }

  closeMobileReturnModal(): void {
    this.showMobileReturnModal = false;
    this.selectedMobileForReturn = null;
    this.mobileActiveSimWarning = null;
  }

  submitMobileReturn(): void {
    if (this.mobileReturnForm.invalid || !this.selectedMobileForReturn) {
      this.mobileReturnForm.markAllAsTouched();
      return;
    }

    if (this.selectedMobileForReturn.activeSimCount > 0) {
      this.errorMessage = `Cannot return mobile phone: ${this.selectedMobileForReturn.activeSimCount} active SIM card(s) remain attached. Return the SIMs first to prevent orphan assignments.`;
      return;
    }

    this.mobileService.returnMobile(this.selectedMobileForReturn.id!, this.mobileReturnForm.value).subscribe({
      next: () => {
        this.successMessage = `Mobile ${this.selectedMobileForReturn?.mobileId} returned successfully.`;
        this.closeMobileReturnModal();
        this.loadMobiles();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to return mobile phone.';
      }
    });
  }

  viewMobileHistory(mob: Mobile): void {
    this.historyMobile = mob;
    this.loadingMobileHistory = true;
    this.showMobileHistoryModal = true;
    this.mobileService.getMobileHistory(mob.id!).subscribe({
      next: (data) => {
        this.mobileHistory = data;
        this.loadingMobileHistory = false;
      },
      error: () => (this.loadingMobileHistory = false)
    });
  }

  closeMobileHistoryModal(): void {
    this.showMobileHistoryModal = false;
    this.historyMobile = null;
    this.mobileHistory = [];
  }

  // ================= SIM CARD METHODS =================
  loadSims(): void {
    this.simService.getSims(this.simSearch, this.simProviderFilter, this.simStatusFilter).subscribe({
      next: (data) => {
        if (this.simTypeFilter !== 'ALL') {
          this.sims = data.filter((s) => s.simType === this.simTypeFilter);
        } else {
          this.sims = data;
        }
      }
    });
  }

  applySimFilter(): void {
    this.loadSims();
  }

  openAddSimModal(): void {
    this.isEditingSim = false;
    this.currentSimId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.simForm.reset({
      provider: 'Airtel',
      simType: 'Physical SIM',
      status: 'Available'
    });
    this.simForm.get('simId')?.enable();
    this.showSimFormModal = true;
  }

  openEditSimModal(sim: SimCard): void {
    this.isEditingSim = true;
    this.currentSimId = sim.id || null;
    this.errorMessage = '';
    this.successMessage = '';
    this.simForm.patchValue({
      simId: sim.simId,
      mobileNumber: sim.mobileNumber,
      provider: sim.provider,
      simSerialNumber: sim.simSerialNumber || '',
      simType: sim.simType,
      status: sim.status
    });
    this.showSimFormModal = true;
  }

  closeSimFormModal(): void {
    this.showSimFormModal = false;
    this.simForm.reset();
  }

  saveSim(): void {
    if (this.simForm.invalid) {
      this.simForm.markAllAsTouched();
      return;
    }

    const payload = this.simForm.getRawValue();

    if (this.isEditingSim && this.currentSimId) {
      this.simService.updateSim(this.currentSimId, payload).subscribe({
        next: () => {
          this.successMessage = 'SIM card updated successfully.';
          this.closeSimFormModal();
          this.loadSims();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update SIM card.';
        }
      });
    } else {
      this.simService.createSim(payload).subscribe({
        next: () => {
          this.successMessage = 'New SIM card registered successfully.';
          this.closeSimFormModal();
          this.loadSims();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to register SIM card.';
        }
      });
    }
  }

  viewSimDetails(sim: SimCard): void {
    this.selectedSimForDetails = sim;
    this.loadingSimDetails = true;
    this.showSimDetailsModal = true;
    this.simDetailsTab = 'history';

    this.simService.getSimHistory(sim.id!).subscribe({
      next: (h) => (this.simDetailsHistory = h)
    });

    this.simService.getSimRecharges(sim.id!).subscribe({
      next: (r) => {
        this.simDetailsRecharges = r;
        this.loadingSimDetails = false;
      },
      error: () => (this.loadingSimDetails = false)
    });
  }

  closeSimDetailsModal(): void {
    this.showSimDetailsModal = false;
    this.selectedSimForDetails = null;
    this.simDetailsHistory = [];
    this.simDetailsRecharges = [];
  }

  // ================= SIM MAPPING METHODS =================
  loadMappingData(): void {
    this.loadingMapping = true;
    this.simMappingService.getMappingStatuses().subscribe({
      next: (devices) => {
        this.mappingDevices = devices;
        this.loadingMapping = false;
      },
      error: () => {
        this.loadingMapping = false;
      }
    });
    this.simMappingService.getAvailableSims().subscribe({
      next: (sims) => {
        this.availableSimsForMapping = sims;
      }
    });
  }

  get filteredMappingDevices(): SimMappingDevice[] {
    if (!this.mappingSearch) return this.mappingDevices;
    const q = this.mappingSearch.toLowerCase().trim();
    return this.mappingDevices.filter(d =>
      d.name?.toLowerCase().includes(q) ||
      d.mobileId?.toLowerCase().includes(q) ||
      d.brand?.toLowerCase().includes(q) ||
      d.imeiNumber?.toLowerCase().includes(q) ||
      d.assignedEmployeeName?.toLowerCase().includes(q) ||
      d.slot1?.mobileNumber?.includes(q) ||
      d.slot2?.mobileNumber?.includes(q)
    );
  }

  get totalMobilesCount(): number {
    return this.mappingDevices.length;
  }

  get mappedMobilesCount(): number {
    return this.mappingDevices.filter(d => d.occupiedSlots > 0).length;
  }

  get totalFreeSlotsCount(): number {
    return this.mappingDevices.reduce((sum, d) => sum + d.freeSlots, 0);
  }

  get unmappedSimsCount(): number {
    return this.availableSimsForMapping.length;
  }

  openMappingModal(device?: SimMappingDevice): void {
    this.selectedMobileForMapping = device || null;
    this.selectedSimForMapping = null;
    this.errorMessage = '';
    this.successMessage = '';

    let defaultSlot = 1;
    let defaultMobileId = device ? device.mobileId : '';
    if (device) {
      if (device.slot1.isOccupied && !device.slot2.isOccupied && device.maxSlots >= 2) {
        defaultSlot = 2;
      }
    }

    this.mappingForm.reset({
      mobileId: defaultMobileId,
      simId: '',
      slotNumber: defaultSlot,
      remarks: ''
    });

    this.simMappingService.getAvailableSims().subscribe({
      next: (sims) => {
        this.availableSimsForMapping = sims;
      }
    });

    this.showMappingModal = true;
  }

  onMappingMobileChange(): void {
    const mobId = this.mappingForm.get('mobileId')?.value;
    this.selectedMobileForMapping = this.mappingDevices.find(d => d.mobileId === mobId) || null;
    if (this.selectedMobileForMapping) {
      if (this.selectedMobileForMapping.slot1.isOccupied && !this.selectedMobileForMapping.slot2.isOccupied && this.selectedMobileForMapping.maxSlots >= 2) {
        this.mappingForm.patchValue({ slotNumber: 2 });
      } else {
        this.mappingForm.patchValue({ slotNumber: 1 });
      }
    }
  }

  onMappingSimChange(): void {
    const simId = this.mappingForm.get('simId')?.value;
    this.selectedSimForMapping = this.availableSimsForMapping.find(s => s.simId === simId) || null;
  }

  closeMappingModal(): void {
    this.showMappingModal = false;
    this.mappingForm.reset();
    this.selectedMobileForMapping = null;
    this.selectedSimForMapping = null;
  }

  confirmMapping(): void {
    if (this.mappingForm.invalid) {
      this.mappingForm.markAllAsTouched();
      return;
    }

    const payload = this.mappingForm.value;
    this.simMappingService.mapSim(payload).subscribe({
      next: () => {
        this.successMessage = 'SIM card successfully mapped to mobile device!';
        this.closeMappingModal();
        this.loadMappingData();
        this.loadMobiles();
        this.loadSims();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to map SIM card to device.';
      }
    });
  }

  unmapSimSlot(device: SimMappingDevice, slotNumber: number): void {
    const slotInfo = slotNumber === 1 ? device.slot1 : device.slot2;
    const simDesc = slotInfo.mobileNumber || slotInfo.simId || `Slot ${slotNumber}`;
    if (!confirm(`Are you sure you want to unmap SIM (${simDesc}) from Slot ${slotNumber} of ${device.name}?`)) {
      return;
    }

    this.simMappingService.unmapSim({
      mobileId: device.mobileId,
      slotNumber: slotNumber
    }).subscribe({
      next: () => {
        this.successMessage = `Slot ${slotNumber} SIM successfully unmapped from ${device.name}.`;
        this.loadMappingData();
        this.loadMobiles();
        this.loadSims();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to unmap SIM.';
      }
    });
  }

  // ================= SIM ASSIGNMENTS METHODS =================
  loadAssignments(): void {
    this.simService.getAllAssignments().subscribe({
      next: (data) => (this.assignments = data)
    });
  }

  getFilteredAssignments(): SimAssignment[] {
    let result = this.assignments;
    if (this.assignmentStatusFilter !== 'ALL') {
      result = result.filter((a) => a.status === this.assignmentStatusFilter);
    }
    if (this.assignmentSearch.trim()) {
      const q = this.assignmentSearch.toLowerCase().trim();
      result = result.filter(
        (a) =>
          a.employeeName.toLowerCase().includes(q) ||
          a.employeeId.toLowerCase().includes(q) ||
          a.mobileNumber.includes(q) ||
          a.mobileName.toLowerCase().includes(q) ||
          a.provider.toLowerCase().includes(q)
      );
    }
    return result;
  }

  openSimAssignModal(preSelectedMobile?: Mobile, targetSlot?: number): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.filteredMobilesForEmployee = [];
    this.availableSimsForIssue = [];
    this.allAvailableSims = [];
    this.selectedMobileForSimIssue = null;
    this.mobileSpecDetails = null;
    this.selectedSimForIssue = null;

    // Load available SIMs
    this.simService.getSims(undefined, undefined, 'Available').subscribe({
      next: (data) => {
        this.allAvailableSims = data;
        this.availableSimsForIssue = data;

        if (preSelectedMobile && preSelectedMobile.assignedEmployeeId) {
          this.simAssignForm.patchValue({
            employeeId: preSelectedMobile.assignedEmployeeId
          });
          this.onEmployeeSelectedForSim();
          this.simAssignForm.patchValue({
            mobileId: preSelectedMobile.id
          });
          this.onMobileSelectedForSim();
          if (targetSlot) {
            this.simAssignForm.patchValue({ slotNumber: targetSlot });
          }
        }
      }
    });

    this.simAssignForm.reset({
      employeeId: '',
      mobileId: '',
      slotNumber: 1,
      simId: '',
      issuedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });

    this.showSimAssignModal = true;
  }

  closeSimAssignModal(): void {
    this.showSimAssignModal = false;
    this.filteredMobilesForEmployee = [];
    this.selectedMobileForSimIssue = null;
    this.mobileSpecDetails = null;
    this.selectedSimForIssue = null;
  }

  onEmployeeSelectedForSim(): void {
    const empId = this.simAssignForm.get('employeeId')?.value;
    this.selectedMobileForSimIssue = null;
    this.mobileSpecDetails = null;
    this.selectedSimForIssue = null;
    this.errorMessage = '';

    if (!empId) {
      this.filteredMobilesForEmployee = [];
      this.simAssignForm.patchValue({ mobileId: '', simId: '' });
      this.availableSimsForIssue = this.allAvailableSims;
      return;
    }

    // Find assigned mobile for this employee
    this.filteredMobilesForEmployee = this.mobiles.filter(
      (m) => m.assignedEmployeeId === empId && m.status === 'Assigned'
    );

    if (this.filteredMobilesForEmployee.length === 1) {
      this.simAssignForm.patchValue({ mobileId: this.filteredMobilesForEmployee[0].id });
      this.onMobileSelectedForSim();
    } else {
      this.simAssignForm.patchValue({ mobileId: '', simId: '' });
      this.availableSimsForIssue = this.allAvailableSims;
    }
  }

  onMobileSelectedForSim(): void {
    const mobId = this.simAssignForm.get('mobileId')?.value;
    this.selectedSimForIssue = null;
    this.simAssignForm.patchValue({ simId: '' });
    this.errorMessage = '';

    if (!mobId) {
      this.selectedMobileForSimIssue = null;
      this.mobileSpecDetails = null;
      this.availableSimsForIssue = this.allAvailableSims;
      return;
    }

    const mob = this.filteredMobilesForEmployee.find((m) => m.id === mobId);
    if (!mob) {
      this.selectedMobileForSimIssue = null;
      this.mobileSpecDetails = null;
      return;
    }

    this.selectedMobileForSimIssue = mob;
    const config = mob.simSlotConfig || 'Dual SIM';
    let maxSlots = 2;
    let supportedFormat = 'Physical Nano-SIM Only';

    if (config === 'Single SIM') {
      maxSlots = 1;
      supportedFormat = 'Physical Nano-SIM (Single Slot)';
    } else if (config === 'Dual SIM') {
      maxSlots = 2;
      supportedFormat = 'Physical Nano-SIM (Dual Slot)';
    } else if (config === 'eSIM') {
      maxSlots = 1;
      supportedFormat = 'Digital eSIM Profile Only';
    } else if (config === 'Hybrid SIM') {
      maxSlots = 2;
      supportedFormat = 'Physical SIM or eSIM (Hybrid Tray)';
    }

    const usedSlots = mob.activeSimCount || 0;
    const isFull = usedSlots >= maxSlots;
    const slot1Occupied = !!(mob.slot1SimId && mob.slot1SimId.trim());
    const slot1Label = mob.slot1MobileNumber ? `${mob.slot1MobileNumber} (${mob.slot1Provider || 'SIM'})` : '';
    const slot2Occupied = !!(mob.slot2SimId && mob.slot2SimId.trim());
    const slot2Label = mob.slot2MobileNumber ? `${mob.slot2MobileNumber} (${mob.slot2Provider || 'SIM'})` : '';

    this.mobileSpecDetails = {
      slotConfig: config,
      maxSlots,
      usedSlots,
      supportedFormat,
      isFull,
      slot1Occupied,
      slot1Label,
      slot2Occupied,
      slot2Label
    };

    // Auto-select preferred empty slot
    if (!slot1Occupied) {
      this.simAssignForm.patchValue({ slotNumber: 1 });
    } else if (maxSlots >= 2 && !slot2Occupied) {
      this.simAssignForm.patchValue({ slotNumber: 2 });
    } else {
      this.simAssignForm.patchValue({ slotNumber: 1 });
    }

    if (isFull) {
      this.errorMessage = `Device capacity reached: ${mob.name} has '${config}' specifications allowing maximum ${maxSlots} SIM card(s), and all ${usedSlots} slot(s) are currently in use. Please return an existing SIM before assigning another.`;
      this.availableSimsForIssue = [];
      return;
    }

    // Filter available SIMs for issue
    this.availableSimsForIssue = this.allAvailableSims;
    if (this.availableSimsForIssue.length === 0) {
      this.errorMessage = `Notice: There are currently no available SIM cards in inventory.`;
    }
  }

  onSimSelectedForIssue(): void {
    const simId = this.simAssignForm.get('simId')?.value;
    if (!simId) {
      this.selectedSimForIssue = null;
      return;
    }
    this.selectedSimForIssue = this.availableSimsForIssue.find((s) => s.id === simId) || null;
  }

  submitSimAssign(): void {
    if (this.simAssignForm.invalid) {
      this.simAssignForm.markAllAsTouched();
      return;
    }

    const { employeeId, mobileId, slotNumber, simId, issuedDate, remarks } = this.simAssignForm.value;

    this.simService.assignSim(simId, { employeeId, mobileId, slotNumber: Number(slotNumber), issuedDate, remarks }).subscribe({
      next: () => {
        this.successMessage = `SIM card assigned to Slot ${slotNumber} and paired successfully.`;
        this.closeSimAssignModal();
        this.loadAllData();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign SIM card.';
      }
    });
  }

  openSimReturnModal(assign: SimAssignment): void {
    this.selectedSimAssignmentForReturn = assign;
    this.errorMessage = '';
    this.simReturnForm.reset({
      returnedDate: new Date().toISOString().substring(0, 10),
      remarks: ''
    });
    this.showSimReturnModal = true;
  }

  closeSimReturnModal(): void {
    this.showSimReturnModal = false;
    this.selectedSimAssignmentForReturn = null;
  }

  submitSimReturn(): void {
    if (this.simReturnForm.invalid || !this.selectedSimAssignmentForReturn) {
      this.simReturnForm.markAllAsTouched();
      return;
    }

    const simId = this.selectedSimAssignmentForReturn.simId;
    this.simService.returnSim(simId, this.simReturnForm.value).subscribe({
      next: () => {
        this.successMessage = 'SIM card returned and released to available inventory.';
        this.closeSimReturnModal();
        this.loadAllData();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to return SIM card.';
      }
    });
  }

  // ================= RECHARGE METHODS =================
  loadRecharges(): void {
    this.rechargeService
      .getRecharges({
        mobileNumber: this.rechargeMobileFilter,
        provider: this.rechargeProviderFilter !== 'ALL' ? this.rechargeProviderFilter : undefined,
        employee: this.rechargeEmployeeFilter
      })
      .subscribe({
        next: (data) => (this.recharges = data)
      });
  }

  applyRechargeFilter(): void {
    this.loadRecharges();
  }

  getFilteredRecharges(): Recharge[] {
    let result = this.recharges;
    if (this.rechargeExpiryOnly) {
      const now = new Date();
      const next7Days = new Date();
      next7Days.setDate(now.getDate() + 7);

      result = result.filter((r) => {
        const end = new Date(r.planEndDate);
        return end <= next7Days;
      });
    }
    return result;
  }

  openAddRechargeModal(sim?: SimCard): void {
    this.errorMessage = '';
    this.successMessage = '';

    const today = new Date().toISOString().substring(0, 10);
    const in28Days = new Date();
    in28Days.setDate(in28Days.getDate() + 28);
    const endStr = in28Days.toISOString().substring(0, 10);

    this.rechargeForm.reset({
      simId: sim ? sim.id : '',
      rechargeAmount: 299,
      rechargePlan: '28 Days Unlimited Voice + 1.5GB/day',
      rechargeDate: today,
      planEndDate: endStr,
      remarks: ''
    });

    this.showRechargeModal = true;
  }

  closeRechargeModal(): void {
    this.showRechargeModal = false;
  }

  submitRecharge(): void {
    if (this.rechargeForm.invalid) {
      this.rechargeForm.markAllAsTouched();
      return;
    }

    this.rechargeService.addRecharge(this.rechargeForm.value).subscribe({
      next: () => {
        this.successMessage = 'Recharge recorded and plan updated successfully.';
        this.closeRechargeModal();
        this.loadRecharges();
        this.loadSims();
        this.loadNotifications();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to record recharge.';
      }
    });
  }
}
