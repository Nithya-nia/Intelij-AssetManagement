import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimCard, SimAssignment, Recharge } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SimService {
  private apiUrl = `${environment.apiUrl}/sims`;
  private assignmentUrl = `${environment.apiUrl}/sim-assignments`;

  constructor(private http: HttpClient) {}

  getSims(search?: string, provider?: string, status?: string): Observable<SimCard[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (provider) params = params.set('provider', provider);
    if (status) params = params.set('status', status);

    return this.http.get<SimCard[]>(this.apiUrl, { params });
  }

  getSimById(id: string): Observable<SimCard> {
    return this.http.get<SimCard>(`${this.apiUrl}/${id}`);
  }

  createSim(sim: SimCard): Observable<SimCard> {
    return this.http.post<SimCard>(this.apiUrl, sim);
  }

  updateSim(id: string, sim: SimCard): Observable<SimCard> {
    return this.http.put<SimCard>(`${this.apiUrl}/${id}`, sim);
  }

  getSimHistory(id: string): Observable<SimAssignment[]> {
    return this.http.get<SimAssignment[]>(`${this.apiUrl}/${id}/history`);
  }

  getSimRecharges(id: string): Observable<Recharge[]> {
    return this.http.get<Recharge[]>(`${this.apiUrl}/${id}/recharges`);
  }

  // SIM Assignment operations
  getAllAssignments(): Observable<SimAssignment[]> {
    return this.http.get<SimAssignment[]>(this.assignmentUrl);
  }

  assignSim(simId: string, payload: { employeeId: string; mobileId: string; issuedDate: string; slotNumber?: number; remarks?: string }): Observable<SimAssignment> {
    return this.http.post<SimAssignment>(`${this.assignmentUrl}/${simId}/assign`, payload);
  }

  returnSim(simId: string, payload: { returnedDate: string; remarks?: string }): Observable<SimCard> {
    return this.http.post<SimCard>(`${this.assignmentUrl}/${simId}/return`, payload);
  }
}
