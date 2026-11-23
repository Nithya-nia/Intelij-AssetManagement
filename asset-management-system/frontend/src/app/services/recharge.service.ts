import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recharge } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RechargeService {
  private apiUrl = `${environment.apiUrl}/recharges`;

  constructor(private http: HttpClient) {}

  getRecharges(filters?: { mobileNumber?: string; provider?: string; employee?: string; fromDate?: string; toDate?: string }): Observable<Recharge[]> {
    let params = new HttpParams();
    if (filters?.mobileNumber) params = params.set('mobileNumber', filters.mobileNumber);
    if (filters?.provider) params = params.set('provider', filters.provider);
    if (filters?.employee) params = params.set('employee', filters.employee);
    if (filters?.fromDate) params = params.set('fromDate', filters.fromDate);
    if (filters?.toDate) params = params.set('toDate', filters.toDate);

    return this.http.get<Recharge[]>(this.apiUrl, { params });
  }

  addRecharge(payload: {
    simId: string;
    rechargeAmount: number;
    rechargePlan: string;
    rechargeDate: string;
    planEndDate: string;
    remarks?: string;
  }): Observable<Recharge> {
    return this.http.post<Recharge>(this.apiUrl, payload);
  }

  getRechargesBySimId(simId: string): Observable<Recharge[]> {
    return this.http.get<Recharge[]>(`${this.apiUrl}/sim/${simId}`);
  }
}
