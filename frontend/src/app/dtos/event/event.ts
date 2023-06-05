import {EventCategory} from './event-category';

export class Event {
  id: number;
  title: string;
  place: string;
  image: string;
  description: string;
  category: EventCategory;
  startDate: Date;
  endDate: Date;
  soldTickets: number;
}
