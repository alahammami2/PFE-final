import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';

@Component({
    standalone: true,
    selector: 'app-notifications-widget',
    imports: [ButtonModule, MenuModule],
    template: `<div class="card">
        <div class="flex items-center justify-between mb-6">
            <div class="font-semibold text-xl">Notifications Équipe</div>
            <div>
                <button pButton type="button" icon="pi pi-ellipsis-v" class="p-button-rounded p-button-text p-button-plain" (click)="menu.toggle($event)"></button>
                <p-menu #menu [popup]="true" [model]="items"></p-menu>
            </div>
        </div>

        <span class="block text-muted-color font-medium mb-4">AUJOURD'HUI</span>
        <ul class="p-0 mx-0 mt-0 mb-6 list-none">
            <li class="flex items-center py-2 border-b border-surface">
                <div class="w-12 h-12 flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-calendar text-xl! text-blue-500"></i>
                </div>
                <span class="text-surface-900 dark:text-surface-0 leading-normal"
                    >Entraînement
                    <span class="text-surface-700 dark:text-surface-100">programmé pour <span class="text-primary font-bold">18h00</span> ce soir</span>
                </span>
            </li>
            <li class="flex items-center py-2">
                <div class="w-12 h-12 flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-user text-xl! text-orange-500"></i>
                </div>
                <span class="text-surface-700 dark:text-surface-100 leading-normal">Ahmed Ben Ali a été <span class="text-primary font-bold">élu joueur du mois</span>.</span>
            </li>
        </ul>

        <span class="block text-muted-color font-medium mb-4">HIER</span>
        <ul class="p-0 m-0 list-none mb-6">
            <li class="flex items-center py-2 border-b border-surface">
                <div class="w-12 h-12 flex items-center justify-center bg-green-100 dark:bg-green-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-trophy text-xl! text-green-500"></i>
                </div>
                <span class="text-surface-900 dark:text-surface-0 leading-normal"
                    >Victoire
                    <span class="text-surface-700 dark:text-surface-100">contre ES Tunis <span class="text-primary font-bold">3-1</span></span>
                </span>
            </li>
            <li class="flex items-center py-2 border-b border-surface">
                <div class="w-12 h-12 flex items-center justify-center bg-pink-100 dark:bg-pink-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-heart text-xl! text-pink-500"></i>
                </div>
                <span class="text-surface-900 dark:text-surface-0 leading-normal"
                    >Blessure
                    <span class="text-surface-700 dark:text-surface-100">Karim Hammami - entorse légère</span>
                </span>
            </li>
        </ul>
        <span class="block text-muted-color font-medium mb-4">CETTE SEMAINE</span>
        <ul class="p-0 m-0 list-none">
            <li class="flex items-center py-2 border-b border-surface">
                <div class="w-12 h-12 flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-star text-xl! text-purple-500"></i>
                </div>
                <span class="text-surface-900 dark:text-surface-0 leading-normal">L'équipe a gagné <span class="text-primary font-bold">2 matchs</span> sur 3.</span>
            </li>
            <li class="flex items-center py-2 border-b border-surface">
                <div class="w-12 h-12 flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-full mr-4 shrink-0">
                    <i class="pi pi-users text-xl! text-cyan-500"></i>
                </div>
                <span class="text-surface-900 dark:text-surface-0 leading-normal"><span class="text-primary font-bold">3 nouveaux joueurs</span> ont rejoint l'équipe.</span>
            </li>
        </ul>
    </div>`
})
export class NotificationsWidget {
    items = [
        { label: 'Add New', icon: 'pi pi-fw pi-plus' },
        { label: 'Remove', icon: 'pi pi-fw pi-trash' }
    ];
}
