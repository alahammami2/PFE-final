import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TabsModule } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { AvatarModule } from 'primeng/avatar';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { ChartModule } from 'primeng/chart';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BodyMapComponent } from './body-map';

interface InjuryRow {
    id: number;
    joueur: string;
    blessure: string;
    zone: string; // ex: Genou (gauche)
    dateBlessure: string;
    status: 'En suivi' | 'Rétabli' | 'Repos';
}

interface HistoryRow {
    id: number;
    joueur: string;
    type: string;
    gravite: 'Légère' | 'Moyenne' | 'Sévère';
    date: string;
    indisponibilite: string;
    statut: 'Rétabli' | 'En suivi';
}

interface AppointmentRow {
    id: number;
    joueur: string;
    lieu: string;
    datetime: string; // 01/09/2025 14:30
    priorite: 'Normale' | 'Haute' | 'Urgente';
    note: string;
    statut: 'En attente' | 'Confirmé' | 'Annulé';
}

@Component({
    selector: 'app-medical-center',
    standalone: true,
    imports: [
        CommonModule,
        TabsModule,
        TableModule,
        ButtonModule,
        TagModule,
        AvatarModule,
        DialogModule,
        InputTextModule,
        DatePickerModule,
        SelectModule,
        ChartModule,
        ReactiveFormsModule,
        BodyMapComponent
    ],
    template: `
        <div class="card">
            <p-tabs value="0">
                <p-tablist>
                    <p-tab value="0"> les Blessures </p-tab>
                    <p-tab value="1">Planification des rendez-vous</p-tab>
                    <p-tab value="2">Résumé de la saison</p-tab>
                </p-tablist>
                <p-tabpanels>
                    <p-tabpanel value="0">
                        <div class="flex items-center justify-between mb-3">
                            <div class="text-lg font-semibold">Blessures en cours</div>
                            <p-button label="Nouvelle blessure" icon="pi pi-plus" (onClick)="openAdd()" />
                        </div>
                        <p-table [value]="currentInjuries" [rows]="10" [paginator]="true" dataKey="id" [rowHover]="true" [tableStyle]="{ 'min-width': '80rem' }">
                            <ng-template #header>
                                <tr>
                                    <th style="width: 18rem">Joueur</th>
                                    <th style="width: 18rem">Blessure/Maladie</th>
                                    <th style="width: 14rem">Statut physique</th>
                                    <th style="width: 14rem">Date de blessure</th>
                                    <th style="width: 12rem">Statut</th>
                                </tr>
                            </ng-template>
                            <ng-template #body let-row let-i="rowIndex">
                                <tr class="cursor-pointer" (click)="toggleActionsIndex(i)">
                                    <td><span class="font-medium">{{ row.joueur }}</span></td>
                                    <td>{{ row.blessure }}</td>
                                    <td>
                                        <div class="flex items-center gap-3">
                                            <app-body-map [highlightedZones]="zoneToHighlights(row.zone)" [width]="40" [height]="80"></app-body-map>
                                            <p-tag [value]="row.zone" severity="danger"></p-tag>
                                        </div>
                                    </td>
                                    <td>{{ row.dateBlessure }}</td>
                                    <td><p-tag [value]="row.status" [severity]="row.status==='Rétabli' ? 'success' : row.status==='Repos' ? 'info' : 'warn'"></p-tag></td>
                                </tr>
                                <ng-container *ngIf="expandedRowIndex === i">
                                    <tr>
                                        <td colspan="5">
                                            <div class="flex gap-2 justify-center py-2">
                                                <p-button size="small" label="Rendez-vous" icon="pi pi-calendar" (onClick)="openAppointment(row); $event.stopPropagation()"></p-button>
                                                <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="editInjury(row); $event.stopPropagation()"></p-button>
                                                <p-button size="small" label="Rétabli" icon="pi pi-check" severity="success" (onClick)="markRecovered(row); $event.stopPropagation()"></p-button>
                                            </div>
                                        </td>
                                    </tr>
                                </ng-container>
                            </ng-template>
                            <ng-template #emptymessage>
                                <tr><td colspan="6">Aucune blessure en cours.</td></tr>
                            </ng-template>
                        </p-table>

                        <p-dialog header="Modifier blessure" [(visible)]="editDialogVisible" [modal]="true" [style]="{ width: '52rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                            <form [formGroup]="editForm" class="grid grid-cols-12 gap-4">
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Nom du joueur</label>
                                    <input pInputText formControlName="nom" />
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Prénom du joueur</label>
                                    <input pInputText formControlName="prenom" />
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Date de blessure</label>
                                    <p-datepicker formControlName="dateBlessure" [showIcon]="true" [showButtonBar]="true" dateFormat="dd-mm-yy"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Blessure/Maladie</label>
                                    <input pInputText formControlName="blessure" />
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Statut</label>
                                    <p-select formControlName="status" [options]="statusOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                                </div>
                                <div class="col-span-12">
                                    <label class="block mb-2">Statut physique (zone)</label>
                                    <div class="flex items-center gap-4">
                                        <app-body-map [highlightedZones]="selectedZones" [clickable]="true" (zoneToggle)="toggleZone($event)" [width]="80" [height]="160"></app-body-map>
                                        <p-tag *ngFor="let z of selectedZones" [value]="labelForZone(z)" severity="danger" class="mr-2"></p-tag>
                                    </div>
                                </div>
                            </form>
                            <ng-template #footer>
                                <div class="flex gap-2 justify-end">
                                    <p-button label="Annuler" severity="secondary" (onClick)="editDialogVisible=false" />
                                    <p-button label="Enregistrer" icon="pi pi-check" [disabled]="editForm.invalid" (onClick)="submitEdit()" />
                                </div>
                            </ng-template>
                        </p-dialog>
                        <p-dialog header="Nouvelle blessure" [(visible)]="addDialogVisible" [modal]="true" [style]="{ width: '52rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                            <form [formGroup]="addForm" class="grid grid-cols-12 gap-4">
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Nom du joueur</label>
                                    <input pInputText formControlName="nom" />
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Prénom du joueur</label>
                                    <input pInputText formControlName="prenom" />
                                </div>

                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Date de blessure</label>
                                    <p-datepicker formControlName="dateBlessure" [showIcon]="true" [showButtonBar]="true" dateFormat="dd-mm-yy"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Blessure/Maladie</label>
                                    <input pInputText formControlName="blessure" placeholder="Ex: Entorse, Déchirure..." />
                                </div>

                                <div class="col-span-12">
                                    <label class="block mb-2">Statut physique (zone)</label>
                                    <div class="flex items-center gap-4">
                                        <app-body-map [highlightedZones]="selectedZones" [clickable]="true" (zoneToggle)="toggleZone($event)" [width]="80" [height]="160"></app-body-map>
                                        <p-tag *ngFor="let z of selectedZones" [value]="labelForZone(z)" severity="danger" class="mr-2"></p-tag>
                                    </div>
                                </div>
                            </form>
                            <ng-template #footer>
                                <div class="flex gap-2 justify-end">
                                    <p-button label="Annuler" severity="secondary" (onClick)="addDialogVisible=false" />
                                    <p-button label="Enregistrer" icon="pi pi-check" [disabled]="addForm.invalid || !selectedZones.length" (onClick)="submitAdd()" />
                                </div>
                            </ng-template>
                        </p-dialog>

                        <!-- Appointment dialog specific to "les Blessures" tab -->
                        <p-dialog [header]="appointmentDialogHeader" [(visible)]="injuryAppointmentDialogVisible" [modal]="true" [style]="{ width: '40rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                            <form [formGroup]="appointmentForm" class="grid grid-cols-12 gap-4">
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Date</label>
                                    <p-datepicker formControlName="date" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Heure</label>
                                    <p-datepicker formControlName="time" [timeOnly]="true" hourFormat="24"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Priorité</label>
                                    <p-select formControlName="priorite" [options]="priorityOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Lieu</label>
                                    <input pInputText formControlName="lieu" />
                                </div>
                                <div class="col-span-12">
                                    <label class="block mb-2">Note</label>
                                    <input pInputText formControlName="note" />
                                </div>
                            </form>
                            <ng-template #footer>
                                <div class="flex gap-2 justify-end">
                                    <p-button label="Annuler" severity="secondary" (onClick)="injuryAppointmentDialogVisible=false" />
                                    <p-button [label]="editingAppointmentId ? 'Enregistrer' : 'Planifier'" icon="pi pi-check" [disabled]="appointmentForm.invalid" (onClick)="submitAppointment()" />
                                </div>
                            </ng-template>
                        </p-dialog>
                        
                    </p-tabpanel>

                    <p-tabpanel value="1">
                        <div class="flex items-center justify-between mb-3">
                            <div class="text-lg font-semibold">Planification des rendez-vous</div>
                        </div>
                        <!-- Appointment dialog lives in this tab so it opens here -->
                        <p-dialog [header]="appointmentDialogHeader" [(visible)]="appointmentDialogVisible" [modal]="true" [style]="{ width: '40rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                            <form [formGroup]="appointmentForm" class="grid grid-cols-12 gap-4">
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Date</label>
                                    <p-datepicker formControlName="date" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Heure</label>
                                    <p-datepicker formControlName="time" [timeOnly]="true" hourFormat="24"></p-datepicker>
                                </div>
                                <div class="col-span-12 md:col-span-4">
                                    <label class="block mb-2">Priorité</label>
                                    <p-select formControlName="priorite" [options]="priorityOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                                </div>
                                <div class="col-span-12 md:col-span-6">
                                    <label class="block mb-2">Lieu</label>
                                    <input pInputText formControlName="lieu" />
                                </div>
                                <div class="col-span-12">
                                    <label class="block mb-2">Note</label>
                                    <input pInputText formControlName="note" />
                                </div>
                            </form>
                            <ng-template #footer>
                                <div class="flex gap-2 justify-end">
                                    <p-button label="Annuler" severity="secondary" (onClick)="appointmentDialogVisible=false" />
                                    <p-button [label]="editingAppointmentId ? 'Enregistrer' : 'Planifier'" icon="pi pi-check" [disabled]="appointmentForm.invalid" (onClick)="submitAppointment()" />
                                </div>
                            </ng-template>
                        </p-dialog>
                        <p-table [value]="appointments" [rows]="10" [paginator]="true" dataKey="id">
                            <ng-template #header>
                                <tr>
                                    <th>Joueur</th>
                                    <th>Lieu</th>
                                    <th>Date & Heure</th>
                                    <th>Priorité</th>
                                    <th>Note</th>
                                    <th>Statut</th>
                                </tr>
                            </ng-template>
                            <ng-template #body let-row let-i="rowIndex">
                                <tr class="cursor-pointer" (click)="toggleAppointmentActionsIndex(i)">
                                    <td>{{ row.joueur }}</td>
                                    <td>{{ row.lieu }}</td>
                                    <td>{{ row.datetime }}</td>
                                    <td><p-tag [value]="row.priorite" [severity]="row.priorite==='Urgente' ? 'danger' : row.priorite==='Haute' ? 'warn' : 'info'"/></td>
                                    <td>{{ row.note }}</td>
                                    <td><p-tag [value]="row.statut" [severity]="row.statut==='Confirmé' ? 'success' : row.statut==='Annulé' ? 'secondary' : 'info'"/></td>
                                </tr>
                                <ng-container *ngIf="appointmentsExpandedRowIndex === i">
                                    <tr>
                                        <td colspan="6">
                                            <div class="flex gap-2 justify-center py-2">
                                                <p-button size="small" label="Supprimer" icon="pi pi-trash" severity="danger" (onClick)="deleteAppointment(row); $event.stopPropagation()"></p-button>
                                                <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="editAppointment(row); $event.stopPropagation()"></p-button>
                                                <p-button size="small" label="Confirmé" icon="pi pi-check" severity="success" (onClick)="confirmAppointment(row); $event.stopPropagation()"></p-button>
                                            </div>
                                        </td>
                                    </tr>
                                </ng-container>
                            </ng-template>
                            <ng-template #emptymessage>
                                <tr><td colspan="6">Aucun rendez-vous planifié.</td></tr>
                            </ng-template>
                        </p-table>
                    </p-tabpanel>

                    <p-tabpanel value="2">
                        <div class="text-lg font-semibold mb-3">Résumé de la saison</div>
                        <div class="card">
                            <p-chart type="bar" [data]="injuryChartData" [options]="injuryChartOptions"></p-chart>
                        </div>
                    </p-tabpanel>
                </p-tabpanels>
            </p-tabs>
        </div>
    `
})
export class MedicalCenterPage {
    expandedRowId: number | null = null; // legacy
    expandedRowIndex: number | null = null;
    appointmentsExpandedRowIndex: number | null = null;
    editingAppointmentId: number | null = null;
    appointmentDialogHeader = 'Nouveau rendez-vous';
    addDialogVisible = false;
    addForm!: FormGroup;
    selectedZones: string[] = [];
    // Appointment dialog states: one for injuries tab, one for planning tab
    appointmentDialogVisible = false; // used in planning tab
    injuryAppointmentDialogVisible = false; // used in injuries tab
    appointmentForm!: FormGroup;
    editDialogVisible = false;
    editForm!: FormGroup;
    selectedRow: InjuryRow | null = null;
    currentInjuries: InjuryRow[] = [
        {
            id: 1,
            joueur: 'Jurrien Timber',
            blessure: 'Entorse des ligaments croisés',
            zone: 'Genou (gauche)',
            dateBlessure: '11-05-2024',
            status: 'En suivi'
        },
        {
            id: 2,
            joueur: 'Oleksandr Zinchenko',
            blessure: 'Déchirure au mollet',
            zone: 'Mollet (gauche)',
            dateBlessure: '23-07-2023',
            status: 'En suivi'
        },
        {
            id: 3,
            joueur: 'Mohamed Elneny',
            blessure: 'Entorse des ligaments croisés',
            zone: 'Genou (gauche)',
            dateBlessure: '18-07-2023',
            status: 'Repos'
        }
    ];

