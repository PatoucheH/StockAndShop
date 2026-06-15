import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HomeService } from '../../shared/services/home.service';

export const homeGuard: CanActivateFn = (route) => {
  const homeService = inject(HomeService);
  const router = inject(Router);
  const id = route.paramMap.get('id');

  if (!id) return router.createUrlTree(['/']);

  const homes = homeService.homesResource.value();
  // Only redirect if homes are already loaded and user is not a member
  if (homes && !homes.some(h => h.id === id)) {
    return router.createUrlTree(['/']);
  }
  // If homes aren't loaded yet, let through — the component effect handles the 403
  return true;
};
