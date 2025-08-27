import { Component } from '@angular/core';
import { StatsWidget } from './components/statswidget';
import { ProchainsMatchs } from './components/prochainsmatchs';
import { Chatbot } from './components/chatbot';
import { ClassementEquipe } from './components/classementequipe';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [StatsWidget, ProchainsMatchs, Chatbot, ClassementEquipe],
    template: `
        <div class="grid grid-cols-12 gap-8">
            <!-- Stats Widgets -->
            <app-stats-widget class="col-span-12" />
            
            <!-- Contenu principal -->
            <div class="col-span-12 xl:col-span-6">
                <app-prochains-matchs />
            </div>
            <div class="col-span-12 xl:col-span-6">
                <app-classement-equipe />
            </div>

            <!-- Assistant en bas de page -->
            <div class="col-span-12">
                <app-chatbot />
            </div>
        </div>
    `
})
export class Dashboard {}

