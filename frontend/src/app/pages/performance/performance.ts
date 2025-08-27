    import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { ChartModule } from 'primeng/chart';
import { TagModule } from 'primeng/tag';
import { AvatarModule } from 'primeng/avatar';
import { HasAnyRoleDirective } from '../../directives/has-any-role.directive';
import { AuthService } from '../../services/auth.service';
import { PerformanceFileService, PerformanceFileDto } from '../../services/performance-file.service';
import { PerformanceService } from '../../services/performance.service';
import { PlanningApiService } from '../../services/planning.service';
import { ChatbotGatewayService } from '../../services/chatbot-gateway.service';
 

interface PlayerPerformance {
    id: number;
    nom: string;
    prenom: string;
    poste: string;
    equipe: string;
    matchsJoues: number;
    pointsMarques: number;
    attaques: number;
    attaquesReussies: number;
    blocs: number;
    blocsReussis: number;
    services: number;
    servicesReussis: number;
    passes: number;
    passesReussies: number;
    reception: number;
    receptionReussie: number;
    efficacite: number;
}

interface PerformanceFile {
    id: number;
    name: string;
    type: string;
    size: number;
    description?: string;
    uploadDate: Date;
    status: 'pending' | 'processed' | 'error';
    url?: string;
}

 

@Component({
    selector: 'app-performance',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        TableModule,
        InputTextModule,
        SelectModule,
        DialogModule,
        InputNumberModule,
        ChartModule,
        TagModule,
        AvatarModule,
        HasAnyRoleDirective
    ],
    template: `
        <div class="grid grid-cols-12 gap-6">
            <!-- Header -->
            <div class="col-span-12">
                <div class="flex items-center justify-between">
                                         <div>
                         <h1 class="text-3xl font-bold text-gray-900">Performance des Joueurs</h1>
                     </div>
                                         <div class="flex items-center gap-3">
                         <p-select [(ngModel)]="selectedSeason" [options]="seasons" optionLabel="label" optionValue="value" placeholder="Saison" styleClass="w-48"></p-select>
                     </div>
                </div>
            </div>

            <!-- Summary Cards -->
            <div class="col-span-12 lg:col-span-4">
                <div class="card bg-green-50 border-l-4 border-green-500">
                    <div class="p-4">
                        <div class="text-green-600 text-sm font-medium">Efficacité Moyenne</div>
                        <div class="text-2xl font-bold text-green-900">{{ averageEfficiencyApi }}%</div>
                    </div>
                </div>
            </div>
            <div class="col-span-12 lg:col-span-4">
                <div class="card bg-purple-50 border-l-4 border-purple-500">
                    <div class="p-4">
                        <div class="text-purple-600 text-sm font-medium">Joueurs Actifs</div>
                        <div class="text-2xl font-bold text-purple-900">{{ activePlayersFromApi }}</div>
                    </div>
                </div>
            </div>
            <div class="col-span-12 lg:col-span-4">
                <div class="card bg-orange-50 border-l-4 border-orange-500">
                    <div class="p-4">
                        <div class="text-orange-600 text-sm font-medium">match statistiqué</div>
                        <div class="text-2xl font-bold text-orange-900">{{ matchesPlayedTotal }}</div>
                    </div>
                </div>
            </div>

            <!-- Performance Tabs -->
            <div class="col-span-12">
                <!-- Navigation des onglets -->
                <div class="flex border-b border-gray-200 mb-6">
                                         <button 
                         (click)="activeTab = 'fichiers'" 
                         [class]="activeTab === 'fichiers' ? 'border-b-2 border-blue-500 text-blue-600 px-4 py-2 font-medium' : 'text-gray-500 hover:text-gray-700 px-4 py-2'">
                         Fichiers de Performance
                     </button>
                     
                                         
                    
                </div>

                <!-- Contenu des onglets -->
                <!-- Fichiers de Performance -->
                <div *ngIf="activeTab === 'fichiers'" class="card">
                    <div class="flex items-center justify-between mb-6">
                        <div>
                            <h2 class="text-xl font-semibold text-gray-900">Gestion des Fichiers de Performance</h2>
                            <p class="text-gray-600 mt-1">Téléchargez et gérez vos fichiers de statistiques</p>
                        </div>
                        <div class="flex items-center gap-3" *hasAnyRole="['COACH']">
                            <input 
                                type="file" 
                                #fileInput 
                                (change)="onFileSelected($event)" 
                                accept=".xlsx,.xls,.csv,.pdf,.doc,.docx" 
                                class="hidden" />
                            <p-button 
                                label="Choisir un fichier" 
                                icon="pi pi-upload" 
                                severity="info" 
                                (onClick)="fileInput.click()">
                            </p-button>
                        </div>
                    </div>

                    <!-- Zone de drop -->
                    <div 
                        *hasAnyRole="['COACH']"
                        class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center mb-6"
                        [class.border-blue-500]="isDragOver"
                        [class.bg-blue-50]="isDragOver"
                        (dragover)="onDragOver($event)"
                        (dragleave)="onDragLeave($event)"
                        (drop)="onDrop($event)">
                        <div class="text-gray-500">
                            <i class="pi pi-cloud-upload text-4xl mb-4"></i>
                            <p class="text-lg font-medium">Glissez et déposez vos fichiers ici</p>
                            <p class="text-sm">ou cliquez sur "Choisir un fichier"</p>
                            <p class="text-xs text-gray-400 mt-2">Formats acceptés: Excel, CSV, PDF, Word</p>
                        </div>
                    </div>

                    <!-- Liste des fichiers -->
                    <div class="bg-white rounded-lg border border-gray-200">
                        <div class="px-6 py-4 border-b border-gray-200">
                            <h3 class="text-lg font-medium text-gray-900">Fichiers téléchargés</h3>
                        </div>
                        <div class="overflow-x-auto">
                            <table class="min-w-full divide-y divide-gray-200">
                                <thead class="bg-gray-50">
                                    <tr>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fichier</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Taille</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date d'upload</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                    </tr>
                                </thead>
                                <tbody class="bg-white divide-y divide-gray-200">
                                    <tr *ngFor="let file of uploadedFiles; let i = index" class="hover:bg-gray-50">
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="flex items-center">
                                                <div class="flex-shrink-0 h-10 w-10">
                                                    <div class="h-10 w-10 rounded-lg bg-blue-100 flex items-center justify-center">
                                                        <i [class]="getFileIcon(file.type)" class="text-blue-600"></i>
                                                    </div>
                                                </div>
                                                <div class="ml-4">
                                                    <div class="text-sm font-medium text-gray-900">{{ file.name }}</div>
                                                    <div class="text-sm text-gray-500">{{ file.description || 'Aucune description' }}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" 
                                                  [class]="getFileTypeClass(file.type)">
                                                {{ getFileTypeLabel(file.type) }}
                                            </span>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                            {{ formatFileSize(file.size) }}
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                             {{ file.uploadDate | date:'dd/MM/yyyy HH:mm' }}
                                         </td>
                                         <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                            <div class="flex items-center gap-2">
                                                <p-button 
                                                    icon="pi pi-external-link" 
                                                    size="small" 
                                                    [text]="true" 
                                                    (onClick)="viewFile(file)"
                                                    pTooltip="Voir">
                                                </p-button>
                                                <p-button 
                                                    icon="pi pi-download" 
                                                    size="small" 
                                                    [text]="true" 
                                                    (onClick)="downloadFile(file)"
                                                    pTooltip="Télécharger">
                                                </p-button>
                                                <p-button 
                                                    icon="pi pi-trash" 
                                                    size="small" 
                                                    [text]="true" 
                                                    severity="danger"
                                                    (onClick)="deleteFile(file, i)"
                                                    pTooltip="Supprimer">
                                                </p-button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr *ngIf="uploadedFiles.length === 0">
                                        <td colspan="5" class="px-6 py-12 text-center text-gray-500">
                                            <i class="pi pi-file text-4xl mb-4 block"></i>
                                            <p class="text-lg font-medium">Aucun fichier téléchargé</p>
                                            <p class="text-sm">Commencez par télécharger votre premier fichier de performance</p>
                                        </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
             </div>
         </div>


    `
})
export class PerformancePage implements OnInit {
    // Data
    players: PlayerPerformance[] = [
       

    ];

