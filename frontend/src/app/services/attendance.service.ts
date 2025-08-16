import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface PresenceRecord {
    id: number;
    membre: string; // Nom Prénom
    role: string;
    date: string; // dd-mm-yyyy
    evenement: string; // titre
}

export interface AbsenceRecord {
    id: number;
    membre: string;
    role: string;
    dateDebut: string; // dd-mm-yyyy
    dateFin: string; // dd-mm-yyyy
    type: string; // Maladie, Personnel, etc.
    justification?: string;
    statut: 'En attente' | 'Approuvée' | 'Refusée';
}

@Injectable({ providedIn: 'root' })
export class AttendanceService {
    private presencesSource = new BehaviorSubject<PresenceRecord[]>([]);
    public presences$ = this.presencesSource.asObservable();

    private absencesSource = new BehaviorSubject<AbsenceRecord[]>([]);
    public absences$ = this.absencesSource.asObservable();

    addPresence(record: Omit<PresenceRecord, 'id'>) {
        const current = this.presencesSource.getValue();
        const nextId = (current[0]?.id || 0) + 1;
        const newRec: PresenceRecord = { id: nextId, ...record };
        this.presencesSource.next([newRec, ...current]);
    }

    addAbsence(record: Omit<AbsenceRecord, 'id'>) {
        const current = this.absencesSource.getValue();
        const nextId = (current[0]?.id || 0) + 1;
        const newRec: AbsenceRecord = { id: nextId, ...record };
        this.absencesSource.next([newRec, ...current]);
    }
}


