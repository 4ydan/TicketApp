import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ArtistService} from '../../services/artist.service';
import {Artist} from '../../dtos/artist/artist';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
  title = 'Search';
  state = 'events';
  artists: Artist[];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private artistService: ArtistService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(_ => {
      this.getArtistsByTerm();
    });
  }

  public getSearchTerm() {
    let term;
    this.route.queryParams.subscribe(params => {
      term = params['searchTerm'];
    });
    return term;
  }

  public formatSearchTerm() {
    return '\'' + this.getSearchTerm() + '\'';
  }

  public eventsView() {
    this.state = 'events';
  }

  getArtistsByTerm() {
    this.artistService.searchArtistsByTerm(this.getSearchTerm()).subscribe({
      next: data => {
        this.artists = data;
      },
      error: error => {
        console.error('Error fetching artists', error.message);
      }
    });
  }

  public artistsView() {
    this.getArtistsByTerm();
    this.state = 'artists';
  }
}
