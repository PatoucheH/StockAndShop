import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      toast.error(extractErrorMessage(error));
      return throwError(() => error);
    })
  );
};

function extractErrorMessage(error: HttpErrorResponse): string {
  if (error.error?.message) return error.error.message;

  switch (error.status) {
    case 400: return 'Données invalides.';
    case 401: return 'Session expirée, veuillez vous reconnecter.';
    case 403: return 'Accès refusé.';
    case 404: return 'Ressource introuvable.';
    case 409: return 'Cette ressource existe déjà.';
    case 500: return 'Erreur serveur, réessayez plus tard.';
    default:  return 'Une erreur est survenue.';
  }
}
