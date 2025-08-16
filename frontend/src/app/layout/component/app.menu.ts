import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from './app.menuitem';

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

    ngOnInit() {
        this.model = [
            {
                label: 'Plateforme Volley-Ball',
                items: [
                    { label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'] },
                    { label: 'Calendrier', icon: 'pi pi-fw pi-calendar', routerLink: ['/calendrier'] },
                    { label: 'Effectif', icon: 'pi pi-fw pi-users', routerLink: ['/effectif'] },
                    { label: 'Centre médical', icon: 'pi pi-fw pi-heart', routerLink: ['/centre-medical'] },
                    { label: 'Demandes administratives', icon: 'pi pi-fw pi-briefcase', routerLink: ['/demandes-administratives'] },
                    { label: 'Finance', icon: 'pi pi-fw pi-wallet', routerLink: ['/finance'] },
                    { label: 'Absence', icon: 'pi pi-fw pi-user-minus', routerLink: ['/absence'] },
                    { label: 'Performance', icon: 'pi pi-fw pi-chart-line', routerLink: ['/performance'] }
                ]
            }
        ];
    }
}
