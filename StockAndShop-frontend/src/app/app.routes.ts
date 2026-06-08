import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './core/layout/layout';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.authRoutes),
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home/home')
          .then((m) => m.HomeComponent),
      },
      {
        path: 'home/:id',
        loadComponent: () =>
          import('./features/home/details-home/details-home')
            .then((m) => m.DetailsHomeComponent),
      },
      {
        path: 'recipes',
        loadComponent: () =>
          import('./features/recipe/recipes-page/recipes-page')
            .then((m) => m.RecipesPageComponent),
      },
      {
        path: 'shopping-list/favorites',
        loadComponent: () =>
          import('./features/shopping-list/favorite-shopping-lists/favorite-shopping-lists')
            .then((m) => m.FavoriteShoppingListsComponent),
      },
      {
        path: 'shopping-list/:id',
        loadComponent: () =>
          import('./features/shopping-list/detail-shopping-list/detail-shopping-list')
            .then((m) => m.DetailShoppingListComponent,
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'auth/login' },
];
