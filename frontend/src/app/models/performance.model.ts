export interface Performance {
  id: number;
  playerId: number;
  matchId: number;
  date: string;
  points: number;
  assists: number;
  blocks: number;
  serves: number;
  digs: number;
  reception: number;
  totalScore: number;
}

export interface PerformanceStats {
  playerId: number;
  playerName: string;
  totalMatches: number;
  averagePoints: number;
  averageAssists: number;
  averageBlocks: number;
  averageServes: number;
  averageDigs: number;
  averageReception: number;
  totalScore: number;
}

export interface CreatePerformanceRequest {
  playerId: number;
  matchId: number;
  date: string;
  points: number;
  assists: number;
  blocks: number;
  serves: number;
  digs: number;
  reception: number;
}