    historyInjuries: HistoryRow[] = [
        { id: 10, joueur: 'Mehdi Jlassi', type: 'Contracture', gravite: 'Légère', date: '10-04-2025', indisponibilite: '5 jours', statut: 'Rétabli' }
    ];

    appointments: AppointmentRow[] = [
        { id: 1, joueur: 'Jurrien Timber', lieu: 'Clinique Sportive', datetime: '12-05-2024 10:00', priorite: 'Haute', note: 'Contrôle genou', statut: 'En attente' },
        { id: 2, joueur: 'Oleksandr Zinchenko', lieu: 'Hôpital Central', datetime: '01-08-2023 15:30', priorite: 'Normale', note: 'Échographie mollet', statut: 'Confirmé' }
    ];

    priorityOptions = [
        { label: 'Normale', value: 'Normale' },
        { label: 'Haute', value: 'Haute' },
        { label: 'Urgente', value: 'Urgente' }
    ];

    statusOptions = [
        { label: 'En suivi', value: 'En suivi' },
        { label: 'Repos', value: 'Repos' },
        { label: 'Rétabli', value: 'Rétabli' }
    ];

    // Chart data for résumé de la saison
    injuryChartData = {
        labels: ['Genou', 'Mollet', 'Cuisse', 'Épaule', 'Tête', 'Abdomen'],
        datasets: [
            {
                label: 'Hommes',
                backgroundColor: '#22d3ee', // cyan-400
                borderColor: '#06b6d4',
                data: [5, 3, 4, 2, 1, 2]
            },
            {
                label: 'Femmes',
                backgroundColor: '#fb7185', // rose-400
                borderColor: '#f43f5e',
                data: [2, 2, 3, 1, 0, 1]
            }
        ]
    } as any;

