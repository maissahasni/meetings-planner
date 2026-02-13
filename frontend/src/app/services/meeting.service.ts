import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Meeting, MeetingRequest } from '../models/meeting.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MeetingService {
  private apiUrl = `${environment.apiUrl}/meetings`;

  constructor(private http: HttpClient) {}

  getAllMeetings(): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(this.apiUrl);
  }

  getMeetingById(id: number): Observable<Meeting> {
    return this.http.get<Meeting>(`${this.apiUrl}/${id}`);
  }

  getMeetingsByOrganizer(organizerId: number): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(`${this.apiUrl}/organizer/${organizerId}`);
  }

  getMeetingsByUser(userId: number): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(`${this.apiUrl}/user/${userId}`);
  }

  createMeeting(meeting: MeetingRequest): Observable<Meeting> {
    return this.http.post<Meeting>(this.apiUrl, meeting);
  }

  updateMeeting(id: number, meeting: MeetingRequest): Observable<Meeting> {
    return this.http.put<Meeting>(`${this.apiUrl}/${id}`, meeting);
  }

  deleteMeeting(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addParticipant(meetingId: number, userId: number): Observable<Meeting> {
    return this.http.post<Meeting>(`${this.apiUrl}/${meetingId}/participants/${userId}`, {});
  }

  removeParticipant(meetingId: number, userId: number): Observable<Meeting> {
    return this.http.delete<Meeting>(`${this.apiUrl}/${meetingId}/participants/${userId}`);
  }
}
