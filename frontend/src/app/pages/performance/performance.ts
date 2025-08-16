import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
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

interface ChatMessage {
    id: number;
    text: string;
    isUser: boolean;
    timestamp: Date;
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
        AvatarModule
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
                        <div class="text-2xl font-bold text-green-900">{{ averageEfficiency }}%</div>
                    </div>
                </div>
            </div>
            <div class="col-span-12 lg:col-span-4">
                <div class="card bg-purple-50 border-l-4 border-purple-500">
                    <div class="p-4">
                        <div class="text-purple-600 text-sm font-medium">Joueurs Actifs</div>
                        <div class="text-2xl font-bold text-purple-900">{{ activePlayers }}</div>
                    </div>
                </div>
            </div>
            <div class="col-span-12 lg:col-span-4">
                <div class="card bg-orange-50 border-l-4 border-orange-500">
                    <div class="p-4">
                        <div class="text-orange-600 text-sm font-medium">Matchs Joués</div>
                        <div class="text-2xl font-bold text-orange-900">{{ totalMatches }}</div>
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
                     <button 
                         (click)="activeTab = 'chat'" 
                         [class]="activeTab === 'chat' ? 'border-b-2 border-blue-500 text-blue-600 px-4 py-2 font-medium' : 'text-gray-500 hover:text-gray-700 px-4 py-2'">
                         Chat Performance
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
                        <div class="flex items-center gap-3">
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
                                                    icon="pi pi-eye" 
                                                    size="small" 
                                                    [text]="true" 
                                                    (onClick)="viewFile(file)"
                                                    pTooltip="Voir le fichier">
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



                
                 
                 <!-- Chat Performance -->
                 <div *ngIf="activeTab === 'chat'" class="card">
                     <div class="flex items-center justify-between mb-6">
                         <div>
                             <h2 class="text-xl font-semibold text-gray-900">Assistant Performance IA</h2>
                             <p class="text-gray-600 mt-1">Posez vos questions sur la performance de l'équipe</p>
                         </div>
                         <div class="flex items-center gap-2">
                             <p-button 
                                 icon="pi pi-refresh" 
                                 label="Nouvelle conversation" 
                                 [outlined]="true" 
                                 (onClick)="startNewChat()">
                             </p-button>
                         </div>
                     </div>

                                           <!-- Zone de chat -->
                      <div class="bg-gray-50 rounded-lg p-4 mb-4 h-[550px] overflow-y-auto">
                         <div *ngIf="chatMessages.length === 0" class="text-center text-gray-500 py-8">
                             <i class="pi pi-comments text-4xl mb-4 block"></i>
                             <p class="text-lg font-medium">Démarrez une conversation</p>
                             <p class="text-sm">Posez des questions sur la performance de l'équipe</p>
                         </div>
                         
                         <div *ngFor="let message of chatMessages" class="mb-4">
                             <div [class]="message.isUser ? 'flex justify-end' : 'flex justify-start'">
                                 <div [class]="message.isUser ? 'bg-blue-500 text-white' : 'bg-white text-gray-800'" 
                                      class="max-w-xs lg:max-w-md px-4 py-2 rounded-lg shadow-sm">
                                     <div class="flex items-center gap-2 mb-1">
                                         <i [class]="message.isUser ? 'pi pi-user' : 'pi pi-robot'" class="text-sm"></i>
                                         <span class="text-xs font-medium">
                                             {{ message.isUser ? 'Vous' : 'Assistant Performance' }}
                                         </span>
                                     </div>
                                     <p class="text-sm">{{ message.text }}</p>
                                     <div class="text-xs opacity-70 mt-1">
                                         {{ message.timestamp | date:'HH:mm' }}
                                     </div>
                                 </div>
                             </div>
                         </div>
                     </div>

                     <!-- Zone de saisie -->
                     <div class="flex gap-3">
                         <div class="flex-1">
                             <input 
                                 pInputText 
                                 type="text" 
                                 [(ngModel)]="userInput" 
                                 placeholder="Posez votre question sur la performance..."
                                 (keyup.enter)="sendMessage()"
                                 class="w-full" />
                         </div>
                         <p-button 
                             icon="pi pi-send" 
                             label="Envoyer" 
                             (onClick)="sendMessage()"
                             [disabled]="!userInput.trim()">
                         </p-button>
                     </div>

