import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Globals} from '../global/globals';
import {Artist} from '../dtos/artist/artist';
import {ArtistDetail} from '../dtos/artist/artist-detail';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  private artistBaseUri: string = this.globals.backendUri + '/artists';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get all artists stored in the system matching a term
   *
   * @param term the term we are matching the artist name with.
   * @return observable list of all artists
   */
  searchArtistsByTerm(term: string): Observable<Artist[]> {
    let params = new HttpParams();
    params = params.set('term', term);
    console.log('GET: ' + this.artistBaseUri + '?' + params);
    return this.httpClient.get<Artist[]>(this.artistBaseUri + '?', {params}).pipe(
      tap(data => console.log('Found artists: ', data))
    );
  }

  /**
   * Get one artist stored in the system by id
   *
   * @param id the artist id
   * @return the artist
   */
  get(id: number): Observable<ArtistDetail> {
    console.log('GET: ' + this.artistBaseUri + '/' + id);
    return this.httpClient.get<ArtistDetail>(this.artistBaseUri + '/' + id).pipe(
      tap(data => console.log('Artist by id: ', data))
    );
  }
}
