import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Globals} from '../global/globals';
import {Event} from '../dtos/event/event';
import {EventDetail} from '../dtos/event/event-detail';
import {EventSearch} from '../dtos/event/event-search';
import {EventCreate} from '../dtos/event/event-create';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventBaseUri: string = this.globals.backendUri + '/events';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get all events stored in the system by category
   *
   * @param eventSearch the dto with the search infos
   * @param page current page of search
   * @param perPage how many items to fetch
   * @return observable list of all events
   */
  search(page, perPage, eventSearch: EventSearch): Observable<Event[]> {
    const paramsMap = new Map<any, any>();

    if (eventSearch.term) {
      paramsMap.set('term', eventSearch.term);
    }
    if (eventSearch.duration) {
      paramsMap.set('duration', eventSearch.duration);
    }
    if (eventSearch.startTime) {
      paramsMap.set('startTime', eventSearch.startTime);
    }
    if (eventSearch.endTime) {
      paramsMap.set('endTime', eventSearch.endTime);
    }
    if (eventSearch.description) {
      paramsMap.set('description', eventSearch.description);
    }
    if (eventSearch.category) {
      paramsMap.set('category', eventSearch.category);
    }
    if (eventSearch.startDate) {
      paramsMap.set('startDate', eventSearch.startDate);
    }
    if (eventSearch.endDate) {
      paramsMap.set('endDate', eventSearch.endDate);
    }
    if (eventSearch.sort) {
      paramsMap.set('sort', eventSearch.sort);
    }
    if (eventSearch.yearMonth) {
      paramsMap.set('year', eventSearch.yearMonth.split('-')[0]);
      paramsMap.set('month', eventSearch.yearMonth.split('-')[1]);
    }
    if (eventSearch.city) {
      paramsMap.set('city', eventSearch.city);
    }
    if (eventSearch.country) {
      paramsMap.set('country', eventSearch.country);
    }
    if (eventSearch.label) {
      paramsMap.set('label', eventSearch.label);
    }
    if (eventSearch.street) {
      paramsMap.set('street', eventSearch.street);
    }
    if (eventSearch.postalCode) {
      paramsMap.set('postalCode', eventSearch.postalCode);
    }
    if (eventSearch.term) {
      paramsMap.set('term', eventSearch.term);
    }
    if (eventSearch.minPrice) {
      paramsMap.set('minPrice', eventSearch.minPrice);
    }
    if (eventSearch.maxPrice) {
      paramsMap.set('maxPrice', eventSearch.maxPrice);
    }
    paramsMap.set('page', page);
    paramsMap.set('perPage', perPage);
    let params = new HttpParams();
    paramsMap.forEach((value: any, key: any) => {
      params = params.set(key, value);
    });
    console.log('GET: ' + this.eventBaseUri + '?' + params);
    return this.httpClient.get<Event[]>(this.eventBaseUri + '?', {params}).pipe(
      tap(data => console.log('Found events: ', data))
    );
  }

  /**
   * Get event by id
   *
   * @param id of the event to retrieve
   * @returns event as EventDetail dto
   */
  get(id: number): Observable<EventDetail> {
    console.log('GET: ' + this.eventBaseUri + '/' + id);
    return this.httpClient.get<EventDetail>(this.eventBaseUri + '/' + id).pipe(
      tap(data => console.log('Event by id: ', data))
    );
  }

  /**
   * Persists event to the backend
   *
   * @param event to persist
   */
  createEvent(event: EventCreate): Observable<EventCreate> {
    console.log('Create event with title ' + event.title);
    const formParams = new FormData();
    formParams.append('title', event.title);
    formParams.append('description', event.description);
    formParams.append('eventCategory', event.eventCategory);
    formParams.append('startDate', event.startDate.toString());
    formParams.append('endDate', event.endDate.toString());
    formParams.append('durationInMinutes', event.durationInMinutes.toString());
    formParams.append('artist', event.artist);
    formParams.append('image', event.imageFile);
    return this.httpClient.post<EventCreate>(this.eventBaseUri, formParams);
  }

}
