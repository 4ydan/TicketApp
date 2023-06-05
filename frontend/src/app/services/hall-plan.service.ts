import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Globals} from '../global/globals';
import {Seat} from '../dtos/seat';
import {SectorCategory} from '../dtos/sectorCategory';
import {HallPlan} from '../dtos/event/hall-plan';


@Injectable({
  providedIn: 'root'
})
export class HallPlanService {

  private hallPlanBaseUri: string = this.globals.backendUri + '/hallplans';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get a hall plan in the system by id
   *
   * @param id the id, which belongs to hall plan
   * @return observable list of all belonging seats
   */
  getHallPlan(id: number): Observable<Seat[]> {
    console.log('GET: ' + this.hallPlanBaseUri + '/' + id + '/seatingplan');
    return this.httpClient.get<Seat[]>(this.hallPlanBaseUri + '/' + id + '/seatingplan');
  }

  /**
   * Get the sector categories of a hall plan in the system by id
   *
   * @param id the id, which belongs to hall plan
   * @return observable list of all belonging sector categories
   */
  getSectorCategories(id: number): Observable<SectorCategory[]> {
    console.log('GET: ' + this.hallPlanBaseUri + '/' + id + '/sectorcategories');
    return this.httpClient.get<SectorCategory[]>(this.hallPlanBaseUri + '/' + id + '/sectorcategories');
  }

  /**
   * Get an array of hall-plans fitting to the input search term
   *
   * @param input string of the hall plan to search
   */
  searchHallPlansByTerm(input: string): Observable<HallPlan[]> {
    let params = new HttpParams();
    params = params.set('term', input);
    console.log('GET: ' + this.hallPlanBaseUri + '?' + params);
    return this.httpClient.get<HallPlan[]>(this.hallPlanBaseUri + '?', {params}).pipe(
      tap(data => console.log('Found hall-plans: ', data))
    );
  }
}
