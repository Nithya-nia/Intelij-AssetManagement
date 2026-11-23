import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LaptopService } from '../../services/laptop.service';
import { EmployeeService } from '../../services/employee.service';
import { Laptop, LaptopAssignment, Employee } from '../../models/models';

@Component({
  selector: 'app-laptops',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './laptops.component.html',
  styleUrls: ['./laptops.component.css']
})
export class LaptopsComponent implements OnInit {
  laptops: Laptop[] = [];
  employees: Employee[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // Filter & Search
  searchQuery: string = '';
  selectedBrand: string = 'ALL';
  selectedStatus: string = 'ALL';
  projectFilter: string = '';
  brands: string[] = ['Lenovo', 'Dell', 'HP', 'Apple', 'Asus', 'Acer'];

  processors: string[] = [
    'Intel Core i3',
    'Intel Core i5',
    'Intel Core i7',
    'Intel Core i9',
    'Intel Core Ultra 5',
    'Intel Core Ultra 7',
    'Intel Core Ultra 9',
    'AMD Ryzen 3',
    'AMD Ryzen 5',
    'AMD Ryzen 7',
    'AMD Ryzen 9',
    'Apple M1',
    'Apple M2',
    'Apple M3',
    'Apple M4',
    'Other'
  ];

  ramOptions: string[] = [
    '4 GB',
    '8 GB',
    '16 GB',
    '24 GB',
    '32 GB',
    '48 GB',
    '64 GB',
    '128 GB',
    'Other'
  ];

  storageOptions: string[] = [
    '128 GB SSD',
    '256 GB SSD',
    '512 GB SSD',
    '1 TB SSD',
    '2 TB SSD',
    '4 TB SSD',
    '500 GB HDD',
    '1 TB HDD',
    '2 TB HDD',
    'Other'
  ];

  // Add / Edit Modal
  showFormModal: boolean = false;
  isEditing: boolean = false;
  laptopForm: FormGroup;
  currentLaptopId: string | null = null;

  // Assign Modal
  showAssignModal: boolean = false;
  assignForm: FormGroup;
  selectedLaptopForAssign: Laptop | null = null;

  // Return Modal
  showReturnModal: boolean = false;
  returnForm: FormGroup;
  selectedLaptopForReturn: Laptop | null = null;

  // History / Details Modal
  showHistoryModal: boolean = false;
  historyLaptop: Laptop | null = null;
  laptopHistory: LaptopAssignment[] = [];
  loadingHistory: boolean = false;

  constructor(
    private laptopService: LaptopService,
    private employeeService: EmployeeService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.laptopForm = this.fb.group({
      laptopId: [{ value: '', disabled: true }, [Validators.required]],
      name: ['', [Validators.required]],
      brand: ['', [Validators.required]],
      projectName: ['', [Validators.required]],
      serialNumber: ['', [Validators.required]],
      configuration: [''],
      processorSelect: ['', [Validators.required]],
      customProcessor: [''],
      ramSelect: ['', [Validators.required]],
      customRam: [''],
      storageSelect: ['', [Validators.required]],
      customStorage: [''],
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
    this.route.queryParams.subscribe((params) => {
      if (params['status']) {
        this.selectedStatus = params['status'];
      }
      if (params['project']) {
        this.projectFilter = params['project'];
      }
      this.loadLaptops();
    });
    this.loadEmployees();
  }

  loadLaptops(): void {
    this.loading = true;
    this.laptopService.getLaptops(this.searchQuery, this.selectedBrand, this.selectedStatus, this.projectFilter).subscribe({
      next: (data) => {
        this.laptops = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load laptops from server.';
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
    this.loadLaptops();
  }

  onProcessorChange(): void {
    const val = this.laptopForm.get('processorSelect')?.value;
    const customControl = this.laptopForm.get('customProcessor');
    if (val === 'Other') {
      customControl?.setValidators([Validators.required]);
    } else {
      customControl?.clearValidators();
      customControl?.setValue('');
    }
    customControl?.updateValueAndValidity();
  }

  onRamChange(): void {
    const val = this.laptopForm.get('ramSelect')?.value;
    const customControl = this.laptopForm.get('customRam');
    if (val === 'Other') {
      customControl?.setValidators([Validators.required]);
    } else {
      customControl?.clearValidators();
      customControl?.setValue('');
    }
    customControl?.updateValueAndValidity();
  }

  onStorageChange(): void {
    const val = this.laptopForm.get('storageSelect')?.value;
    const customControl = this.laptopForm.get('customStorage');
    if (val === 'Other') {
      customControl?.setValidators([Validators.required]);
    } else {
      customControl?.clearValidators();
      customControl?.setValue('');
    }
    customControl?.updateValueAndValidity();
  }

  openAddModal(): void {
    this.isEditing = false;
    this.currentLaptopId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.laptopForm.reset({
      laptopId: 'Generating...',
      status: 'Available',
      projectName: '',
      purchaseDate: new Date().toISOString().substring(0, 10),
      processorSelect: '',
      customProcessor: '',
      ramSelect: '',
      customRam: '',
      storageSelect: '',
      customStorage: ''
    });
    this.laptopForm.get('laptopId')?.disable();
    this.laptopService.getNextLaptopId().subscribe({
      next: (res) => {
        this.laptopForm.patchValue({ laptopId: res.nextId });
      },
      error: () => {
        this.laptopForm.patchValue({ laptopId: 'LAP-0001' });
      }
    });
    this.showFormModal = true;
  }

  openEditModal(lpt: Laptop): void {
    this.isEditing = true;
    this.currentLaptopId = lpt.id || null;
    this.errorMessage = '';
    this.successMessage = '';

    let procSelect = lpt.processor || '';
    let customProc = '';
    if (lpt.processor && !this.processors.includes(lpt.processor)) {
      procSelect = 'Other';
      customProc = lpt.processor;
    }

    let ramSel = lpt.ram || '';
    let customR = '';
    if (lpt.ram && !this.ramOptions.includes(lpt.ram)) {
      ramSel = 'Other';
      customR = lpt.ram;
    }

    let storSel = lpt.storage || '';
    let customS = '';
    if (lpt.storage && !this.storageOptions.includes(lpt.storage)) {
      storSel = 'Other';
      customS = lpt.storage;
    }

    this.laptopForm.patchValue({
      laptopId: lpt.laptopId,
      name: lpt.name,
      brand: lpt.brand,
      projectName: lpt.projectName || '',
      serialNumber: lpt.serialNumber,
      configuration: lpt.configuration || '',
      processorSelect: procSelect,
      customProcessor: customProc,
      ramSelect: ramSel,
      customRam: customR,
      storageSelect: storSel,
      customStorage: customS,
      purchaseDate: lpt.purchaseDate || '',
      warrantyExpiryDate: lpt.warrantyExpiryDate || '',
      status: lpt.status
    });
    this.laptopForm.get('laptopId')?.disable();
    this.onProcessorChange();
    this.onRamChange();
    this.onStorageChange();
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.laptopForm.reset();
  }

  saveLaptop(): void {
    if (this.laptopForm.invalid) {
      this.laptopForm.markAllAsTouched();
      return;
    }

    const formVal = this.laptopForm.getRawValue();
    const finalProcessor = formVal.processorSelect === 'Other' ? formVal.customProcessor : formVal.processorSelect;
    const finalRam = formVal.ramSelect === 'Other' ? formVal.customRam : formVal.ramSelect;
    const finalStorage = formVal.storageSelect === 'Other' ? formVal.customStorage : formVal.storageSelect;

    const payload: Laptop = {
      ...formVal,
      processor: finalProcessor,
      ram: finalRam,
      storage: finalStorage
    };

    if (this.isEditing && this.currentLaptopId) {
      this.laptopService.updateLaptop(this.currentLaptopId, payload).subscribe({
        next: () => {
          this.successMessage = 'Laptop asset updated successfully.';
          this.closeFormModal();
          this.loadLaptops();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update laptop asset.';
        }
      });
    } else {
      this.laptopService.createLaptop(payload).subscribe({
        next: () => {
          this.successMessage = 'New laptop asset registered successfully.';
          this.closeFormModal();
          this.loadLaptops();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to register laptop.';
        }
      });
    }
  }

  // Assign Laptop
  openAssignModal(laptop: Laptop): void {
    this.selectedLaptopForAssign = laptop;
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
    this.selectedLaptopForAssign = null;
  }

  submitAssign(): void {
    if (this.assignForm.invalid || !this.selectedLaptopForAssign) {
      this.assignForm.markAllAsTouched();
      return;
    }

    this.laptopService.assignLaptop(this.selectedLaptopForAssign.id!, this.assignForm.value).subscribe({
      next: () => {
        this.successMessage = `Laptop ${this.selectedLaptopForAssign?.laptopId} assigned successfully.`;
        this.closeAssignModal();
        this.loadLaptops();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign laptop.';
      }
    });
  }

  // Return Laptop
  openReturnModal(laptop: Laptop): void {
    this.selectedLaptopForReturn = laptop;
    this.errorMessage = '';
    this.returnForm.reset({
      returnedDate: new Date().toISOString().substring(0, 10),
      returnCondition: 'Good',
      remarks: ''
    });
    this.showReturnModal = true;
  }

  closeReturnModal(): void {
    this.showReturnModal = false;
    this.selectedLaptopForReturn = null;
  }

  submitReturn(): void {
    if (this.returnForm.invalid || !this.selectedLaptopForReturn) {
      this.returnForm.markAllAsTouched();
      return;
    }

    this.laptopService.returnLaptop(this.selectedLaptopForReturn.id!, this.returnForm.value).subscribe({
      next: () => {
        this.successMessage = `Laptop ${this.selectedLaptopForReturn?.laptopId} returned successfully.`;
        this.closeReturnModal();
        this.loadLaptops();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to return laptop.';
      }
    });
  }

  // Eligible Employees: Exclude any employee who already has an active assigned laptop
  get eligibleEmployeesForAssign(): Employee[] {
    const assignedIds = new Set<string>();
    for (const lpt of this.laptops) {
      if (lpt.status === 'Assigned' && lpt.assignedEmployeeId) {
        assignedIds.add(lpt.assignedEmployeeId);
      }
    }
    return this.employees.filter((emp) => !assignedIds.has(emp.employeeId));
  }

  // Delete Laptop Asset
  deleteLaptop(laptop: Laptop): void {
    if (laptop.status === 'Assigned' || laptop.assignedEmployeeId) {
      alert(`Cannot delete laptop ${laptop.laptopId} (${laptop.name}) because it is currently assigned to ${laptop.assignedEmployeeName || 'an employee'}. Please return the laptop before deleting.`);
      return;
    }

    if (!confirm(`Are you sure you want to delete laptop asset "${laptop.name}" (${laptop.laptopId})? This action cannot be undone.`)) {
      return;
    }

    this.laptopService.deleteLaptop(laptop.id!).subscribe({
      next: () => {
        this.successMessage = `Laptop ${laptop.laptopId} deleted successfully.`;
        this.loadLaptops();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || `Failed to delete laptop ${laptop.laptopId}.`;
      }
    });
  }

  // History
  viewHistory(laptop: Laptop): void {
    this.historyLaptop = laptop;
    this.loadingHistory = true;
    this.showHistoryModal = true;
    this.laptopService.getLaptopHistory(laptop.id!).subscribe({
      next: (data) => {
        this.laptopHistory = data;
        this.loadingHistory = false;
      },
      error: () => {
        this.loadingHistory = false;
      }
    });
  }

  closeHistoryModal(): void {
    this.showHistoryModal = false;
    this.historyLaptop = null;
    this.laptopHistory = [];
  }
}
