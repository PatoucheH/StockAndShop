import { Component, inject, input, OnDestroy, output, signal } from '@angular/core';
import { Subject, EMPTY, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { HomeService } from '../../../../shared/services/home.service';
import { UserSearchResult } from '../../../../shared/models/user.models';

@Component({
  selector: 'app-user-search-input',
  imports: [],
  templateUrl: './user-search-input.html',
})
export class UserSearchInputComponent implements OnDestroy {
  private homeService = inject(HomeService);

  errorMessage = input<string | null>(null);
  submitted = output<{ user: UserSearchResult; role: 'USER' | 'VIEWER' }>();

  searchQuery = signal('');
  suggestions = signal<UserSearchResult[]>([]);
  selectedUser = signal<UserSearchResult | null>(null);
  selectedRole = signal<'USER' | 'VIEWER'>('USER');
  isSearching = signal(false);

  private searchSubject = new Subject<string>();
  private sub = this.searchSubject.pipe(
    debounceTime(300),
    distinctUntilChanged(),
    switchMap(query => {
      if (query.length < 2) {
        this.suggestions.set([]);
        return EMPTY;
      }
      this.isSearching.set(true);
      return this.homeService.searchUsers(query).pipe(catchError(() => of([])));
    })
  ).subscribe(results => {
    this.isSearching.set(false);
    this.suggestions.set(results as UserSearchResult[]);
  });

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  onSearchInput(value: string) {
    this.searchQuery.set(value);
    this.selectedUser.set(null);
    this.searchSubject.next(value);
  }

  selectUser(user: UserSearchResult) {
    this.selectedUser.set(user);
    this.searchQuery.set(`${user.name} (${user.email})`);
    this.suggestions.set([]);
  }

  hideSuggestions() {
    setTimeout(() => this.suggestions.set([]), 150);
  }

  submit() {
    const user = this.selectedUser();
    if (!user) return;
    this.submitted.emit({ user, role: this.selectedRole() });
  }
}
