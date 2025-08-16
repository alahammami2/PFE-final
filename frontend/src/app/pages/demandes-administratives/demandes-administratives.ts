import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Table, TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
// Removed DatePicker as date is set automatically
import { InputNumberModule } from 'primeng/inputnumber';

type TypeDemande = 'CONGE' | 'MATERIEL' | 'TRANSPORT' | 'HEBERGEMENT' | 'BUDGET' | 'EVENEMENT' | 'AUTRE';
type Priorite = 'BASSE' | 'NORMALE' | 'URGENTE' | 'CRITIQUE';
type Statut = 'EN_COURS' | 'EN_ATTENTE' | 'REJETEE' | 'TERMINEE';

interface DemandeAdministrativeRow {
    id: string;
    demande: string;
    dateCreation: string;
    demandeur: string;
    typeDemande: TypeDemande;
    priorite: Priorite;
    budgetDemande: string;
    statut: Statut;
}

@Component({
    selector: 'app-demandes-administratives',
    standalone: true,
    imports: [
        CommonModule,
        TableModule,
        ButtonModule,
        InputTextModule,
        IconFieldModule,
        InputIconModule,
        TagModule,
        DialogModule,
        SelectModule,
        InputNumberModule,
        ReactiveFormsModule
    ],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <div class="text-2xl font-semibold">Demandes administratives</div>
                <p-button icon="pi pi-plus" label="Nouvelle demande" severity="primary" (onClick)="openNew()" />
            </div>

            <p-table
                #dt
                [value]="rows"
                dataKey="id"
                [rows]="10"
                [paginator]="true"
                [rowHover]="true"
                [rowsPerPageOptions]="[10, 20, 30]"
                [globalFilterFields]="['demande','demandeur','typeDemande','priorite','statut']"
                [tableStyle]="{ 'min-width': '95rem' }"
            >
                <ng-template #caption>
                    <div class="flex items-center justify-end">
                        <label class="mr-3 font-medium">Recherche</label>
                        <p-iconfield>
                            <p-inputicon styleClass="pi pi-search" />
                            <input pInputText type="text" (input)="onGlobalFilter(dt, $event)" placeholder="Rechercher..." />
                        </p-iconfield>
                    </div>
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th style="min-width: 18rem">Demande</th>
                        <th pSortableColumn="dateCreation" style="min-width: 12rem">Date de création <p-sortIcon field="dateCreation" /></th>
                        <th style="min-width: 16rem">demandeur</th>
                        <th pSortableColumn="typeDemande" style="min-width: 14rem">Type de demande <p-sortIcon field="typeDemande" /></th>
                        <th pSortableColumn="priorite" style="min-width: 12rem">Priorité <p-sortIcon field="priorite" /></th>
                        <th style="min-width: 14rem">Budget demandé</th>
                        <th pSortableColumn="statut" style="min-width: 12rem">Statut <p-sortIcon field="statut" /></th>
                    
                    </tr>
                </ng-template>

                <ng-template #body let-row>
                    <tr (click)="toggleActions(row)" class="cursor-pointer">
                        <td>{{ row.demande }}</td>
                        <td>{{ row.dateCreation }}</td>
                        <td>{{ row.demandeur }}</td>
                        <td>
                            <p-tag [value]="row.typeDemande" severity="info" />
                        </td>
                        <td>
                            <p-tag [value]="row.priorite" [severity]="getPrioritySeverity(row.priorite)" />
                        </td>
                        <td>{{ row.budgetDemande }}</td>
                        <td>
                            <p-tag [value]="row.statut" [severity]="getStatusSeverity(row.statut)" />
                        </td>
                       
                    </tr>
                    <tr *ngIf="expandedRowId === row.id">
                        <td colspan="7">
                            <div class="flex gap-2 py-2 justify-center items-center">
                                <p-button size="small" label="EN_COURS" severity="warn" (onClick)="updateStatus(row, 'EN_COURS'); $event.stopPropagation()"></p-button>
                                <p-button size="small" label="REJETEE" severity="danger" (onClick)="updateStatus(row, 'REJETEE'); $event.stopPropagation()"></p-button>
                                <p-button size="small" label="TERMINEE" severity="success" (onClick)="updateStatus(row, 'TERMINEE'); $event.stopPropagation()"></p-button>
                            </div>
                        </td>
                    </tr>
                </ng-template>

                <ng-template #emptymessage>
                    <tr>
                        <td colspan="7">Aucune demande trouvée.</td>
                    </tr>
                </ng-template>
            </p-table>

            <div class="mt-4">Total d'élément(s) : {{ rows.length }}</div>
        </div>

        <p-dialog
            header="Nouvelle demande"
            [(visible)]="newDialogVisible"
            [modal]="true"
            [draggable]="false"
            [dismissableMask]="true"
            [style]="{ width: '48rem' }"
            [style]="{ height:'40rem' }"
            [breakpoints]="{ '960px': '75vw', '640px': '95vw' }"
        >
            <form [formGroup]="form" class="flex flex-col gap-4">
                <div class="grid grid-cols-12 gap-4">
                    <div class="col-span-12">
                        <label class="block mb-2">Demande</label>
                        <input pInputText formControlName="demande" placeholder="Ex: Demande de congé annuel" />
                    </div>
                </div>

                <div class="grid grid-cols-12 gap-4">
                    <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Type de demande</label>
                    <p-select formControlName="typeDemande" [options]="typeOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner" appendTo="body"></p-select>
                    </div>

                    <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Priorité</label>
                    <p-select formControlName="priorite" [options]="priorityOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner" appendTo="body"></p-select>
                    </div>
                </div>

                <div class="grid grid-cols-12 gap-4">
                    <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Budget demandé (TND)</label>
                    <p-inputnumber formControlName="budget" inputId="budget" mode="currency" currency="TND" currencyDisplay="code" locale="fr-TN"></p-inputnumber>
                    </div>
                </div>
            </form>

            <ng-template #footer>
                <div class="flex gap-2 justify-end">
                    <p-button label="Annuler" severity="secondary" (onClick)="closeNew()"></p-button>
                    <p-button label="Enregistrer" icon="pi pi-check" [disabled]="form.invalid" (onClick)="submitNew()"></p-button>
                </div>
            </ng-template>
        </p-dialog>
    `
})
export class DemandesAdministratives {
    newDialogVisible: boolean = false;
    form: FormGroup;
    currentUserName: string = 'Utilisateur connecté';
    expandedRowId: string | null = null;

    typeOptions = [
        { label: 'CONGE', value: 'CONGE' },
        { label: 'MATERIEL', value: 'MATERIEL' },
        { label: 'TRANSPORT', value: 'TRANSPORT' },
        { label: 'HEBERGEMENT', value: 'HEBERGEMENT' },
        { label: 'BUDGET', value: 'BUDGET' },
        { label: 'EVENEMENT', value: 'EVENEMENT' },
        { label: 'AUTRE', value: 'AUTRE' }
    ] as { label: TypeDemande; value: TypeDemande }[];

    priorityOptions = [
        { label: 'BASSE', value: 'BASSE' },
        { label: 'NORMALE', value: 'NORMALE' },
        { label: 'URGENTE', value: 'URGENTE' },
        { label: 'CRITIQUE', value: 'CRITIQUE' }
    ] as { label: Priorite; value: Priorite }[];

    rows: DemandeAdministrativeRow[] = [
        {
            id: '1',
            demande: "Demande de congé annuel",
            dateCreation: '05/08/2024',
            demandeur: 'Amine Ben Salah',
            typeDemande: 'CONGE',
            priorite: 'NORMALE',
            budgetDemande: '-',
            statut: 'EN_COURS'
        },
        {
            id: '2',
            demande: "Achat de matériel d'entraînement",
            dateCreation: '17/09/2024',
            demandeur: 'Sarra Gharbi',
            typeDemande: 'MATERIEL',
            priorite: 'URGENTE',
            budgetDemande: '1 500 TND',
            statut: 'EN_ATTENTE'
        },
        {
            id: '3',
            demande: 'Réservation transport match extérieur',
            dateCreation: '01/10/2024',
            demandeur: 'Mehdi Jlassi',
            typeDemande: 'TRANSPORT',
            priorite: 'BASSE',
            budgetDemande: '800 TND',
            statut: 'TERMINEE'
        },
        {
            id: '4',
            demande: 'Hébergement tournoi régional',
            dateCreation: '12/10/2024',
            demandeur: 'Ons Kefi',
            typeDemande: 'HEBERGEMENT',
            priorite: 'CRITIQUE',
            budgetDemande: '3 200 TND',
            statut: 'REJETEE'
        },
        {
            id: '5',
            demande: 'Budget équipement saison',
            dateCreation: '21/10/2024',
            demandeur: 'Rami Trabelsi',
            typeDemande: 'BUDGET',
            priorite: 'NORMALE',
            budgetDemande: '12 000 TND',
            statut: 'EN_COURS'
        },
        {
            id: '6',
            demande: 'Organisation évènement caritatif',
            dateCreation: '28/10/2024',
            demandeur: 'Nour Chebbi',
            typeDemande: 'EVENEMENT',
            priorite: 'URGENTE',
            budgetDemande: '4 500 TND',
            statut: 'EN_ATTENTE'
        },
        {
            id: '7',
            demande: 'Autre demande administrative',
            dateCreation: '02/11/2024',
            demandeur: 'Yassine Ferchichi',
            typeDemande: 'AUTRE',
            priorite: 'BASSE',
            budgetDemande: '-',
            statut: 'TERMINEE'
        }
    ];

    constructor(private fb: FormBuilder) {
        this.form = this.fb.group({
            demande: [''],
            typeDemande: [null, Validators.required],
            priorite: [null, Validators.required],
            budget: [null]
        });
    }

    openNew() {
        this.form.reset({
            demande: '',
            dateCreation: new Date(),
            demandeur: '',
            typeDemande: null,
            priorite: null,
            budget: null
        });
        this.newDialogVisible = true;
    }

    closeNew() {
        this.newDialogVisible = false;
    }

    submitNew() {
        if (this.form.invalid) return;

        const value = this.form.value as {
            demande: string;
            typeDemande: TypeDemande;
            priorite: Priorite;
            budget: number | null;
        };

        const today = new Date();
        const formatDate = (d: Date) => `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`;

        const newRow: DemandeAdministrativeRow = {
            id: Date.now().toString(),
            demande: value.demande,
            dateCreation: formatDate(today),
            demandeur: this.currentUserName,
            typeDemande: value.typeDemande,
            priorite: value.priorite,
            budgetDemande: value.budget ? `${value.budget} TND` : '-',
            statut: 'EN_ATTENTE'
        };

        this.rows = [newRow, ...this.rows];
        this.newDialogVisible = false;
    }

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
    }

    toggleActions(row: DemandeAdministrativeRow) {
        this.expandedRowId = this.expandedRowId === row.id ? null : row.id;
    }

    updateStatus(row: DemandeAdministrativeRow, statut: Statut) {
        row.statut = statut;
        this.expandedRowId = null;
    }

    getStatusSeverity(statut: Statut) {
        switch (statut) {
            case 'TERMINEE':
                return 'success';
            case 'EN_COURS':
            case 'EN_ATTENTE':
                return 'warn';
            case 'REJETEE':
                return 'danger';
            default:
                return 'info';
        }
    }

    getPrioritySeverity(priorite: Priorite) {
        switch (priorite) {
            case 'BASSE':
                return 'success';
            case 'NORMALE':
                return 'info';
            case 'URGENTE':
                return 'warn';
            case 'CRITIQUE':
                return 'danger';
            default:
                return 'info';
        }
    }
}


