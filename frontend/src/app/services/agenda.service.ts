import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Agenda, AgendaRequest } from '../models/agenda.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AgendaService {
  private apiUrl = `${environment.apiUrl}/agendas`;

  constructor(private http: HttpClient) {}

  getAgendaById(id: number): Observable<Agenda> {
    return this.http.get<Agenda>(`${this.apiUrl}/${id}`);
  }

  getAgendasByUser(userId: number): Observable<Agenda[]> {
    return this.http.get<Agenda[]>(`${this.apiUrl}/user/${userId}`);
  }

  createAgenda(agenda: AgendaRequest): Observable<Agenda> {
    return this.http.post<Agenda>(this.apiUrl, agenda);
  }

  updateAgenda(id: number, agenda: AgendaRequest): Observable<Agenda> {
    return this.http.put<Agenda>(`${this.apiUrl}/${id}`, agenda);
  }

  deleteAgenda(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
