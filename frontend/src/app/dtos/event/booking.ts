import {TicketToCreate} from './ticket';

export class BookingToCreate {
  id: number;
  isPaid: boolean;
  tickets: TicketToCreate[];
  date: Date;
  userId: number;
  isCanceled: boolean;
  constructor(id: number, isPaid: boolean, tickets: TicketToCreate[], date: Date, userId: number, isCanceled: boolean) {
    this.id = id;
    this.isPaid = isPaid;
    this.tickets = tickets;
    this.date = date;
    this.userId = userId;
    this.isCanceled = isCanceled;
  }
}
