import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { DashboardService, EventItem } from '../services/dashboard.service';
import { HasAnyRoleDirective } from '@/directives/has-any-role.directive';

@Component({
    standalone: true,
    selector: 'app-stats-widget',
    imports: [CommonModule, HttpClientModule, HasAnyRoleDirective],
    template: `
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 w-full">
            <!-- Matchs Joués - Toujours visible pour tous les rôles -->
            <div class="h-40 flex min-w-[10rem]">
                <div class="card flex-1 flex flex-col">
                    <div class="flex justify-between items-start h-full">
                        <div class="flex-1">
                            <span class="block text-muted-color font-medium text-sm mb-2">Matchs Joués</span>
                            <div class="text-surface-900 dark:text-surface-0 font-bold text-2xl">{{ matchesCount === null ? '...' : matchesCount }}</div>
                        </div>
                        <div class="flex-shrink-0 ml-4 flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-full" style="width: 2.5rem; height: 2.5rem">
                            <i class="pi pi-calendar text-blue-500 text-xl"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Budget Équipe - Visible pour ADMIN et RESPONSABLE_FINANCIER -->
            <div class="h-40 flex min-w-[10rem]" *hasAnyRole="['ADMIN','RESPONSABLE_FINANCIER']">
                <div class="card flex-1 flex flex-col">
                    <div class="flex justify-between items-start h-full">
                        <div class="flex-1">
                            <span class="block text-muted-color font-medium text-sm mb-2">Budget Équipe</span>
                            <div class="text-surface-900 dark:text-surface-0 font-bold text-2xl">{{ teamBudget === null ? '...' : (teamBudget | number:'1.0-0') + ' DT' }}</div>
                            <div class="text-muted-color text-xs mt-1">Total</div>
                        </div>
                        <div class="flex-shrink-0 ml-4 flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-full" style="width: 2.5rem; height: 2.5rem">
                            <i class="pi pi-dollar text-orange-500 text-xl"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Nombre d'Utilisateurs - Visible uniquement pour ADMIN -->
            <div class="h-40 flex min-w-[10rem]" *hasAnyRole="'ADMIN'">
                <div class="card flex-1 flex flex-col">
                    <div class="flex justify-between items-start h-full">
                        <div class="flex-1">
                            <span class="block text-muted-color font-medium text-sm mb-2">Utilisateurs</span>
                            <div class="text-surface-900 dark:text-surface-0 font-bold text-2xl">{{ userCount === null ? '...' : userCount }}</div>
                        </div>
                        <div class="flex-shrink-0 ml-4 flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-full" style="width: 2.5rem; height: 2.5rem">
                            <i class="pi pi-users text-cyan-500 text-xl"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Prochain Événement - Visible pour la plupart des rôles -->
            <div class="h-40 flex min-w-[10rem]" *hasAnyRole="['ADMIN','JOUEUR','COACH','INVITE','STAF_MEDICAL']">
                <div class="card flex-1 flex flex-col">
                    <div class="flex justify-between items-start h-full">
                        <div class="flex-1">
                            <span class="block text-muted-color font-medium text-sm mb-2">Prochain Événement</span>
                            <div class="text-surface-900 dark:text-surface-0 font-semibold text-lg truncate">{{ nextEventTitle || '...' }}</div>
                            <div class="text-primary font-medium text-sm mt-1">{{ nextEventWhen || 'Aucun événement' }}</div>
                            <div class="text-muted-color text-xs">{{ nextEvent?.lieu || '' }}</div>
                        </div>
                        <div class="flex-shrink-0 ml-4 flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-full" style="width: 2.5rem; height: 2.5rem">
                            <i class="pi pi-star text-purple-500 text-xl"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>`
})
export class StatsWidget implements OnInit {
  teamBudget: number | null = null;
  userCount: number | null = null;
  nextEvent: EventItem | null = null;
  matchesCount: number | null = null;

  constructor(private dashboard: DashboardService) {}

  ngOnInit(): void {
    // Subscribe to reactive budget stream and trigger initial refresh
    this.dashboard.teamBudget$.subscribe({
      next: (v) => (this.teamBudget = v ?? 0),
      error: () => (this.teamBudget = 0)
    });
    this.dashboard.refreshTeamTotalBudget();

    this.dashboard.getUserCount().subscribe({
      next: (v) => (this.userCount = v),
      error: () => (this.userCount = 0)
    });

    this.dashboard.getNextEvent().subscribe({
      next: (ev) => (this.nextEvent = ev),
      error: () => (this.nextEvent = null)
    });

    this.dashboard.getPastMatchesCount().subscribe({
      next: (v) => (this.matchesCount = v),
      error: () => (this.matchesCount = 0)
    });
  }

  get nextEventTitle(): string {
    if (!this.nextEvent) return '...';
    return this.nextEvent.titre || 'Événement';
  }

  get nextEventWhen(): string {
    if (!this.nextEvent?.dateDebut) return '';
    const d = new Date(this.nextEvent.dateDebut);
    return d.toLocaleDateString() + ' ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}
