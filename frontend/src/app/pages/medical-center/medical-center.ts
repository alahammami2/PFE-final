import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
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
import { HasAnyRoleDirective } from '../../directives/has-any-role.directive';
import { BodyMapComponent } from './body-map';
import { MedicalApiService, HealthRecordDto, MedicalRendezvousDto, MedicalStatus } from '../../services/medical.service';

interface InjuryRow {
    id: number;
    playerId: number; // needed for backend updates
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
    playerId: number; // needed for backend updates
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
        BodyMapComponent,
        HasAnyRoleDirective
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
                            <p-button *hasAnyRole="['JOUEUR','STAF_MEDICAL']" label="Nouvelle blessure" icon="pi pi-plus" (onClick)="openAdd()" />
                        </div>
                        <p-table [value]="currentInjuries" [rows]="10" [paginator]="true" dataKey="id" [rowHover]="true" [tableStyle]="{ 'min-width': '80rem' }">
                            <ng-template #header>
                                <tr>
                                    <th pSortableColumn="joueur" style="width: 18rem">Joueur <p-sortIcon field="joueur"></p-sortIcon></th>
                                    <th style="width: 18rem">Blessure/Maladie</th>
                                    <th style="width: 14rem">Statut physique</th>
                                    <th style="width: 14rem">Date de blessure</th>
                                    <th pSortableColumn="status" style="width: 12rem">Statut <p-sortIcon field="status"></p-sortIcon></th>
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
                                                <ng-container *hasAnyRole="['STAF_MEDICAL']">
                                                    <p-button size="small" label="Rendez-vous" icon="pi pi-calendar" (onClick)="openAppointment(row); $event.stopPropagation()"></p-button>
                                                    <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="editInjury(row); $event.stopPropagation()"></p-button>
                                                    <p-button size="small" label="Rétabli" icon="pi pi-check" severity="success" (onClick)="markRecovered(row); $event.stopPropagation()"></p-button>
                                                </ng-container>
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
                                                <ng-container *hasAnyRole="['STAF_MEDICAL']">
                                                    <p-button size="small" label="Supprimer" icon="pi pi-trash" severity="danger" (onClick)="deleteAppointment(row); $event.stopPropagation()"></p-button>
                                                    <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="editAppointment(row); $event.stopPropagation()"></p-button>
                                                </ng-container>
                                                <p-button *hasAnyRole="['JOUEUR']" size="small" label="Confirmé" icon="pi pi-check" severity="success" (onClick)="confirmAppointment(row); $event.stopPropagation()"></p-button>
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
export class MedicalCenterPage implements OnInit {
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
    currentInjuries: InjuryRow[] = [];

    historyInjuries: HistoryRow[] = [
        { id: 10, joueur: 'Mehdi Jlassi', type: 'Contracture', gravite: 'Légère', date: '10-04-2025', indisponibilite: '5 jours', statut: 'Rétabli' }
    ];

    appointments: AppointmentRow[] = [];

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

    // Chart data for résumé de la saison (single dataset: Hommes)
    injuryChartData = {
        labels: ['Genou', 'Mollet', 'Cuisse', 'Épaule', 'Tête', 'Abdomen'],
        datasets: [
            {
                label: 'Hommes',
                backgroundColor: '#22d3ee', // cyan-400
                borderColor: '#06b6d4',
                data: [0, 0, 0, 0, 0, 0]
            }
        ]
    } as any;

    ngOnInit(): void {
        this.loadHealthRecords();
        this.loadRendezvous();
    }

    private loadHealthRecords() {
        this.medicalApi.getHealthRecords().subscribe({
            next: (list: HealthRecordDto[]) => {
                const rows = (list || []).map((r) => this.mapHealthRecordToInjuryRow(r));
                // Sort by date desc if available
                this.currentInjuries = rows.sort((a, b) => {
                    const da = this.tryParseDate(a.dateBlessure)?.getTime() || 0;
                    const db = this.tryParseDate(b.dateBlessure)?.getTime() || 0;
                    return db - da;
                });
                this.updateInjuryChartFromRows();
            },
            error: (err) => console.error('Erreur chargement dossiers santé:', err)
        });
    }

    private updateInjuryChartFromRows() {
        const labels = ['Genou', 'Mollet', 'Cuisse', 'Épaule', 'Tête', 'Abdomen'];
        const counts = labels.map(() => 0);
        for (const r of this.currentInjuries) {
            const z = (r.zone || '').toLowerCase();
            if (z.includes('genou')) counts[0]++;
            else if (z.includes('mollet')) counts[1]++;
            else if (z.includes('cuisse') || z.includes('quadriceps')) counts[2]++;
            else if (z.includes('épaule') || z.includes('epaule') || z.includes('bras')) counts[3]++;
            else if (z.includes('tête') || z.includes('tete') || z.includes('crâne') || z.includes('crane')) counts[4]++;
            else if (z.includes('abdomen')) counts[5]++;
        }
        this.injuryChartData = {
            labels,
            datasets: [
                {
                    label: 'Hommes',
                    backgroundColor: '#22d3ee',
                    borderColor: '#06b6d4',
                    data: counts
                }
            ]
        } as any;
    }

