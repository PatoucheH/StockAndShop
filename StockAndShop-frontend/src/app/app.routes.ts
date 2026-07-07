import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { homeGuard } from './core/guards/home.guard';
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
        title: 'Accueil — Stock&Shop',
        loadComponent: () => import('./features/home/pages/home/home')
          .then((m) => m.Home),
      },
      {
        path: 'home/:id',
        canActivate: [homeGuard],
        loadComponent: () =>
          import('./features/home/pages/details-home/details-home')
            .then((m) => m.DetailsHome),
      },
      {
        path: 'recipes',
        title: 'Recettes — Stock&Shop',
        loadComponent: () =>
          import('./features/recipe/pages/recipes/recipes-page')
            .then((m) => m.RecipesPage),
      },
      {
        path: 'recipes/:id',
        loadComponent: () =>
          import('./features/recipe/pages/recipe-detail/recipe-detail-page')
            .then((m) => m.RecipeDetailPage),
      },
      {
        path: 'shopping-list/:id',
        loadComponent: () =>
          import('./features/shopping-list/pages/detail-shopping-list/detail-shopping-list')
            .then((m) => m.DetailShoppingList,
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'auth/login' },
];
