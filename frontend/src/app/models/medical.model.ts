export interface MedicalRecord {
  id: number;
  playerId: number;
  date: string;
  type: 'CONSULTATION' | 'EXAMEN' | 'TRAITEMENT' | 'SUIVI';
  description: string;
  diagnostic?: string;
  traitement?: string;
  medecin: string;
  statut: 'ACTIF' | 'RESOLU' | 'EN_COURS';
}

export interface Injury {
  id: number;
  playerId: number;
  dateBlessure: string;
  typeBlessure: string;
  localisation: string;
  gravite: 'LEGERE' | 'MODEREE' | 'GRAVE';
  description: string;
  traitement: string;
  dateRetour?: string;
  statut: 'ACTIF' | 'RESOLU';
}

export interface MedicalHistory {
  playerId: number;
  playerName: string;
  records: MedicalRecord[];
  injuries: Injury[];
}

export interface CreateMedicalRecordRequest {
  playerId: number;
  type: string;
  description: string;
  diagnostic?: string;
  traitement?: string;
  medecin: string;
}