    injuryChartOptions = {
        maintainAspectRatio: false,
        responsive: true,
        plugins: {
            legend: {
                position: 'top' as const
            },
            tooltip: { enabled: true }
        },
        scales: {
            x: {
                title: { display: true, text: 'Type de blessure' }
            },
            y: {
                beginAtZero: true,
                title: { display: true, text: 'Nombre de joueurs' },
                ticks: { stepSize: 1 }
            }
        }
    } as any;

    constructor(private fb: FormBuilder) {
        this.addForm = this.fb.group({
            nom: ['', Validators.required],
            prenom: ['', Validators.required],
            dateBlessure: [new Date(), Validators.required],
            blessure: ['', Validators.required]
        });
        this.appointmentForm = this.fb.group({ date: [new Date(), Validators.required], time: [new Date(), Validators.required], priorite: ['Normale', Validators.required], lieu: ['', Validators.required], note: [''] });
        this.editForm = this.fb.group({ nom: ['', Validators.required], prenom: ['', Validators.required], dateBlessure: [new Date(), Validators.required], blessure: ['', Validators.required], status: ['En suivi', Validators.required] });
    }

    get recoveredCount(): number {
        return this.historyInjuries.filter((i) => i.statut === 'Rétabli').length;
    }

    getSeverity(g: HistoryRow['gravite']) {
        switch (g) {
            case 'Légère':
                return 'info';
            case 'Moyenne':
                return 'warn';
            case 'Sévère':
                return 'danger';
            default:
                return 'info';
        }
    }

