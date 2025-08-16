import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { ChartModule } from 'primeng/chart';

interface ExpenseRow {
    id: number;
    date: string; // dd-mm-yyyy
    categorie: string;
    montant: number;
    statut: 'Payée' | 'En attente';
    description?: string;
    saison: string;
}

interface IncomeRow {
    id: number;
    date: string; // dd-mm-yyyy
    categorie: string;
    montant: number;
    description?: string;
    saison: string;
}

@Component({
    selector: 'app-finance',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        TableModule,
        ButtonModule,
        TagModule,
        DialogModule,
        InputTextModule,
        InputNumberModule,
        DatePickerModule,
        SelectModule,
        ChartModule
    ],
    template: `
        <div class="card mb-4">
            <div class="grid grid-cols-12 gap-4">
                <div class="col-span-12 md:col-span-6">
                    <div class="card flex items-center justify-between">
                        <div class="text-4xl text-primary"><i class="pi pi-credit-card"></i></div>
                        <div class="text-right">
                            <div class="text-2xl font-semibold">{{ totalFacture | number:'1.2-2' }}</div>
                            <div class="text-muted-color">Montant total </div>
                        </div>
                    </div>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <div class="card flex items-center justify-between">
                        <div class="text-4xl" style="color:#0b48a0"><i class="pi pi-credit-card" style="transform: translateY(2px);"></i><span class="ml-1 text-primary">+</span></div>
                        <div class="text-right">
                            <div class="text-2xl font-semibold">{{ resteAPayer | number:'1.2-2' }}</div>
                            <div class="text-muted-color">Reste à payer</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mt-4 flex justify-end">
            <div class="w-64">
                <label class="block mb-2 font-medium">Saison</label>
                <p-select [(ngModel)]="selectedSaison" [options]="saisonOptions" optionLabel="label" optionValue="value" appendTo="body"></p-select>
            </div>
        </div>

        <div class="card mb-4">
            <div class="flex items-center justify-between mb-3">
                <div class="text-lg font-semibold">Dépenses</div>
                <div class="flex gap-2">
                    <p-button label="Nouvelle dépense" icon="pi pi-plus" (onClick)="openAddExpense()"></p-button>
                </div>
            </div>
            <p-table [value]="visibleExpenses" [rows]="10" [paginator]="true" dataKey="id">
                <ng-template #header>
                    <tr>
                        <th>Date</th>
                        <th>Catégorie</th>
                        <th>Montant</th>
                        <th>Statut</th>
                        <th>description</th>
                    </tr>
                </ng-template>
                <ng-template #body let-row let-i="rowIndex">
                    <tr class="cursor-pointer" (click)="toggleExpenseActionsIndex(i)">
                        <td>{{ row.date }}</td>
                        <td>{{ row.categorie }}</td>
                        <td>{{ row.montant | number:'1.2-2' }} DT</td>
                        <td><p-tag [value]="row.statut" [severity]="row.statut==='Payée' ? 'success' : 'warn'"></p-tag></td>
                        <td>{{ row.description }}</td>
                    </tr>
                    <ng-container *ngIf="expensesExpandedRowIndex === i">
                        <tr>
                            <td colspan="5">
                                <div class="flex justify-center py-2">
                                    <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="openEditExpense(row); $event.stopPropagation()"></p-button>
                                </div>
                            </td>
                        </tr>
                    </ng-container>
                </ng-template>
                <ng-template #emptymessage>
                    <tr><td colspan="5">Aucune dépense enregistrée.</td></tr>
                </ng-template>
            </p-table>
        </div>

        <div class="card">
            <div class="text-lg font-semibold mb-3">Dépenses par catégorie</div>
            <p-chart type="bar" [data]="expenseChartData" [options]="expenseChartOptions"></p-chart>
        </div>

        <div class="card mb-4 mt-6">
            <div class="flex items-center justify-between mb-3">
                <div class="text-lg font-semibold">Recettes</div>
                <div class="flex gap-2">
                    <p-button label="Nouvelle recette" icon="pi pi-plus" (onClick)="openAddIncome()"></p-button>
                </div>
            </div>
            <p-table [value]="visibleIncomes" [rows]="10" [paginator]="true" dataKey="id">
                <ng-template #header>
                    <tr>
                        <th>Date</th>
                        <th>Catégorie</th>
                        <th>Montant</th>
                        <th>description</th>
                    </tr>
                </ng-template>
                <ng-template #body let-row let-i="rowIndex">
                    <tr class="cursor-pointer" (click)="toggleIncomeActionsIndex(i)">
                        <td>{{ row.date }}</td>
                        <td>{{ row.categorie }}</td>
                        <td>{{ row.montant | number:'1.2-2' }} DT</td>
                        <td>{{ row.description }}</td>
                    </tr>
                    <ng-container *ngIf="incomesExpandedRowIndex === i">
                        <tr>
                            <td colspan="4">
                                <div class="flex justify-center py-2">
                                    <p-button size="small" label="Modifier" icon="pi pi-pencil" (onClick)="openEditIncome(row); $event.stopPropagation()"></p-button>
                                </div>
                            </td>
                        </tr>
                    </ng-container>
                </ng-template>
                <ng-template #emptymessage>
                    <tr><td colspan="4">Aucune recette enregistrée.</td></tr>
                </ng-template>
            </p-table>
        </div>

        <div class="card">
            <div class="text-lg font-semibold mb-3">Recettes par catégorie</div>
            <p-chart type="bar" [data]="incomeChartData" [options]="expenseChartOptions"></p-chart>
        </div>

        <p-dialog [header]="expenseDialogHeader" [(visible)]="addDialogVisible" [modal]="true" [style]="{ width: '36rem' }" [draggable]="false">
            <form [formGroup]="addForm" class="grid grid-cols-12 gap-4">
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Date</label>
                    <p-datepicker formControlName="date" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Catégorie</label>
                    <p-select formControlName="categorie" [options]="categorieOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Montant</label>
                    <p-inputnumber inputId="montant" formControlName="montant" mode="currency" currency="TND" locale="fr-TN"></p-inputnumber>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Statut</label>
                    <p-select formControlName="statut" [options]="statutOptions" optionLabel="label" optionValue="value" appendTo="body"></p-select>
                </div>
                <div class="col-span-12">
                    <label class="block mb-2">description</label>
                    <input pInputText formControlName="description" />
                </div>
            </form>
            <ng-template #footer>
                <div class="flex gap-2 justify-end">
                    <p-button label="Annuler" severity="secondary" (onClick)="addDialogVisible=false" />
                    <p-button [label]="editingExpenseId ? 'Enregistrer' : 'Enregistrer'" icon="pi pi-check" [disabled]="addForm.invalid" (onClick)="submitAddExpense()" />
                </div>
            </ng-template>
        </p-dialog>

        <p-dialog [header]="incomeDialogHeader" [(visible)]="addIncomeDialogVisible" [modal]="true" [style]="{ width: '36rem' }" [draggable]="false">
            <form [formGroup]="addIncomeForm" class="grid grid-cols-12 gap-4">
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Date</label>
                    <p-datepicker formControlName="date" [showIcon]="true" dateFormat="dd-mm-yy"></p-datepicker>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Catégorie</label>
                    <p-select formControlName="categorie" [options]="incomeCategorieOptions" optionLabel="label" optionValue="value" appendTo="body" placeholder="Sélectionner"></p-select>
                </div>
                <div class="col-span-12 md:col-span-6">
                    <label class="block mb-2">Montant</label>
                    <p-inputnumber inputId="montantRecette" formControlName="montant" mode="currency" currency="TND" locale="fr-TN"></p-inputnumber>
                </div>
                <div class="col-span-12">
                    <label class="block mb-2">description</label>
                    <input pInputText formControlName="description" />
                </div>
            </form>
            <ng-template #footer>
                <div class="flex gap-2 justify-end">
                    <p-button label="Annuler" severity="secondary" (onClick)="addIncomeDialogVisible=false" />
                    <p-button [label]="editingIncomeId ? 'Enregistrer' : 'Enregistrer'" icon="pi pi-check" [disabled]="addIncomeForm.invalid" (onClick)="submitAddIncome()" />
                </div>
            </ng-template>
        </p-dialog>
    `
})
export class FinancePage {
    addDialogVisible = false;
    addForm!: FormGroup;
    addIncomeDialogVisible = false;
    addIncomeForm!: FormGroup;
    expensesExpandedRowIndex: number | null = null;
    editingExpenseId: number | null = null;
    expenseDialogHeader = 'Nouvelle dépense';
    incomesExpandedRowIndex: number | null = null;
    editingIncomeId: number | null = null;
    incomeDialogHeader = 'Nouvelle recette';

