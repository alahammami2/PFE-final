import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { HasAnyRoleDirective } from '@/directives/has-any-role.directive';

interface Match {
    id: number;
    adversaire: string;
    date: string;
    lieu: string;
    statut: string;
    score?: string;
}

@Component({
    standalone: true,
    selector: 'app-prochains-matchs',
    imports: [CommonModule, TableModule, HttpClientModule, HasAnyRoleDirective],
    template: `<div class="card mb-8!" *hasAnyRole="['ADMIN','JOUEUR','COACH','INVITE','STAF_MEDICAL']">
        <div class="font-semibold text-xl mb-4">Prochains Matchs</div>
        <p-table [value]="matches" [paginator]="true" [rows]="5" responsiveLayout="scroll">
            <ng-template pTemplate="header">
                <tr>
                    <th>Adversaire</th>
                    <th pSortableColumn="date">Date <p-sortIcon field="date"></p-sortIcon></th>
                    <th>Lieu</th>
                    <th>Statut</th>
                </tr>
            </ng-template>
            <ng-template pTemplate="body" let-match>
                <tr>
                    <td style="width: 25%; min-width: 7rem;">{{ match.adversaire }}</td>
                    <td style="width: 25%; min-width: 8rem;">{{ match.date }}</td>
                    <td style="width: 25%; min-width: 8rem;">{{ match.lieu }}</td>
                    <td style="width: 15%;">
                        <span [class]="getStatusClass(match.statut)">{{ match.statut }}</span>
                    </td>
                </tr>
            </ng-template>
        </p-table>
    </div>`
})
export class ProchainsMatchs implements OnInit {
    matches: Match[] = [];

    constructor(private http: HttpClient) {}

    ngOnInit(): void {
        // Récupérer les événements à venir depuis planning-service
        const url = `${environment.planningServiceUrl}/events/upcoming`;
        this.http.get<any>(url).subscribe({
            next: (res) => {
                const list: any[] = Array.isArray(res) ? res : (res?.data ?? []);
                const now = new Date();
                // Types acceptés d'après l'enum backend
                const allowedEnum = new Set(['CHAMPIONNAT', 'COUPE', 'AMICAL']);
                const typed = list.filter(ev => allowedEnum.has((ev?.type || '').toString().toUpperCase()));

                // Séparer futurs et passés
                const getDateStr = (ev: any) => ev?.dateDebut || ev?.date_debut || ev?.date_debuit || ev?.date;
                const toTime = (s: any) => {
                    const t = s ? Date.parse(s) : NaN;
                    return isNaN(t) ? null : t;
                };
                const withTime = typed
                    .map(ev => ({ ev, t: toTime(getDateStr(ev)) }))
                    .filter(x => x.t !== null) as { ev: any; t: number }[];

                const future = withTime.filter(x => x.t >= now.getTime())
                    .sort((a, b) => a.t - b.t)
                    .slice(0, 5)
                    .map(x => x.ev);

                // Fallback: si aucun futur, prendre les 5 plus récents (même passés)
                const chosen = future.length > 0
                    ? future
                    : withTime.sort((a, b) => b.t - a.t).slice(0, 5).map(x => x.ev);

                this.matches = chosen.map((ev, idx) => ({
                    id: ev?.id ?? idx,
                    adversaire: ev?.titre ?? '—',
                    date: this.formatDate(getDateStr(ev)),
                    lieu: ev?.lieu ?? '—',
                    statut: 'À venir'
                }));
            },
            error: () => {
                this.matches = [];
            }
        });
    }

    private formatDate(s: any): string {
        if (!s) return '';
        const d = new Date(s);
        if (isNaN(d.getTime())) return '';
        // Affiche date + heure si disponible
        return d.toLocaleString();
    }

    getStatusClass(statut: string): string {
        switch(statut) {
            case 'À venir': return 'bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs';
            case 'En cours': return 'bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-xs';
            case 'Terminé': return 'bg-green-100 text-green-800 px-2 py-1 rounded text-xs';
            default: return 'bg-gray-100 text-gray-800 px-2 py-1 rounded text-xs';
        }
    }
}

