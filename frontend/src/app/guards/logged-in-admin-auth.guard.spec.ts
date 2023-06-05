import { TestBed } from '@angular/core/testing';

import { LoggedInAdminAuthGuard } from './logged-in-admin-auth.guard';

describe('LoggedInAdminAuthGuard', () => {
  let guard: LoggedInAdminAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(LoggedInAdminAuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