    expenses: ExpenseRow[] = [
        { id: 1, date: '05-06-2025', categorie: 'Équipement', montant: 1200, statut: 'Payée', description: 'Ballons et filets', saison: '2025-2026' },
        { id: 2, date: '12-06-2025', categorie: 'Transport', montant: 800, statut: 'En attente', description: 'Déplacement match', saison: '2025-2026' },
        { id: 3, date: '20-06-2025', categorie: 'Hébergement', montant: 1500, statut: 'Payée', saison: '2025-2026' }
    ];

    saisonOptions = [

        { label: '2025-2026', value: '2025-2026' }
    ];
    selectedSaison = '2025-2026';

    categorieOptions = [
        { label: 'Équipement', value: 'Équipement' },
        { label: 'Transport', value: 'Transport' },
        { label: 'Hébergement', value: 'Hébergement' },
        { label: 'Stage', value: 'Stage' },
        { label: 'Salaires', value: 'Salaires' },
        { label: 'Recrues', value: 'Recrues' },
        { label: 'Primes', value: 'Primes' },
        { label: 'salle de musculation', value: 'salle de musculation' },
        { label: 'Autre', value: 'Autre' }
    ];

    statutOptions = [
        { label: 'Payée', value: 'Payée' },
        { label: 'En attente', value: 'En attente' }
    ];

