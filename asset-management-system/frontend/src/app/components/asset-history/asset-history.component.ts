import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HistoryService } from '../../services/history.service';
import { AssetHistory } from '../../models/models';

@Component({
  selector: 'app-asset-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './asset-history.component.html',
  styleUrls: ['./asset-history.component.css']
})
export class AssetHistoryComponent implements OnInit {
  historyList: AssetHistory[] = [];
  loading: boolean = true;
  errorMessage: string = '';

  // Filter criteria
  searchEmployee: string = '';
  selectedAssetType: string = 'ALL';
  selectedStatus: string = 'ALL';
  fromDate: string = '';
  toDate: string = '';

  // Asset types
  assetTypes: string[] = ['Laptop', 'Mobile', 'SIM'];

  constructor(private historyService: HistoryService) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.loading = true;
    this.historyService.getHistory({
      employee: this.searchEmployee,
      assetType: this.selectedAssetType,
      status: this.selectedStatus,
      fromDate: this.fromDate,
      toDate: this.toDate
    }).subscribe({
      next: (data) => {
        this.historyList = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load historical asset records.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.loadHistory();
  }

  resetFilters(): void {
    this.searchEmployee = '';
    this.selectedAssetType = 'ALL';
    this.selectedStatus = 'ALL';
    this.fromDate = '';
    this.toDate = '';
    this.loadHistory();
  }
}
