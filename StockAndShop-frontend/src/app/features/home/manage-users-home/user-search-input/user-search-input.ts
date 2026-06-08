import { Component, computed, inject, input, output, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged, switchMap, catchError, map, startWith } from 'rxjs/operators';
import { of } from 'rxjs';
import { HomeService } from '../../../../shared/services/home.service';
import { UserSearchResult } from '../../../../shared/models/user.models';

interface SearchState {
  users: UserSearchResult[];
  searching: boolean;
}

@Component({
  selector: 'app-user-search-input',
  imports: [],
  templateUrl: './user-search-input.html',
})
export class UserSearchInputComponent {
  private homeService = inject(HomeService);

  errorMessage = input<string | null>(null);
  submitted = output<{ user: UserSearchResult; role: 'USER' | 'VIEWER' }>();

  searchQuery = signal('');
  selectedUser = signal<UserSearchResult | null>(null);
  selectedRole = signal<'USER' | 'VIEWER'>('USER');
  private showSuggestions = signal(false);

  private searchState = toSignal(
    toObservable(this.searchQuery).pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (query.length < 2) return of<SearchState>({ users: [], searching: false });
        return this.homeService.searchUsers(query).pipe(
          map(users => ({ users: users as UserSearchResult[], searching: false })),
          startWith<SearchState>({ users: [], searching: true }),
          catchError(() => of<SearchState>({ users: [], searching: false }))
        );
      })
    ),
    { initialValue: { users: [], searching: false } as SearchState }
  );

  suggestions = computed(() => this.showSuggestions() ? this.searchState().users : []);
  isSearching = computed(() => this.searchState().searching);

  onSearchInput(value: string) {
    this.searchQuery.set(value);
    this.selectedUser.set(null);
    this.showSuggestions.set(true);
  }

  selectUser(user: UserSearchResult) {
    this.selectedUser.set(user);
    this.searchQuery.set(`${user.name} (${user.email})`);
    this.showSuggestions.set(false);
  }

  hideSuggestions() {
    setTimeout(() => this.showSuggestions.set(false), 150);
  }

  submit() {
    const user = this.selectedUser();
    if (!user) return;
    this.submitted.emit({ user, role: this.selectedRole() });
  }
}
