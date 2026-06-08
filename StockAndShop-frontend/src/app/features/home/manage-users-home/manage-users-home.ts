import { Component, computed, inject, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subject, EMPTY, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { HomeService } from '../../../shared/services/home.service';
import { AuthService } from '../../auth/auth.service';
import { UserSearchResult } from '../../../shared/models/user.models';

@Component({
  selector: 'app-manage-users-home',
  imports: [FormsModule],
  templateUrl: './manage-users-home.html',
  styleUrl: './manage-users-home.scss',
})
export class ManageUsersHomeComponent implements OnDestroy {
  private homeService = inject(HomeService);
  private authService = inject(AuthService);

  users = this.homeService.users;
  home = this.homeService.selectedHome;
  isOwner = computed(() => this.home()?.ownerEmail === this.authService.getUserEmail());

  showAddForm = signal(false);
  searchQuery = signal('');
  suggestions = signal<UserSearchResult[]>([]);
  selectedUser = signal<UserSearchResult | null>(null);
  selectedRole = signal<'USER' | 'VIEWER'>('USER');
  isSearching = signal(false);
  errorMessage = signal<string | null>(null);

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
    this.errorMessage.set(null);
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
    const homeId = this.home()?.id;
    if (!user || !homeId) return;

    this.homeService.addUser(homeId, user.email, this.selectedRole()).subscribe({
      next: () => {
        this.showAddForm.set(false);
        this.resetForm();
      },
      error: () => this.errorMessage.set("Impossible d'ajouter cet utilisateur."),
    });
  }

  removeUser(userId: string) {
    const homeId = this.home()?.id;
    if (!homeId) return;
    this.homeService.removeUser(homeId, userId).subscribe();
  }

  toggleAddForm() {
    this.showAddForm.update(v => !v);
    if (!this.showAddForm()) this.resetForm();
  }

  private resetForm() {
    this.searchQuery.set('');
    this.selectedUser.set(null);
    this.selectedRole.set('USER');
    this.suggestions.set([]);
    this.errorMessage.set(null);
  }
}
