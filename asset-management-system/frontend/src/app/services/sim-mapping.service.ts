import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimCard, Mobile, SimMappingDevice, SimMappingRequest, SimUnmapRequest } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class SimMappingService {
  private apiUrl = 'http://localhost:8085/api/sim-mapping';

  constructor(private http: HttpClient) {}

  getMappingStatuses(): Observable<SimMappingDevice[]> {
    return this.http.get<SimMappingDevice[]>(`${this.apiUrl}/status`);
  }

  getAvailableSims(): Observable<SimCard[]> {
    return this.http.get<SimCard[]>(`${this.apiUrl}/available-sims`);
  }

  mapSim(request: SimMappingRequest): Observable<Mobile> {
    return this.http.post<Mobile>(`${this.apiUrl}/map`, request);
  }

  unmapSim(request: SimUnmapRequest): Observable<Mobile> {
    return this.http.post<Mobile>(`${this.apiUrl}/unmap`, request);
  }
}
