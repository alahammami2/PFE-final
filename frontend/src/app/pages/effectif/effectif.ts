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

type MembreStatut = 'ACTIF' | 'INACTIF' | 'SUSPENDU';

interface MembreRow {
    id: number;
    photo?: string | null;
    nom: string;
    prenom: string;
    email?: string | null;
    password?: string | null;
    telephone?: string | null;
    role?: string | null;
    taille?: string | null; // cm
    poids?: string | null; // kg
    poste?: string | null;
    dateNaissance?: string | null; // dd/mm/yyyy
    attaque?: string | null; // cm
    bloc?: string | null; // cm
    numeroMaillot?: string | null;
    salaire?: string | null;
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
        InputGroupAddonModule
    ],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <div class="text-2xl font-semibold">Effectif</div>
                <div class="flex gap-2">
                    <p-button label="Ajouter un membre" icon="pi pi-user-plus" severity="primary" (onClick)="openAdd()" />
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
                        <th class="whitespace-nowrap" *ngIf="showPassword">Mot de passe</th>
                        <th class="whitespace-nowrap" *ngIf="showTelephone">Téléphone</th>
                        <th class="whitespace-nowrap" *ngIf="showRole" pSortableColumn="role">Rôle <p-sortIcon field="role" /></th>
                        <th class="whitespace-nowrap" *ngIf="showTaille" pSortableColumn="taille">Taille (cm) <p-sortIcon field="taille" /></th>
                        <th class="whitespace-nowrap" *ngIf="showPoids" pSortableColumn="poids">Poids (kg) <p-sortIcon field="poids" /></th>
                        <th class="whitespace-nowrap" *ngIf="showPoste" pSortableColumn="poste">Poste <p-sortIcon field="poste" /></th>
                        <th class="whitespace-nowrap" *ngIf="showDateNaissance" pSortableColumn="dateNaissance">Date de naissance <p-sortIcon field="dateNaissance" /></th>
                        <th class="whitespace-nowrap" *ngIf="showAttaque" pSortableColumn="attaque">Attaque (cm) <p-sortIcon field="attaque" /></th>
                        <th class="whitespace-nowrap" *ngIf="showBloc" pSortableColumn="bloc">Bloc (cm) <p-sortIcon field="bloc" /></th>
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
                        <td class="whitespace-nowrap" *ngIf="showPassword">{{ row.password ? '********' : '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showTelephone">{{ row.telephone || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showRole">{{ row.role || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showTaille">{{ row.taille || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showPoids">{{ row.poids || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showPoste">{{ row.poste || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showDateNaissance">{{ row.dateNaissance || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showAttaque">{{ row.attaque || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showBloc">{{ row.bloc || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showNumeroMaillot">{{ row.numeroMaillot || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showSalaire">{{ row.salaire || '-' }}</td>
                        <td class="whitespace-nowrap" *ngIf="showStatut">
                            <p-tag [value]="row.statut || 'INACTIF'" [severity]="getMemberStatusSeverity(row.statut || 'INACTIF')" />
                        </td>
                        <td class="flex gap-2 whitespace-nowrap">
                            <p-button type="button" icon="pi pi-ellipsis-h" [rounded]="true" size="small" outlined (click)="op.toggle($event)" />
                            <p-popover #op [style]="{ width: '220px' }">
                                <div class="flex flex-col gap-2">
                                    <p-button label="Modifier le profil" icon="pi pi-user-edit" (click)="openEdit(row); op.hide()" />
                                    <p-button label="Supprimer le profil" icon="pi pi-trash" severity="danger" (click)="deleteProfile(row); op.hide()" />
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
                    <div class="flex items-center justify-between"><span>Mot de passe</span><p-toggleswitch [(ngModel)]="showPassword" /></div>
                    <div class="flex items-center justify-between"><span>Taille</span><p-toggleswitch [(ngModel)]="showTaille" /></div>
                    <div class="flex items-center justify-between"><span>Poids</span><p-toggleswitch [(ngModel)]="showPoids" /></div>
                    <div class="flex items-center justify-between"><span>Poste</span><p-toggleswitch [(ngModel)]="showPoste" /></div>
                    <div class="flex items-center justify-between"><span>Date de naissance</span><p-toggleswitch [(ngModel)]="showDateNaissance" /></div>
                    <div class="flex items-center justify-between"><span>Attaque</span><p-toggleswitch [(ngModel)]="showAttaque" /></div>
                    <div class="flex items-center justify-between"><span>Bloc</span><p-toggleswitch [(ngModel)]="showBloc" /></div>
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
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Poste</label><input pInputText formControlName="poste" /></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date de naissance</label><input pInputText formControlName="dateNaissance" placeholder="dd/mm/yyyy" /></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Attaque (cm)</label><p-inputnumber formControlName="attaque" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Bloc (cm)</label><p-inputnumber formControlName="bloc" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Numéro maillot</label><input pInputText formControlName="numeroMaillot" /></div>
                    </ng-container>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Salaire</label><input pInputText formControlName="salaire" placeholder="Ex: 2500 DT" /></div>
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
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Poste</label><input pInputText formControlName="poste" /></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Date de naissance</label><input pInputText formControlName="dateNaissance" placeholder="dd/mm/yyyy" /></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Attaque (cm)</label><p-inputnumber formControlName="attaque" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-6 md:col-span-3"><label class="block mb-2">Bloc (cm)</label><p-inputnumber formControlName="bloc" mode="decimal"></p-inputnumber></div>
                        <div class="col-span-12 md:col-span-6"><label class="block mb-2">Numéro maillot</label><input pInputText formControlName="numeroMaillot" /></div>
                    </ng-container>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Salaire</label><input pInputText formControlName="salaire" placeholder="Ex: 2500 DT" /></div>
                    <div class="col-span-12 md:col-span-6"><label class="block mb-2">Statut</label><p-select styleClass="w-full" appendTo="body" formControlName="statut" [options]="statutOptions" optionLabel="label" optionValue="value" placeholder="Sélectionner"></p-select></div>
                </form>
                <ng-template #footer>
                    <div class="flex gap-2 justify-end">
                        <p-button label="Annuler" severity="secondary" (onClick)="editDialogVisible=false" />
                        <p-button label="Enregistrer" icon="pi pi-check" (onClick)="submitEdit()" />
                    </div>
                </ng-template>
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
    showAttaque = true;
    showBloc = true;
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
        { label: 'INACTIF', value: 'INACTIF' },
        { label: 'SUSPENDU', value: 'SUSPENDU' }
    ];
    rows: MembreRow[] = [
        {
            id: 1,
            photo: null,
            nom: '-',
            prenom: 'alahammami772',
            email: 'user@example.com',
            password: 'pass1234',
            telephone: '+216 00 000 000',
            role: 'Joueur',
            taille: '190',
            poids: '80',
            poste: 'Attaquant',
            dateNaissance: '01/01/2000',
            attaque: '330',
            bloc: '310',
            numeroMaillot: '7',
            salaire: '2500 DT',
            statut: 'ACTIF'
        }
    ];

    constructor(private fb: FormBuilder) {
        this.addForm = this.fb.group({
            nom: ['', Validators.required],
            prenom: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required],
            telephone: [''],
            role: [''],
            taille: [null],
            poids: [null],
            poste: [''],
            dateNaissance: [''],
            attaque: [null],
            bloc: [null],
            numeroMaillot: [''],
            salaire: [''],
            statut: ['ACTIF']
        });
    }

    openAdd() {
        this.addForm.reset({ statut: 'ACTIF' });
        this.addDialogVisible = true;
    }

    submitAdd() {
        if (this.addForm.invalid) return;
        const value = this.addForm.value as any;
        const nextId = (this.rows[0]?.id || 0) + 1;
        const newRow: MembreRow = { id: nextId, photo: null, statut: value.statut || 'ACTIF', ...value };
        this.rows = [newRow, ...this.rows];
        this.addDialogVisible = false;
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
            password: [row.password || ''],
            telephone: [row.telephone || ''],
            role: [row.role || ''],
            taille: [row.taille ? Number(row.taille) : null],
            poids: [row.poids ? Number(row.poids) : null],
            poste: [row.poste || ''],
            dateNaissance: [row.dateNaissance || ''],
            attaque: [row.attaque ? Number(row.attaque) : null],
            bloc: [row.bloc ? Number(row.bloc) : null],
            numeroMaillot: [row.numeroMaillot || ''],
            salaire: [row.salaire || ''],
            statut: [row.statut || 'ACTIF']
        });
        this.editDialogVisible = true;
    }

    submitEdit() {
        if (!this.editForm || this.editingRowId === null) return;
        const value = this.editForm.value as any;
        this.rows = this.rows.map((r) => (r.id === this.editingRowId ? { ...r, ...value } : r));
        this.editDialogVisible = false;
        this.editingRowId = null;
    }

    deleteProfile(row: MembreRow) {
        this.rows = this.rows.filter((r) => r.id !== row.id);
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

    editPasswordHidden = true;
    toggleEditPassword() {
        this.editPasswordHidden = !this.editPasswordHidden;
    }

    isRoleJoueurEdit(): boolean {
        return (this.editForm?.get('role')?.value || '') === 'Joueur';
    }
}


