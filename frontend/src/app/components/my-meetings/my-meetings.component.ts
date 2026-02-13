import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Meeting } from '../../models/meeting.model';
import { MeetingService } from '../../services/meeting.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-meetings',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-meetings.component.html',
  styleUrls: ['./my-meetings.component.css']
})
export class MyMeetingsComponent implements OnInit {
  meetings: Meeting[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private meetingService: MeetingService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadMyMeetings();
  }

  loadMyMeetings(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.error = 'Please login to view your meetings';
      return;
    }

    this.loading = true;
    this.error = null;
    this.meetingService.getMeetingsByUser(currentUser.id).subscribe({
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
          this.loadMyMeetings();
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

  getParticipantNames(meeting: Meeting): string {
    if (!meeting.participants || meeting.participants.length === 0) {
      return 'None';
    }
    return meeting.participants.map(p => p.name).join(', ');
  }
}