    // Season selector
    selectedSeason = '2025-2026';
    
    // Active tab
    activeTab = 'fichiers';
    
    // File management
    uploadedFiles: PerformanceFile[] = [];
    isDragOver = false;
    isLoadingFiles = false;



    // Options
    seasons = [
        { label: 'Saison 2025-2026', value: '2025-2026' }
    ];



    // API-backed KPIs
    activePlayersFromApi: number = 0;
    averageEfficiencyApi: number = 0;
    matchesPlayedTotal: number = 0;
    // Gateway integration state
    defaultTeam = 'CLUB OLYMPIQUE KELIBIA';
    selectedFileId: number | null = null;
    playersForFile: string[] = [];
    selectedPlayer: string | null = null;
    playerStats: any = null;
    playersLoading = false;
    statsLoading = false;

    constructor(private authService: AuthService,
                private performanceFileService: PerformanceFileService,
                private performanceService: PerformanceService,
                private planningApi: PlanningApiService,
                private chatbotGateway: ChatbotGatewayService) {
        // Initialisation
    }

    ngOnInit(): void {
        this.loadAverageEfficiency();
        this.loadActivePlayersCount();
        this.loadPerformanceFiles();
        this.loadMatchesPlayedCount();
    }

    // Computed properties
    get averageEfficiency(): number {
        const avg = this.players.reduce((sum, player) => sum + player.efficacite, 0) / this.players.length;
        return Math.round(avg);
    }

