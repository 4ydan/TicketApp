import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {ResetPassword} from '../dtos/reset-password';
import {UntypedFormGroup} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {

  private passwordBaseUri: string = this.globals.backendUri + '/password-resets';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Send email for password resetting
   */
  sendResetMail(formGroup: UntypedFormGroup): any {
    console.log('Sending password reset link to : ' + formGroup.controls.email.value);
    return this.httpClient.post(this.passwordBaseUri, formGroup.controls.email.value.toString(), {responseType: 'text'});
  }

  /**
   * Update password
   */
  updatePassword(passwordDto: ResetPassword) {
    console.log('Updating password');
   return this.httpClient.put(this.passwordBaseUri + '/updatePassword', passwordDto, {responseType: 'text'});
  }
}
