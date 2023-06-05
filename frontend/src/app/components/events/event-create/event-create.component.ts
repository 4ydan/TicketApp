import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, ReplaySubject } from 'rxjs';
import { EventService } from 'src/app/services/event.service';
import {EventCreate} from '../../../dtos/event/event-create';

@Component({
  selector: 'app-event-create',
  templateUrl: './event-create.component.html',
  styleUrls: ['./event-create.component.scss']
})
export class EventCreateComponent implements OnInit {
  event: EventCreate = {
    title: null,
    image: null,
    imageFile: null,
    description: null,
    durationInMinutes: null,
    startDate: null,
    endDate: null,
    eventCategory: null,
    artist: null,
  };
  errorMessage = '';

  constructor(
    private eventService: EventService,
    private router: Router,
    private notification: ToastrService,
    private http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.http.get('assets/initialImage.txt', {responseType: 'text'})
      .subscribe(data => this.event.image = data);
  }

  uploadSingleImage(event) {
    const file = event.target.files[0];
    this.getBase64(file).subscribe(base64 => {
      this.event.image = base64;
    });
    this.event.imageFile = file;
  }

  getBase64(file: File): Observable<string> {
    const result = new ReplaySubject<string>(1);
    const reader = new FileReader();
    reader.readAsBinaryString(file);
    reader.onload = (event) => result.next(btoa(event.target.result.toString()));
    return result;
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      const eventObservable = this.eventService.createEvent(this.event);
      eventObservable.subscribe({
        next: data => {
          this.notification.success(`Event ${this.event.title} successfully created.`);
          this.router.navigate(['/events/cinema/']);
        },
        error: error => {
          console.error('Error creating event', error);
          if (error.status === 0) {
            this.errorMessage = 'Is the backend up?';
          } else if (error.status === 409) {
            this.errorMessage = error.error;
          } else if (error.status === 422) {
            this.errorMessage = error.error;
          }
          this.notification.error(this.errorMessage, 'Could not create event');
        }
      });
    }
  }

}
