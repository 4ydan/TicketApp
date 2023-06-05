import { TestBed } from '@angular/core/testing';

import { LoggedOutOrCustomerOrVendorAuthGuard } from './logged-out-or-customer-or-vendor-auth.guard';

describe('LoggedOutOrCustomerOrVendorAuthGuard', () => {
  let guard: LoggedOutOrCustomerOrVendorAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(LoggedOutOrCustomerOrVendorAuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
