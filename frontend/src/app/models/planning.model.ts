export interface Event {
  id: number;
  titre: string;
  description: string;
  dateDebut: string;
  dateFin: string;
  type: 'ENTRAINEMENT' | 'MATCH' | 'REUNION' | 'AUTRE';
  lieu: string;
  participants: number[];
  statut: 'PLANIFIE' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
}

export interface Match {
  id: number;
  equipeAdverse: string;
  date: string;
  lieu: string;
  type: 'DOMICILE' | 'EXTERIEUR';
  statut: 'PLANIFIE' | 'EN_COURS' | 'TERMINE';
  scoreEquipe?: string;
  scoreAdverse?: string;
  joueurs: number[];
}

export interface Training {
  id: number;
  titre: string;
  description: string;
  date: string;
  duree: number; // en minutes
  type: 'TECHNIQUE' | 'TACTIQUE' | 'PHYSIQUE' | 'MENTAL';
  entraineur: string;
  joueurs: number[];
}

export interface CreateEventRequest {
  titre: string;
  description: string;
  dateDebut: string;
  dateFin: string;
  type: string;
  lieu: string;
  participants: number[];
}
