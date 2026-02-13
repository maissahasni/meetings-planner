import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { MeetingService } from '../../services/meeting.service';
import { UserService } from '../../services/user.service';
import { MeetingRequest } from '../../models/meeting.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-admin-meeting-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-meeting-form.component.html',
  styleUrls: ['./admin-meeting-form.component.css']
})
export class AdminMeetingFormComponent implements OnInit {
  meeting: MeetingRequest = {
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    organizerId: 0,
    participantIds: []
  };
  
  users: User[] = [];
  selectedParticipants: { [key: number]: boolean } = {};
  meetingId: number | null = null;
  isEditMode = false;
  error: string | null = null;
  submitting = false;

  constructor(
    private meetingService: MeetingService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    
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
        
        // Set selected participants
        meeting.participants.forEach(p => {
          if (p.id) {
            this.selectedParticipants[p.id] = true;
          }
        });
      },
      error: (err) => {
        this.error = 'Failed to load meeting';
        console.error('Error loading meeting:', err);
      }
    });
  }

  toggleParticipant(userId: number): void {
    this.selectedParticipants[userId] = !this.selectedParticipants[userId];
  }

  onSubmit(): void {
    this.submitting = true;
    this.error = null;

    // Collect selected participant IDs
    this.meeting.participantIds = Object.keys(this.selectedParticipants)
      .filter(key => this.selectedParticipants[+key])
      .map(key => +key);

    if (this.isEditMode && this.meetingId) {
      this.meetingService.updateMeeting(this.meetingId, this.meeting).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/admin/meetings']);
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
          this.router.navigate(['/admin/meetings']);
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
