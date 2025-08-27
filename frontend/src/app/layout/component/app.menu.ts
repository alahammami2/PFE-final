import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from './app.menuitem';
import { AuthService } from '@/services/auth.service';

@Component({
    selector: 'app-menu',
    standalone: true,
    imports: [CommonModule, AppMenuitem, RouterModule],
    template: `<ul class="layout-menu">
        <ng-container *ngFor="let item of model; let i = index">
            <li app-menuitem *ngIf="!item.separator" [item]="item" [index]="i" [root]="true"></li>
            <li *ngIf="item.separator" class="menu-separator"></li>
        </ng-container>
    </ul> `
})
export class AppMenu {
    model: MenuItem[] = [];

    constructor(private auth: AuthService) {}

    ngOnInit() {
        const items: MenuItem[] = [
            { label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'] },
        ];

        // Only show Calendrier for allowed roles
        if (this.auth.hasAnyRole(['JOUEUR', 'COACH', 'ADMIN', 'INVITE', 'STAF_MEDICAL'])) {
            items.push({ label: 'Calendrier', icon: 'pi pi-fw pi-calendar', routerLink: ['/calendrier'] });
        }

        if (this.auth.hasAnyRole(['COACH', 'ADMIN', 'INVITE'])) {
            items.push({ label: 'Effectif', icon: 'pi pi-fw pi-users', routerLink: ['/effectif'] });
        }

        if (this.auth.hasAnyRole(['COACH', 'ADMIN', 'JOUEUR', 'STAF_MEDICAL'])) {
            items.push({ label: 'Centre médical', icon: 'pi pi-fw pi-heart', routerLink: ['/centre-medical'] });
        }

        if (this.auth.hasAnyRole(['COACH', 'ADMIN', 'JOUEUR', 'STAF_MEDICAL'])) {
            items.push({ label: 'Demandes administratives', icon: 'pi pi-fw pi-briefcase', routerLink: ['/demandes-administratives'] });
        }

        if (this.auth.hasAnyRole(['ADMIN', 'RESPONSABLE_FINANCIER'])) {
            items.push({ label: 'Finance', icon: 'pi pi-fw pi-wallet', routerLink: ['/finance'] });
        }

        if (this.auth.hasAnyRole(['COACH', 'ADMIN', 'INVITE', 'RESPONSABLE_FINANCIER', 'JOUEUR'])) {
            items.push({ label: 'Absence', icon: 'pi pi-fw pi-user-minus', routerLink: ['/absence'] });
        }

        if (this.auth.hasAnyRole(['ADMIN', 'JOUEUR', 'COACH', 'INVITE'])) {
            items.push({ label: 'Performance', icon: 'pi pi-fw pi-chart-line', routerLink: ['/performance'] });
        }

        this.model = [
            {
                label: 'Plateforme Volley-Ball',
                items
            }
        ];
    }
}