                     <!-- Suggestions rapides -->
                     <div class="mt-4">
                         <p class="text-sm text-gray-600 mb-2">Suggestions :</p>
                         <div class="flex flex-wrap gap-2">
                             <p-button 
                                 *ngFor="let suggestion of quickSuggestions" 
                                 [label]="suggestion" 
                                 size="small" 
                                 [outlined]="true"
                                 (onClick)="useSuggestion(suggestion)">
                             </p-button>
                         </div>
                     </div>
                 </div>
                 
             </div>
         </div>


    `
})
export class PerformancePage {
    // Data
    players: PlayerPerformance[] = [
        {
            id: 1,
            nom: 'Ben Ali',
            prenom: 'Ahmed',
            poste: 'Attaquant',
            equipe: 'Séniors Homme',
            matchsJoues: 15,
            pointsMarques: 89,
            attaques: 120,
            attaquesReussies: 78,
            blocs: 45,
            blocsReussis: 23,
            services: 67,
            servicesReussis: 45,
            passes: 89,
            passesReussies: 67,
            reception: 56,
            receptionReussie: 42,
            efficacite: 75
        },
        {
            id: 2,
            nom: 'Trabelsi',
            prenom: 'Sara',
            poste: 'Passeur',
            equipe: 'Séniors Fille',
            matchsJoues: 12,
            pointsMarques: 45,
            attaques: 67,
            attaquesReussies: 34,
            blocs: 23,
            blocsReussis: 12,
            services: 45,
            servicesReussis: 32,
            passes: 156,
            passesReussies: 134,
            reception: 34,
            receptionReussie: 28,
            efficacite: 82
        }
    ];

    // Season selector
    selectedSeason = '2025-2026';
    
    // Active tab
    activeTab = 'fichiers';
    
    // Chat management
    chatMessages: ChatMessage[] = [];
    userInput = '';
    quickSuggestions = [
        'Quel est le joueur le plus performant ?',
        'Comment améliorer l\'efficacité de l\'équipe ?',
        'Quels sont les points forts de l\'équipe ?',
        'Analyse des statistiques de la saison'
    ];
    
    // File management
    uploadedFiles: PerformanceFile[] = [
        {
            id: 1,
            name: 'Statistiques_Match_1.xlsx',
            type: 'xlsx',
            size: 245760,
            description: 'Statistiques du premier match de la saison',
            uploadDate: new Date('2025-01-15'),
            status: 'processed'
        },
        {
            id: 2,
            name: 'Performance_Equipe_2025.pdf',
            type: 'pdf',
            size: 1024000,
            description: 'Rapport de performance de l\'équipe',
            uploadDate: new Date('2025-01-10'),
            status: 'processed'
        }
    ];
    isDragOver = false;



    // Options
    seasons = [
        { label: 'Saison 2025-2026', value: '2025-2026' }
    ];



    constructor() {
        // Initialisation
    }

    // Computed properties
    get averageEfficiency(): number {
        const avg = this.players.reduce((sum, player) => sum + player.efficacite, 0) / this.players.length;
        return Math.round(avg);
    }

    get activePlayers(): number {
        return this.players.filter(player => player.matchsJoues > 0).length;
    }

    get totalMatches(): number {
        return this.players.reduce((sum, player) => sum + player.matchsJoues, 0);
    }



    // File management methods
    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.uploadFile(file);
        }
    }

    uploadFile(file: File) {
        const newFile: PerformanceFile = {
            id: Date.now(),
            name: file.name,
            type: this.getFileExtension(file.name),
            size: file.size,
            description: `Fichier uploadé le ${new Date().toLocaleDateString()}`,
            uploadDate: new Date(),
            status: 'pending'
        };
        
        this.uploadedFiles.unshift(newFile);
        
        // Simuler le traitement
        setTimeout(() => {
            newFile.status = 'processed';
        }, 2000);
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
        } else {
            // Simuler l'ouverture du fichier (pour les fichiers mockés)
            const fileUrl = URL.createObjectURL(new Blob(['Contenu du fichier ' + file.name], { type: 'text/plain' }));
            window.open(fileUrl, '_blank');
            // Libérer l'URL après utilisation
            setTimeout(() => URL.revokeObjectURL(fileUrl), 1000);
        }
    }

    downloadFile(file: PerformanceFile) {
        // Créer un lien de téléchargement et cliquer dessus
        const link = document.createElement('a');
        
        if (file.url) {
            link.href = file.url;
        } else {
            // Simuler le téléchargement (pour les fichiers mockés)
            const content = 'Contenu du fichier ' + file.name + '\n\nCeci est un fichier de performance pour l\'équipe de volley-ball.';
            const blob = new Blob([content], { type: 'text/plain' });
            link.href = URL.createObjectURL(blob);
        }
        
        link.download = file.name;
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Libérer l'URL si elle a été créée
        if (!file.url) {
            setTimeout(() => URL.revokeObjectURL(link.href), 1000);
        }
    }



    deleteFile(file: PerformanceFile, index: number) {
        if (confirm(`Êtes-vous sûr de vouloir supprimer "${file.name}" ?`)) {
            this.uploadedFiles.splice(index, 1);
        }
    }

    // Chat methods
    startNewChat() {
        this.chatMessages = [];
        this.userInput = '';
    }

    sendMessage() {
        if (!this.userInput.trim()) return;

        // Ajouter le message de l'utilisateur
        const userMessage: ChatMessage = {
            id: Date.now(),
            text: this.userInput.trim(),
            isUser: true,
            timestamp: new Date()
        };
        this.chatMessages.push(userMessage);

        const userQuestion = this.userInput.trim();
        this.userInput = '';

        // Simuler la réponse de l'IA
        setTimeout(() => {
            const aiResponse = this.generateAIResponse(userQuestion);
            const aiMessage: ChatMessage = {
                id: Date.now() + 1,
                text: aiResponse,
                isUser: false,
                timestamp: new Date()
            };
            this.chatMessages.push(aiMessage);
        }, 1000);
    }

    useSuggestion(suggestion: string) {
        this.userInput = suggestion;
        this.sendMessage();
    }

    private generateAIResponse(question: string): string {
        const lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.includes('joueur') && lowerQuestion.includes('performant')) {
            return `Selon nos statistiques, ${this.players[0].prenom} ${this.players[0].nom} est actuellement le joueur le plus performant avec une efficacité de ${this.players[0].efficacite}% et ${this.players[0].pointsMarques} points marqués.`;
        }
        
        if (lowerQuestion.includes('améliorer') && lowerQuestion.includes('efficacité')) {
            return `Pour améliorer l'efficacité de l'équipe, je recommande de :\n1. Travailler sur la réception des services\n2. Améliorer la précision des attaques\n3. Optimiser la communication entre joueurs\n4. Analyser les statistiques après chaque match`;
        }
        
        if (lowerQuestion.includes('points forts')) {
            return `Les points forts de l'équipe sont :\n• Service : ${this.players.reduce((sum, p) => sum + p.servicesReussis, 0)} services réussis\n• Attaque : ${this.players.reduce((sum, p) => sum + p.attaquesReussies, 0)} attaques réussies\n• Efficacité moyenne : ${this.averageEfficiency}%`;
        }
        
        if (lowerQuestion.includes('statistiques') || lowerQuestion.includes('saison')) {
            return `Voici un résumé de la saison :\n• ${this.players.length} joueurs actifs\n• ${this.totalMatches} matchs joués au total\n• Efficacité moyenne de l'équipe : ${this.averageEfficiency}%\n• Meilleur marqueur : ${this.players[0].prenom} ${this.players[0].nom} avec ${this.players[0].pointsMarques} points`;
        }
        
        return `Je suis votre assistant performance spécialisé volley-ball. Je peux vous aider à analyser les statistiques de l'équipe, identifier les points d'amélioration et suivre les performances des joueurs. Posez-moi des questions spécifiques pour obtenir des réponses détaillées.`;
    }
}