    expenseChartData: any;
    expenseChartOptions: any;
    incomes: IncomeRow[] = [
        { id: 1, date: '03-06-2025', categorie: 'Sponsoring', montant: 5000, description: 'Sponsor A', saison: '2025-2026' },
        { id: 2, date: '15-06-2025', categorie: 'Billetterie', montant: 2300, description: 'Match amical', saison: '2025-2026' }
    ];
    incomeCategorieOptions = [
        { label: 'Sponsoring', value: 'Sponsoring' },
        { label: 'Billetterie', value: 'Billetterie' },
        { label: 'Transferts', value: 'Transferts' },
        { label: 'Investissement', value: 'Investissement' },
        { label: 'Gains des compétitions', value: 'Gains des compétitions' },
        { label: 'Municipalité', value: 'Municipalité' },
        { label: 'Fédération', value: 'Fédération' },
        { label: 'Ministère ', value: 'Ministère ' },
        { label: 'Autre', value: 'Autre' }
    ];
    incomeChartData: any;

    constructor(private fb: FormBuilder) {
        this.addForm = this.fb.group({
            date: [new Date(), Validators.required],
            categorie: [null, Validators.required],
            montant: [null, Validators.required],
            statut: ['En attente', Validators.required],
            description: ['']
        });
        this.addIncomeForm = this.fb.group({
            date: [new Date(), Validators.required],
            categorie: [null, Validators.required],
            montant: [null, Validators.required],
            description: ['']
        });

        this.rebuildChart();
        this.rebuildIncomeChart();
    }

    get visibleExpenses(): ExpenseRow[] {
        return this.expenses.filter((e) => e.saison === this.selectedSaison);
    }

    get totalFacture(): number {
        return this.visibleExpenses.reduce((sum, e) => sum + (e.montant || 0), 0);
    }

    get resteAPayer(): number {
        return this.visibleExpenses.filter((e) => e.statut !== 'Payée').reduce((sum, e) => sum + (e.montant || 0), 0);
    }

    get montantPaye(): number {
        return this.totalFacture - this.resteAPayer;
    }

    openAddExpense() {
        this.addForm.reset({ date: new Date(), categorie: null, montant: null, statut: 'En attente', description: '' });
        this.addDialogVisible = true;
        this.editingExpenseId = null;
        this.expenseDialogHeader = 'Nouvelle dépense';
    }

