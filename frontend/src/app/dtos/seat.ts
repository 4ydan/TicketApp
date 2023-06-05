import {SectorType} from './sectorType';

export interface Seat {
  id: number;
  seatRow: number;
  seatNumber: number;
  sectorType: SectorType;
  booked: boolean;
  isSelected?: boolean;
}
