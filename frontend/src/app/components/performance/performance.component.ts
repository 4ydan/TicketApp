import {Component, OnInit} from '@angular/core';
import {EventService} from '../../services/event.service';
import {ActivatedRoute} from '@angular/router';
import {EventDetail} from '../../dtos/event/event-detail';
import {AuthService} from 'src/app/services/auth.service';
import {PerformanceService} from '../../services/performance.service';

@Component({
  selector: 'app-performance',
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class PerformanceComponent implements OnInit {
  performance;
  state = 'performances';
  event: EventDetail;
  monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  constructor(
    private performanceService: PerformanceService,
    private eventService: EventService,
    private route: ActivatedRoute,
    public authService: AuthService,
  ) {
    this.route.params.subscribe(_ => {
      const id = +Number(this.route.snapshot.paramMap.get('id'));
      this.getEvent(id);
      console.log(this.event);
    });
  }

  ngOnInit(): void {
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  isVendor(): boolean {
    return this.authService.getUserRole() === 'VENDOR';
  }

  getEvent(id: number): void {
    this.eventService.get(id).subscribe(event => this.event = event);
  }

  public getDay(date: Date) {
    date = new Date(date);
    return date.getDate();
  }

  public getMonth(date: Date) {
    date = new Date(date);
    return this.monthNames[date.getMonth()].substring(0, 3);
  }

  public getYear(date: Date) {
    date = new Date(date);
    return date.getFullYear();
  }

  public formatTime(startTime: string, endTime: string) {
    return startTime.substring(0, 5) + '-' + endTime.substring(0, 5);
  }

  public about() {
    this.state = 'about';
  }

  public performances() {
    this.state = 'performances';
  }
}
