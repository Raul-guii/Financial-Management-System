import { Component, OnInit, signal } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {

  constructor(
  private router: Router,
  private activatedRoute: ActivatedRoute,
  private titleService: Title
) {}

ngOnInit(): void {
  this.router.events.pipe(
    filter(e => e instanceof NavigationEnd),
    map(() => {
      let route = this.activatedRoute;
      while (route.firstChild) route = route.firstChild;
      return route.snapshot.data['title'] ?? 'SGF';
    })
  ).subscribe(title => {
    this.titleService.setTitle(title);
  });
}
}