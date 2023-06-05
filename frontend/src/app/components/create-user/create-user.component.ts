import {Component, OnInit} from '@angular/core';
import {UserService} from '../../services/user.service';
import {UserDetailed} from '../../dtos/user-detailed';
import {Router} from '@angular/router';
import {RegisterService} from '../../services/register.service';
import {AbstractControl, AsyncValidatorFn, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {debounceTime, distinctUntilChanged, first, map, switchMap} from 'rxjs';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent implements OnInit {

  createForm: UntypedFormGroup;
  errorMessage = '';
  submitted = false;

  constructor(
    private registerService: RegisterService,
    public router: Router,
    private formBuilder: UntypedFormBuilder,
    private userService: UserService,
    private notification: ToastrService,
  ) {
    this.createForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.maxLength(50), this.noWhitespaceValidator]],
      lastName: ['', [Validators.required, Validators.maxLength(50), this.noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')], this.validateEmailNotTaken()],
      password: ['noPassword'],
      isActivated: [false],
      country: ['', this.noWhitespaceValidator],
      city: ['', this.noWhitespaceValidator],
      postalCode: ['', Validators.maxLength(10)],
      street: ['', this.noWhitespaceValidator],
      streetNr: ['', Validators.maxLength(10)],
      role: ['', Validators.required],
      createFrom: [1],
    });
  }

  ngOnInit(): void {
  }

  add() {
    this.submitted = true;
    if (this.createForm.valid) {
      const newUser: UserDetailed = Object.assign(this.createForm.value);
      newUser.address = {
        country: this.createForm.get('country').value,
        city: this.createForm.get('city').value,
        postalCode: this.createForm.get('postalCode').value,
        street: this.createForm.get('street').value,
        streetNr: this.createForm.get('streetNr').value,
      };
      newUser.email = this.createForm.controls['email'].value.toLowerCase();

      this.registerService.signUpUser(newUser).subscribe({
        next: () => {
          this.notification.success('Registration successful.' + '\n' +
            'We have send a password and an verification email to the address: ' + newUser.email + ' \n Please confirm your account.');
          console.log('Successfully signed up user: ' + newUser.email);
          this.router.navigate(['/admin']);
        },
        error: err => {
          console.error('Error', err.error.error);
        }
      });
    } else {
      console.log('Error, Invalid Input!');
    }
  }

  noWhitespaceValidator(control: AbstractControl) {
    const isWhitespace = (control && control.value && control.value.toString() || '').trim().length === 0 && control.value !== '';
    const isValid = !isWhitespace;
    return isValid ? null : {whitespace: true};
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
}
