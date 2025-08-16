import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { CommonModule } from '@angular/common';

@Component({
    standalone: true,
    selector: 'app-media-galleria',
    imports: [ButtonModule, MenuModule, CommonModule],
    template: `<div class="card">
        <div class="flex items-center justify-between mb-6">
            <div class="font-semibold text-xl">Media Galleria</div>
            <div>
                <button pButton type="button" icon="pi pi-ellipsis-v" class="p-button-rounded p-button-text p-button-plain" (click)="menu.toggle($event)"></button>
                <p-menu #menu [popup]="true" [model]="items"></p-menu>
            </div>
        </div>

        <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="relative group cursor-pointer">
                <div class="w-full h-24 bg-gradient-to-br from-blue-400 to-purple-600 rounded-lg flex items-center justify-center">
                    <i class="pi pi-image text-2xl text-white"></i>
                </div>
                <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all duration-200 rounded-lg flex items-center justify-center">
                    <span class="text-white opacity-0 group-hover:opacity-100 text-sm font-medium">Match Photos</span>
                </div>
            </div>
            <div class="relative group cursor-pointer">
                <div class="w-full h-24 bg-gradient-to-br from-green-400 to-blue-600 rounded-lg flex items-center justify-center">
                    <i class="pi pi-video text-2xl text-white"></i>
                </div>
                <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all duration-200 rounded-lg flex items-center justify-center">
                    <span class="text-white opacity-0 group-hover:opacity-100 text-sm font-medium">Vidéos</span>
                </div>
            </div>
        </div>

        <div class="space-y-4">
            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
                <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center mr-3">
                    <i class="pi pi-image text-red-500"></i>
                </div>
                <div class="flex-1">
                    <div class="text-sm font-medium text-gray-900">Photos Match vs ES Tunis</div>
                    <div class="text-xs text-gray-500">15 photos • Il y a 2 jours</div>
                </div>
                <button class="text-blue-600 hover:text-blue-800 text-sm font-medium">Voir</button>
            </div>

            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
                <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                    <i class="pi pi-video text-blue-500"></i>
                </div>
                <div class="flex-1">
                    <div class="text-sm font-medium text-gray-900">Highlights Entraînement</div>
                    <div class="text-xs text-gray-500">3 vidéos • Il y a 1 semaine</div>
                </div>
                <button class="text-blue-600 hover:text-blue-800 text-sm font-medium">Voir</button>
            </div>

            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
                <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center mr-3">
                    <i class="pi pi-image text-green-500"></i>
                </div>
                <div class="flex-1">
                    <div class="text-sm font-medium text-gray-900">Portraits Joueurs</div>
                    <div class="text-xs text-gray-500">8 photos • Il y a 2 semaines</div>
                </div>
                <button class="text-blue-600 hover:text-blue-800 text-sm font-medium">Voir</button>
            </div>
        </div>

        <div class="mt-6 pt-4 border-t border-gray-200">
            <button class="w-full p-3 text-center text-blue-600 hover:text-blue-800 font-medium rounded-lg border border-blue-200 hover:border-blue-300 transition-colors">
                <i class="pi pi-plus mr-2"></i>
                Ajouter du contenu
            </button>
        </div>
    </div>`
})
export class MediaGalleria {
    items = [
        { label: 'Ajouter Photos', icon: 'pi pi-fw pi-image' },
        { label: 'Ajouter Vidéos', icon: 'pi pi-fw pi-video' },
        { label: 'Organiser', icon: 'pi pi-fw pi-folder' },
        { label: 'Partager', icon: 'pi pi-fw pi-share-alt' }
    ];
}
