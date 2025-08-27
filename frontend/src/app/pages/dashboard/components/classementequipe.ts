import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { HttpClientModule } from '@angular/common/http';
import { MatchService, MatchDto } from '@/services/match.service';
import { HasAnyRoleDirective } from '@/directives/has-any-role.directive';

@Component({
    standalone: true,
    selector: 'app-classement-equipe',
    imports: [CommonModule, FormsModule, TableModule, DialogModule, ConfirmDialogModule, HttpClientModule, HasAnyRoleDirective],
    providers: [ConfirmationService],
    styles: [`
      .menu-popover {
        position: absolute;
        right: 0;
        top: 100%;
        margin-top: 8px;
        width: 200px;
        background: #fff;
        border: 1px solid #e5e7eb; /* gray-200 */
        border-radius: 10px;
        box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -4px rgba(0,0,0,0.1);
        z-index: 20;
        padding: 8px;
      }
      .menu-popover:after {
        content: "";
        position: absolute;
        right: 14px;
        top: -8px;
        border-width: 8px;
        border-style: solid;
        border-color: transparent transparent #fff transparent;
        filter: drop-shadow(0 -1px 0 rgba(229,231,235,1));
      }
      .action-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        width: 100%;
        padding: 10px 12px;
        border-radius: 8px;
        color: #fff;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: background-color .15s ease-in-out;
      }
      .action-btn + .action-btn { margin-top: 8px; }
      .action-btn.edit { background: #0ea5e9; } /* sky-500 */
      .action-btn.edit:hover { background: #0284c7; } /* sky-600 */
      .action-btn.delete { background: #ef4444; } /* red-500 */
      .action-btn.delete:hover { background: #dc2626; } /* red-600 */
    `],
    template: `<div class="card mb-8!" *hasAnyRole="['JOUEUR','COACH','ADMIN','INVITE']">
        <div class="flex items-center justify-between mb-4">
            <div class="font-semibold text-xl">Classement Équipe</div>
            <button type="button" class="p-button p-component p-button-primary p-button-rounded p-button-sm" (click)="openAddDialog()" aria-label="Ajouter une équipe" *hasAnyRole="'ADMIN'">
                <i class="pi pi-plus"></i>
            </button>
        </div>

        <p-table [value]="classement" responsiveLayout="scroll">
            <ng-template #header>
                <tr>
                    <th style="width: 10%">Rang</th>
                    <th style="width: 50%">Équipe</th>
                    <th style="width: 20%" class="text-right">Points</th>
                    <th style="width: 20%" class="text-right">Action</th>
                </tr>
            </ng-template>
            <ng-template #body let-row let-ri="rowIndex">
                <ng-container>
                <tr (click)="onRowClick(row, $event)" class="cursor-pointer hover:bg-gray-50">
                    <td>{{ row.rang }}</td>
                    <td>{{ row.equipe }}</td>
                    <td class="text-right font-semibold">{{ row.points }}</td>
                    <td class="text-right relative" (click)="$event.stopPropagation()">
                        <button type="button" class="p-button p-component p-button-text p-button-rounded" (click)="row.menuOpen = !row.menuOpen" aria-label="Actions" *hasAnyRole="'ADMIN'">
                            <i class="pi pi-ellipsis-v"></i>
                        </button>
                        <ng-container *hasAnyRole="'ADMIN'">
                            <div *ngIf="row.menuOpen" class="menu-popover" (click)="$event.stopPropagation()">
                                <button class="action-btn edit" (click)="openEditDialog(row)">
                                    <i class="pi pi-user-edit"></i>
                                    <span>Modifier l'équipe</span>
                                </button>
                                <button class="action-btn delete" (click)="confirmDelete(row)">
                                    <i class="pi pi-trash"></i>
                                    <span>Supprimer l'équipe</span>
                                </button>
                            </div>
                        </ng-container>
                    </td>
                </tr>
                <tr *ngIf="row.expanded" class="bg-gray-50">
                    <td colspan="4" class="py-2">
                        <div class="flex items-center gap-3 justify-center">
                            <button class="p-button p-component p-button-sm p-button-success" (click)="addPoints(row, 3)">+3 points</button>
                            <button class="p-button p-component p-button-sm p-button-help" (click)="addPoints(row, 2)">+2 points</button>
                            <button class="p-button p-component p-button-sm p-button-info" (click)="addPoints(row, 1)">+1 point</button>
                        </div>
                    </td>
                </tr>
                </ng-container>
            </ng-template>
        </p-table>

        <!-- Dialog: Add/Edit Team -->
        <p-dialog [(visible)]="displayDialog" [modal]="true" [draggable]="false" [resizable]="false" [style]="{width: '420px'}" [dismissableMask]="true" *hasAnyRole="'ADMIN'">
            <ng-template pTemplate="header">
                <div class="font-semibold">{{ dialogTitle }}</div>
            </ng-template>
            <div class="space-y-3">
                <div>
                    <label class="block text-sm mb-1">Nom de l'équipe</label>
                    <input type="text" class="p-inputtext p-component w-full" [(ngModel)]="form.equipe" placeholder="Ex: C.O.Kelibia" />
                </div>
                <div>
                    <label class="block text-sm mb-1">Points</label>
                    <input type="number" class="p-inputtext p-component w-full" [(ngModel)]="form.points" />
                </div>
            </div>
            <ng-template pTemplate="footer">
                <button class="p-button p-component p-button-text" (click)="displayDialog=false">Annuler</button>
                <button class="p-button p-component p-button-primary" (click)="saveDialog()">Enregistrer</button>
            </ng-template>
        </p-dialog>

        <!-- Confirm Dialog -->
        <p-confirmDialog></p-confirmDialog>
    </div>`
})
export class ClassementEquipe {
    classement = [] as Array<{ id?: number; rang: number; equipe: string; points: number; expanded?: boolean; menuOpen?: boolean }>;

