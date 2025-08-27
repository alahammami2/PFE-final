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
import { RoleGuard } from './app/guards/role.guard';

export const appRoutes: Routes = [
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
    {
        path: '',
        component: AppLayout,
        children: [
            { path: 'dashboard', component: Dashboard },
            { path: 'calendrier', component: CalendarPage, canActivate: [RoleGuard], data: { roles: ['JOUEUR', 'COACH', 'ADMIN', 'INVITE', 'STAF_MEDICAL'] } },
            { path: 'demandes-administratives', component: DemandesAdministratives, canActivate: [RoleGuard], data: { roles: ['COACH', 'ADMIN', 'JOUEUR', 'STAF_MEDICAL'] } },
            { path: 'effectif', component: EffectifPage, canActivate: [RoleGuard], data: { roles: ['COACH', 'ADMIN', 'INVITE'] } },
            { path: 'centre-medical', component: MedicalCenterPage, canActivate: [RoleGuard], data: { roles: ['COACH', 'ADMIN', 'JOUEUR', 'STAF_MEDICAL'] } },
            { path: 'finance', component: FinancePage, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'RESPONSABLE_FINANCIER'] } },
            { path: 'absence', component: AbsencePage, canActivate: [RoleGuard], data: { roles: ['COACH', 'ADMIN', 'INVITE', 'RESPONSABLE_FINANCIER', 'JOUEUR'] } },
            { path: 'performance', component: PerformancePage, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'JOUEUR', 'COACH', 'INVITE'] } },
            { path: 'pages', loadChildren: () => import('./app/pages/pages.routes') }
        ]
    },
    { path: 'auth', loadChildren: () => import('./app/pages/auth/auth.routes') },
    { path: '**', redirectTo: '/notfound' }
];
