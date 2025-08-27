import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
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
import { AdminRequestsApiService, AdminRequestDto, RequestType, RequestPriority, RequestStatus } from '../../services/admin-requests.service';
import { HasAnyRoleDirective } from '../../directives/has-any-role.directive';
import { AuthService } from '../../services/auth.service';

// We use backend-aligned DTOs/types from AdminRequestsApiService

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
        ReactiveFormsModule,
        HasAnyRoleDirective
    ],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <div class="text-2xl font-semibold">Demandes administratives</div>
                <p-button *hasAnyRole="['JOUEUR','STAF_MEDICAL','COACH']" icon="pi pi-plus" label="Nouvelle demande" severity="primary" (onClick)="openNew()" />
            </div>

            <p-table #dt [value]="adminRequests" dataKey="id" [rows]="10" [paginator]="true" [rowHover]="true" [rowsPerPageOptions]="[10, 20, 30]" [tableStyle]="{ 'min-width': '90rem' }">
                <ng-template #header>
                    <tr>
                        <th>Créée le</th>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Type</th>
                        <th>Priorité</th>
                        <th>Statut</th>
                        <th>Budget</th>
                        <th>Description</th>
                        <th *hasAnyRole="['ADMIN']" style="width: 260px">Actions</th>
                    </tr>
                </ng-template>
                <ng-template #body let-row>
                    <tr>
                        <td>{{ row.createdAt | date:'dd-MM-yyyy HH:mm' }}</td>
                        <td>{{ getUserNom(row.requesterId) }}</td>
                        <td>{{ getUserPrenom(row.requesterId) }}</td>
                        <td>{{ row.type }}</td>
                        <td><p-tag [value]="row.priority" [severity]="getPrioritySeverity(row.priority)" /></td>
                        <td><p-tag [value]="row.status" [severity]="getStatusSeverity(row.status)" /></td>
                        <td>{{ (row.budgetRequested ?? 0) | number:'1.2-2' }} DT</td>
                        <td>{{ row.description }}</td>
                        <td *hasAnyRole="['ADMIN']" class="flex gap-2">
                            <p-button size="small" label="EN_COURS" severity="warn" (onClick)="updateStatus(row, 'EN_COURS')"></p-button>
                            <p-button size="small" label="REJETEE" severity="danger" (onClick)="updateStatus(row, 'REJETEE')"></p-button>
                            <p-button size="small" label="TERMINEE" severity="success" (onClick)="updateStatus(row, 'TERMINEE')"></p-button>
                        </td>
                    </tr>
                </ng-template>
                <ng-template #emptymessage>
                    <tr *hasAnyRole="['ADMIN']"><td colspan="9">Aucune demande trouvée.</td></tr>
                    <tr *hasAnyRole="['JOUEUR','STAF_MEDICAL','COACH']"><td colspan="8">Aucune demande trouvée.</td></tr>
                </ng-template>
            </p-table>

            <div class="mt-4">Total d'élément(s) : {{ adminRequests.length }}</div>
        </div>

        <p-dialog header="Nouvelle demande" [(visible)]="newDialogVisible" [modal]="true" [draggable]="false" [dismissableMask]="true" [style]="{ width: '36rem' }" [breakpoints]="{ '960px': '75vw', '640px': '95vw' }">
            <form [formGroup]="form" class="grid grid-cols-12 gap-4">
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Type</label>
                    <p-select formControlName="type" [options]="typeOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner" appendTo="body"></p-select>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Priorité</label>
                    <p-select formControlName="priority" [options]="priorityOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner" appendTo="body"></p-select>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Budget demandé</label>
                    <p-inputnumber formControlName="budgetRequested" inputId="budget" mode="currency" currency="TND" locale="fr-TN"></p-inputnumber>
                </div>
                <div class="col-span-12">
                    <label class="block mb-2">Description (optionnel)</label>
                    <input pInputText formControlName="description" placeholder="Optionnel" />
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
export class DemandesAdministratives implements OnInit {
    newDialogVisible: boolean = false;
    form: FormGroup;
    adminRequests: AdminRequestDto[] = [];
    private isAdmin: boolean = false;
    private currentUserId: number | null = null;
    private usersIndex: Map<number, { nom: string; prenom: string }> = new Map();

