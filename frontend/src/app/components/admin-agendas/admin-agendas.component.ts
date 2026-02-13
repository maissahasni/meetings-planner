import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Agenda } from '../../models/agenda.model';
import { User } from '../../models/user.model';
import { AgendaService } from '../../services/agenda.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-admin-agendas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-agendas.component.html',
  styleUrls: ['./admin-agendas.component.css']
})
export class AdminAgendasComponent implements OnInit {
  agendas: Agenda[] = [];
  users: User[] = [];
  selectedUserId: number | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private agendaService: AgendaService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (err) => {
        this.error = 'Failed to load users';
        console.error('Error loading users:', err);
      }
    });
  }

  loadAgendasForUser(): void {
    const userId = this.selectedUserId !== null ? Number(this.selectedUserId) : 0;
    if (!userId) {
      this.agendas = [];
      return;
    }

    this.loading = true;
    this.error = null;
    this.agendaService.getAgendasByUser(userId).subscribe({
      next: (data) => {
        this.agendas = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load agendas';
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
          this.loadAgendasForUser();
        },
        error: (err) => {
          this.error = 'Failed to delete agenda';
          console.error('Error deleting agenda:', err);
        }
      });
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString();
  }
}
