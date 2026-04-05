import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DashboardSummary } from '../../models/dashboard.model';
import { Observable } from 'rxjs';
import { environment } from './../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private API = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.API}/summary`);
  }
}