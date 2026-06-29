import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../features/auth/services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (
  req,
  next
) => {
  // Skip auth endpoints — they use the httpOnly cookie and must not carry the access token
  if (req.url.includes('/auth/')) return next(req);
  const token = inject(AuthService).getToken();
  if (!token) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
