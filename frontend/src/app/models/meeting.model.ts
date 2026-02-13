import { User } from './user.model';

export interface Meeting {
  id?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  organizer: User;
  participants: User[];
}

export interface MeetingRequest {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  organizerId: number;
  participantIds: number[];
}