    private loadRendezvous() {
        this.medicalApi.listRendezvous().subscribe({
            next: (list: MedicalRendezvousDto[]) => {
                const rows = (list || []).map((rv) => this.mapRendezvousToAppointmentRow(rv));
                this.appointments = rows.sort((a, b) => {
                    const da = this.tryParseDateTime(a.datetime)?.getTime() || 0;
                    const db = this.tryParseDateTime(b.datetime)?.getTime() || 0;
                    return db - da;
                });
            },
            error: (err) => console.error('Erreur chargement rendez-vous:', err)
        });
    }

    private mapHealthRecordToInjuryRow(r: HealthRecordDto): InjuryRow {
        const statusLabel = this.statusEnumToLabel(r.status);
        const dateStr = r.blessureDate ? this.isoDateToDisplay(r.blessureDate) : '-';
        return {
            id: r.id,
            playerId: r.playerId as unknown as number,
            joueur: r.playerName,
            blessure: r.blessureType || '-',
            zone: r.statutPhysique || '-',
            dateBlessure: dateStr,
            status: statusLabel
        } as InjuryRow;
    }

    private mapRendezvousToAppointmentRow(rv: MedicalRendezvousDto): AppointmentRow {
        return {
            id: rv.id,
            playerId: rv.playerId as unknown as number,
            joueur: rv.playerName,
            lieu: rv.lieu || '-',
            datetime: this.isoDateTimeToDisplay(rv.rendezvousDatetime),
            priorite: (rv.priority as any) || 'Normale',
            note: rv.notes || '',
            statut: this.rvStatusToLabel(rv.status)
        } as AppointmentRow;
    }

    private statusEnumToLabel(s: MedicalStatus): InjuryRow['status'] {
        switch (s) {
            case 'RETABLI':
                return 'Rétabli';
            case 'REPOS':
                return 'Repos';
            default:
                return 'En suivi';
        }
    }

    private rvStatusToLabel(s?: string | null): AppointmentRow['statut'] {
        switch ((s || '').toUpperCase()) {
            case 'CONFIRMED':
                return 'Confirmé';
            case 'CANCELLED':
                return 'Annulé';
            default:
                return 'En attente';
        }
    }

    private isoDateToDisplay(iso: string): string {
        // iso yyyy-MM-dd
        const [y, m, d] = iso.split('T')[0].split('-').map((x) => parseInt(x, 10));
        return `${String(d).padStart(2, '0')}-${String(m).padStart(2, '0')}-${y}`;
    }

    private isoDateTimeToDisplay(iso: string): string {
        // expect yyyy-MM-ddTHH:mm[:ss]
        const [date, time] = iso.split('T');
        const [y, m, d] = date.split('-').map((x) => parseInt(x, 10));
        const [hh, mi] = (time || '00:00').split(':');
        return `${String(d).padStart(2, '0')}-${String(m).padStart(2, '0')}-${y} ${hh}:${mi}`;
    }

    private tryParseDate(s: string): Date | null {
        try {
            return this.parseDate(s);
        } catch {
            return null;
        }
    }

    private tryParseDateTime(s: string): Date | null {
        // format dd-mm-yyyy HH:MM
        try {
            const [dpart, tpart] = s.split(' ');
            const [dd, mm, yyyy] = dpart.split('-').map((x) => parseInt(x, 10));
            const [hh = 0, mi = 0] = (tpart || '0:0').split(':').map((x) => parseInt(x, 10));
            const d = new Date(yyyy, mm - 1, dd, hh, mi, 0, 0);
            return isNaN(d.getTime()) ? null : d;
        } catch {
            return null;
        }
    }

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

