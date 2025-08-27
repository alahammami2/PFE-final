import { Directive, Input, TemplateRef, ViewContainerRef, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '@/services/auth.service';

@Directive({
  selector: '[hasAnyRole]',
  standalone: true
})
export class HasAnyRoleDirective implements OnDestroy {
  private rolesWanted: string[] = [];
  private sub: Subscription;
  private isVisible = false;

  constructor(
    private tpl: TemplateRef<any>,
    private vcr: ViewContainerRef,
    private auth: AuthService
  ) {
    this.sub = this.auth.roles$.subscribe(() => this.updateView());
  }

  @Input()
  set hasAnyRole(value: string | string[]) {
    if (typeof value === 'string') {
      this.rolesWanted = [value];
    } else if (Array.isArray(value)) {
      this.rolesWanted = value;
    } else {
      this.rolesWanted = [];
    }
    this.updateView();
  }

  private updateView() {
    const canShow = this.rolesWanted.length === 0 || this.auth.hasAnyRole(this.rolesWanted);
    if (canShow && !this.isVisible) {
      this.vcr.clear();
      this.vcr.createEmbeddedView(this.tpl);
      this.isVisible = true;
    } else if (!canShow && this.isVisible) {
      this.vcr.clear();
      this.isVisible = false;
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
