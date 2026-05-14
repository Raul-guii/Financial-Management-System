import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DashboardService } from '../../../core/services/dashboard.service';
import { DashboardSummary } from '../../../models/dashboard.model';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { Chart, registerables } from 'chart.js';

// Registre os componentes do Chart.js
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  summary?: DashboardSummary;
  pieChartData?: ChartConfiguration<'pie'>['data'];
  pieChartOptions?: ChartConfiguration<'pie'>['options'];
  lineChartData?: ChartConfiguration<'line'>['data'];
  lineChartOptions?: ChartConfiguration<'line'>['options'];

  constructor(private dashboardService: DashboardService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.buildCharts();
        this.cdr.detectChanges();

        console.log('PIE DATA:', this.pieChartData);
        console.log('LINE DATA:', this.lineChartData);
      },
      error: (err) => console.error('Erro ao carregar dashboard:', err)
    });
  }

  buildCharts() {
    console.log('BUILD CHARTS FOI CHAMADO');

    if (!this.summary){
      console.log('SUMMARY ESTÁ UNDEFINED');
      return;
    } 

    // Gráfico de Pizza
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
          labels: {
            padding: 12,
            font: { size: 11 }
          }
        }
      }
    };

    // Gráfico de Linha
    this.lineChartData = {
      labels: ['Dez', 'Jan', 'Fev', 'Mar', 'Abr', 'Mai'],
      datasets: [{
        label: 'Receita Líquida',
        data: [65000, 72000, 68000, 78000, 82000, this.summary.netRevenue],
        borderColor: '#185FA5',
        backgroundColor: 'rgba(24, 95, 165, 0.1)',
        tension: 0.3,
        fill: true,
        pointRadius: 4,
        pointBackgroundColor: '#185FA5'
      }]
    };

    this.lineChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => {
              return 'R$ ' + (Number(value) / 1000) + 'k';
            }
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