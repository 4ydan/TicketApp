import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {RegisterService} from '../../services/register.service';

@Component({
  selector: 'app-confirmed',
  templateUrl: './confirmed.component.html',
  styleUrls: ['./confirmed.component.scss']
})
export class ConfirmedComponent implements OnInit {

  confirmed: boolean;
  private token: any;

  constructor(private router: Router, private route: ActivatedRoute, private registerService: RegisterService) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params.token;
      this.registerService.confirmAccount(this.token).subscribe({
        next: (data: string) => {
          console.log(data);
          this.confirmed = true;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 5000);
        },
        error: (error) => {
          this.confirmed = false;
          console.log(error);
          this.handleError('Error confirming account', error);
        }
      });
    });
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      console.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