    // Dialog state
    displayDialog = false;
    dialogTitle = '';
    form: { equipe: string; points: number } = { equipe: '', points: 0 };
    editingRow: any | null = null;

    constructor(private confirm: ConfirmationService, private matchService: MatchService) {}

    ngOnInit() {
        this.load();
        // Fermer menus si clic extérieur
        window.addEventListener('click', () => {
            this.classement.forEach(r => (r.menuOpen = false));
        });
    }

    private load() {
        this.matchService.list().subscribe(({ data }) => {
            const mapped = (data || []).map(d => ({ id: d.id, equipe: d.nomEquipe, points: d.points, rang: 0, expanded: false, menuOpen: false }));
            this.classement = mapped;
            this.recomputeRanks();
        });
    }

    onRowClick(row: any, event: MouseEvent) {
        const target = event.target as HTMLElement;
        if (target.closest('button') || target.closest('i')) return; // éviter toggle quand on clique sur un bouton
        row.expanded = !row.expanded;
        // fermer autres
        this.classement.forEach(r => {
            if (r !== row) r.expanded = false;
        });
    }

    addPoints(row: any, pts: number) {
        if (!row?.id) {
            // si pas encore persisté, maj locale seulement
            row.points = (row.points || 0) + pts;
            this.recomputeRanks();
            return;
        }
        this.matchService.increment(row.id, pts).subscribe(({ data }) => {
            row.points = data.points;
            this.recomputeRanks();
        });
    }

    openAddDialog() {
        this.editingRow = null;
        this.dialogTitle = 'Ajouter une équipe';
        this.form = { equipe: '', points: 0 };
        this.displayDialog = true;
    }

    openEditDialog(row: any) {
        row.menuOpen = false;
        this.editingRow = row;
        this.dialogTitle = "Modifier l'équipe";
        this.form = { equipe: row.equipe, points: row.points };
        this.displayDialog = true;
    }

    saveDialog() {
        const name = (this.form.equipe || '').trim();
        if (!name) { this.displayDialog = false; return; }
        const pts = Number(this.form.points) || 0;
        if (this.editingRow && this.editingRow.id) {
            // update backend
            const body: MatchDto = { id: this.editingRow.id, nomEquipe: name, points: pts };
            this.matchService.update(this.editingRow.id, body).subscribe(({ data }) => {
                this.editingRow.equipe = data.nomEquipe;
                this.editingRow.points = data.points;
                this.recomputeRanks();
                this.displayDialog = false;
            });
        } else if (this.editingRow && !this.editingRow.id) {
            // cas improbable (row non persistée modifiée) -> local
            this.editingRow.equipe = name;
            this.editingRow.points = pts;
            this.recomputeRanks();
            this.displayDialog = false;
        } else {
            // create backend
            const body: MatchDto = { nomEquipe: name, points: pts } as any;
            this.matchService.create(body).subscribe(({ data }) => {
                const item = { id: data.id, rang: 0, equipe: data.nomEquipe, points: data.points, expanded: false, menuOpen: false };
                this.classement.push(item);
                this.recomputeRanks();
                this.displayDialog = false;
            });
        }
    }

    confirmDelete(row: any) {
        row.menuOpen = false;
        this.confirm.confirm({
            message: `Supprimer l'équipe “${row.equipe}” ?`,
            header: 'Confirmation',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Supprimer',
            rejectLabel: 'Annuler',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => {
                if (row?.id) {
                    this.matchService.remove(row.id).subscribe(() => {
                        this.classement = this.classement.filter(r => r !== row);
                        this.recomputeRanks();
                    });
                } else {
                    this.classement = this.classement.filter(r => r !== row);
                    this.recomputeRanks();
                }
            }
        });
    }

    private recomputeRanks() {
        // Trier par points décroissants, puis nom
        this.classement.sort((a, b) => b.points - a.points || a.equipe.localeCompare(b.equipe));
        // Réaffecter les rangs
        this.classement.forEach((r, i) => (r.rang = i + 1));
    }
}

