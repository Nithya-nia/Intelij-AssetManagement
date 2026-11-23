import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { NotificationItem } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  private alertCountSubject = new BehaviorSubject<number>(0);
  public alertCount$ = this.alertCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAllNotifications(): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(this.apiUrl);
  }

  getAlertNotifications(): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(`${this.apiUrl}/alerts`).pipe(
      tap((alerts) => {
        this.alertCountSubject.next(alerts ? alerts.length : 0);
      })
    );
  }

  refreshNotifications(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/refresh`, {}).pipe(
      tap(() => {
        this.getAlertNotifications().subscribe();
      })
    );
  }
}
