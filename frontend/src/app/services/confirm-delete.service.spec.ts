import { TestBed } from '@angular/core/testing';

import { ConfirmationDialogService } from './confirm-delete.service';

describe('ConfirmDeleteService', () => {
  let service: ConfirmationDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfirmationDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
