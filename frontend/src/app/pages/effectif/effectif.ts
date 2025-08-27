import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { Popover, PopoverModule } from 'primeng/popover';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { AuthService } from '../../services/auth.service';
import { HasAnyRoleDirective } from '../../directives/has-any-role.directive';
import { PerformanceService, CreatePlayerRequest, PositionEnum } from '../../services/performance.service';
import { UserResponse } from '../../models/auth.model';
import { forkJoin } from 'rxjs';

type MembreStatut = 'ACTIF' | 'INACTIF' | 'SUSPENDU';

interface MembreRow {
    id: number;
    playerId?: number | null;
    photo?: string | null;
    nom: string;
    prenom: string;
    email?: string | null;
    // Plaintext password if available (creation/reset), never persist it; do NOT show hashes
    passwordPlain?: string | null;
    passwordShown?: boolean;
    telephone?: string | null;
    role?: string | null;
    taille?: string | null; // cm
    poids?: string | null; // kg
    poste?: string | null;
    dateNaissance?: string | null; // dd/mm/yyyy
    numeroMaillot?: string | null;
    salaire?: number | null;
    statut?: MembreStatut;
}

@Component({
    selector: 'app-effectif',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        TableModule,
        InputTextModule,
        ButtonModule,
        IconFieldModule,
        InputIconModule,
        AvatarModule,
        TagModule,
        PopoverModule,
        DialogModule,
        SelectModule,
        InputNumberModule,
        ToggleSwitchModule,
        InputGroupModule,
        InputGroupAddonModule,
        HasAnyRoleDirective
    ],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <div class="text-2xl font-semibold">Effectif</div>
                <div class="flex gap-2">
                    <p-button *hasAnyRole="['ADMIN']" label="Ajouter un membre" icon="pi pi-user-plus" severity="primary" (onClick)="openAdd()" />
                </div>
            </div>

            <div class="flex flex-wrap gap-2 items-center mb-3">
                <div class="flex-1 min-w-64">
                    <p-iconfield>
                        <p-inputicon styleClass="pi pi-search" />
                        <input pInputText type="text" (input)="onGlobalFilter(dt, $event)" placeholder="Rechercher" />
                    </p-iconfield>
                </div>
                <div class="flex gap-2">
                    <p-button type="button" icon="pi pi-eye" label="Afficher" severity="secondary" outlined (click)="columnsOp.toggle($event)" />
                </div>
            </div>

            

            <p-table
                #dt
                [value]="rows"
                dataKey="id"
                [rows]="20"
                [paginator]="true"
                [rowHover]="true"
                [rowsPerPageOptions]="[20, 50, 100]"
                [style]="{ width: 'auto' }"
            >
                <ng-template #header>
                    <tr>
                        <th class="whitespace-nowrap" style="width: 4rem">#</th>
                        <th class="whitespace-nowrap" style="width: 8rem">Photo</th>
                        <th class="whitespace-nowrap" pSortableColumn="nom">Nom <p-sortIcon field="nom" /></th>
                        <th class="whitespace-nowrap" pSortableColumn="prenom">Prénom <p-sortIcon field="prenom" /></th>
                        <th class="whitespace-nowrap" *ngIf="showEmail">Email</th>
                        <ng-container *hasAnyRole="['ADMIN']">
                            <th class="whitespace-nowrap" *ngIf="showPassword">Mot de passe</th>
                        </ng-container>
                        <th class="whitespace-nowrap" *ngIf="showTelephone">Téléphone</th>
                        <th class="whitespace-nowrap" *ngIf="showRole" pSortableColumn="role">Rôle <p-sortIcon field="role" /></th>
                        <th class="whitespace-nowrap" *ngIf="showTaille" pSortableColumn="taille">Taille (cm) <p-sortIcon field="taille" /></th>
                        <th class="whitespace-nowrap" *ngIf="showPoids" pSortableColumn="poids">Poids (kg) <p-sortIcon field="poids" /></th>
                        <th class="whitespace-nowrap" *ngIf="showPoste" pSortableColumn="poste">Poste <p-sortIcon field="poste" /></th>
                        <th class="whitespace-nowrap" *ngIf="showDateNaissance" pSortableColumn="dateNaissance">Date de naissance <p-sortIcon field="dateNaissance" /></th>
                        <th class="whitespace-nowrap" *ngIf="showNumeroMaillot">Numéro maillot</th>
                        <th class="whitespace-nowrap" *ngIf="showSalaire" pSortableColumn="salaire">Salaire <p-sortIcon field="salaire" /></th>
                        <th class="whitespace-nowrap" *ngIf="showStatut" pSortableColumn="statut">Statut <p-sortIcon field="statut" /></th>
                        <th class="whitespace-nowrap" style="width: 8rem">Actions</th>
                    </tr>
                </ng-template>

                <ng-template #body let-row>
                    <tr>
                        <td class="whitespace-nowrap">{{ row.id }}</td>
                        <td class="whitespace-nowrap">
                            <p-avatar [image]="getAvatarImage(row)" shape="circle" size="large" class="mr-2"></p-avatar>
                        </td>
                        <td class="whitespace-nowrap">{{ row.nom || '-' }}</td>
                        <td class="whitespace-nowrap">{{ row.prenom || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showEmail">{{ row.email || '-' }}</td>
                        <ng-container *hasAnyRole="['ADMIN']">
                        <td class="whitespace-nowrap" *ngIf="showPassword">
                            <ng-container *ngIf="row.passwordPlain; else noPwd">
                                <span class="mr-2">{{ row.passwordShown ? row.passwordPlain : '********' }}</span>
                                <p-button [icon]="row.passwordShown ? 'pi pi-eye-slash' : 'pi pi-eye'" text size="small" (onClick)="row.passwordShown = !row.passwordShown" />
                                <p-button icon="pi pi-copy" text size="small" (onClick)="copy(row.passwordPlain!)" />
                            </ng-container>
                            <ng-template #noPwd>-</ng-template>
                        </td>
                        </ng-container>
                        <td class="whitespace-nowrap" *ngIf="showTelephone">{{ row.telephone || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showRole">{{ row.role || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showTaille">{{ row.taille || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showPoids">{{ row.poids || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showPoste">{{ row.poste || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showDateNaissance">{{ row.dateNaissance || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showNumeroMaillot">{{ row.numeroMaillot || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showSalaire">{{ row.salaire != null ? formatDt(row.salaire) : '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showStatut">
                            <p-tag [value]="row.statut || 'INACTIF'" [severity]="getMemberStatusSeverity(row.statut || 'INACTIF')" />
                        </td>
                        <td class="flex gap-2 whitespace-nowrap">
                            <p-button type="button" icon="pi pi-ellipsis-h" [rounded]="true" size="small" outlined (click)="op.toggle($event)" />
                            <p-popover #op [style]="{ width: '220px' }">
                                <div class="flex flex-col gap-2">
                                    <p-button *hasAnyRole="['COACH','ADMIN']" label="Modifier le profil" icon="pi pi-user-edit" (click)="openEdit(row); op.hide()" />
                                    <p-button *hasAnyRole="['ADMIN']" label="Supprimer le profil" icon="pi pi-trash" severity="danger" (click)="deleteProfile(row); op.hide()" />
                                </div>
                            </p-popover>
                        </td>
                    </tr>
                </ng-template>

                <ng-template #emptymessage>
                    <tr>
                        <td colspan="7">Aucun membre trouvé.</td>
                    </tr>
                </ng-template>
            </p-table>

            <p-popover #columnsOp [style]="{ width: '260px' }">
                <div class="font-semibold mb-2">Afficher</div>
                <div class="flex flex-col gap-3">
                    <div class="flex items-center justify-between"><span>Rôle</span><p-toggleswitch [(ngModel)]="showRole" /></div>
                    <div class="flex items-center justify-between"><span>Email</span><p-toggleswitch [(ngModel)]="showEmail" /></div>
                    <div class="flex items-center justify-between"><span>Téléphone</span><p-toggleswitch [(ngModel)]="showTelephone" /></div>
                    <div class="flex items-center justify-between" *hasAnyRole="['ADMIN']"><span>Mot de passe</span><p-toggleswitch [(ngModel)]="showPassword" /></div>
                    <div class="flex items-center justify-between"><span>Taille</span><p-toggleswitch [(ngModel)]="showTaille" /></div>
                    <div class="flex items-center justify-between"><span>Poids</span><p-toggleswitch [(ngModel)]="showPoids" /></div>
                    <div class="flex items-center justify-between"><span>Poste</span><p-toggleswitch [(ngModel)]="showPoste" /></div>
                    <div class="flex items-center justify-between"><span>Date de naissance</span><p-toggleswitch [(ngModel)]="showDateNaissance" /></div>
                    <div class="flex items-center justify-between"><span>Numéro maillot</span><p-toggleswitch [(ngModel)]="showNumeroMaillot" /></div>
                    <div class="flex items-center justify-between"><span>Salaire</span><p-toggleswitch [(ngModel)]="showSalaire" /></div>
                    <div class="flex items-center justify-between"><span>Statut</span><p-toggleswitch [(ngModel)]="showStatut" /></div>
                </div>
            </p-popover>

            <p-dialog header="Ajouter un membre" [(visible)]="addDialogVisible" [modal]="true" [style]="{ width: '60rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                <form [formGroup]="addForm" class="grid grid-cols-12 gap-4">
                    <div class="col-span-12 md:col-span-6">
                        <label class="block mb-2">Nom <span class="text-red-500">*</span></label>
                        <input pInputText formControlName="nom" />
                        <small class="text-red-500" *ngIf="addForm.get('nom')?.invalid && addForm.get('nom')?.touched">Le nom est obligatoire</small>
                    </div>
                    <div class="col-span-12 md:col-span-6">
                        <label class="block mb-2">Prénom <span class="text-red-500">*</span></label>
                        <input pInputText formControlName="prenom" />
                        <small class="text-red-500" *ngIf="addForm.get('prenom')?.invalid && addForm.get('prenom')?.touched">Le prénom est obligatoire</small>
                    </div>
                    <div class="col-span-12 md:col-span-6">
                        <label class="block mb-2">Email <span class="text-red-500">*</span></label>
                        <input pInputText formControlName="email" />
                        <small class="text-red-500" *ngIf="addForm.get('email')?.hasError('required') && addForm.get('email')?.touched">L'email est obligatoire</small>
                        <small class="text-red-500" *ngIf="addForm.get('email')?.hasError('email') && addForm.get('email')?.touched">Format d'email invalide</small>
                    </div>
                    <div class="col-span-12 md:col-span-6">
                        <label class="block mb-2">Mot de passe <span class="text-red-500">*</span></label>
                        <p-inputgroup class="w-full">
                            <input pInputText class="w-full" [type]="passwordHidden ? 'password' : 'text'" formControlName="password" />
                            <p-inputgroup-addon>
                                <p-button [icon]="passwordHidden ? 'pi pi-eye' : 'pi pi-eye-slash'" text (onClick)="togglePassword()" />
                            </p-inputgroup-addon>
                        </p-inputgroup>
                        <small class="text-red-500" *ngIf="addForm.get('password')?.invalid && addForm.get('password')?.touched">Le mot de passe est obligatoire</small>
                    </div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Téléphone</label><input pInputText formControlName="telephone" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Rôle</label><p-select styleClass="w-full" appendTo="body" formControlName="role" [options]="roleOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select></div>

                    <ng-container *ngIf="isRoleJoueur()">
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Taille (cm)</label><p-inputnumber formControlName="taille" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Poids (kg)</label><p-inputnumber formControlName="poids" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Poste <span class="text-red-500">*</span></label><p-select styleClass="w-full" appendTo="body" formControlName="poste" [options]="positionOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select><small class="text-red-500" *ngIf="addForm.get('poste')?.hasError('required') && addForm.get('poste')?.touched">Le poste est obligatoire</small></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date de naissance <span class="text-red-500">*</span></label><input pInputText formControlName="dateNaissance" placeholder="dd/mm/yyyy" /><small class="text-red-500" *ngIf="addForm.get('dateNaissance')?.hasError('required') && addForm.get('dateNaissance')?.touched">La date de naissance est obligatoire</small></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Numéro maillot</label><p-inputnumber formControlName="numeroMaillot" [min]="1" [max]="99" [useGrouping]="false" [showButtons]="true"></p-inputnumber></div>
                    </ng-container>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Salaire</label><p-inputnumber formControlName="salaire" mode="decimal" [minFractionDigits]="0" [maxFractionDigits]="3" suffix=" DT"></p-inputnumber></div>
                </form>
                <ng-template #footer>
                    <div class="flex gap-2 justify-end">
                        <p-button label="Annuler" severity="secondary" (onClick)="addDialogVisible=false" />
                        <p-button label="Enregistrer" icon="pi pi-check" [disabled]="addForm.invalid" (onClick)="submitAdd()" />
                    </div>
                </ng-template>
            </p-dialog>

            <p-dialog header="Modifier le profil" [(visible)]="editDialogVisible" [modal]="true" [style]="{ width: '60rem' }" [draggable]="false" [contentStyle]="{ overflow: 'visible' }">
                <form *ngIf="editDialogVisible && editForm" [formGroup]="editForm" class="grid grid-cols-12 gap-4">
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Nom</label><input pInputText formControlName="nom" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Prénom</label><input pInputText formControlName="prenom" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Email</label><input pInputText formControlName="email" /></div>
                    <div class="col-span-12 md:col-span-6">
                        <label class="block mb-2">Mot de passe</label>
                        <p-inputgroup class="w-full">
                            <input pInputText class="w-full" [type]="editPasswordHidden ? 'password' : 'text'" formControlName="password" />
                            <p-inputgroup-addon>
                                <p-button [icon]="editPasswordHidden ? 'pi pi-eye' : 'pi pi-eye-slash'" text (onClick)="toggleEditPassword()" />
                            </p-inputgroup-addon>
                        </p-inputgroup>
                    </div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Téléphone</label><input pInputText formControlName="telephone" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Rôle</label><p-select styleClass="w-full" appendTo="body" formControlName="role" [options]="roleOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select></div>
                    <ng-container *ngIf="isRoleJoueurEdit()">
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Taille (cm)</label><p-inputnumber formControlName="taille" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Poids (kg)</label><p-inputnumber formControlName="poids" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Poste</label><p-select styleClass="w-full" appendTo="body" formControlName="poste" [options]="positionOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date de naissance</label><input pInputText formControlName="dateNaissance" placeholder="dd/mm/yyyy" /></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Numéro maillot</label><p-inputnumber formControlName="numeroMaillot" [min]="1" [max]="99" [useGrouping]="false" [showButtons]="true"></p-inputnumber></div>
                    </ng-container>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Salaire</label><p-inputnumber formControlName="salaire" mode="decimal" [minFractionDigits]="0" [maxFractionDigits]="3" suffix=" DT"></p-inputnumber></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Statut</label><p-select styleClass="w-full" appendTo="body" formControlName="statut" [options]="statutOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select></div>
                </form>
                <ng-template #footer>
                    <div class="flex gap-2 justify-end">
                        <p-button label="Annuler" severity="secondary" (onClick)="editDialogVisible=false" />
                        <p-button label="Enregistrer" icon="pi pi-check" (onClick)="submitEdit()" />
                    </div>
                </ng-template>
            </p-dialog>

            <p-dialog header="Mot de passe" [(visible)]="passwordDialogVisible" [modal]="true" [style]="{ width: '32rem' }" [draggable]="false">
                <div class="flex items-center gap-2" *ngIf="currentPlainPassword">
                    <input pInputText [value]="currentPlainPassword" readonly class="flex-1" />
                    <p-button icon="pi pi-copy" label="Copier" (onClick)="copy(currentPlainPassword)" />
                </div>
                <small class="block mt-2">Note: ce mot de passe n'est affiché qu'une seule fois. Sauvegardez-le maintenant.</small>
            </p-dialog>
        </div>
    `
})
export class EffectifPage {
    // Column visibility toggles
    showEmail = true;
    showPassword = true;
    showTelephone = true;
    showRole = true;
    showTaille = true;
    showPoids = true;
    showPoste = true;
    showDateNaissance = true;
    showAttaque = false;
    showBloc = false;
    showNumeroMaillot = true;
    showSalaire = true;
    showStatut = true;

    addDialogVisible = false;
    addForm: FormGroup;
    editDialogVisible = false;
    editForm!: FormGroup;
    editingRowId: number | null = null;
    roleOptions = [
        { label: 'Joueur', value: 'Joueur' },
        { label: 'Coach', value: 'Coach' },
        { label: 'Admin', value: 'Admin' },
        { label: 'Invité', value: 'Invité' },
        { label: 'Staff médical', value: 'Staff médical' },
        { label: 'Responsable financier', value: 'Responsable financier' }
    ];
    statutOptions = [
        { label: 'ACTIF', value: 'ACTIF' },
        { label: 'INACTIF', value: 'INACTIF' }
    ];
    positionOptions = [
        { label: 'Passeur', value: 'PASSEUR' },
        { label: 'Attaquant', value: 'ATTAQUANT' },
        { label: 'Central', value: 'CENTRAL' },
        { label: 'Libéro', value: 'LIBERO' },
        { label: 'Pointu', value: 'POINTU' },
        { label: 'Récepteur-Attaquant', value: 'RECEPTEUR_ATTAQUANT' }
    ];
    rows: MembreRow[] = [];
    passwordDialogVisible = false;
    currentPlainPassword: string | null = null;
    // keep the latest created user's plaintext to display in table after reload
    private pendingPasswordUserId: number | null = null;
    private pendingPassword: string | null = null;
    private pendingPasswordEmail: string | null = null;

    constructor(private fb: FormBuilder, private authService: AuthService, private performanceService: PerformanceService) {
        this.addForm = this.fb.group({
            nom: ['', [Validators.required, Validators.maxLength(50)]],
            prenom: ['', [Validators.required, Validators.maxLength(50)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            telephone: ['', [Validators.maxLength(20)]],
            role: [''],
            taille: [null],
            poids: [null],
            poste: [''],
            dateNaissance: [''],
            numeroMaillot: [''],
            salaire: [null, [Validators.min(0)]],
            statut: ['ACTIF']
        });
        // Dynamic validators for Joueur-only fields
        this.addForm.get('role')?.valueChanges.subscribe(() => this.updateAddJoueurValidators());
        this.updateAddJoueurValidators();
        // Charger les utilisateurs à l'initialisation
        this.loadUsers();
    }

    private loadUsers() {
        forkJoin({
            users: this.authService.getUsers(),
            players: this.performanceService.getAllPlayers()
        }).subscribe({
            next: ({ users, players }) => {
                const byEmail = new Map((players || []).filter(p => !!p.email).map(p => [String(p.email).toLowerCase(), p] as const));
                this.rows = (users || []).map((u) => {
                    const p = u.email ? byEmail.get(String(u.email).toLowerCase()) : undefined;
                    const row: MembreRow = {
                        id: u.id,
                        playerId: p?.id ?? null,
                        photo: null,
                        nom: u.nom,
                        prenom: u.prenom,
                        email: u.email,
                        // never show DB hash; only show plaintext when we explicitly set it (creation/reset)
                        passwordPlain: null,
                        passwordShown: false,
                        telephone: u.telephone ?? null,
                        role: u.role,
                        taille: p?.tailleCm != null ? String(p.tailleCm) : null,
                        poids: p?.poidsKg != null ? String(p.poidsKg) : null,
                        poste: p?.position ? this.mapPositionEnumToLabel(p.position) : null,
                        dateNaissance: p?.dateNaissance ? this.formatIsoToFr(p.dateNaissance) : null,
                        numeroMaillot: p?.numeroMaillot != null ? String(p.numeroMaillot) : null,
                        salaire: (u as any).salaire != null ? Number((u as any).salaire) : (p?.salaire != null ? Number(p.salaire) : null),
                        statut: (u.actif ? 'ACTIF' : 'INACTIF') as MembreStatut,
                    } as MembreRow;
                    if (this.pendingPassword && (
                        (this.pendingPasswordUserId != null && u.id === this.pendingPasswordUserId) ||
                        (this.pendingPasswordEmail && u.email && u.email.toLowerCase() === this.pendingPasswordEmail.toLowerCase())
                    )) {
                        row.passwordPlain = this.pendingPassword;
                        row.passwordShown = true;
                    }
                    return row;
                });
                // clear pending after merge
                this.pendingPasswordUserId = null;
                this.pendingPassword = null;
                this.pendingPasswordEmail = null;
            },
            error: (err) => {
                console.error('Erreur de chargement des utilisateurs/joueurs:', err);
                this.rows = [];
            }
        });
    }

    

    openAdd() {
        this.addForm.reset({ statut: 'ACTIF' });
        this.updateAddJoueurValidators();
        this.addDialogVisible = true;
    }

    submitAdd() {
        if (this.addForm.invalid) return;
        const raw = this.addForm.value as any;
        const value = {
            nom: (raw.nom || '').trim(),
            prenom: (raw.prenom || '').trim(),
            email: (raw.email || '').trim(),
            password: (raw.password || '').trim(),
            telephone: (raw.telephone || '').trim(),
            role: raw.role
        } as const;
        const roleMap: Record<string, string> = {
            'Joueur': 'JOUEUR',
            'Coach': 'COACH',
            'Admin': 'ADMIN',
            'Invité': 'INVITE',
            'Staff médical': 'STAFF_MEDICAL',
            'Responsable financier': 'RESPONSABLE_FINANCIER'
        };
        const salaireNum = typeof raw.salaire === 'number' ? raw.salaire : undefined;
        const payload = {
            nom: value.nom,
            prenom: value.prenom,
            email: value.email,
            motDePasse: value.password,
            role: roleMap[value.role] || 'INVITE',
            telephone: value.telephone ? value.telephone : undefined,
            salaire: Number.isFinite(salaireNum as number) ? (salaireNum as number) : undefined
        } as const;
        this.authService.register(payload as any).subscribe({
            next: (res: any) => {
                const mp = (res?.data?.motDePasseClair ?? res?.motDePasseClair) as string | undefined;
                const uid = (res?.data?.id ?? res?.id) as number | undefined;
                if (mp) {
                    this.currentPlainPassword = mp;
                    this.passwordDialogVisible = true;
                }
                if (uid && mp) {
                    this.pendingPasswordUserId = uid;
                    this.pendingPassword = mp;
                }
                // also store email to be safe
                if (mp && value.email) {
                    this.pendingPasswordEmail = value.email;
                }
                // Si Joueur, créer aussi le Player dans performance-service
                if ((value.role || '') === 'Joueur') {
                    const position = this.mapPosteToPosition(raw.poste);
                    let dateIso = this.parseDateToIso(raw.dateNaissance) || '2000-01-01';
                    // ensure past date
                    try {
                        const d = new Date(dateIso);
                        const today = new Date();
                        if (isNaN(d.getTime()) || d > today) {
                            dateIso = '2000-01-01';
                        }
                    } catch {}

                    const numMaillot = Number(raw.numeroMaillot);
                    const numeroMaillot = Number.isFinite(numMaillot) && numMaillot >= 1 && numMaillot <= 99 ? numMaillot : undefined;
                    const t = Number(raw.taille);
                    const tailleCm = Number.isFinite(t) && t >= 100 && t <= 250 ? t : undefined;
                    const p = Number(raw.poids);
                    const poidsKg = Number.isFinite(p) && p >= 30 && p <= 200 ? p : undefined;

                    const playerReq: CreatePlayerRequest = {
                        nom: value.nom,
                        prenom: value.prenom,
                        email: value.email || undefined,
                        dateNaissance: dateIso,
                        position: position || 'RECEPTEUR_ATTAQUANT',
                        numeroMaillot,
                        tailleCm,
                        poidsKg,
                        salaire: typeof raw.salaire === 'number' ? raw.salaire : undefined,
                        statut: 'ACTIF'
                    };
                    // Debug minimal
                    console.debug('createPlayer payload', playerReq);
                    this.performanceService.createPlayer(playerReq).subscribe({
                        next: () => {
                            this.afterAddSuccess();
                        },
                        error: (err) => {
                            console.error('Création Player échouée (performance-service):', err?.error || err);
                            // On continue quand même côté UI
                            this.afterAddSuccess();
                        }
                    });
                } else {
                    this.afterAddSuccess();
                }
            },
            error: (err) => {
                const serverMsg = err?.error?.message || err?.message || 'Erreur inconnue';
                console.error('Erreur lors de la création du membre:', serverMsg, err?.error);
            }
        });
    }

    passwordHidden = true;
    togglePassword() {
        this.passwordHidden = !this.passwordHidden;
    }

    isRoleJoueur(): boolean {
        return (this.addForm.get('role')?.value || '') === 'Joueur';
    }

    getAvatarImage(row: MembreRow): string {
        if (row.photo) return row.photo;
        // Default avatars rotation
        const defaults = [
            'https://primefaces.org/cdn/primeng/images/demo/avatar/amyelsner.png',
            'https://primefaces.org/cdn/primeng/images/demo/avatar/asiyajavayant.png',
            'https://primefaces.org/cdn/primeng/images/demo/avatar/onyamalimba.png',
            'https://primefaces.org/cdn/primeng/images/demo/avatar/walter.jpg',
            'https://www.gravatar.com/avatar/05dfd4b41340d09cae045235eb0893c3?d=mp'
        ];
        return defaults[row.id % defaults.length];
    }

    getInitials(value: string) {
        if (!value) return '';
        const parts = value.trim().split(/\s+/);
        const initials = parts.map((p) => p.charAt(0).toUpperCase()).slice(0, 2).join('');
        return initials || '?';
    }

    onGlobalFilter(table: any, event: Event) {
        const target = event.target as HTMLInputElement | null;
        table.filterGlobal(target ? target.value : '', 'contains');
    }

    openEdit(row: MembreRow) {
        this.editingRowId = row.id;
        this.editForm = this.fb.group({
            nom: [row.nom || ''],
            prenom: [row.prenom || ''],
            email: [row.email || ''],
            // Pas de pré-remplissage du mot de passe
            password: [''],
            telephone: [row.telephone || ''],
            role: [this.mapBackendRoleToLabel(row.role) || ''],
            taille: [row.taille ? Number(row.taille) : null],
            poids: [row.poids ? Number(row.poids) : null],
            // Convertir le libellé poste -> enum si possible
            poste: [this.mapPosteToPosition(row.poste) || null],
            dateNaissance: [row.dateNaissance || ''],
            numeroMaillot: [row.numeroMaillot ? Number(row.numeroMaillot) : null],
            salaire: [row.salaire != null ? Number(row.salaire) : null],
            statut: [row.statut || 'ACTIF']
        });
        this.editDialogVisible = true;
    }

    private mapBackendRoleToLabel(val?: string | null): string {
        const map: Record<string, string> = {
            'JOUEUR': 'Joueur',
            'COACH': 'Coach',
            'ADMIN': 'Admin',
            'INVITE': 'Invité',
            'STAFF_MEDICAL': 'Staff médical',
            'RESPONSABLE_FINANCIER': 'Responsable financier'
        };
        if (!val) return '';
        return map[val] || val;
    }

    submitEdit() {
        if (!this.editForm || this.editingRowId === null) return;
        const value = this.editForm.value as any;
        const roleMap: Record<string, string> = {
            'Joueur': 'JOUEUR',
            'Coach': 'COACH',
            'Admin': 'ADMIN',
            'Invité': 'INVITE',
            'Staff médical': 'STAFF_MEDICAL',
            'Responsable financier': 'RESPONSABLE_FINANCIER'
        };
        const actif = (value.statut || 'ACTIF') === 'ACTIF';
        const salaireNum = typeof value.salaire === 'number' ? value.salaire : undefined;
        const payload = {
            nom: value.nom || '',
            prenom: value.prenom || '',
            email: value.email || '',
            role: roleMap[value.role] || (value.role || 'INVITE'),
            actif,
            telephone: value.telephone || null,
            salaire: Number.isFinite(salaireNum as number) ? (salaireNum as number) : undefined
        } as const;
        const id = this.editingRowId;
        this.authService.updateUser(id, payload as any).subscribe({
            next: () => {
                const isJoueur = (value.role || '') === 'Joueur';
                if (isJoueur) {
                    const row = this.rows.find(r => r.id === id);
                    const position = this.mapPosteToPosition(value.poste);
                    let dateIso = this.parseDateToIso(value.dateNaissance) || '2000-01-01';
                    try {
                        const d = new Date(dateIso);
                        const today = new Date();
                        if (isNaN(d.getTime()) || d > today) dateIso = '2000-01-01';
                    } catch {}
                    const numMaillot = Number(value.numeroMaillot);
                    const numeroMaillot = Number.isFinite(numMaillot) && numMaillot >= 1 && numMaillot <= 99 ? numMaillot : undefined;
                    const t = Number(value.taille);
                    const tailleCm = Number.isFinite(t) && t >= 100 && t <= 250 ? t : undefined;
                    const pds = Number(value.poids);
                    const poidsKg = Number.isFinite(pds) && pds >= 30 && pds <= 200 ? pds : undefined;
                    const playerReq: CreatePlayerRequest = {
                        nom: value.nom || '',
                        prenom: value.prenom || '',
                        email: (value.email || '').trim() || undefined,
                        dateNaissance: dateIso,
                        position: position || 'RECEPTEUR_ATTAQUANT',
                        numeroMaillot,
                        tailleCm,
                        poidsKg,
                        salaire: typeof value.salaire === 'number' ? value.salaire : undefined,
                        statut: (value.statut || 'ACTIF')
                    };
                    const originalEmail = row?.email || '';
                    if (originalEmail) {
                        this.performanceService.updatePlayerByEmail(originalEmail, playerReq).subscribe({
                            next: () => {
                                this.editDialogVisible = false;
                                this.editingRowId = null;
                                this.loadUsers();
                            },
                            error: (err) => {
                                console.error('Erreur lors de la modification du joueur par email (performance-service):', err?.error || err);
                                this.editDialogVisible = false;
                                this.editingRowId = null;
                                this.loadUsers();
                            }
                        });
                    } else {
                        // Si pas d'email original (cas rare), créer le player
                        this.performanceService.createPlayer(playerReq).subscribe({
                            next: () => {
                                this.editDialogVisible = false;
                                this.editingRowId = null;
                                this.loadUsers();
                            },
                            error: (err) => {
                                console.error('Création du joueur lors de la modification (email manquant):', err?.error || err);
                                this.editDialogVisible = false;
                                this.editingRowId = null;
                                this.loadUsers();
                            }
                        });
                    }
                } else {
                    this.editDialogVisible = false;
                    this.editingRowId = null;
                    this.loadUsers();
                }
            },
            error: (err) => {
                console.error('Erreur lors de la modification du membre:', err);
            }
        });
    }

    deleteProfile(row: MembreRow) {
        const email = row.email || '';
        // Supprimer le joueur côté performance-service d'abord (si email existe)
        const afterPlayerDelete = () => {
            this.authService.deleteUser(row.id).subscribe({
                next: () => {
                    this.rows = this.rows.filter((r) => r.id !== row.id);
                },
                error: (err) => {
                    console.error('Erreur lors de la suppression du membre:', err);
                }
            });
        };
        if (email) {
            this.performanceService.deletePlayerByEmail(email).subscribe({
                next: () => afterPlayerDelete(),
                error: (err) => {
                    console.warn('Suppression du joueur (performance-service) a échoué ou joueur introuvable:', err?.error || err);
                    afterPlayerDelete();
                }
            });
        } else {
            afterPlayerDelete();
        }
    }

    getMemberStatusSeverity(statut: MembreStatut): 'success' | 'secondary' | 'danger' {
        switch (statut) {
            case 'ACTIF':
                return 'success';
            case 'SUSPENDU':
                return 'danger';
            default:
                return 'secondary';
        }
    }

    private afterAddSuccess() {
        this.addDialogVisible = false;
        this.addForm.reset({ statut: 'ACTIF' });
        this.loadUsers();
    }

    // reset password feature removed per request

    copy(text: string) {
        try {
            navigator.clipboard.writeText(text);
        } catch {}
    }

    private updateAddJoueurValidators() {
        const isJoueur = this.isRoleJoueur();
        const posteCtrl = this.addForm.get('poste');
        const dateCtrl = this.addForm.get('dateNaissance');
        if (!posteCtrl || !dateCtrl) return;
        if (isJoueur) {
            posteCtrl.setValidators([Validators.required]);
            dateCtrl.setValidators([Validators.required]);
        } else {
            posteCtrl.clearValidators();
            dateCtrl.clearValidators();
            posteCtrl.setValue(posteCtrl.value || '');
            dateCtrl.setValue(dateCtrl.value || '');
        }
        posteCtrl.updateValueAndValidity({ emitEvent: false });
        dateCtrl.updateValueAndValidity({ emitEvent: false });
    }

    private mapPosteToPosition(poste: string | null | undefined): PositionEnum | null {
        const raw = (poste || '').trim();
        // If already an enum value from dropdown
        const enums = ['PASSEUR','ATTAQUANT','CENTRAL','LIBERO','POINTU','RECEPTEUR_ATTAQUANT'] as const;
        if ((enums as readonly string[]).includes(raw)) return raw as PositionEnum;
        const p = raw.toLowerCase();
        const mapping: Record<string, PositionEnum> = {
            'passeur': 'PASSEUR',
            'attaquant': 'ATTAQUANT',
            'central': 'CENTRAL',
            'libero': 'LIBERO',
            'libéro': 'LIBERO',
            'pointu': 'POINTU',
            'recepteur-attaquant': 'RECEPTEUR_ATTAQUANT',
            'récepteur-attaquant': 'RECEPTEUR_ATTAQUANT'
        };
        return mapping[p] || null;
    }

    private mapLabelToPositionEnum(label: string | null | undefined): PositionEnum | null {
        const l = (label || '').trim().toLowerCase();
        const mapping: Record<string, PositionEnum> = {
            'passeur': 'PASSEUR',
            'attaquant': 'ATTAQUANT',
            'central': 'CENTRAL',
            'libéro': 'LIBERO',
            'libero': 'LIBERO',
            'pointu': 'POINTU',
            'récepteur-attaquant': 'RECEPTEUR_ATTAQUANT',
            'recepteur-attaquant': 'RECEPTEUR_ATTAQUANT'
        };
        // If already enum
        const enums = ['PASSEUR','ATTAQUANT','CENTRAL','LIBERO','POINTU','RECEPTEUR_ATTAQUANT'] as const;
        if ((enums as readonly string[]).includes((label || '').trim())) return (label as PositionEnum);
        return mapping[l] || null;
    }

    private parseDateToIso(dateStr: string | null | undefined): string | null {
        const s = (dateStr || '').trim();
        // attend "dd/mm/yyyy"
        const m = s.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
        if (!m) return null;
        const dd = m[1].padStart(2, '0');
        const mm = m[2].padStart(2, '0');
        const yyyy = m[3];
        return `${yyyy}-${mm}-${dd}`;
    }

    private formatIsoToFr(iso: string): string {
        // expects yyyy-mm-dd
        const m = (iso || '').match(/^(\d{4})-(\d{2})-(\d{2})/);
        if (!m) return iso || '';
        return `${m[3]}/${m[2]}/${m[1]}`;
    }

    private mapPositionEnumToLabel(pos: PositionEnum): string {
        const labels: Record<PositionEnum, string> = {
            PASSEUR: 'Passeur',
            ATTAQUANT: 'Attaquant',
            CENTRAL: 'Central',
            LIBERO: 'Libéro',
            POINTU: 'Pointu',
            RECEPTEUR_ATTAQUANT: 'Récepteur-Attaquant'
        };
        return labels[pos] || pos;
    }

    formatDt(value: number): string {
        try {
            const formatted = new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 0, maximumFractionDigits: 3 }).format(value);
            return `${formatted} DT`;
        } catch {
            return `${value} DT`;
        }
    }

    editPasswordHidden = true;
    toggleEditPassword() {
        this.editPasswordHidden = !this.editPasswordHidden;
    }

    isRoleJoueurEdit(): boolean {
        return (this.editForm?.get('role')?.value || '') === 'Joueur';
    }
}


