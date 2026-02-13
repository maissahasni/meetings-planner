import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MeetingService } from '../../services/meeting.service';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { MeetingRequest } from '../../models/meeting.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-meeting-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './meeting-form.component.html',
  styleUrls: ['./meeting-form.component.css']
})
export class MeetingFormComponent implements OnInit {
  meeting: MeetingRequest = {
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    organizerId: 0,
    participantIds: []
  };
  
  users: User[] = [];
  selectedParticipants: number[] = [];
  error: string | null = null;
  submitting = false;
  meetingId: number | null = null;
  isEditMode = false;

  constructor(
    private meetingService: MeetingService,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.meeting.organizerId = currentUser.id;
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.meetingId = +id;
      this.isEditMode = true;
      this.loadMeeting(this.meetingId);
    }
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

  toggleParticipant(userId: number): void {
    const index = this.selectedParticipants.indexOf(userId);
    if (index > -1) {
      this.selectedParticipants.splice(index, 1);
    } else {
      this.selectedParticipants.push(userId);
    }
  }

  isParticipantSelected(userId: number): boolean {
    return this.selectedParticipants.includes(userId);
  }

  loadMeeting(id: number): void {
    this.meetingService.getMeetingById(id).subscribe({
      next: (meeting) => {
        this.meeting = {
          title: meeting.title,
          description: meeting.description || '',
          startTime: meeting.startTime,
          endTime: meeting.endTime,
          organizerId: meeting.organizer.id!,
          participantIds: meeting.participants.map(p => p.id!)
        };
        this.selectedParticipants = [...this.meeting.participantIds];
      },
      error: (err) => {
        this.error = 'Failed to load meeting';
        console.error('Error loading meeting:', err);
      }
    });
  }

  onSubmit(): void {
    this.submitting = true;
    this.error = null;
    this.meeting.participantIds = this.selectedParticipants;

    if (this.isEditMode && this.meetingId) {
      this.meetingService.updateMeeting(this.meetingId, this.meeting).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/my-meetings']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to update meeting';
          this.submitting = false;
          console.error('Error updating meeting:', err);
        }
      });
    } else {
      this.meetingService.createMeeting(this.meeting).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/my-meetings']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to create meeting';
          this.submitting = false;
          console.error('Error creating meeting:', err);
        }
      });
    }
  }
}
