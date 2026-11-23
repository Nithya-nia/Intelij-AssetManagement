import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { NotificationItem } from '../../models/models';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent implements OnInit {
  currentUser: any = null;
  alertCount: number = 0;
  notifications: NotificationItem[] = [];
  showNotifDropdown: boolean = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getUser();
    this.loadNotifications();
    this.notificationService.alertCount$.subscribe((count) => {
      this.alertCount = count;
    });
  }

  loadNotifications(): void {
    this.notificationService.getAlertNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.alertCount = data.length;
      },
      error: (err) => console.error('Failed to load notifications', err)
    });
  }

  toggleNotifDropdown(): void {
    this.showNotifDropdown = !this.showNotifDropdown;
    if (this.showNotifDropdown) {
      this.loadNotifications();
    }
  }

  getExpiringSoonList(): NotificationItem[] {
    return this.notifications.filter(
      (n) => n.expiryStatus === 'Expiring Soon' || n.expiryStatus === 'Expires Today'
    );
  }

  getExpiredList(): NotificationItem[] {
    return this.notifications.filter((n) => n.expiryStatus === 'Expired');
  }

  refreshNotifications(): void {
    this.notificationService.refreshNotifications().subscribe({
      next: () => {
        this.loadNotifications();
      }
    });
  }

  logout(): void {
    if (confirm('Are you sure you want to log out?')) {
      this.authService.logout();
    }
  }
}
