import { Component, input, output } from '@angular/core';
import { User } from '../../../../shared/models/user.models';

@Component({
  selector: 'app-home-member-list',
  imports: [],
  templateUrl: './home-member-list.html',
})
export class HomeMemberListComponent {
  users = input.required<User[]>();
  isOwner = input.required<boolean>();
  userRemoved = output<string>();
}
