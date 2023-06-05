import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Globals} from '../global/globals';
import {Performance} from '../dtos/event/performance';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {

  private performanceBaseUri: string = this.globals.backendUri + '/performances';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get one performance by id
   *
   * @return observable performance
   */
  get(id: number): Observable<Performance> {
    console.log('GET: ' + this.performanceBaseUri + '/' + id);
    return this.httpClient.get<Performance>(this.performanceBaseUri + '/' + id).pipe(
      tap(data => console.log('Performance by id: ', data))
    );
  }

  /**
   * Persists performance to the backend
   *
   * @param performance to persist
   * @param eventId of the corresponding event
   */
  createPerformance(performance: Performance, eventId: number): Observable<Performance> {
    console.log('Create performance ' + performance.name);
    const formParams = new FormData();
    formParams.append('eventId', eventId.toString());
    formParams.append('name', performance.name);
    formParams.append('country', performance.country);
    formParams.append('city', performance.city);
    formParams.append('postalCode', performance.postalCode.toString());
    formParams.append('street', performance.street);
    formParams.append('streetNr', performance.streetNr.toString());
    formParams.append('date', performance.date.toString());
    formParams.append('startTime', performance.startTime);
    formParams.append('price', performance.price.toString());
    formParams.append('hallPlanId', performance.hallPlan.id.toString());
    return this.httpClient.post<Performance>(this.performanceBaseUri, formParams);
  }
}
