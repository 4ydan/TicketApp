import {EventSort} from './event-sort';
import {EventCategory} from './event-category';

export class EventSearch {
  term?: string;
  sort?: EventSort;
  category?: EventCategory;
  yearMonth?: string;
  label?: string;
  street?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  startTime?: string;
  endTime?: string;
  duration?: Date;
  description?: string;
  minPrice?: number;
  maxPrice?: number;
  startDate?: Date;
  endDate?: Date;
  page?: number;
  perPage?: number;
}
