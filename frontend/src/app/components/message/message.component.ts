import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {Message} from '../../dtos/message';
import {ToastrService} from 'ngx-toastr';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder} from '@angular/forms';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  private messages: Message[];

  constructor(private messageService: MessageService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private notification: ToastrService,
              private authService: AuthService,) {
  }

  ngOnInit() {
    if (localStorage.getItem('gotBack') !== 'true') {
      localStorage.setItem('page', '0');
    } else {
      localStorage.setItem('gotBack', 'false');
    }
    if (localStorage.getItem('read') === undefined) {
      localStorage.setItem('read', 'false');
    }
    if (this.isLoggedIn() && !this.isAdmin()) {
      this.loadMessages();
    } else {
      this.loadMessages();
    }
  }

  setRead(read: string) {
    localStorage.setItem('read', read);
  }

  getRead(): string {
    return localStorage.getItem('read');
  }

  getMessages(): Message[] {
    return this.messages;
  }

  getPage(): number {
    return +localStorage.getItem('page');
  }

  resetPage() {
    localStorage.setItem('page','0');
  }

  incrementPage() {
   const page = localStorage.getItem('page');
   localStorage.setItem('page', (+page+1).toString());
  }

  decrementPage() {
    const page = localStorage.getItem('page');
    localStorage.setItem('page', (+page-1).toString());
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  /**
   * Loads the specified page of message from the backend
   */
  public loadMessages() {
    let readParam = null;
    if (this.isLoggedIn() && !this.isAdmin()) {
      readParam = localStorage.getItem('read');
    }
    this.messageService.getMessages(+localStorage.getItem('page'),readParam).subscribe({
      next: (messages: Message[]) => {
        this.messages = messages;
      },
      error: error => {
        const errorMessage = error.status === 0
        ? 'Backend is not running'
        : error.error.message;
        this.notification.error(errorMessage, 'Could Not Fetch News');
      }
    });
  }
}
