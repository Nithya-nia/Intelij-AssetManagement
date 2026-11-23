import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Laptop, LaptopAssignment } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LaptopService {
  private apiUrl = `${environment.apiUrl}/laptops`;

  constructor(private http: HttpClient) {}

  getLaptops(search?: string, brand?: string, status?: string, project?: string): Observable<Laptop[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (brand) params = params.set('brand', brand);
    if (status) params = params.set('status', status);
    if (project) params = params.set('project', project);

    return this.http.get<Laptop[]>(this.apiUrl, { params });
  }

  getNextLaptopId(): Observable<{ nextId: string }> {
    return this.http.get<{ nextId: string }>(`${this.apiUrl}/next-id`);
  }

  getLaptopById(id: string): Observable<Laptop> {
    return this.http.get<Laptop>(`${this.apiUrl}/${id}`);
  }

  createLaptop(laptop: Laptop): Observable<Laptop> {
    return this.http.post<Laptop>(this.apiUrl, laptop);
  }

  updateLaptop(id: string, laptop: Laptop): Observable<Laptop> {
    return this.http.put<Laptop>(`${this.apiUrl}/${id}`, laptop);
  }

  assignLaptop(id: string, payload: { employeeId: string; assignedDate: string; remarks?: string }): Observable<Laptop> {
    return this.http.post<Laptop>(`${this.apiUrl}/${id}/assign`, payload);
  }

  returnLaptop(id: string, payload: { returnedDate: string; returnCondition?: string; remarks?: string }): Observable<Laptop> {
    return this.http.post<Laptop>(`${this.apiUrl}/${id}/return`, payload);
  }

  getLaptopHistory(id: string): Observable<LaptopAssignment[]> {
    return this.http.get<LaptopAssignment[]>(`${this.apiUrl}/${id}/history`);
  }

  deleteLaptop(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
