import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, EmployeeAssets } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  getEmployees(search?: string, department?: string, status?: string): Observable<Employee[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (department) params = params.set('department', department);
    if (status) params = params.set('status', status);

    return this.http.get<Employee[]>(this.apiUrl, { params });
  }

  getEmployeeById(id: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${id}`);
  }

  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee);
  }

  updateEmployee(id: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}`, employee);
  }

  getEmployeeAssets(employeeId: string): Observable<EmployeeAssets> {
    return this.http.get<EmployeeAssets>(`${this.apiUrl}/${employeeId}/assets`);
  }

  deleteEmployee(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
