import {SectorType} from './sectorType';

export interface SectorCategory {
  id: number;
  sectorType: SectorType;
  surcharge: number;
  color?: string;
}
