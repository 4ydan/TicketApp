import { EventCategory } from './event-category';

export class EventCreate {
    title: string;
    image: string;
    imageFile: File;
    description: string;
    durationInMinutes: number;
    startDate: Date;
    endDate: Date;
    eventCategory: EventCategory;
    artist: string;
}
