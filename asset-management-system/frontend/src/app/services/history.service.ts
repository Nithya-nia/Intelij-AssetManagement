import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssetHistory } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private apiUrl = `${environment.apiUrl}/asset-history`;

  constructor(private http: HttpClient) {}

  getHistory(filters?: { employee?: string; assetType?: string; status?: string; fromDate?: string; toDate?: string }): Observable<AssetHistory[]> {
    let params = new HttpParams();
    if (filters?.employee) params = params.set('employee', filters.employee);
    if (filters?.assetType) params = params.set('assetType', filters.assetType);
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.fromDate) params = params.set('fromDate', filters.fromDate);
    if (filters?.toDate) params = params.set('toDate', filters.toDate);

    return this.http.get<AssetHistory[]>(this.apiUrl, { params });
  }

  getEmployeeHistory(employeeId: string): Observable<AssetHistory[]> {
    return this.http.get<AssetHistory[]>(`${this.apiUrl}/employee/${employeeId}`);
  }

  getAssetHistory(assetType?: string, identifier?: string): Observable<AssetHistory[]> {
    let params = new HttpParams();
    if (assetType) params = params.set('assetType', assetType);
    if (identifier) params = params.set('identifier', identifier);

    return this.http.get<AssetHistory[]>(`${this.apiUrl}/asset`, { params });
  }
}
