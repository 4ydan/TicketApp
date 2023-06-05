import {Component, HostListener, Input, OnInit} from '@angular/core';
import {EventService} from '../../services/event.service';
import {Event} from '../../dtos/event/event';
import {ActivatedRoute} from '@angular/router';
import {EventSort} from '../../dtos/event/event-sort';
import {EventSearch} from '../../dtos/event/event-search';
import {EventCategory} from '../../dtos/event/event-category';
import {debounceTime, Subject} from 'rxjs';
import {AuthService} from '../../services/auth.service';
import Chart from 'chart.js/auto';
import {AddressService} from '../../services/address.service';


@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
  @Input()
  mode;
  @Input()
  searchTerm: string;
  searching: boolean;
  bannerPath = '../../../assets/theatre-banner.jpg';
  title: string;
  events: Event[] = [];
  isCollapsed = true;
  chart: any;
  page = 0;
  modelChanged = new Subject<string>();
  category: EventCategory;
  cities;
  postalCodes;
  countries;
  monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  eventSearch: EventSearch = {
    sort: EventSort.date,
    startDate: undefined,
    yearMonth: '' + new Date().getFullYear() + '-' + (new Date().getMonth() + 1)
  };

  constructor(
    public eventService: EventService,
    public addressService: AddressService,
    public route: ActivatedRoute,
    public authService: AuthService
  ) {
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
      this.page++;
      console.log('Page: ' + this.page);
      if (this.eventSearch.sort !== EventSort.topten) {
        this.scrollSearch();
      }
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.category = data.category;
      if (this.category) {
        this.title = data.category;
      }
      if (this.mode !== 'searchResults') {
        this.getBanner();
      }
      this.getUniqueCities();
      this.getUniquePostalCodes();
      this.getUniqueCountries();
      this.restoreFilter();
    });
    this.modelChanged.pipe(debounceTime(300)).subscribe(() => {
      this.search();
    });
    this.route.queryParams.subscribe(params => {
      this.eventSearch.term = params['searchTerm'];
      this.search();
    });
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  changed(e) {
    this.cacheInput(e);
    this.modelChanged.next('_');
  }

  public getBanner() {
    switch (this.category) {
      case EventCategory.cinema:
        this.bannerPath = '../../../assets/cinema-banner.webp';
        break;
      case EventCategory.concert:
        this.bannerPath = '../../../assets/concert-banner.jpg';
        break;
      case EventCategory.opera:
        this.bannerPath = '../../../assets/opera-banner.jpg';
        break;
      case EventCategory.theatre:
        this.bannerPath = '../../../assets/theatre-banner.jpg';
        break;
    }

  }

  public getUniqueCities() {
    this.addressService.getUniqueCities().subscribe({
      next: data => {
        this.cities = data;
      },
      error: error => {
        console.error('Error fetching all cities', error.message);
      }
    });
  }

  public getUniquePostalCodes() {
    this.addressService.getUniquePostalCodes().subscribe({
      next: data => {
        this.postalCodes = data;
      },
      error: error => {
        console.error('Error fetching all postal codes', error.message);
      }
    });
  }

  public getUniqueCountries() {
    this.addressService.getUniqueCountries().subscribe({
      next: data => {
        this.countries = data;
      },
      error: error => {
        console.error('Error fetching all countries', error.message);
      }
    });
  }

  public async search() {
    this.searching = true;
    this.eventSearch.category = this.category;
    await this.sleep(150);
    this.route.queryParams.subscribe(params => {
      this.eventSearch.term = params['searchTerm'];
    });
    this.eventService.search(0, 10, this.eventSearch).subscribe({
      next: data => {
        this.events = data;
        if (this.eventSearch.sort === EventSort.topten) {
          if (this.chart) {
            this.chart.destroy();
          }
          this.createChart();
        }
      },
      error: error => {
        console.error('Error fetching all events', error.message);
      }
    });
    await this.sleep(150);
    this.searching = false;
  }

  public scrollSearch() {
    this.eventSearch.category = this.category;
    console.log(this.eventSearch);
    this.eventService.search(this.page, 10, this.eventSearch).subscribe({
      next: data => {
        this.events = [...this.events, ...data];
      },
      error: error => {
        console.error('Error fetching all events', error.message);
      }
    });
  }

  public dateWithoutYear(date: Date) {
    date = new Date(date);
    return this.monthNames[date.getMonth()] + ' ' + date.getDate();
  }

  public dateFormat(date: Date) {
    date = new Date(date);
    return this.monthNames[date.getMonth()] + ' ' + date.getDate() + ', ' + date.getFullYear();
  }

  public titleFormat(title: string) {
    if (title) {
      return title.toLowerCase().charAt(0).toUpperCase() + title.toLowerCase().slice(1);
    }
  }

  createChart() {
    this.chart = new Chart('MyChart', {
      type: 'bar', //this denotes tha type of chart

      data: {// values on X-Axis
        labels: [...this.events.map(event => event.title)],
        datasets: [
          {
            label: 'Tickets',
            data: [...this.events.map(event => event.soldTickets)],
            backgroundColor: 'blue'
          }
        ]
      },
      options: {
        aspectRatio: 5
      }
    });
  }

  public sleep(milliseconds) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
  }

  resetFilter() {
    this.eventSearch = {
      sort: EventSort.date,
      startDate: undefined,
      yearMonth: '' + new Date().getFullYear() + '-' + (new Date().getMonth() + 1)
    };
    localStorage.removeItem('eventSearch');
    this.modelChanged.next('_');
  }

  cacheInput(e) {
    localStorage.setItem('eventSearch', JSON.stringify(e.eventSearch));
  }

  restoreFilter() {
    if (localStorage.getItem('eventSearch')) {
      this.eventSearch = JSON.parse(localStorage.getItem('eventSearch'));
    }
    if (
      this.eventSearch.description
      || this.eventSearch.minPrice
      || this.eventSearch.maxPrice
      || this.eventSearch.startDate
      || this.eventSearch.endDate
      || this.eventSearch.duration
      || this.eventSearch.street
      || this.eventSearch.city
      || this.eventSearch.country
      || this.eventSearch.postalCode
    ) {
      this.isCollapsed = false;
    }

  }
}