    // Deprecated local calc kept as fallback reference
    get activePlayers(): number {
        return this.players.filter(player => player.matchsJoues > 0).length;
    }

    get totalMatches(): number {
        return this.players.reduce((sum, player) => sum + player.matchsJoues, 0);
    }



    private loadActivePlayersCount(): void {
        this.authService.getActivePlayersCount().subscribe({
            next: (count: number) => {
                this.activePlayersFromApi = count ?? 0;
            },
            error: (err: any) => {
                console.error('Erreur lors du chargement du nombre de joueurs actifs:', err);
                // fallback to local mocked calculation so UI shows something
                this.activePlayersFromApi = this.activePlayers;
            }
        });
    }

    private loadAverageEfficiency(): void {
        this.performanceService.getAverageNotePercentage().subscribe({
            next: (pct: number) => {
                this.averageEfficiencyApi = Math.round(pct ?? 0);
            },
            error: (err: any) => {
                console.error('Erreur lors du chargement de la moyenne des notes:', err);
                // Fallback to local computed value from mocked players
                this.averageEfficiencyApi = this.averageEfficiency;
            }
        });
    }

    private loadMatchesPlayedCount(): void {
        this.performanceFileService.getCount().subscribe({
            next: (count: number) => {
                this.matchesPlayedTotal = count ?? 0;
            },
            error: (err: any) => {
                console.error('Erreur lors du chargement du nombre de fichiers de performance:', err);
                // fallback to local mocked calculation so UI shows quelque chose
                this.matchesPlayedTotal = 0;
            }
        });
    }

