import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { Employee, EmployeeAssets } from '../../models/models';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.css']
})
export class EmployeesComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // Filter & Search
  searchQuery: string = '';
  selectedDepartment: string = 'ALL';
  selectedStatus: string = 'ALL';
  departments: string[] = ['HR', 'Developers', 'Admin'];

  // Modal states
  showFormModal: boolean = false;
  isEditing: boolean = false;
  employeeForm: FormGroup;
  currentEmployeeId: string | null = null;

  showDetailsModal: boolean = false;
  selectedEmployeeAssets: EmployeeAssets | null = null;
  loadingAssets: boolean = false;

  constructor(
    private employeeService: EmployeeService,
    private fb: FormBuilder
  ) {
    this.employeeForm = this.fb.group({
      employeeId: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9-_]+$/)]],
      name: ['', [Validators.required, Validators.minLength(2)]],
      department: ['', [Validators.required]],
      designation: ['', [Validators.required]],
      joiningDate: ['', [Validators.required]],
      email: ['', [Validators.email]],
      phone: ['', [Validators.pattern(/^[0-9]{10,15}$/)]],
      status: ['Active', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading = true;
    this.employeeService.getEmployees(this.searchQuery, this.selectedDepartment, this.selectedStatus).subscribe({
      next: (data) => {
        this.employees = data;
        this.filteredEmployees = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load employees from server.';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    this.loadEmployees();
  }

  openAddModal(): void {
    this.isEditing = false;
    this.currentEmployeeId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.employeeForm.reset({
      status: 'Active',
      joiningDate: new Date().toISOString().substring(0, 10)
    });
    this.employeeForm.get('employeeId')?.enable();
    this.showFormModal = true;
  }

  openEditModal(emp: Employee): void {
    this.isEditing = true;
    this.currentEmployeeId = emp.id || null;
    this.errorMessage = '';
    this.successMessage = '';
    this.employeeForm.patchValue({
      employeeId: emp.employeeId,
      name: emp.name,
      department: emp.department,
      designation: emp.designation,
      joiningDate: emp.joiningDate,
      email: emp.email || '',
      phone: emp.phone || '',
      status: emp.status
    });
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.employeeForm.reset();
  }

  saveEmployee(): void {
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    const formValues = this.employeeForm.getRawValue();

    if (this.isEditing && this.currentEmployeeId) {
      this.employeeService.updateEmployee(this.currentEmployeeId, formValues).subscribe({
        next: () => {
          this.successMessage = 'Employee details updated successfully.';
          this.closeFormModal();
          this.loadEmployees();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update employee.';
        }
      });
    } else {
      this.employeeService.createEmployee(formValues).subscribe({
        next: () => {
          this.successMessage = 'New employee added successfully.';
          this.closeFormModal();
          this.loadEmployees();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to add employee.';
        }
      });
    }
  }

  viewDetails(emp: Employee): void {
    this.loadingAssets = true;
    this.selectedEmployeeAssets = null;
    this.showDetailsModal = true;
    this.employeeService.getEmployeeAssets(emp.employeeId).subscribe({
      next: (data) => {
        this.selectedEmployeeAssets = data;
        this.loadingAssets = false;
      },
      error: () => {
        this.loadingAssets = false;
      }
    });
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedEmployeeAssets = null;
  }

  deleteEmployee(emp: Employee): void {
    if (!confirm(`Are you sure you want to delete employee "${emp.name}" (${emp.employeeId})? This action cannot be undone.`)) {
      return;
    }

    this.employeeService.deleteEmployee(emp.id!).subscribe({
      next: () => {
        this.successMessage = `Employee "${emp.name}" deleted successfully.`;
        this.loadEmployees();
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || `Failed to delete employee ${emp.name}.`;
      }
    });
  }
}