    constructor(private fb: FormBuilder, private medicalApi: MedicalApiService) {
        this.addForm = this.fb.group({
            nom: ['', Validators.required],
            prenom: ['', Validators.required],
            dateBlessure: [new Date(), Validators.required],
            blessure: ['']
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
        this.medicalApi.setHealthStatus(row.id, 'RETABLI').subscribe({
            next: () => {
                this.expandedRowId = null;
                this.loadHealthRecords();
            },
            error: (err) => console.error('Erreur MAJ statut santé:', err)
        });
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
        const isoDate = `${v.dateBlessure.getFullYear()}-${String(v.dateBlessure.getMonth()+1).padStart(2,'0')}-${String(v.dateBlessure.getDate()).padStart(2,'0')}`;
        const playerName = `${v.nom} ${v.prenom}`.trim();
        // TEMP: playerId placeholder until player linking is implemented
        const payload = {
            playerId: 1,
            playerName,
            blessureType: v.blessure || null,
            blessureDate: isoDate,
            statutPhysique: this.labelForZone(this.selectedZones[0])
        };
        this.medicalApi.createHealthRecord(payload).subscribe({
            next: () => {
                this.addDialogVisible = false;
                this.loadHealthRecords();
            },
            error: (err) => console.error('Erreur création dossier santé:', err)
        });
    }
    submitAppointment() {
        if (this.appointmentForm.invalid || !this.selectedRow) return;
        const v = this.appointmentForm.value as any;
        const d: Date = v.date;
        const t: Date = v.time;
        const iso = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}T${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}:00`;
        if (this.editingAppointmentId) {
            this.medicalApi.updateRendezvous(this.editingAppointmentId, { playerId: this.selectedRow.playerId, playerName: this.selectedRow.joueur, lieu: v.lieu, rendezvousDatetime: iso, priority: v.priorite, notes: v.note || '' }).subscribe({
                next: () => {
                    this.afterAppointmentSubmit();
                },
                error: (err) => console.error('Erreur modification rendez-vous:', err)
            });
        } else {
            // TEMP playerId placeholder
            this.medicalApi.createRendezvous({ playerId: 1, playerName: this.selectedRow.joueur, rendezvousDatetime: iso, lieu: v.lieu, priority: v.priorite, notes: v.note || '' }).subscribe({
                next: () => {
                    this.afterAppointmentSubmit();
                },
                error: (err) => console.error('Erreur création rendez-vous:', err)
            });
        }
    }

    private afterAppointmentSubmit() {
        this.appointmentDialogVisible = false;
        this.injuryAppointmentDialogVisible = false;
        this.editingAppointmentId = null;
        this.loadRendezvous();
    }

    submitEdit() {
        if (this.editForm.invalid || !this.selectedRow) return;
        const v = this.editForm.value as any;
        const isoDate = `${v.dateBlessure.getFullYear()}-${String(v.dateBlessure.getMonth()+1).padStart(2,'0')}-${String(v.dateBlessure.getDate()).padStart(2,'0')}`;
        const statusMap: Record<string, MedicalStatus> = { 'En suivi': 'EN_SUIVI', 'Repos': 'REPOS', 'Rétabli': 'RETABLI' };
        const updatePayload = {
            playerId: this.selectedRow.playerId,
            playerName: `${v.nom} ${v.prenom}`.trim(),
            blessureType: v.blessure,
            blessureDate: isoDate,
            statutPhysique: this.selectedZones.length ? this.labelForZone(this.selectedZones[0]) : this.selectedRow.zone,
            status: statusMap[v.status] || 'EN_SUIVI'
        };
        this.medicalApi.updateHealthRecord(this.selectedRow.id, updatePayload).subscribe({
            next: () => {
                this.editDialogVisible = false;
                this.expandedRowIndex = null;
                this.loadHealthRecords();
            },
            error: (err) => console.error('Erreur modification dossier santé:', err)
        });
    }

    // Appointments actions
    toggleAppointmentActionsIndex(idx: number) {
        this.appointmentsExpandedRowIndex = this.appointmentsExpandedRowIndex === idx ? null : idx;
    }

    deleteAppointment(row: AppointmentRow) {
        this.medicalApi.deleteRendezvous(row.id).subscribe({
            next: () => {
                this.appointmentsExpandedRowIndex = null;
                this.loadRendezvous();
            },
            error: (err) => console.error('Erreur suppression rendez-vous:', err)
        });
    }

    confirmAppointment(row: AppointmentRow) {
        this.medicalApi.confirmRendezvous(row.id).subscribe({
            next: () => {
                this.appointmentsExpandedRowIndex = null;
                this.loadRendezvous();
            },
            error: (err) => console.error('Erreur confirmation rendez-vous:', err)
        });
    }

    editAppointment(row: AppointmentRow) {
        const [dd, mm, yyyyAndTime] = row.datetime.split('-');
        const [yyyy, time] = yyyyAndTime.split(' ');
        const [hh = '0', mi = '0'] = (time || '').split(':');
        const d = new Date(parseInt(yyyy, 10), parseInt(mm, 10) - 1, parseInt(dd, 10));
        const t = new Date();
        t.setHours(parseInt(hh, 10), parseInt(mi, 10), 0, 0);
        this.appointmentForm.reset({ date: d, time: t, priorite: row.priorite, lieu: row.lieu, note: row.note });
        this.selectedRow = { id: 0, playerId: row.playerId, joueur: row.joueur, blessure: '', zone: '', dateBlessure: this.formatDate(new Date()), status: 'En suivi' } as any;
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


