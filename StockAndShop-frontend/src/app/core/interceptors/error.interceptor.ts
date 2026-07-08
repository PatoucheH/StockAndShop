import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../features/auth/services/auth.service';
import { ToastService } from '../services/toast.service';
import { switchMap } from 'rxjs/operators';

export const errorInterceptor: HttpInterceptorFn = (
  req,
  next
) => {
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  const toast = inject(ToastService);
  const auth = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // On first 401, try to refresh the token and retry the request once
      // X-Retry header prevents an infinite loop if the retried request also returns 401
      if (error.status === 401 && !req.headers.has("X-Retry")) {
        return auth.tryRestoreSession().pipe(
          switchMap(ok => {
            if(!ok){
              auth.clearSession();
              router.navigate(['/auth/login']);
              return throwError(() => error);
            }
            // The retry travels the full interceptor chain again, so it needs its own
            // catchError — otherwise a 401 on the retry itself would bypass this handler
            // entirely and reach the caller as a raw, unhandled error.
            return next(req.clone({ setHeaders: {'X-Retry': 'true'} })).pipe(
              catchError((retryError: HttpErrorResponse) => {
                if (retryError.status === 401) {
                  auth.clearSession();
                  router.navigate(['/auth/login']);
                } else {
                  toast.error(extractErrorMessage(retryError));
                }
                return throwError(() => retryError);
              })
            );
          })
        );
      }
      toast.error(extractErrorMessage(error));
      return throwError(() => error);
    })
  );
};

/** Falls back to a generic French message when the server doesn't send a structured error body. */
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
