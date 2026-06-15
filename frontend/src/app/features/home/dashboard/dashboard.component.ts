import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../../core/services/dashboard.service';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { Chart, registerables } from 'chart.js';
import { RouterModule } from '@angular/router';
import { DashboardSummary } from '../../../models/dashboard/dashboard.model';
import { MonthlyRevenue } from '../../../models/dashboard/monthly-revenue.model';
import { FormsModule } from '@angular/forms';

Chart.register(...registerables);

const STORAGE_KEY_START = 'dashboard_startDate';
const STORAGE_KEY_END   = 'dashboard_endDate';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, RouterModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  summary?: DashboardSummary;
  pieChartData?: ChartConfiguration<'pie'>['data'];
  pieChartOptions?: ChartConfiguration<'pie'>['options'];
  barChartData?: ChartConfiguration<'bar'>['data'];
  barChartOptions?: ChartConfiguration<'bar'>['options'];
  lineChartData?: ChartConfiguration<'line'>['data'];
  lineChartOptions?: ChartConfiguration<'line'>['options'];

  startDate = '';
  endDate   = '';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.restoreOrDefaultDates();
    this.loadDashboard();
    this.loadMonthlyRevenue();
  }

  private restoreOrDefaultDates() {
    const savedStart = sessionStorage.getItem(STORAGE_KEY_START);
    const savedEnd   = sessionStorage.getItem(STORAGE_KEY_END);

    if (savedStart && savedEnd) {
      this.startDate = savedStart;
      this.endDate   = savedEnd;
    } else {
      const end   = new Date();
      const start = new Date(end.getFullYear(), end.getMonth() - 5, 1);
      this.startDate = this.formatDate(start);
      this.endDate   = this.formatDate(end);
      this.saveDates();
    }
  }

  private saveDates() {
    sessionStorage.setItem(STORAGE_KEY_START, this.startDate);
    sessionStorage.setItem(STORAGE_KEY_END,   this.endDate);
  }

  onStartDateChange(event: Event) {
    this.startDate = (event.target as HTMLInputElement).value;
    this.saveDates();
    if (this.startDate && this.endDate) this.loadMonthlyRevenue();
  }

  onEndDateChange(event: Event) {
    this.endDate = (event.target as HTMLInputElement).value;
    this.saveDates();
    if (this.startDate && this.endDate) this.loadMonthlyRevenue();
  }

  loadDashboard() {
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.buildCharts();
      },
      error: (err) => console.error('Erro ao carregar dashboard:', err)
    });
  }

  loadMonthlyRevenue() {
    if (!this.startDate || !this.endDate) return;
    this.dashboardService.getMonthlyRevenue(this.startDate, this.endDate).subscribe({
      next: (data) => this.buildLineChart(data),
      error: (err) => console.error('Erro ao carregar receita mensal:', err)
    });
  }

  private formatDate(date: Date): string {
    const year  = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day   = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  buildCharts() {
    if (!this.summary) return;

    this.pieChartData = {
      labels: ['Pagas', 'Pendentes', 'Vencidas'],
      datasets: [{
        data: [
          this.summary.paidInvoices,
          this.summary.pendingInvoices,
          this.summary.overdueInvoices
        ],
        backgroundColor: ['#3B6D11', '#854F0B', '#A32D2D'],
        borderWidth: 0
      }]
    };

    this.pieChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: { padding: 12, font: { size: 11 } }
        }
      }
    };

    this.barChartData = {
      labels: ['Receita Bruta', 'Reembolsos', 'Receita Líquida', 'Em Aberto'],
      datasets: [{
        label: 'Valores (R$)',
        data: [
          Number(this.summary.grossRevenue),
          Number(this.summary.refunded),
          Number(this.summary.netRevenue),
          Number(this.summary.totalPending)
        ],
        backgroundColor: ['#3B6D11', '#A32D2D', '#185FA5', '#854F0B'],
        borderWidth: 0,
        borderRadius: 6
      }]
    };

    this.barChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => 'R$ ' + (Number(value) / 1000) + 'k'
          }
        }
      }
    };
  }

  buildLineChart(data: MonthlyRevenue[]) {
    this.lineChartData = {
      labels: data.map(d => d.month),
      datasets: [
        {
          label: 'Receita',
          data: data.map(d => Number(d.revenue)),
          borderColor: '#3B6D11',
          backgroundColor: 'rgba(59, 109, 17, 0.1)',
          fill: true,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#3B6D11',
        },
        {
          label: 'Pendente',
          data: data.map(d => Number(d.pending)),
          borderColor: '#854F0B',
          backgroundColor: 'rgba(133, 79, 11, 0.1)',
          fill: true,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#854F0B',
        },
        {
          label: 'Vencida',
          data: data.map(d => Number(d.overdue)),
          borderColor: '#A32D2D',
          backgroundColor: 'rgba(163, 45, 45, 0.1)',
          fill: true,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#A32D2D',
        }
      ]
    };

    this.lineChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: { padding: 12, font: { size: 11 } }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => 'R$ ' + (Number(value) / 1000) + 'k'
          }
        }
      }
    };
  }

  getRefundPercentage(): string {
    if (!this.summary || this.summary.grossRevenue === 0) return '0.0';
    const pct = (this.summary.refunded / this.summary.grossRevenue) * 100;
    return pct.toFixed(1);
  }
}