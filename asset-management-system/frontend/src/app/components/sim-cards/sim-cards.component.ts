import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { SimService } from '../../services/sim.service';
import { SimCard, SimAssignment, Recharge } from '../../models/models';

@Component({
  selector: 'app-sim-cards',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './sim-cards.component.html',
  styleUrls: ['./sim-cards.component.css']
})
export class SimCardsComponent implements OnInit {
  sims: SimCard[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  // Filter & Search
  searchQuery: string = '';
  selectedProvider: string = 'ALL';
  selectedStatus: string = 'ALL';
  providers: string[] = ['Airtel', 'Jio', 'Vi', 'BSNL', 'Other'];

  // Add / Edit Modal
  showFormModal: boolean = false;
  isEditing: boolean = false;
  simForm: FormGroup;
  currentSimId: string | null = null;

  // History & Recharges Modal
  showHistoryModal: boolean = false;
  selectedSim: SimCard | null = null;
  simHistory: SimAssignment[] = [];
  simRecharges: Recharge[] = [];
  activeTab: 'history' | 'recharges' = 'history';
  loadingHistory: boolean = false;

  constructor(
    private simService: SimService,
    private fb: FormBuilder
  ) {
    this.simForm = this.fb.group({
      simId: ['', [Validators.required]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      provider: ['Airtel', [Validators.required]],
      simSerialNumber: [''],
      simType: ['Physical SIM', [Validators.required]],
      status: ['Available', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadSims();
  }

  loadSims(): void {
    this.loading = true;
    this.simService.getSims(this.searchQuery, this.selectedProvider, this.selectedStatus).subscribe({
      next: (data) => {
        this.sims = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load SIM cards from server.';
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    this.loadSims();
  }

  openAddModal(): void {
    this.isEditing = false;
    this.currentSimId = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.simForm.reset({
      provider: 'Airtel',
      simType: 'Physical SIM',
      status: 'Available'
    });
    this.simForm.get('simId')?.enable();
    this.showFormModal = true;
  }

  openEditModal(sim: SimCard): void {
    this.isEditing = true;
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
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.simForm.reset();
  }

  saveSim(): void {
    if (this.simForm.invalid) {
      this.simForm.markAllAsTouched();
      return;
    }

    const payload = this.simForm.getRawValue();

    if (this.isEditing && this.currentSimId) {
      this.simService.updateSim(this.currentSimId, payload).subscribe({
        next: () => {
          this.successMessage = 'SIM card updated successfully.';
          this.closeFormModal();
          this.loadSims();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update SIM card.';
        }
      });
    } else {
      this.simService.createSim(payload).subscribe({
        next: () => {
          this.successMessage = 'New SIM card registered successfully.';
          this.closeFormModal();
          this.loadSims();
          setTimeout(() => this.successMessage = '', 4000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to register SIM card.';
        }
      });
    }
  }

  viewDetails(sim: SimCard): void {
    this.selectedSim = sim;
    this.loadingHistory = true;
    this.showHistoryModal = true;
    this.activeTab = 'history';

    this.simService.getSimHistory(sim.id!).subscribe({
      next: (data) => {
        this.simHistory = data;
        this.loadingHistory = false;
      },
      error: () => this.loadingHistory = false
    });

    this.simService.getSimRecharges(sim.id!).subscribe({
      next: (data) => {
        this.simRecharges = data;
      }
    });
  }

  closeHistoryModal(): void {
    this.showHistoryModal = false;
    this.selectedSim = null;
    this.simHistory = [];
    this.simRecharges = [];
  }
}
