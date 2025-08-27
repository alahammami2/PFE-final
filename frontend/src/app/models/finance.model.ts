export interface Transaction {
  id: number;
  date: string;
  montant: number;
  type: 'ENTREE' | 'SORTIE';
  categorie: string;
  description: string;
  statut: 'EN_ATTENTE' | 'APPROUVE' | 'REJETE';
  approbateur?: string;
}

export interface Budget {
  id: number;
  annee: number;
  montantTotal: number;
  montantUtilise: number;
  montantRestant: number;
  categorie: string;
}

export interface FinancialReport {
  periode: string;
  totalEntrees: number;
  totalSorties: number;
  solde: number;
  transactions: Transaction[];
}

export interface CreateTransactionRequest {
  montant: number;
  type: string;
  categorie: string;
  description: string;
}
