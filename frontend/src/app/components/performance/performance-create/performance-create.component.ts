import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of} from 'rxjs';
import {Performance} from 'src/app/dtos/event/performance';
import {PerformanceService} from 'src/app/services/performance.service';
import {Location, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {HallPlan} from 'src/app/dtos/event/hall-plan';
import {HallPlanService} from 'src/app/services/hall-plan.service';

@Component({
  selector: 'app-performance-create',
  templateUrl: './performance-create.component.html',
  styleUrls: ['./performance-create.component.scss'],
  providers: [Location, {provide: LocationStrategy, useClass: PathLocationStrategy}]
})
export class PerformanceCreateComponent implements OnInit {
  performance: Performance = {
    id: null,
    name: null,
    country: null,
    city: null,
    postalCode: null,
    street: null,
    streetNr: null,
    date: null,
    startTime: null,
    price: null,
    hallPlan: null
  };
  errorMessage = '';

  constructor(
    private service: PerformanceService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
    private hallPlanService: HallPlanService,
  ) {
  }

  ngOnInit(): void {
  }

  public formatHallPlanName(artist: HallPlan | null | undefined): string {
    return (artist == null)
      ? ''
      : `${artist.name}`;
  }

  hallPlanSuggestions = (input: string) => (input === '')
    ? of([])
    : this.hallPlanSearchTerm(input);

  hallPlanSearchTerm(input: string): Observable<HallPlan[]> {
    return this.hallPlanService.searchHallPlansByTerm(input);
  }

  getEventId(): number {
    console.log(this.router.url);
    const eventId = this.router.url.split('/')[2];
    return Number(eventId);
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      const performanceObservable = this.service.createPerformance(this.performance, this.getEventId());
      performanceObservable.subscribe({
        next: _ => {
          this.notification.success(`Performance ${this.performance.name} successfully created.`);
          this.router.navigate(['/events/cinema']);
        },
        error: error => {
          console.error('Error creating performance', error);
          if (error.status === 0) {
            this.errorMessage = 'Is the backend up?';
          } else if (error.status === 409) {
            this.errorMessage = error.error;
          } else if (error.status === 422) {
            this.errorMessage = error.error;
          }
          this.notification.error(this.errorMessage, 'Could not create performance');
        }
      });
    }
  }
}
