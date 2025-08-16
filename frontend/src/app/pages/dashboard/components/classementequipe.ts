import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';

@Component({
    standalone: true,
    selector: 'app-classement-equipe',
    imports: [CommonModule, TableModule],
    template: `<div class="card mb-8!">
        <div class="font-semibold text-xl mb-4">Classement Équipe</div>
        <p-table [value]="classement" responsiveLayout="scroll">
            <ng-template #header>
                <tr>
                    <th style="width: 10%">Rang</th>
                    <th style="width: 60%">Équipe</th>
                    <th style="width: 30%" class="text-right">Points</th>
                </tr>
            </ng-template>
            <ng-template #body let-row>
                <tr>
                    <td>{{ row.rang }}</td>
                    <td>{{ row.equipe }}</td>
                    <td class="text-right font-semibold">{{ row.points }}</td>
                </tr>
            </ng-template>
        </p-table>
    </div>`
})
export class ClassementEquipe {
    classement = [
        { rang: 1, equipe: 'ES Tunis', points: 56 },
        { rang: 2, equipe: 'C.O.Kelibia', points: 48 },
        { rang: 3, equipe: 'CA Bizerte', points: 41 },
        { rang: 4, equipe: 'US Monastir', points: 38 },
        { rang: 5, equipe: 'CS Sfaxien', points: 35 }
    ];

    ngOnInit() {
        // Assure l'ordre fixe du classement par rang croissant
        this.classement.sort((a, b) => a.rang - b.rang);
    }
}

