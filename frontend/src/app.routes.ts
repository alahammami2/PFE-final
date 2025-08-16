import { Routes } from '@angular/router';
import { AppLayout } from './app/layout/component/app.layout';
import { Dashboard } from './app/pages/dashboard/dashboard';
import { CalendarPage } from './app/pages/calendar/calendar';
import { DemandesAdministratives } from './app/pages/demandes-administratives/demandes-administratives';
import { EffectifPage } from './app/pages/effectif/effectif';
import { MedicalCenterPage } from './app/pages/medical-center/medical-center';
import { FinancePage } from './app/pages/finance/finance';
import { AbsencePage } from './app/pages/absence/absence';
import { PerformancePage } from './app/pages/performance/performance';

export const appRoutes: Routes = [
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
    {
        path: '',
        component: AppLayout,
        children: [
            { path: 'dashboard', component: Dashboard },
            { path: 'calendrier', component: CalendarPage },
            { path: 'demandes-administratives', component: DemandesAdministratives },
            { path: 'effectif', component: EffectifPage },
            { path: 'centre-medical', component: MedicalCenterPage },
            { path: 'finance', component: FinancePage },
            { path: 'absence', component: AbsencePage },
            { path: 'performance', component: PerformancePage },
            { path: 'pages', loadChildren: () => import('./app/pages/pages.routes') }
        ]
    },
    { path: 'auth', loadChildren: () => import('./app/pages/auth/auth.routes') },
    { path: '**', redirectTo: '/notfound' }
];