    zoneToHighlights(zone: string): string[] {
        const z = zone.toLowerCase();
        if (z.includes('genou')) {
            return z.includes('(gauche)') ? ['left-knee'] : z.includes('(droit') ? ['right-knee'] : ['left-knee', 'right-knee'];
        }
        if (z.includes('mollet')) {
            return z.includes('(gauche)') ? ['left-calf'] : z.includes('(droit') ? ['right-calf'] : ['left-calf', 'right-calf'];
        }
        if (z.includes('quadriceps') || z.includes('cuisse')) {
            return z.includes('(gauche)') ? ['left-quad'] : z.includes('(droit') ? ['right-quad'] : ['left-quad', 'right-quad'];
        }
        if (z.includes('tête') || z.includes('crâne')) {
            return ['head'];
        }
        if (z.includes('épaule') || z.includes('bras')) {
            return z.includes('(gauche)') ? ['left-arm'] : z.includes('(droit') ? ['right-arm'] : ['left-arm', 'right-arm'];
        }
        if (z.includes('thorax') || z.includes('poitrine')) {
            return ['chest'];
        }
        if (z.includes('abdomen')) {
            return ['abdomen'];
        }
        return [];
    }

    toggleActions(row: InjuryRow) {
        this.expandedRowId = this.expandedRowId === row.id ? null : row.id;
    }

