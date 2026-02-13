import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Meeting } from '../../models/meeting.model';
import { MeetingService } from '../../services/meeting.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-meeting-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './meeting-list.component.html',
  styleUrls: ['./meeting-list.component.css']
})
export class MeetingListComponent implements OnInit {
  meetings: Meeting[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private meetingService: MeetingService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadMeetings();
  }

  loadMeetings(): void {
    this.loading = true;
    this.error = null;
    this.meetingService.getAllMeetings().subscribe({
      next: (data) => {
        this.meetings = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load meetings';
        this.loading = false;
        console.error('Error loading meetings:', err);
      }
    });
  }

  deleteMeeting(id: number | undefined): void {
    if (!id) return;
    
    if (confirm('Are you sure you want to delete this meeting?')) {
      this.meetingService.deleteMeeting(id).subscribe({
        next: () => {
          this.loadMeetings();
        },
        error: (err) => {
          this.error = 'Failed to delete meeting';
          console.error('Error deleting meeting:', err);
        }
      });
    }
  }

  formatDateTime(dateTime: string): string {
    return new Date(dateTime).toLocaleString();
  }
}
