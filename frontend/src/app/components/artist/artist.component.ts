import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event/event';
import {EventService} from '../../services/event.service';
import {ActivatedRoute} from '@angular/router';
import {ArtistDetail} from '../../dtos/artist/artist-detail';
import {ArtistService} from '../../services/artist.service';
import {EventsComponent} from '../events/events.component';
import {AuthService} from '../../services/auth.service';
import {AddressService} from '../../services/address.service';

@Component({
  selector: 'app-artist',
  templateUrl: '../events/events.component.html',
  styleUrls: ['../events/events.component.scss']
})
export class ArtistComponent extends EventsComponent implements OnInit {
  title: string;
  events: Event[];

  constructor(
    public eventService: EventService,
    public addressService: AddressService,
    private artistService: ArtistService,
    public route: ActivatedRoute,
    private aService: AuthService,
  ) {
    super(eventService, addressService, route, aService);
    this.route.params.subscribe(_ => {
      this.getArtistDetails();
    });
  }

  ngOnInit(): void {
    this.mode='Artist';
    this.getArtistDetails();
  }

  getArtistDetails() {
    this.artistService.get(+Number(this.route.snapshot.paramMap.get('id')))
      .subscribe((artist: ArtistDetail) => {
        this.title = artist.name;
        this.events = artist.events;
      });
  }
}
