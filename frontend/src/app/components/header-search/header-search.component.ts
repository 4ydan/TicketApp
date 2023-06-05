import {Component, OnInit} from '@angular/core';
import {debounceTime, Subject} from 'rxjs';
import {EventService} from '../../services/event.service';
import {Event} from '../../dtos/event/event';
import {ArtistService} from '../../services/artist.service';
import {Artist} from '../../dtos/artist/artist';
import {ActivatedRoute, Router} from '@angular/router';
import {EventSearch} from '../../dtos/event/event-search';

@Component({
  selector: 'app-header-search',
  templateUrl: './header-search.component.html',
  styleUrls: ['./header-search.component.scss']
})
export class HeaderSearchComponent implements OnInit {
  modelChanged = new Subject<string>();
  eventSearch: EventSearch = {
    term: '',
  };
  hide = false;
  events: Event[];
  artists: Artist[];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private eventService: EventService,
              private artistService: ArtistService
  ) {
  }

  ngOnInit() {
    this.modelChanged.pipe(debounceTime(500)).subscribe(() => {
      if (this.eventSearch.term) {
        this.searchArtist();
        this.searchEvent();
      }
    });
  }

  public searchArtist() {
    this.hide = false;
    this.artistService.searchArtistsByTerm(this.eventSearch.term).subscribe({
      next: data => {
        this.artists = data;
      },
      error: error => {
        console.error('Error fetching artists', error.message);
      }
    });
  }

  public searchEvent() {
    this.hide = false;
    this.eventService.search(0, 7, this.eventSearch).subscribe({
      next: data => {
        this.events = data;
      },
      error: error => {
        console.error('Error fetching events', error.message);
      }
    });
  }

  public onKeyDownEvent() {
    this.events = [];
    this.artists = [];
    this.hide = true;
    this.router.navigate(['/search'], {queryParams: {searchTerm: this.eventSearch.term}});
  }

  changed() {
    this.modelChanged.next('_');
  }

  formatDate(startDate: Date, endDate: Date) {
    return startDate + ' - ' + endDate;
  }

  noResultsFound() {
    return 'No results found for \'' + this.eventSearch.term + '\' ';
  }

  onBlur(): void {
    this.events = [];
    this.artists = [];
    this.hide = true;
  }

  public timeoutBlur() {
    setTimeout(this.onBlur,150);
  }
}
