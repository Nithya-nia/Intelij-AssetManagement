import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mobile, MobileAssignment } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MobileService {
  private apiUrl = `${environment.apiUrl}/mobiles`;

  constructor(private http: HttpClient) {}

  getMobiles(search?: string, brand?: string, status?: string, project?: string, slotConfig?: string): Observable<Mobile[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (brand) params = params.set('brand', brand);
    if (status) params = params.set('status', status);
    if (project) params = params.set('project', project);
    if (slotConfig) params = params.set('slotConfig', slotConfig);

    return this.http.get<Mobile[]>(this.apiUrl, { params });
  }

  getNextMobileId(): Observable<{ nextId: string }> {
    return this.http.get<{ nextId: string }>(`${this.apiUrl}/next-id`);
  }

  getMobileById(id: string): Observable<Mobile> {
    return this.http.get<Mobile>(`${this.apiUrl}/${id}`);
  }

  createMobile(mobile: Mobile): Observable<Mobile> {
    return this.http.post<Mobile>(this.apiUrl, mobile);
  }

  updateMobile(id: string, mobile: Mobile): Observable<Mobile> {
    return this.http.put<Mobile>(`${this.apiUrl}/${id}`, mobile);
  }

  assignMobile(id: string, payload: { employeeId: string; assignedDate: string; remarks?: string }): Observable<Mobile> {
    return this.http.post<Mobile>(`${this.apiUrl}/${id}/assign`, payload);
  }

  returnMobile(id: string, payload: { returnedDate: string; returnCondition?: string; remarks?: string }): Observable<Mobile> {
    return this.http.post<Mobile>(`${this.apiUrl}/${id}/return`, payload);
  }

  getMobileHistory(id: string): Observable<MobileAssignment[]> {
    return this.http.get<MobileAssignment[]>(`${this.apiUrl}/${id}/history`);
  }
}
