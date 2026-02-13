export enum AgendaStatus {
  FREE = 'FREE',
  BUSY = 'BUSY'
}

export interface Agenda {
  id?: number;
  userId: number;
  userName: string;
  meetingId?: number;
  meetingTitle?: string;
  date: string;
  startTime: string;
  endTime: string;
  status: AgendaStatus;
}

export interface AgendaRequest {
  userId: number;
  date: string;
  startTime: string;
  endTime: string;
  status: AgendaStatus;
}