    submitAddExpense() {
        if (this.addForm.invalid) return;
        const v = this.addForm.value as any;
        const d: Date = v.date;
        const formatDate = (x: Date) => `${String(x.getDate()).padStart(2,'0')}-${String(x.getMonth()+1).padStart(2,'0')}-${x.getFullYear()}`;
        if (this.editingExpenseId) {
            this.expenses = this.expenses.map((e) =>
                e.id === this.editingExpenseId
                    ? { ...e, date: formatDate(d), categorie: v.categorie, montant: Number(v.montant || 0), statut: v.statut, description: v.description }
                    : e
            );
        } else {
            const nextId = (this.expenses[0]?.id || 0) + 1;
            this.expenses = [
                { id: nextId, date: formatDate(d), categorie: v.categorie, montant: Number(v.montant || 0), statut: v.statut, description: v.description, saison: this.selectedSaison },
                ...this.expenses
            ];
        }
        this.addDialogVisible = false;
        this.rebuildChart();
    }

    toggleExpenseActionsIndex(idx: number) {
        this.expensesExpandedRowIndex = this.expensesExpandedRowIndex === idx ? null : idx;
    }

    openEditExpense(row: ExpenseRow) {
        // prefill form
        const [dd, mm, yyyy] = row.date.split('-').map((x) => parseInt(x, 10));
        this.addForm.reset({ date: new Date(yyyy, mm - 1, dd), categorie: row.categorie, montant: row.montant, statut: row.statut, description: row.description || '' });
        this.addDialogVisible = true;
        this.editingExpenseId = row.id;
        this.expenseDialogHeader = 'Modifier dépense';
    }

    private rebuildChart() {
        const dataSet = this.visibleExpenses;
        const labels: string[] = Array.from(new Set(dataSet.map((e) => e.categorie)));
        const dataValues: number[] = labels.map((l) => dataSet.filter((e) => e.categorie === l).reduce((s, e) => s + e.montant, 0));
        this.expenseChartData = {
            labels,
            datasets: [
                {
                    type: 'bar',
                    label: 'Montant total',
                    backgroundColor: '#4f46e5',
                    data: dataValues
                }
            ]
        };
        this.expenseChartOptions = { responsive: true, maintainAspectRatio: false };
    }

    get visibleIncomes(): IncomeRow[] {
        return this.incomes.filter((i) => i.saison === this.selectedSaison);
    }

    openAddIncome() {
        this.addIncomeForm.reset({ date: new Date(), categorie: null, montant: null, description: '' });
        this.addIncomeDialogVisible = true;
        this.editingIncomeId = null;
        this.incomeDialogHeader = 'Nouvelle recette';
    }

    submitAddIncome() {
        if (this.addIncomeForm.invalid) return;
        const v = this.addIncomeForm.value as any;
        const d: Date = v.date;
        const formatDate = (x: Date) => `${String(x.getDate()).padStart(2,'0')}-${String(x.getMonth()+1).padStart(2,'0')}-${x.getFullYear()}`;
        if (this.editingIncomeId) {
            this.incomes = this.incomes.map((i) =>
                i.id === this.editingIncomeId
                    ? { ...i, date: formatDate(d), categorie: v.categorie, montant: Number(v.montant || 0), description: v.description }
                    : i
            );
        } else {
            const nextId = (this.incomes[0]?.id || 0) + 1;
            this.incomes = [
                { id: nextId, date: formatDate(d), categorie: v.categorie, montant: Number(v.montant || 0), description: v.description, saison: this.selectedSaison },
                ...this.incomes
            ];
        }
        this.addIncomeDialogVisible = false;
        this.rebuildIncomeChart();
    }

    private rebuildIncomeChart() {
        const dataSet = this.visibleIncomes;
        const labels: string[] = Array.from(new Set(dataSet.map((e) => e.categorie)));
        const dataValues: number[] = labels.map((l) => dataSet.filter((e) => e.categorie === l).reduce((s, e) => s + e.montant, 0));
        this.incomeChartData = {
            labels,
            datasets: [
                {
                    type: 'bar',
                    label: 'Montant total',
                    backgroundColor: '#22c55e',
                    data: dataValues
                }
            ]
        };
    }

    toggleIncomeActionsIndex(idx: number) {
        this.incomesExpandedRowIndex = this.incomesExpandedRowIndex === idx ? null : idx;
    }

    openEditIncome(row: IncomeRow) {
        const [dd, mm, yyyy] = row.date.split('-').map((x) => parseInt(x, 10));
        this.addIncomeForm.reset({ date: new Date(yyyy, mm - 1, dd), categorie: row.categorie, montant: row.montant, description: row.description || '' });
        this.addIncomeDialogVisible = true;
        this.editingIncomeId = row.id;
        this.incomeDialogHeader = 'Modifier recette';
    }
}


