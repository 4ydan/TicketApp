import {Component, Input, OnInit} from '@angular/core';
import jsPDF from 'jspdf';
import {robotoEncoded} from '../../../../assets/font/robotoEncoded';
import autoTable from 'jspdf-autotable';
import {UserDetailed} from '../../../dtos/user-detailed';
import {UserService} from '../../../services/user.service';
import {DatePipe} from '@angular/common';
import {TicketToCreate} from '../../../dtos/event/ticket';

@Component({
  selector: 'app-cancellation-invoice',
  templateUrl: './cancellation-invoice.component.html',
})
export class CancellationInvoiceComponent implements OnInit {

  @Input() tickets: TicketToCreate[];
  @Input() location: any;
  @Input() event: any;
  @Input() date: any;

  pdfRow: number;
  user: UserDetailed;
  price: string;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit(): void {
    this.userService.getLoggedInUser().subscribe({
      next: value => {
        this.user = value;
      }
    });
  }

  openPDF() {
    console.log('Open PDF Cancellation Invoice');
    const pdf = new jsPDF({
      orientation: 'p',
      unit: 'cm',
      format: 'a4'
    });
    this.generatePDF(pdf);
    const blobPDF = new Blob([pdf.output()], {type: 'application/pdf'});
    const blobUrl = URL.createObjectURL(blobPDF);
    window.open(blobUrl);
  }

  private generatePDF(pdf: jsPDF) {
    pdf.addFileToVFS('Roboto-Regular.ttf', robotoEncoded);
    pdf.addFont('Roboto-Regular.ttf', 'Roboto', 'normal');
    pdf.setFont('Roboto');

    // Title
    pdf.setFontSize(30);
    pdf.text('Cancellation Invoice', 3, 3);

    // Company data
    pdf.setFontSize(14);
    pdf.text('Ticketline GmbH', 18, 5, {align: 'right'});
    pdf.text('Musterstrasse 1', 18, 5.6, {align: 'right'});
    pdf.text('A-1040 Vienna', 18, 6.2, {align: 'right'});

    // Personal data
    pdf.text(this.user.firstName + ' ' + this.user.lastName, 3, 8);
    pdf.text(this.user.address.street + ' ' + this.user.address.streetNr, 3, 8.6);
    pdf.text(this.user.address.postalCode + ' ' + this.user.address.city, 3, 9.2);

    // Billing details
    pdf.setFontSize(12);
    pdf.text('Invoice reference: ', 3, 11);
    const datePipe = new DatePipe('en-DE');
    const date = datePipe.transform(new Date(), 'dd/MM/yyyy');
    pdf.text('Date: ' + date, 18, 11, {align: 'right'});

    let amount = 0;
    this.tickets.forEach(item => {
      amount += item.price;
    });
    this.price = amount.toFixed(2);
    // Start monitoring row from now on
    this.pdfRow = 12;


    // headers for tickets
    const head = [['Event', 'Location', 'Date', 'Price']];
    const data = this.getDataForTicketTable(this.pdfRow);

    autoTable(pdf, {
      theme: 'grid',
      styles: {halign: 'center', lineColor: [0, 0, 0], lineWidth: 0.01, fontSize: 10},
      headStyles: {fillColor: [169, 169, 169], textColor: [0, 0, 0]},
      margin: [11.5, 3, 0, 3],
      head,
      body: data
    });


    // summary
    pdf.setFontSize(12);
    this.pdfRow += 2;

    pdf.setDrawColor(0, 0, 0);
    pdf.line(11, this.nextRow4(), 18, this.pdfRow);

    pdf.text('Order total', 11, this.nextRow6());
    const orderTotal = (amount).toFixed(2);
    const paid = '' + orderTotal + '€';

    pdf.text(paid, 18, this.pdfRow, {align: 'right'});

    pdf.line(11, this.nextRow4(), 18, this.pdfRow);

    pdf.text('Refunded', 11, this.nextRow6());

    pdf.text('' + orderTotal + '€', 18, this.pdfRow, {align: 'right'});


  }

  private getDataForTicketTable(pdfRow: number) {
    const data = [];
    this.nextRow8();
    const item = [
      this.event,
      this.location,
      this.date,
      this.price
    ];
    data.push(item);
    return data;
  }

  private nextRow8(): number {
    this.pdfRow += 0.8;
    return this.pdfRow;
  }

  private nextRow6(): number {
    this.pdfRow += 0.6;
    return this.pdfRow;
  }

  private nextRow4(): number {
    this.pdfRow += 0.4;
    return this.pdfRow;
  }

}