    toggleActionsIndex(idx: number) {
        this.expandedRowIndex = this.expandedRowIndex === idx ? null : idx;
    }

    openAppointment(row: InjuryRow) {
        this.selectedRow = row;
        this.appointmentForm.reset({ date: new Date(), time: new Date(), priorite: 'Normale', lieu: '', note: '' });
        this.editingAppointmentId = null;
        this.appointmentDialogHeader = 'Nouveau rendez-vous';
        this.injuryAppointmentDialogVisible = true; // open in injuries tab
    }

    editInjury(row: InjuryRow) {
        this.selectedRow = row;
        const [first, ...rest] = row.joueur.split(' ');
        const prenom = rest.join(' ');
        this.editForm.reset({
            nom: first || row.joueur,
            prenom: prenom || '',
            dateBlessure: this.parseDate(row.dateBlessure),
            blessure: row.blessure,
            status: row.status
        });
        this.selectedZones = this.zoneToHighlights(row.zone);
        this.editDialogVisible = true;
    }

    markRecovered(row: InjuryRow) {
        row.status = 'Rétabli';
        this.expandedRowId = null;
    }

    openAdd() {
        this.addForm.reset({ nom: '', prenom: '', dateBlessure: new Date(), blessure: '' });
        this.selectedZones = [];
        this.addDialogVisible = true;
    }

    toggleZone(zone: string) {
        this.selectedZones = this.selectedZones.includes(zone)
            ? this.selectedZones.filter((z) => z !== zone)
            : [...this.selectedZones, zone];
    }

    labelForZone(z: string) {
        const map: Record<string, string> = {
            'left-knee': 'Genou (gauche)',
            'right-knee': 'Genou (droit)',
            'left-calf': 'Mollet (gauche)',
            'right-calf': 'Mollet (droit)',
            'left-quad': 'Cuisse (gauche)',
            'right-quad': 'Cuisse (droite)',
            head: 'Tête',
            chest: 'Thorax',
            abdomen: 'Abdomen',
            'left-arm': 'Bras (gauche)',
            'right-arm': 'Bras (droit)'
        };
        return map[z] || z;
    }

