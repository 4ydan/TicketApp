import {HallPlan} from './hall-plan';

export class Performance {
  id?: number;
  name: string;
  country: string;
  city: string;
  postalCode: number;
  street: string;
  streetNr: number;
  date: Date;
  startTime: string;
  endTime?: string;
  price: number;
  hallPlan: HallPlan;
}
