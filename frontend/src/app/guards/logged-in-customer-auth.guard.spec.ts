import { TestBed } from '@angular/core/testing';

import { LoggedInCustomerAuthGuard } from './logged-in-customer-auth.guard';

describe('LoggedInCustomerAuthGuard', () => {
  let guard: LoggedInCustomerAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(LoggedInCustomerAuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
