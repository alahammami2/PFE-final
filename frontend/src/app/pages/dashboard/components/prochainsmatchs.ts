import { Component } from '@angular/core';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';

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
    imports: [CommonModule, TableModule, ButtonModule, RippleModule],
    template: `<div class="card mb-8!">
        <div class="font-semibold text-xl mb-4">Prochains Matchs</div>
        <p-table [value]="matches" [paginator]="true" [rows]="5" responsiveLayout="scroll">
            <ng-template #header>
                <tr>
                    <th>Adversaire</th>
                    <th pSortableColumn="date">Date <p-sortIcon field="date"></p-sortIcon></th>
                    <th>Lieu</th>
                    <th>Statut</th>
                    <th>Actions</th>
                </tr>
            </ng-template>
            <ng-template #body let-match>
                <tr>
                    <td style="width: 25%; min-width: 7rem;">{{ match.adversaire }}</td>
                    <td style="width: 25%; min-width: 8rem;">{{ match.date }}</td>
                    <td style="width: 25%; min-width: 8rem;">{{ match.lieu }}</td>
                    <td style="width: 15%;">
                        <span [class]="getStatusClass(match.statut)">{{ match.statut }}</span>
                    </td>
                    <td style="width: 10%;">
                        <button pButton pRipple type="button" icon="pi pi-eye" class="p-button p-component p-button-text p-button-icon-only"></button>
                    </td>
                </tr>
            </ng-template>
        </p-table>
    </div>`
})
export class ProchainsMatchs {
    matches: Match[] = [
        { id: 1, adversaire: 'ES Tunis', date: '15/12/2024', lieu: 'Stade Municipal', statut: 'À venir' },
        { id: 2, adversaire: 'CA Bizerte', date: '22/12/2024', lieu: 'Salle Omnisport', statut: 'À venir' },
        { id: 3, adversaire: 'ST Sfax', date: '29/12/2024', lieu: 'Stade Municipal', statut: 'À venir' },
        { id: 4, adversaire: 'CS Sfaxien', date: '05/01/2025', lieu: 'Salle Omnisport', statut: 'À venir' },
        { id: 5, adversaire: 'US Monastir', date: '12/01/2025', lieu: 'Stade Municipal', statut: 'À venir' }
    ];

    getStatusClass(statut: string): string {
        switch(statut) {
            case 'À venir': return 'bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs';
            case 'En cours': return 'bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-xs';
            case 'Terminé': return 'bg-green-100 text-green-800 px-2 py-1 rounded text-xs';
            default: return 'bg-gray-100 text-gray-800 px-2 py-1 rounded text-xs';
        }
    }
}

