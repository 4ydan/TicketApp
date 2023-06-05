import {Component, OnInit} from '@angular/core';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {PasswordResetService} from '../../services/password-reset.service';
import {Router} from '@angular/router';
import {Observable, of} from 'rxjs';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {


  resetPasswordForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  pending = false;


  constructor(private formBuilder: UntypedFormBuilder, private passwordResetService: PasswordResetService,
              private router: Router, private notification: ToastrService) {
    this.resetPasswordForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'), this.noWhitespaceValidator]],
    });
  }


  async submit() {
    this.submitted = true;
    if (this.resetPasswordForm.valid) {
      this.notification.success(`Email for password reset was sent to the address ` + this.resetPasswordForm.controls.email.value);
      console.log('Email sent: ' + this.resetPasswordForm.controls.email.value);
      await this.resetPassword();
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send email data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   */
  async resetPassword() {
    await this.passwordResetService.sendResetMail(this.resetPasswordForm).subscribe({
      next: () => {
      },
      error: error => {
        console.log('Failed sending mail to user');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
        this.notification.error(`Sending mail failed : ` + error.error);
        console.log(this.errorMessage);
      }
    });
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

  ngOnInit(): void {
  }

}
