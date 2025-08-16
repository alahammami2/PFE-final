import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    standalone: true,
    selector: 'app-stats-widget',
    imports: [CommonModule],
    template: `<div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Matchs Joués</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">24</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-calendar text-blue-500 text-xl!"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">3 victoires </span>
                <span class="text-muted-color">ce mois</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Budget Équipe</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">15,000 DT</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-dollar text-orange-500 text-xl!"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">2,500 DT </span>
                <span class="text-muted-color">restant</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Nombre d'Utilisateurs</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">18</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-users text-cyan-500 text-xl!"></i>
                    </div>
                </div>
                        <span class="text-primary font-medium">12 joueurs </span>
                        <span class="text-muted-color">+ 6 staff</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Prochain Événement</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">Match vs ES Tunis</div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-star text-purple-500 text-xl!"></i>
                    </div>
                </div>
                <span class="text-primary font-medium">Dans 3 jours </span>
                <span class="text-muted-color">Stade Municipal</span>
            </div>
        </div>`
})
export class StatsWidget {}
