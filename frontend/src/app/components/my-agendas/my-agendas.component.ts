import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Agenda } from '../../models/agenda.model';
import { AgendaService } from '../../services/agenda.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-agendas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-agendas.component.html',
  styleUrls: ['./my-agendas.component.css']
})
export class MyAgendasComponent implements OnInit {
  agendas: Agenda[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private agendaService: AgendaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadMyAgendas();
  }

  loadMyAgendas(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.error = 'Please login to view your agenda';
      return;
    }

    this.loading = true;
    this.error = null;
    this.agendaService.getAgendasByUser(currentUser.id).subscribe({
      next: (data) => {
        this.agendas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load agendas';
        this.loading = false;
        console.error('Error loading agendas:', err);
      }
    });
  }

  deleteAgenda(id: number | undefined): void {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this agenda?')) {
      this.agendaService.deleteAgenda(id).subscribe({
        next: () => {
          this.loadMyAgendas();
        },
        error: (err) => {
          this.error = 'Failed to delete agenda';
          console.error('Error deleting agenda:', err);
        }
      });
    }
  }
}
