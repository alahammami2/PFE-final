export interface AdminRequest {
  id: number;
  type: 'CONGE' | 'MATERIEL' | 'FORMATION' | 'AUTRE';
  titre: string;
  description: string;
  demandeur: number;
  dateDemande: string;
  dateDebut?: string;
  dateFin?: string;
  statut: 'EN_ATTENTE' | 'APPROUVE' | 'REJETE';
  approbateur?: number;
  dateApprobation?: string;
  commentaire?: string;
  priorite: 'BASSE' | 'MOYENNE' | 'HAUTE' | 'URGENTE';
}

export interface CreateAdminRequestRequest {
  type: string;
  titre: string;
  description: string;
  dateDebut?: string;
  dateFin?: string;
  priorite: string;
}

export interface UpdateAdminRequestRequest {
  statut: string;
  commentaire?: string;
  approbateur: number;
}
