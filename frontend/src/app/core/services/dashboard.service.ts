import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DashboardSummary } from '../../models/dashboard/dashboard.model';
import { Observable } from 'rxjs';
import { environment } from './../../../environments/environment';
import { MonthlyRevenue } from '../../models/dashboard/monthly-revenue.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private API = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.API}/summary`);
  }

  getMonthlyRevenue(startDate: string, endDate: string): Observable<MonthlyRevenue[]> {
    return this.http.get<MonthlyRevenue[]>(`${this.API}/monthly-revenue`, {
      params: { startDate, endDate }
    });
  }
}