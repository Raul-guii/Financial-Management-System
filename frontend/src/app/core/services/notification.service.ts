import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationResponse } from '../../models/notifications/notification-response.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private api = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.api);
  }

  triggerTest(): Observable<string> {
    return this.http.post(`${this.api}/test`, {}, { responseType: 'text' });
  }

  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.api}/read-all`, {});
  }

  
}