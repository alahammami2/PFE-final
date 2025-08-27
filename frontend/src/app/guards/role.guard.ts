import { Injectable } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@/services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuardService {
  constructor(private auth: AuthService, private router: Router) {}

  can(allowed: string[] | undefined): boolean | UrlTree {
    if (!allowed || allowed.length === 0) return true; // no restriction
    if (this.auth.hasAnyRole(allowed)) return true;
    // If unauthorized, redirect to dashboard (avoid hitting login default redirect)
    return this.router.createUrlTree(['/dashboard']);
  }
}

export const RoleGuard: CanActivateFn = (route) => {
  const svc = inject(RoleGuardService);
  const allowed = (route.data?.['roles'] as string[]) || [];
  return svc.can(allowed);
};
