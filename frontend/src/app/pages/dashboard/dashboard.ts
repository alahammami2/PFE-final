import { Component } from '@angular/core';
import { StatsWidget } from './components/statswidget';
import { ProchainsMatchs } from './components/prochainsmatchs';
import { MeilleursPerformeurs } from './components/meilleursperformeurs';
import { ClassementEquipe } from './components/classementequipe';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [StatsWidget, ProchainsMatchs, MeilleursPerformeurs, ClassementEquipe],
    template: `
        <div class="grid grid-cols-12 gap-8">
            <app-stats-widget class="contents" />
            <div class="col-span-12 xl:col-span-6">
                <app-prochains-matchs />
                <app-meilleurs-performeurs />
            </div>
            <div class="col-span-12 xl:col-span-6">
                <app-classement-equipe />
            </div>
        </div>
    `
})
export class Dashboard {}

