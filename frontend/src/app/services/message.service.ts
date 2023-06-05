import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import { CreateEditMessage } from '../dtos/CreateEditMessage';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = this.globals.backendUri + '/messages';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from the backend
   */
  getMessages(page: number, read?: boolean): Observable<Message[]> {
    if (read === null) {
      return this.httpClient.get<Message[]>(this.messageBaseUri + '?page=' + page);
    } else {
      return this.httpClient.get<Message[]>(this.messageBaseUri + '?read=' + read + '&page=' + page);
    }
  }

  /**
   * Loads specific message from the backend
   *
   * @param id of message to load
   */
  getMessageById(id: number): Observable<Message> {
    console.log('Load message details for ' + id);
    return this.httpClient.get<Message>(this.messageBaseUri + '/' + id);
  }

  /**
   * Persists message to the backend
   *
   * @param message to persist
   */
  createMessage(newMessage: CreateEditMessage): Observable<CreateEditMessage> {
    console.log('Create message with title ' + newMessage.title);
    const formParams = new FormData();
    formParams.append('title', newMessage.title);
    formParams.append('shortDescription', newMessage.shortDescription);
    formParams.append('description', newMessage.description);
    formParams.append('picture', newMessage.picture);
    return this.httpClient.post<CreateEditMessage>(this.messageBaseUri, formParams);
  }

  /**
   * Edit an existing message in the backend
   *
   * @param message that should be edited
   * @param id of the message that should be edited
   */
   editMessage(message: CreateEditMessage, id: number): Observable<CreateEditMessage> {
    console.log('Update message with id: ' + id);
    const formParams = new FormData();
    formParams.append('title', message.title);
    formParams.append('shortDescription', message.shortDescription);
    formParams.append('description', message.description);
    if (message.picture != null) {
      formParams.append('picture', message.picture);
    }
    return this.httpClient.put<CreateEditMessage>(this.messageBaseUri + '/' + id, formParams);
  }

  /**
   * Delete message
   *
   * @param id of the message to be deleted
   */
  deleteById(id: number): Observable<any> {
    return this.httpClient.delete(this.messageBaseUri + '/' + id);
  }
}
