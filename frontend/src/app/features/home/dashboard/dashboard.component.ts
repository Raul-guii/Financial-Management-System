import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../../core/services/dashboard.service';
import { DashboardSummary } from '../../../models/dashboard.model';
import { ChartConfiguration } from 'chart.js/auto';
import { ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ CommonModule,BaseChartDirective ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  summary?: DashboardSummary;
  pieChartData!: ChartConfiguration<'pie'>['data'];

  constructor(private dashboardService: DashboardService, private cdr: ChangeDetectorRef) {}

ngOnInit() {
  this.dashboardService.getSummary().subscribe((data) => {
    this.summary = data;

    setTimeout(() => {
      console.log('FORÇANDO UPDATE');
      this.summary = { ...data }; 
    }, 0);
  });
}
}