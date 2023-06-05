import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {PasswordResetService} from '../../services/password-reset.service';
import {ResetPassword} from '../../dtos/reset-password';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit {

  resetPasswordForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  private token: any;

  constructor(private formBuilder: UntypedFormBuilder, private passwordResetService: PasswordResetService,
              private router: Router, private notification: ToastrService, private route: ActivatedRoute) {
    this.resetPasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8), this.noWhitespaceValidator]],
      confirmPassword: ['', [Validators.required, this.noWhitespaceValidator]],
    }, {validators: [this.matchPassword('password', 'confirmPassword')]});
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
        this.token = params.token;
      },
      this.token = null
    );
  };

  /**
   * Send email data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   */
  resetPassword() {
    this.submitted = true;
    if (this.resetPasswordForm.valid) {
      const reset: ResetPassword = Object.assign(this.resetPasswordForm.value);
      reset.token = this.token;
      this.passwordResetService.updatePassword(reset).subscribe({
        next: () => {
          console.log('Password successfully updated. Please log in');
          this.notification.success(`Password reset was successful`);
          this.router.navigate(['../login']);
        },
        error: error => {
          console.log('Failed updating user password');
          console.log(error);
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error.error.error;
          } else {
            this.errorMessage = error.error;
          }
          this.notification.error(`Password reset failed : ` + error.error);
        }
      });
    } else {
      console.log('Invalid input');
    }
  }

  matchPassword(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (
        matchingControl.errors && !matchingControl.errors.match) {
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({match: true});
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  noWhitespaceValidator(control: AbstractControl) {
    const isWhitespace = (control && control.value && control.value.toString() || '').trim().length === 0 && control.value !== '';
    const isValid = !isWhitespace;
    return isValid ? null : {whitespace: true};
  }


  handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      console.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
