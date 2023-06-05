import {Address} from './Address';

export class UserDetailed {

  id: number;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  isActivated: boolean;
  role: Role;
  createFrom: number;
  address: Address;
  failedLogins: number;
  bonusPoints: number;
}

enum Role {
  user = 'user',
  admin = 'admin',
  vendor = 'vendor'
}
