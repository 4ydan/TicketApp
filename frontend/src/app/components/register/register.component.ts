import {Component, OnInit} from '@angular/core';
import {
  AbstractControl, AsyncValidatorFn,
  FormGroup,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import {Router} from '@angular/router';
import {UserDetailed} from '../../dtos/user-detailed';
import {AddressService} from '../../services/address.service';
import {UserService} from '../../services/user.service';
import {debounceTime, distinctUntilChanged, first, map, switchMap} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {RegisterService} from '../../services/register.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  signupForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  pending = false;

  constructor(private formBuilder: UntypedFormBuilder, private registerService: RegisterService, private userService: UserService
    , private addressService: AddressService, private router: Router, private notification: ToastrService) {
    this.signupForm = this.formBuilder.group({
      firstName: ['', [Validators.required, this.noWhitespaceValidator]],
      lastName: ['', [Validators.required, this.noWhitespaceValidator]],
      password: ['', [Validators.required, Validators.minLength(8), this.noWhitespaceValidator]],
      confirmPassword: ['', [Validators.required, this.noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')], this.validateEmailNotTaken()],
      isActivated: [false],
      country: ['', this.noWhitespaceValidator],
      city: ['', this.noWhitespaceValidator],
      postalCode: ['', Validators.maxLength(10)],
      street: ['', this.noWhitespaceValidator],
      streetNr: ['', Validators.maxLength(10)],
      role: [0],
      createFrom: [0],
    }, {validators: [this.matchPassword('password', 'confirmPassword')]});
  }

  async submit() {
    this.submitted = true;
    if (this.signupForm.valid) {
      if (this.pending === false) {
        this.pending = true;
        await this.signupUser();
      } else {
        console.log('wait for request before to finish before calling again');
      }
    } else {
      console.log('Invalid input');
    }
  }

  signupUser() {
    if (this.signupForm.valid) {
      const newUser: UserDetailed = Object.assign(this.signupForm.value);
      newUser.address = {
        country: this.signupForm.get('country').value,
        city: this.signupForm.get('city').value,
        postalCode: this.signupForm.get('postalCode').value,
        street: this.signupForm.get('street').value,
        streetNr: this.signupForm.get('streetNr').value,
      };
      delete newUser['confirmPassword'];
      console.log(newUser);
      newUser.email = this.signupForm.controls['email'].value.toLowerCase();
      this.registerService.signUpUser(newUser).subscribe({
        next: () => {
          this.notification.success('Registration successful.' + '\n' +
            'We have send an verification email to the address: ' + newUser.email + ' \n Please confirm your account.');
          console.log('Successfully signed up user: ' + newUser.email);
          this.pending = false;
          this.router.navigate(['../login']);
        },
        error: error => {
          console.log('Could not store user due to:');
          console.log(error);
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error?.error.error;
          } else {
            this.errorMessage = error?.error;
          }
          this.notification.error(`Registration failed : ` + error.error);
          this.pending = false;
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

  validateEmailNotTaken(): AsyncValidatorFn {
    return control => control.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(value => this.userService.getActivatedUserByEmail(value)),
        map((unique: UserDetailed) => (unique ? {emailTaken: true} : null)),
        first());
  }


  noWhitespaceValidator(control: AbstractControl) {
    const isWhitespace = (control && control.value && control.value.toString() || '').trim().length === 0 && control.value !== '';
    const isValid = !isWhitespace;
    return isValid ? null : {whitespace: true};
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit(): void {
  }

}
