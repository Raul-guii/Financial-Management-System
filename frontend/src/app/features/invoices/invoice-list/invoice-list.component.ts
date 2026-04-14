import { Component, OnInit } from '@angular/core';
import { InvoiceService } from '../../../core/services/invoice.service';
import { Router } from '@angular/router';
import { Invoice } from '../../../models/invoices/invoice.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss']

})
export class InvoiceListComponent implements OnInit {

  invoices: Invoice[] = [];

  constructor(
    private service: InvoiceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.service.findAll().subscribe(res => {
      this.invoices = res;
    });
  }

  open(id: number): void {
    this.router.navigate(['/invoices', id]);
  }
}