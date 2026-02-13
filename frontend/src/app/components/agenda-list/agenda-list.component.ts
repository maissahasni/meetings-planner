import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Agenda } from '../../models/agenda.model';
import { User } from '../../models/user.model';
import { AgendaService } from '../../services/agenda.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-agenda-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agenda-list.component.html',
  styleUrls: ['./agenda-list.component.css']
})
export class AgendaListComponent implements OnInit {
  agendas: Agenda[] = [];
  users: User[] = [];
  selectedUserId: number = 0;
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
        if (data.length > 0) {
          this.selectedUserId = data[0].id!;
          this.loadAgendas();
        }
      },
      error: (err) => {
        this.error = 'Failed to load users';
        console.error('Error loading users:', err);
      }
    });
  }

  loadAgendas(): void {
    if (!this.selectedUserId) return;
    
    this.loading = true;
    this.error = null;
    this.agendaService.getAgendasByUser(this.selectedUserId).subscribe({
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

  onUserChange(): void {
    this.loadAgendas();
  }

  deleteAgenda(id: number | undefined): void {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this agenda?')) {
      this.agendaService.deleteAgenda(id).subscribe({
        next: () => {
          this.loadAgendas();
        },
        error: (err) => {
          this.error = 'Failed to delete agenda';
          console.error('Error deleting agenda:', err);
        }
      });
    }
  }
}
