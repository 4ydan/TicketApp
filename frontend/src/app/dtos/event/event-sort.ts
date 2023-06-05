/**
 * EventSort enum
 * Used for defining the different sorts of the events
 * ALL - no filter
 * TOPTEN - top ten events by tickets sold and category
 * ALPHA - events sorted alphabetically
 */
export enum EventSort {
  date = 'DATE',
  topten = 'TOPTEN',
  alpha = 'ALPHA'
}
