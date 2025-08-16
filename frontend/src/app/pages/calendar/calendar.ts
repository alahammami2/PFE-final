import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { SelectModule } from 'primeng/select';
import { DialogModule } from 'primeng/dialog';
import { DatePickerModule } from 'primeng/datepicker';
import { InputTextModule } from 'primeng/inputtext';
import { AttendanceService } from '../../services/attendance.service';

type EventType = 'Entraînement' | 'Match amical' | 'Match entre nous' | 'visionnage' | 'Réunion' | 'Championnats' | 'Match coupe'  ;

interface CalendarEvent {
    date: string; // YYYY-MM-DD
    title: string;
    type: EventType;
    team?: string;
    time?: string; // HH:mm
    lieu?: string; // Lieu de l'événement
    dateDebut?: string; // YYYY-MM-DD HH:mm:ss
    dateFin?: string; // YYYY-MM-DD HH:mm:ss
}

@Component({
    selector: 'app-calendrier',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, ButtonModule, CheckboxModule, SelectModule, DialogModule, InputTextModule, DatePickerModule],
    template: `
        <div class="grid grid-cols-12 gap-6">
            <!-- Left Filters -->
            <div class="hidden lg:block col-span-12 md:col-span-4 lg:col-span-3 xl:col-span-3">
                <div class="card">
                    <div class="flex items-center justify-between mb-4">
                        <div class="font-semibold text-xl">Affiner par :</div>
                        <p-button icon="pi pi-sliders-h" [text]="true"></p-button>
                    </div>
                    <div class="font-medium mb-3">Évènements</div>
                    <div class="flex flex-col gap-3 mb-6">
                        <label class="flex items-center gap-2" *ngFor="let t of eventTypes">
                            <p-checkbox [(ngModel)]="filters[t]" [binary]="true"></p-checkbox>
                            <span>{{ t }}</span>
                        </label>
                    </div>

                    <div class="font-medium mb-3">Équipes</div>
                    <div class="flex flex-col gap-3">
                        <label class="flex items-center gap-2" *ngFor="let team of teams">
                            <p-checkbox [(ngModel)]="teamFilters[team]" [binary]="true"></p-checkbox>
                            <span>{{ team }}</span>
                        </label>
                    </div>
                </div>
            </div>

            <!-- Calendar Section -->
            <div class="col-span-12 md:col-span-8 lg:col-span-9 xl:col-span-9">
                <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
                    <div class="flex items-center gap-2">
                        <p-button label="Créer un événement" icon="pi pi-plus" severity="success" (onClick)="createNewEvent()"></p-button>
                        <p-button icon="pi pi-print" [text]="true" (onClick)="print()"></p-button>
                    </div>
                    <!-- Success message -->
                    <div *ngIf="showSuccessMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-2 rounded">
                        {{ successMessage }}
                    </div>
                    <div class="flex items-center gap-2">
                        <p-select [(ngModel)]="selectedSeason" [options]="seasons" optionLabel="label" optionValue="value" placeholder="Saison" styleClass="w-48"></p-select>
                    </div>
                </div>

                <div class="card">
                    <!-- Calendar header -->
                    <div class="flex flex-wrap items-center justify-between mb-4 gap-2">
                        <div class="font-semibold text-xl">{{ headerLabel }}</div>
                        <div class="flex items-center gap-2">
                            <p-button icon="pi pi-angle-left" [text]="true" (onClick)="prev()"></p-button>
                            <p-button label="Aujourd'hui" [text]="true" (onClick)="goToday()"></p-button>
                            <p-button icon="pi pi-angle-right" [text]="true" (onClick)="next()"></p-button>
                            <span class="mx-2 border-l border-surface h-6"></span>
                            <p-button label="Semaine" [text]="viewMode!=='week'" [raised]="viewMode==='week'" (onClick)="setView('week')"></p-button>
                            <p-button label="Mois" [text]="viewMode!=='month'" [raised]="viewMode==='month'" (onClick)="setView('month')"></p-button>
                        </div>
                    </div>

                    <!-- Month header -->
                    <div *ngIf="viewMode==='month'" class="grid grid-cols-7 gap-2 mb-2 text-center text-sm font-medium text-muted-color">
                        <div *ngFor="let wd of weekdays">{{ wd }}</div>
                    </div>

                    <!-- Month grid -->
                    <div *ngIf="viewMode==='month'" class="grid grid-cols-7 gap-2">
                        <div *ngFor="let day of days" class="min-h-28 border border-surface rounded-md p-2">
                            <div class="text-right text-sm text-muted-color mb-2">{{ day.dayNumber }}</div>
                            <ng-container *ngFor="let ev of getEvents(day.iso)">
                                <div class="text-white text-xs rounded-full px-2 py-1 mb-1 truncate cursor-pointer" [ngClass]="getEventColor(ev.type)" (click)="openEvent(ev)">
                                    {{ ev.type }} <span class="text-white" *ngIf="ev.team">- {{ ev.team }}</span>
                                </div>
                            </ng-container>
                        </div>
                    </div>

                    <!-- Week grid -->
                    <div *ngIf="viewMode==='week'">
                        <div class="grid grid-cols-7 gap-2 mb-2 text-center text-sm font-medium text-muted-color">
                            <div *ngFor="let d of weekDays">{{ d.label }}</div>
                        </div>
                        <div class="grid grid-cols-7 gap-2">
                            <div *ngFor="let d of weekDays" class="min-h-28 border border-surface rounded-md p-2">
                                <div class="text-right text-sm text-muted-color mb-2">{{ d.date.getDate() }}</div>
                                <ng-container *ngFor="let ev of getEvents(iso(d.date.getFullYear(), d.date.getMonth(), d.date.getDate()))">
                                    <div class="text-white text-xs rounded-full px-2 py-1 mb-1 truncate cursor-pointer" [ngClass]="getEventColor(ev.type)" (click)="openEvent(ev)">
                                        {{ ev.type }} <span class="text-white" *ngIf="ev.team">- {{ ev.team }}</span>
                                    </div>
                                </ng-container>
                            </div>
                            </div>
                        </div>
                    </div>


                </div>
            </div>

        <!-- Event Dialog -->
        <p-dialog [(visible)]="dialogVisible" [modal]="true" [style]="{ width: '720px', height: '700px' }" [draggable]="false" [resizable]="false" [contentStyle]="{ overflow: 'visible' }" (onHide)="isEditMode=false">
            <ng-template pTemplate="header">
                <div class="text-2xl font-semibold">Bonjour ALA,</div>
            </ng-template>
            <div *ngIf="selectedEvent as ev" class="flex flex-col gap-4">
                <div>
                    <div class="font-semibold text-lg mb-2">Es-tu disponible pour cet évènement ?</div>
                    <div class="text-base">Tu es convoqué(e) pour {{ ev.title || ev.type }} du {{ formatDateShort(ev.date) }}.</div>
                    <div class="text-base" *ngIf="ev.time">Le rendez-vous est prévu à {{ ev.time }}.</div>
                </div>

                <div class="flex justify-center gap-3 mt-4">
                    <p-button label="Présent(e)" severity="success" styleClass="px-5 py-3 text-base" [outlined]="availability!=='present'" (onClick)="setAvailability('present')"></p-button>
                    <p-button label="Absent(e)" severity="danger" styleClass="px-5 py-3 text-base" [outlined]="availability!=='absent'" (onClick)="setAvailability('absent')"></p-button>
                </div>

                <div class="p-5 rounded-md border border-surface bg-surface-50">
                    <div class="font-medium text-lg mb-3">Récapitulatif :</div>
                    <div class="text-base mb-2"><span class="text-muted-color">Quoi  :   </span> <span class="font-medium"> {{ ev.title }}</span></div>
                    <div class="text-base mb-2"><span class="text-muted-color">Quand  :   </span> <span class="font-medium">{{ formatDate(ev.date) }}<span *ngIf="ev.time"> à {{ ev.time }}</span></span></div>
                    <div class="text-base mb-2"><span class="text-muted-color">Lieu  :   </span> <span class="font-medium">salle issa ben nasr</span></div>
                    <div class="text-base" *ngIf="ev.team"><span class="text-muted-color">Équipe :</span> <span class="font-medium">{{ ev.team }}</span></div>
                </div>

                <!-- Edition simple -->
                <div *ngIf="isEditMode" class="grid grid-cols-12 gap-3">
                    <div class="col-span-12">
                        <label class="block text-sm mb-1">Titre</label>
                        <input class="p-inputtext w-full" [(ngModel)]="editedEvent!.title" />
                    </div>
                    <div class="col-span-12">
                        <label class="block text-sm mb-1">Type</label>
                        <select class="p-inputtext w-full" [(ngModel)]="editedEvent!.type">
                            <option *ngFor="let t of eventTypes" [ngValue]="t">{{ t }}</option>
                        </select>
                    </div>
                    <div class="col-span-4">
                        <label class="block text-sm mb-1">Date</label>
                        <input type="date" class="p-inputtext w-full" [(ngModel)]="editedEvent!.date" />
                    </div>
                    <div class="col-span-4">
                        <label class="block text-sm mb-1">Heure</label>
                        <p-datepicker [timeOnly]="true" hourFormat="24" appendTo="body" [(ngModel)]="timeModel" (ngModelChange)="onTimeChange($event)"></p-datepicker>
                    </div>
                    <div class="col-span-4">
                        <label class="block text-sm mb-1">Équipe</label>
                        <select class="p-inputtext w-full" [(ngModel)]="editedEvent!.team">
                            <option [ngValue]="undefined">—</option>
                            <option *ngFor="let tm of teams" [ngValue]="tm" [disabled]="tm==='Toutes les équipes'">{{ tm }}</option>
                        </select>
                    </div>
                </div>
            </div>

            <ng-template pTemplate="footer">
                <div class="flex items-center justify-center w-full gap-3 flex-wrap">
                    <div class="flex gap-2">
                        <p-button label="Modifier" icon="pi pi-pencil" (onClick)="startEdit()"></p-button>
                        <p-button label="Supprimer" icon="pi pi-trash" severity="danger" (onClick)="deleteSelected()"></p-button>
                    </div>
                    <div class="flex gap-2" *ngIf="isEditMode">
                        <p-button label="Annuler" text (onClick)="isEditMode=false"></p-button>
                        <p-button label="Enregistrer" icon="pi pi-check" (onClick)="saveSelected()"></p-button>
                    </div>
                </div>
            </ng-template>
        </p-dialog>

        <!-- Create new event dialog -->
        <p-dialog header="Créer un nouvel événement" [(visible)]="createEventDialogVisible" [modal]="true" [style]="{ width: '720px' }" [draggable]="false" [resizable]="false">
            <div class="grid grid-cols-12 gap-4">
                <div class="col-span-12">
                    <label class="block text-sm mb-1">Titre de l'événement (optionnel)</label>
                    <input class="p-inputtext w-full" [(ngModel)]="newEvent.title" placeholder="Ex: Entraînement technique" />
                </div>
                <div class="col-span-12">
                    <label class="block text-sm mb-1">Type d'événement</label>
                    <select class="p-inputtext w-full" [(ngModel)]="newEvent.type">
                        <option *ngFor="let t of eventTypes" [ngValue]="t">{{ t }}</option>
                    </select>
                </div>
                <div class="col-span-12">
                    <label class="block text-sm mb-1">Lieu</label>
                    <input class="p-inputtext w-full" [(ngModel)]="newEvent.lieu" placeholder="Ex: Salle de sport" />
                </div>
                <div class="col-span-6">
                    <label class="block text-sm mb-1">Date de début</label>
                    <input type="date" class="p-inputtext w-full" [(ngModel)]="newEventDate" />
                </div>
                <div class="col-span-6">
                    <label class="block text-sm mb-1">Heure de début</label>
                    <p-datepicker [(ngModel)]="newEventTimeDebut" [timeOnly]="true" hourFormat="24" appendTo="body"></p-datepicker>
                </div>
                <div class="col-span-6">
                    <label class="block text-sm mb-1">Heure de fin</label>
                    <p-datepicker [(ngModel)]="newEventTimeFin" [timeOnly]="true" hourFormat="24" appendTo="body"></p-datepicker>
                </div>
                <div class="col-span-12">
                    <label class="block text-sm mb-1">Équipe (optionnel)</label>
                    <select class="p-inputtext w-full" [(ngModel)]="newEvent.team">
                        <option [ngValue]="undefined">—</option>
                        <option *ngFor="let tm of teams" [ngValue]="tm" [disabled]="tm==='Toutes les équipes'">{{ tm }}</option>
                    </select>
                </div>
            </div>
            <ng-template pTemplate="footer">
                <div class="flex gap-2 justify-end">
                    <p-button label="Annuler" severity="secondary" (onClick)="createEventDialogVisible=false"></p-button>
                    <p-button label="Créer" icon="pi pi-check" [disabled]="!newEvent.type" (onClick)="submitNewEvent()"></p-button>
                </div>
            </ng-template>
        </p-dialog>

        <!-- Absence details dialog -->
        <p-dialog header="Marquer comme absent(e)" [(visible)]="absenceDialogVisible" [modal]="true" [style]="{ width: '520px' }" [draggable]="false">
            <form [formGroup]="absenceForm" class="grid grid-cols-12 gap-4">
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Type d'absence</label>
                    <p-select formControlName="type" [options]="absenceTypeOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                </div>
                <div class="col-span-12">
                    <label class="block mb-2">Justification</label>
                    <input pInputText formControlName="justification" placeholder="Ex: raison personnelle" />
                </div>
            </form>
            <ng-template pTemplate="footer">
                <div class="flex gap-2 justify-end">
                    <p-button label="Annuler" severity="secondary" (onClick)="absenceDialogVisible=false"></p-button>
                    <p-button label="Confirmer" icon="pi pi-check" [disabled]="absenceForm.invalid || !selectedEvent" (onClick)="submitAbsence()"></p-button>
                </div>
            </ng-template>
        </p-dialog>
    `
})
export class CalendarPage {
    // header + navigation
    cursorDate = new Date();
    viewMode: 'week' | 'month' = 'month';