    // File management methods
    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.uploadFile(file);
        }
    }

    uploadFile(file: File) {
        // Uploader le binaire (multipart) pour stocker le contenu en base
        this.performanceFileService.uploadBinary(file).subscribe({
            next: (created: PerformanceFileDto) => {
                const mapped = this.mapDtoToUi(created);
                mapped.status = 'processed';
                this.uploadedFiles.unshift(mapped);
            },
            error: (err) => {
                console.error('Erreur lors de l\'upload du fichier', err);
            }
        });
    }



    onDragOver(event: DragEvent) {
        event.preventDefault();
        this.isDragOver = true;
    }

    onDragLeave(event: DragEvent) {
        event.preventDefault();
        this.isDragOver = false;
    }

    onDrop(event: DragEvent) {
        event.preventDefault();
        this.isDragOver = false;
        
        const files = event.dataTransfer?.files;
        if (files && files.length > 0) {
            this.uploadFile(files[0]);
        }
    }

    getFileExtension(filename: string): string {
        return filename.split('.').pop()?.toLowerCase() || '';
    }

    getFileIcon(type: string): string {
        const icons: { [key: string]: string } = {
            'xlsx': 'pi pi-file-excel',
            'xls': 'pi pi-file-excel',
            'csv': 'pi pi-file',
            'pdf': 'pi pi-file-pdf',
            'doc': 'pi pi-file-word',
            'docx': 'pi pi-file-word'
        };
        return icons[type] || 'pi pi-file';
    }

    getFileTypeLabel(type: string): string {
        const labels: { [key: string]: string } = {
            'xlsx': 'Excel',
            'xls': 'Excel',
            'csv': 'CSV',
            'pdf': 'PDF',
            'doc': 'Word',
            'docx': 'Word'
        };
        return labels[type] || 'Fichier';
    }

    getFileTypeClass(type: string): string {
        const classes: { [key: string]: string } = {
            'xlsx': 'bg-green-100 text-green-800',
            'xls': 'bg-green-100 text-green-800',
            'csv': 'bg-blue-100 text-blue-800',
            'pdf': 'bg-red-100 text-red-800',
            'doc': 'bg-blue-100 text-blue-800',
            'docx': 'bg-blue-100 text-blue-800'
        };
        return classes[type] || 'bg-gray-100 text-gray-800';
    }

    formatFileSize(bytes: number): string {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }



    viewFile(file: PerformanceFile) {
        // Ouvrir le fichier dans une nouvelle fenêtre
        if (file.url) {
            window.open(file.url, '_blank');
            return;
        }
        // Télécharger le binaire depuis l'API et l'ouvrir avec le bon MIME
        this.performanceFileService.downloadFile(file.id).subscribe({
            next: (blob: Blob) => {
                // Determine extension from type or fallback to file name
                let ext = (file.type || '').toLowerCase();
                if (!ext && file.name) {
                    const parts = file.name.split('.');
                    ext = parts.length > 1 ? parts.pop()!.toLowerCase() : '';
                }
                // Map to appropriate MIME type
                const mime = ext === 'pdf' ? 'application/pdf'
                            : ext === 'xlsx' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                            : ext === 'xls' ? 'application/vnd.ms-excel'
                            : ext === 'csv' ? 'text/csv'
                            : ext === 'docx' ? 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                            : ext === 'doc' ? 'application/msword'
                            : 'application/octet-stream';
                const typedBlob = new Blob([blob], { type: mime });
                const url = URL.createObjectURL(typedBlob);
                window.open(url, '_blank');
                setTimeout(() => URL.revokeObjectURL(url), 60_000);
            },
            error: (err) => {
                console.error('Erreur lors de l\'ouverture du fichier', err);
            }
        });
    }

    downloadFile(file: PerformanceFile) {
        if (file.url) {
            // Lien direct si disponible
            const link = document.createElement('a');
            link.href = file.url;
            link.download = file.name;
            link.style.display = 'none';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            return;
        }

        // Télécharger le binaire depuis la base via l'API
        this.performanceFileService.downloadFile(file.id).subscribe({
            next: (blob: Blob) => {
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = file.name;
                link.style.display = 'none';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                setTimeout(() => URL.revokeObjectURL(url), 1000);
            },
            error: (err) => {
                console.error('Erreur lors du téléchargement du fichier', err);
            }
        });
    }



    deleteFile(file: PerformanceFile, index: number) {
        if (!confirm(`Êtes-vous sûr de vouloir supprimer "${file.name}" ?`)) return;
        this.performanceFileService.deleteFile(file.id).subscribe({
            next: () => {
                this.uploadedFiles.splice(index, 1);
            },
            error: (err) => {
                console.error('Erreur lors de la suppression du fichier', err);
            }
        });
    }

    

    // Chargement depuis l'API
    private loadPerformanceFiles() {
        this.isLoadingFiles = true;
        this.performanceFileService.getFiles().subscribe({
            next: (list: PerformanceFileDto[]) => {
                this.uploadedFiles = list.map((d) => this.mapDtoToUi(d));
                this.isLoadingFiles = false;
            },
            error: (err) => {
                console.error('Erreur lors du chargement des fichiers de performance', err);
                this.isLoadingFiles = false;
            }
        });
    }

    private mapDtoToUi(d: PerformanceFileDto): PerformanceFile {
        return {
            id: d.id,
            name: d.originalName,
            type: (d.fileType || '').toLowerCase(),
            size: d.fileSize,
            uploadDate: d.uploadDate ? new Date(d.uploadDate) : new Date(),
            status: 'processed',
            url: d.filePath || undefined,
            description: undefined
        };
    }

    

    // --- Chatbot Gateway interactions ---
    loadPlayersForFile(fileId: number) {
        this.selectedFileId = fileId;
        this.playersForFile = [];
        this.selectedPlayer = null;
        this.playerStats = null;
        this.playersLoading = true;
        this.chatbotGateway.getPlayers(fileId, this.defaultTeam).subscribe({
            next: (players) => {
                this.playersForFile = players || [];
                this.playersLoading = false;
            },
            error: (err) => {
                console.error('Erreur lors de l\'extraction des joueurs depuis le PDF', err);
                this.playersLoading = false;
            }
        });
    }

    selectPlayer(player: string) {
        if (!this.selectedFileId) return;
        this.selectedPlayer = player;
        this.playerStats = null;
        this.statsLoading = true;
        this.chatbotGateway.getPlayerStats(this.selectedFileId, player, this.defaultTeam).subscribe({
            next: (stats) => {
                this.playerStats = stats;
                this.statsLoading = false;
            },
            error: (err) => {
                console.error('Erreur lors de la récupération des statistiques du joueur', err);
                this.statsLoading = false;
            }
        });
    }

}
