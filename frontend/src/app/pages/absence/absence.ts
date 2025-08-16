import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { AttendanceService, AbsenceRecord, PresenceRecord } from '../../services/attendance.service';

type AbsenceStatus = 'En attente' | 'Approuvée' | 'Refusée';

interface AbsenceRow {
    id: number;
    membre: string; // Nom Prénom
    role: string;
    dateDebut: string; // dd-mm-yyyy
    dateFin: string; // dd-mm-yyyy
    type: 'Maladie' | 'Blessure' | 'Personnel' | 'éducation' | 'Autre';
    justification?: string;
    statut: AbsenceStatus;
}

@Component({
    selector: 'app-absence',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, DatePickerModule, SelectModule, TagModule],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <div class="text-2xl font-semibold">Absences</div>
            </div>

            <p-table [value]="rows" [rows]="10" [paginator]="true" dataKey="id">
                <ng-template #header>
                    <tr>
                        <th>Membre</th>
                        <th>Rôle</th>
                        <th>date</th>
                        <th>heure</th>
                        <th>Type</th>
                        <th>Statut</th>
                    </tr>
                </ng-template>
                <ng-template #body let-row let-i="rowIndex">
                    <tr class="cursor-pointer" (click)="toggleActionsIndex(i)">
                        <td>{{ row.membre }}</td>
                        <td>{{ row.role }}</td>
                        <td>{{ row.dateDebut }}</td>
                        <td>{{ row.dateFin }}</td>
                        <td>{{ row.type }}</td>
                        <td><p-tag [value]="row.statut" [severity]="getStatusSeverity(row.statut)" /></td>
                    </tr>
                    <ng-container *ngIf="expandedRowIndex === i">
                        <tr>
                            <td colspan="6">
                                <div class="flex gap-2 justify-center py-2">
                                    <p-button size="small" label="Approuver" icon="pi pi-check" severity="success" (onClick)="setStatus(row, 'Approuvée'); $event.stopPropagation()" />
                                    <p-button size="small" label="Refuser" icon="pi pi-times" severity="danger" (onClick)="setStatus(row, 'Refusée'); $event.stopPropagation()" />
                                    
                                </div>
                            </td>
                        </tr>
                    </ng-container>
                </ng-template>
                <ng-template #emptymessage>
                    <tr><td colspan="6">Aucune absence enregistrée.</td></tr>
                </ng-template>
            </p-table>

            <p-dialog header="Nouvelle absence" [(visible)]="addDialogVisible" [modal]="true" [style]="{ width: '40rem' }" [draggable]="false">
                <form [formGroup]="addForm" class="grid grid-cols-12 gap-4">
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Nom</label><input pInputText formControlName="nom" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Prénom</label><input pInputText formControlName="prenom" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Rôle</label><p-select formControlName="role" [options]="roleOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Type</label><p-select formControlName="type" [options]="typeOptions" optionLabel="label" optionValue="value" appendTo="body"></p-select></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date début</label><p-datepicker formControlName="dateDebut" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date fin</label><p-datepicker formControlName="dateFin" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker></div>
                    <div class="col-span-12"><label class="block mb-2">Justification</label><input pInputText formControlName="justification" /></div>
                </form>
                <ng-template #footer>
                    <div class="flex gap-2 justify-end">
                        <p-button label="Annuler" severity="secondary" (onClick)="addDialogVisible=false" />
                        <p-button label="Enregistrer" icon="pi pi-check" [disabled]="addForm.invalid" (onClick)="submitAdd()" />
                    </div>
                </ng-template>
            </p-dialog>

            <p-dialog header="Justification" [(visible)]="justifyDialogVisible" [modal]="true" [style]="{ width: '36rem' }" [draggable]="false">
                <form [formGroup]="justifyForm" class="grid grid-cols-12 gap-4">
                    <div class="col-span-12"><label class="block mb-2">Justification</label><input pInputText formControlName="justification" /></div>
                </form>
                <ng-template #footer>
                    <div class="flex gap-2 justify-end">
                        <p-button label="Fermer" severity="secondary" (onClick)="justifyDialogVisible=false" />
                        <p-button label="Enregistrer" icon="pi pi-check" [disabled]="justifyForm.invalid" (onClick)="submitJustify()" />
                    </div>
                </ng-template>
            </p-dialog>

            <div class="mt-6">
                <div class="text-lg font-semibold mb-2">Présences marquées</div>
                <p-table [value]="presences" [rows]="5" [paginator]="true" dataKey="id">
                    <ng-template #header>
                        <tr><th>Date</th><th>Membre</th><th>Rôle</th><th>Evènement</th></tr>
                    </ng-template>
                    <ng-template #body let-p>
                        <tr><td>{{ p.date }}</td><td>{{ p.membre }}</td><td>{{ p.role }}</td><td>{{ p.evenement }}</td></tr>
                    </ng-template>
                    <ng-template #emptymessage><tr><td colspan="4">Aucune présence.</td></tr></ng-template>
                </p-table>
            </div>
        </div>
    `
})
export class AbsencePage {
    rows: AbsenceRow[] = [
        { id: 1, membre: 'Ahmed Ben Ali', role: 'Joueur', dateDebut: '10-08-2025', dateFin: '12-08-2025', type: 'Maladie', justification: 'Grippe', statut: 'En attente' },
        { id: 2, membre: 'Sana Trabelsi', role: 'Coach', dateDebut: '05-08-2025', dateFin: '05-08-2025', type: 'Personnel', justification: 'Administratif', statut: 'Approuvée' }
    ];

    expandedRowIndex: number | null = null;
    addDialogVisible = false;
    justifyDialogVisible = false;
    addForm!: FormGroup;
    justifyForm!: FormGroup;
    selectedRow: AbsenceRow | null = null;

    roleOptions = [
        { label: 'Joueur', value: 'Joueur' },
        { label: 'Coach', value: 'Coach' },
        { label: 'Staff médical', value: 'Staff médical' },
        { label: 'Responsable financier', value: 'Responsable financier' },
        { label: 'Invité', value: 'Invité' }
    ];

    typeOptions = [
        { label: 'Maladie', value: 'Maladie' },
        { label: 'Blessure', value: 'Blessure' },
        { label: 'Personnel', value: 'Personnel' },
        { label: 'éducation', value: 'éducation' },
        { label: 'Autre', value: 'Autre' }
    ];

    presences: PresenceRecord[] = [];

    constructor(private fb: FormBuilder, private attendance: AttendanceService) {
        this.addForm = this.fb.group({
            nom: ['', Validators.required],
            prenom: ['', Validators.required],
            role: [null, Validators.required],
            type: ['Maladie', Validators.required],
            dateDebut: [new Date(), Validators.required],
            dateFin: [new Date(), Validators.required],
            justification: ['']
        });
        this.justifyForm = this.fb.group({ justification: ['', Validators.required] });
        this.attendance.presences$.subscribe((p) => (this.presences = p));
        this.attendance.absences$.subscribe((list) => {
            // Merge new absences coming from calendar
            if (list.length) {
                // naive merge: show service absences first
                const existingIds = new Set(this.rows.map((r) => r.id));
                const mapped: AbsenceRow[] = list.map((a) => ({
                    id: (this.rows[0]?.id || 0) + 1,
                    membre: a.membre,
                    role: a.role,
                    dateDebut: a.dateDebut,
                    dateFin: a.dateFin,
                    type: a.type as any,
                    justification: a.justification,
                    statut: a.statut
                }));
                // Simple prepend (avoid duplicates in real impl.)
                this.rows = [...mapped, ...this.rows];
            }
        });
    }

    getStatusSeverity(s: AbsenceStatus): 'info' | 'success' | 'danger' | 'warn' {
        switch (s) {
            case 'Approuvée':
                return 'success';
            case 'Refusée':
                return 'danger';
            default:
                return 'warn';
        }
    }

    toggleActionsIndex(idx: number) {
        this.expandedRowIndex = this.expandedRowIndex === idx ? null : idx;
    }

    setStatus(row: AbsenceRow, s: AbsenceStatus) {
        row.statut = s;
        this.expandedRowIndex = null;
    }

    openAdd() {
        this.addForm.reset({ nom: '', prenom: '', role: null, type: 'Maladie', dateDebut: new Date(), dateFin: new Date(), justification: '' });
        this.addDialogVisible = true;
    }

    submitAdd() {
        if (this.addForm.invalid) return;
        const v = this.addForm.value as any;
        const formatDate = (d: Date) => `${String(d.getDate()).padStart(2, '0')}-${String(d.getMonth() + 1).padStart(2, '0')}-${d.getFullYear()}`;
        const nextId = (this.rows[0]?.id || 0) + 1;
        const newRow: AbsenceRow = {
            id: nextId,
            membre: `${v.nom} ${v.prenom}`.trim(),
            role: v.role,
            dateDebut: formatDate(v.dateDebut),
            dateFin: formatDate(v.dateFin),
            type: v.type,
            justification: v.justification || '',
            statut: 'En attente'
        };
        this.rows = [newRow, ...this.rows];
        this.addDialogVisible = false;
    }

    openJustify(row: AbsenceRow) {
        this.selectedRow = row;
        this.justifyForm.reset({ justification: row.justification || '' });
        this.justifyDialogVisible = true;
    }

    submitJustify() {
        if (this.justifyForm.invalid || !this.selectedRow) return;
        this.selectedRow.justification = this.justifyForm.value.justification;
        this.justifyDialogVisible = false;
        this.selectedRow = null;
    }
}