    weekdays = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
    days: { iso: string; dayNumber: number }[] = [];
    weekDays: { date: Date; label: string }[] = [];

    seasons = [
        { label: 'Saison 2025-2025', value: '2025-2026' }
    ];
    selectedSeason: string | null = this.seasons[0].value;

    eventTypes: EventType[] = [
        'Entraînement',
        'Match amical',
        'Championnats',
        'Match coupe',
        'Réunion',
        'visionnage'
    ];

    teams: string[] = ['Toutes les équipes', 'Séniors Homme', 'Séniors Fille'];

    filters: Record<EventType, boolean> = {
        'Entraînement': true,
        'Match amical': true,
        'Match entre nous': true,
        'Réunion': true,
        'visionnage': true,
        'Championnats': true,
        'Match coupe': true
    };

    teamFilters: Record<string, boolean> = {
        'Toutes les équipes': true,
        'Séniors Homme': true,
        'Séniors Fille': true
    };

    events: CalendarEvent[] = [
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 1), title: 'Match amical', type: 'Match amical', team: 'Séniors Homme' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 1), title: 'Match de coupe', type: 'Match coupe', team: 'Séniors Fille' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 6), title: 'Entraînement', type: 'Entraînement', team: 'Séniors Homme' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 7), title: 'Entraînement', type: 'Entraînement', team: 'Séniors Fille' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 14), title: 'Match amical', type: 'Match amical', team: 'Séniors Homme' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 21), title: 'Entraînement', type: 'Entraînement', team: 'Séniors Fille' },
        { date: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 28), title: 'Championnats', type: 'Championnats', team: 'Séniors Homme' }
    ];



    constructor(private attendance: AttendanceService, private fb: FormBuilder) {
        this.buildMonth();
        this.buildWeek();
        this.absenceForm = this.fb.group({
            type: ['Personnel', Validators.required],
            justification: ['']
        });
    }

    get monthLabel(): string {
        return new Date(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 1).toLocaleDateString('fr-FR', { month: 'long' }).replace(/^./, (s) => s.toUpperCase());
    }

    get headerLabel(): string {
        if (this.viewMode === 'week') {
            const start = this.getWeekStart(this.cursorDate);
            const end = new Date(start);
            end.setDate(start.getDate() + 6);
            return `Semaine du ${start.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long' })} au ${end.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })}`;
        }
        return `${this.monthLabel} ${this.cursorDate.getFullYear()}`;
    }

    buildMonth() {
        const first = new Date(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), 1);
        const startWeekday = (first.getDay() + 6) % 7; // make Monday first (0)
        const daysInMonth = new Date(this.cursorDate.getFullYear(), this.cursorDate.getMonth() + 1, 0).getDate();
        const cells: { iso: string; dayNumber: number }[] = [];

        // leading blanks from previous month
        for (let i = 0; i < startWeekday; i++) {
            cells.push({ iso: '', dayNumber: 0 });
        }
        for (let d = 1; d <= daysInMonth; d++) {
            cells.push({ iso: this.iso(this.cursorDate.getFullYear(), this.cursorDate.getMonth(), d), dayNumber: d });
        }
        // ensure full 6 weeks grid (42 cells)
        while (cells.length < 42) {
            cells.push({ iso: '', dayNumber: 0 });
        }
        this.days = cells;
    }

    buildWeek() {
        const start = this.getWeekStart(this.cursorDate);
        this.weekDays = Array.from({ length: 7 }).map((_, i) => {
            const d = new Date(start);
            d.setDate(start.getDate() + i);
            return { date: d, label: this.weekdays[i] };
        });
    }

    getEvents(iso: string): CalendarEvent[] {
        if (!iso) return [];
        return this.events.filter((e) => e.date === iso && this.filters[e.type] && this.isTeamVisible(e.team));
    }

    isTeamVisible(team?: string | undefined): boolean {
        if (!team) return true;
        if (this.teamFilters['Toutes les équipes']) return true;
        return !!this.teamFilters[team];
    }

    getEventColor(type: EventType): string {
        switch (type) {
            case 'Entraînement':
                return 'bg-emerald-500';
            case 'Match amical':
                return 'bg-pink-500';
            case 'Match entre nous':
                return 'bg-orange-400';
            case 'Réunion':
                return 'bg-cyan-500';
            case 'visionnage':
                return 'bg-indigo-500';
            case 'Championnats':
                return 'bg-blue-500';
            case 'Match coupe':
                return 'bg-purple-500';
            default:
                return 'bg-gray-500';
        }
    }

    prev() {
        if (this.viewMode === 'week') {
            this.cursorDate.setDate(this.cursorDate.getDate() - 7);
            this.buildWeek();
        } else {
            this.cursorDate.setMonth(this.cursorDate.getMonth() - 1);
            this.buildMonth();
        }
        this.buildMonth();
        this.buildWeek();
    }

    next() {
        if (this.viewMode === 'week') {
            this.cursorDate.setDate(this.cursorDate.getDate() + 7);
            this.buildWeek();
        } else {
            this.cursorDate.setMonth(this.cursorDate.getMonth() + 1);
            this.buildMonth();
        }
        this.buildMonth();
        this.buildWeek();
    }

    goToday() {
        const now = new Date();
        this.cursorDate = now;
        this.buildMonth();
        this.buildWeek();
    }

    iso(year: number, month: number, day: number): string {
        const m = (month + 1).toString().padStart(2, '0');
        const d = day.toString().padStart(2, '0');
        return `${year}-${m}-${d}`;
    }

    private getWeekStart(date: Date): Date {
        const d = new Date(date);
        const day = (d.getDay() + 6) % 7; // 0=Mon
        d.setDate(d.getDate() - day);
        d.setHours(0, 0, 0, 0);
        return d;
    }

    setView(mode: 'week' | 'month') {
        this.viewMode = mode;
        this.buildWeek();
        this.buildMonth();
    }

    print() {
        window.print();
    }

    // Dialog state & actions
    dialogVisible = false;
    selectedEvent: CalendarEvent | null = null;
    selectedIndex: number | null = null;
    isEditMode = false;
    editedEvent: CalendarEvent | null = null;
    availability: 'present' | 'absent' | null = null;
    absenceDialogVisible = false;
    absenceForm!: FormGroup;
    timeModel: Date = new Date();
    
    // New event creation
    createEventDialogVisible = false;
    newEvent: CalendarEvent = {
        date: '',
        title: '',
        type: 'Entraînement',
        team: undefined,
        time: undefined,
        lieu: undefined,
        dateDebut: undefined,
        dateFin: undefined
    };
    
    // Date picker models for new event
    newEventDate: string = '';
    newEventTimeDebut: Date | null = null;
    newEventTimeFin: Date | null = null;
    
    // Success message
    showSuccessMessage = false;
    successMessage = '';
    absenceTypeOptions = [
        { label: 'Maladie', value: 'Maladie' },
        { label: 'Blessure', value: 'Blessure' },
        { label: 'Personnel', value: 'Personnel' },
        { label: 'éducation', value: 'éducation' },
        { label: 'Autre', value: 'Autre' }
    ];

    openEvent(ev: CalendarEvent) {
        this.selectedIndex = this.events.indexOf(ev);
        this.selectedEvent = ev;
        this.availability = null;
        this.isEditMode = false;
        this.dialogVisible = true;
    }
    setAvailability(v: 'present' | 'absent') {
        this.availability = v;
        if (!this.selectedEvent) return;
        // Simple mocked member/role for demo
        const membre = 'Joueur Exemple';
        const role = 'Joueur';
        const d = this.selectedEvent.date;
        const dd = d.slice(8, 10);
        const mm = d.slice(5, 7);
        const yyyy = d.slice(0, 4);
        const ddmmyyyy = `${dd}-${mm}-${yyyy}`;
        if (v === 'present') {
            this.attendance.addPresence({ membre, role, date: ddmmyyyy, evenement: this.selectedEvent.title });
        } else {
            // open absence details dialog
            this.absenceForm.reset({ type: 'Personnel', justification: '' });
            this.absenceDialogVisible = true;
        }
    }

    submitAbsence() {
        if (!this.selectedEvent || this.absenceForm.invalid) return;
        const d = this.selectedEvent.date;
        const dd = d.slice(8, 10);
        const mm = d.slice(5, 7);
        const yyyy = d.slice(0, 4);
        const ddmmyyyy = `${dd}-${mm}-${yyyy}`;
        const membre = 'Joueur Exemple';
        const role = 'Joueur';
        const v = this.absenceForm.value as any;
        this.attendance.addAbsence({ membre, role, dateDebut: ddmmyyyy, dateFin: ddmmyyyy, type: v.type, justification: v.justification, statut: 'En attente' });
        this.absenceDialogVisible = false;
    }

    startEdit() {
        try {
            if (!this.selectedEvent) return;
            this.isEditMode = true;
            this.editedEvent = { ...this.selectedEvent };
            
            // Ensure time is always set to prevent null issues
            if (!this.editedEvent.time || this.editedEvent.time.trim() === '') {
                this.editedEvent.time = '09:00';
            }
            
            // Initialize timeModel for the picker
            this.timeModel = this.timeToDate(this.editedEvent.time) || new Date();
            
            // Validate that editedEvent is properly initialized
            if (!this.editedEvent.title || !this.editedEvent.type || !this.editedEvent.date) {
                console.error('Invalid editedEvent state:', this.editedEvent);
                return;
            }
            
            console.log('Edit mode started with event:', this.editedEvent);
            console.log('Time model initialized to:', this.timeModel);
        } catch (error) {
            console.error('Error in startEdit:', error);
            this.isEditMode = false;
            this.editedEvent = null;
        }
    }

    saveSelected() {
        if (this.selectedIndex == null || !this.editedEvent) return;
        this.events[this.selectedIndex] = { ...this.editedEvent };
        this.selectedEvent = this.events[this.selectedIndex];
        this.isEditMode = false;
    }

    deleteSelected() {
        if (this.selectedIndex == null) return;
        this.events.splice(this.selectedIndex, 1);
        this.dialogVisible = false;
        this.selectedEvent = null;
        this.selectedIndex = null;
        this.isEditMode = false;
    }

    formatDate(iso: string): string {
        const d = new Date(iso);
        return d.toLocaleString('fr-FR', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' });
    }

    formatDateShort(iso: string): string {
        const d = new Date(iso);
        return d.toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' });
    }

    // Helpers to convert HH:mm <-> Date for time-only picker
    timeToDate(time?: string): Date | null {
        if (!time) return null;
        const [hh, mm] = time.split(':').map((x) => parseInt(x, 10));
        const d = new Date();
        d.setHours(hh || 0, mm || 0, 0, 0);
        return d;
    }

    getDefaultTime(): Date {
        return new Date();
    }

    getTimeForPicker(): Date {
        try {
            if (!this.editedEvent) {
                return new Date();
            }
            if (!this.editedEvent.time) {
                return new Date();
            }
            const time = this.timeToDate(this.editedEvent.time);
            if (time && time instanceof Date && !isNaN(time.getTime())) {
                return time;
            }
            return new Date();
        } catch (error) {
            console.error('Error in getTimeForPicker:', error);
            return new Date();
        }
    }

    onTimeChange(date: Date) {
        try {
            if (this.editedEvent && date && date instanceof Date && !isNaN(date.getTime())) {
                this.editedEvent.time = this.dateToTime(date);
                console.log('Time changed to:', this.editedEvent.time);
            }
        } catch (error) {
            console.error('Error in onTimeChange:', error);
        }
    }



    // New event creation methods
    createNewEvent() {
        this.createEventDialogVisible = true;
        this.newEvent = {
            date: '',
            title: '',
            type: 'Entraînement',
            team: undefined,
            time: undefined,
            lieu: undefined,
            dateDebut: undefined,
            dateFin: undefined
        };
        this.timeModel = new Date();
        // Initialiser avec la date d'aujourd'hui par défaut
        const today = new Date();
        this.newEventDate = this.iso(today.getFullYear(), today.getMonth(), today.getDate());
        this.newEventTimeDebut = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 9, 0, 0);
        this.newEventTimeFin = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 10, 0, 0);
    }

    submitNewEvent() {
        if (!this.newEvent.type || !this.newEventDate) return; // Type et date sont obligatoires
        
        // Mettre à jour l'événement avec les valeurs du formulaire
        this.newEvent.date = this.newEventDate;
        this.newEvent.time = this.dateToTime(this.newEventTimeDebut);
        
        // Si pas de titre, utiliser le type comme titre
        if (!this.newEvent.title) {
            this.newEvent.title = this.newEvent.type;
        }
        
        // Ajouter l'événement à la liste
        this.events.push({ ...this.newEvent });
        
        // Fermer le dialogue et réinitialiser le formulaire
        this.createEventDialogVisible = false;
        this.newEvent = {
            date: '',
            title: '',
            type: 'Entraînement',
            team: undefined,
            time: undefined,
            lieu: undefined,
            dateDebut: undefined,
            dateFin: undefined
        };
        
        // Réinitialiser les sélecteurs
        this.newEventDate = '';
        this.newEventTimeDebut = null;
        this.newEventTimeFin = null;
        
        // Actualiser les vues du calendrier
        this.buildMonth();
        this.buildWeek();
        
        // Afficher le message de succès
        this.successMessage = `Événement "${this.newEvent.type}" créé avec succès !`;
        this.showSuccessMessage = true;
        
        // Masquer le message après 3 secondes
        setTimeout(() => {
            this.showSuccessMessage = false;
        }, 3000);
        
        console.log('Nouvel événement créé et ajouté au calendrier:', this.newEvent);
        console.log('Total des événements dans le calendrier:', this.events.length);
    }





    // Helper method to format date for database (timestamp format)
    formatDateTimeForDatabase(date: Date): string {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }

    dateToTime(d?: Date | null): string | undefined {
        if (!d) return undefined;
        const hh = String(d.getHours()).padStart(2, '0');
        const mm = String(d.getMinutes()).padStart(2, '0');
        return `${hh}:${mm}`;
    }
}