    submitAdd() {
        if (this.addForm.invalid || !this.selectedZones.length) return;
        const v = this.addForm.value as any;
        const formatDate = (d: Date) => `${String(d.getDate()).padStart(2, '0')}-${String(d.getMonth() + 1).padStart(2, '0')}-${d.getFullYear()}`;
        const nextId = (this.currentInjuries[0]?.id || 0) + 1;
        const newRow: InjuryRow = {
            id: nextId,
            joueur: `${v.nom} ${v.prenom}`.trim(),
            blessure: v.blessure || '—',
            zone: this.labelForZone(this.selectedZones[0]),
            dateBlessure: formatDate(v.dateBlessure),
            status: 'En suivi',
        };
        
        this.currentInjuries = [newRow, ...this.currentInjuries];
        this.addDialogVisible = false;
    }
    submitAppointment() {
        if (this.appointmentForm.invalid || !this.selectedRow) return;
        const v = this.appointmentForm.value as any;
        const d: Date = v.date;
        const t: Date = v.time;
        const combined = new Date(d.getFullYear(), d.getMonth(), d.getDate(), t.getHours(), t.getMinutes());
        const formatted = `${String(combined.getDate()).padStart(2, '0')}-${String(combined.getMonth() + 1).padStart(2, '0')}-${combined.getFullYear()} ${String(combined.getHours()).padStart(2, '0')}:${String(combined.getMinutes()).padStart(2, '0')}`;
        if (this.editingAppointmentId) {
            this.appointments = this.appointments.map(a => a.id === this.editingAppointmentId ? { ...a, lieu: v.lieu, datetime: formatted, priorite: v.priorite, note: v.note || '' } : a);
        } else {
            const nextId = (this.appointments[0]?.id || 0) + 1;
            this.appointments = [
                { id: nextId, joueur: this.selectedRow.joueur, lieu: v.lieu, datetime: formatted, priorite: v.priorite, note: v.note || '', statut: 'En attente' },
                ...this.appointments
            ];
        }
        this.appointmentDialogVisible = false;
        this.injuryAppointmentDialogVisible = false;
        this.editingAppointmentId = null;
    }

    submitEdit() {
        if (this.editForm.invalid || !this.selectedRow) return;
        const v = this.editForm.value as any;
        const updated: InjuryRow = {
            ...this.selectedRow,
            joueur: `${v.nom} ${v.prenom}`.trim(),
            blessure: v.blessure,
            dateBlessure: this.formatDate(v.dateBlessure),
            zone: this.selectedZones.length ? this.labelForZone(this.selectedZones[0]) : this.selectedRow.zone,
            status: v.status
        };
        this.currentInjuries = this.currentInjuries.map((r) => (r.id === this.selectedRow!.id ? updated : r));
        this.editDialogVisible = false;
        this.expandedRowIndex = null;
    }

    // Appointments actions
    toggleAppointmentActionsIndex(idx: number) {
        this.appointmentsExpandedRowIndex = this.appointmentsExpandedRowIndex === idx ? null : idx;
    }

    deleteAppointment(row: AppointmentRow) {
        this.appointments = this.appointments.filter(a => a.id !== row.id);
        this.appointmentsExpandedRowIndex = null;
    }

    confirmAppointment(row: AppointmentRow) {
        this.appointments = this.appointments.map(a => a.id === row.id ? { ...a, statut: 'Confirmé' } : a);
        this.appointmentsExpandedRowIndex = null;
    }

    editAppointment(row: AppointmentRow) {
        const [dd, mm, yyyyAndTime] = row.datetime.split('-');
        const [yyyy, time] = yyyyAndTime.split(' ');
        const [hh = '0', mi = '0'] = (time || '').split(':');
        const d = new Date(parseInt(yyyy, 10), parseInt(mm, 10) - 1, parseInt(dd, 10));
        const t = new Date();
        t.setHours(parseInt(hh, 10), parseInt(mi, 10), 0, 0);
        this.appointmentForm.reset({ date: d, time: t, priorite: row.priorite, lieu: row.lieu, note: row.note });
        this.selectedRow = { id: 0, joueur: row.joueur, blessure: '', zone: '', dateBlessure: this.formatDate(new Date()), status: 'En suivi' } as any;
        this.editingAppointmentId = row.id;
        this.appointmentDialogHeader = 'Modifier rendez-vous';
        this.appointmentDialogVisible = true;
    }

    parseDate(s: string): Date {
        const [dd, mm, yyyy] = s.includes('/') ? s.split('/') : s.split('-');
        return new Date(parseInt(yyyy, 10), parseInt(mm, 10) - 1, parseInt(dd, 10));
    }

    formatDate(d: Date): string {
        return `${String(d.getDate()).padStart(2, '0')}-${String(d.getMonth() + 1).padStart(2, '0')}-${d.getFullYear()}`;
    }
}