    typeOptions = [
        { label: 'CONGE', value: 'CONGE' },
        { label: 'ABSENCE', value: 'ABSENCE' },
        { label: 'MATERIEL', value: 'MATERIEL' },
        { label: 'TRANSPORT', value: 'TRANSPORT' },
        { label: 'HEBERGEMENT', value: 'HEBERGEMENT' },
        { label: 'BUDGET', value: 'BUDGET' },
        { label: 'FORMATION', value: 'FORMATION' },
        { label: 'EVENEMENT', value: 'EVENEMENT' },
        { label: 'PARTENARIAT', value: 'PARTENARIAT' },
        { label: 'AUTRE', value: 'AUTRE' }
    ] as { label: RequestType; value: RequestType }[];

    priorityOptions = [
        { label: 'BASSE', value: 'BASSE' },
        { label: 'NORMALE', value: 'NORMALE' },
        { label: 'HAUTE', value: 'HAUTE' },
        { label: 'URGENTE', value: 'URGENTE' },
        { label: 'CRITIQUE', value: 'CRITIQUE' }
    ] as { label: RequestPriority; value: RequestPriority }[];

    constructor(private fb: FormBuilder, private adminApi: AdminRequestsApiService, private auth: AuthService) {
        this.form = this.fb.group({
            type: [null, Validators.required],
            priority: [null, Validators.required],
            budgetRequested: [0, [Validators.min(0)]],
            description: ['']
        });
    }

    // Resolve requester name
    getUserNom(userId: number): string {
        return this.usersIndex.get(userId)?.nom || '—';
    }
    getUserPrenom(userId: number): string {
        return this.usersIndex.get(userId)?.prenom || '—';
    }

    ngOnInit(): void {
        const user = this.auth.getCurrentUser();
        this.currentUserId = user?.id ?? null;
        this.isAdmin = this.auth.hasRole('ADMIN');
        this.loadData();
    }

    private loadData() {
        // Load requests and users in parallel, build index, then filter rows
        forkJoin({
            requests: this.adminApi.getAll(),
            users: this.auth.getUsers()
        }).subscribe({
            next: ({ requests, users }) => {
                const rows = requests || [];
                const us = users || [];
                this.usersIndex = new Map(us.map(u => [u.id, { nom: u.nom, prenom: u.prenom }]));

                if (!this.isAdmin && this.currentUserId != null) {
                    this.adminRequests = rows.filter(r => r.requesterId === this.currentUserId);
                } else if (!this.isAdmin) {
                    this.adminRequests = [];
                } else {
                    this.adminRequests = rows;
                }
            },
            error: (err) => console.error('Erreur chargement demandes/users:', err)
        });
    }

    openNew() {
        this.form.reset({ type: null, priority: null, budgetRequested: 0, description: '' });
        this.newDialogVisible = true;
    }

    closeNew() {
        this.newDialogVisible = false;
    }

    submitNew() {
        if (this.form.invalid) return;
        const v = this.form.value as { type: RequestType; priority: RequestPriority; budgetRequested?: number; description?: string };
        this.adminApi.create(v).subscribe({
            next: (created) => {
                // Optionally move to EN_ATTENTE right after creation
                this.adminApi.setStatus(created.id, 'EN_ATTENTE').subscribe({
                    next: () => {
                        this.newDialogVisible = false;
                        this.loadData();
                    },
                    error: () => {
                        this.newDialogVisible = false;
                        this.loadData();
                    }
                });
            },
            error: (err) => console.error('Erreur création demande admin:', err)
        });
    }

    updateStatus(row: AdminRequestDto, statut: RequestStatus) {
        this.adminApi.setStatus(row.id, statut).subscribe({
            next: () => this.loadData(),
            error: (err) => console.error('Erreur MAJ statut:', err)
        });
    }

    getStatusSeverity(statut: RequestStatus) {
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

    getPrioritySeverity(priorite: RequestPriority) {
        switch (priorite) {
            case 'BASSE':
                return 'success';
            case 'NORMALE':
                return 'info';
            case 'HAUTE':
            case 'URGENTE':
                return 'warn';
            case 'CRITIQUE':
                return 'danger';
            default:
                return 'info';
        }
    }
}


