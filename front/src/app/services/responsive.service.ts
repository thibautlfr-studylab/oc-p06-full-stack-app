import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, fromEvent, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ResponsiveService implements OnDestroy {
  private isMobileSubject = new BehaviorSubject<boolean>(false);
  public isMobile$ = this.isMobileSubject.asObservable();
  private readonly resizeSubscription: Subscription;

  constructor() {
    this.checkViewport();
    
    this.resizeSubscription = fromEvent(window, 'resize')
      .pipe(
        debounceTime(200),
        map(() => window.innerWidth < 768),
        distinctUntilChanged()
      )
      .subscribe(isMobile => {
        this.isMobileSubject.next(isMobile);
      });
  }

  private checkViewport(): void {
    this.isMobileSubject.next(window.innerWidth < 768);
  }

  ngOnDestroy(): void {
    if (this.resizeSubscription) {
      this.resizeSubscription.unsubscribe();
    }
  }
}
