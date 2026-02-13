import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Role, UserRequest } from '../../models/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
  user: UserRequest = {
    name: '',
    email: '',
    password: '',
    role: Role.USER
  };
  
  userId: number | null = null;
  isEditMode = false;
  roles = Object.values(Role);
  error: string | null = null;
  submitting = false;

  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId = +id;
      this.isEditMode = true;
      this.loadUser(this.userId);
    }
  }

  loadUser(id: number): void {
    this.userService.getUserById(id).subscribe({
      next: (user) => {
        this.user = {
          name: user.name,
          email: user.email,
          password: '', // Don't load password for security
          role: user.role
        };
      },
      error: (err) => {
        this.error = 'Failed to load user';
        console.error('Error loading user:', err);
      }
    });
  }

  onSubmit(): void {
    this.submitting = true;
    this.error = null;

    if (this.isEditMode && this.userId) {
      this.userService.updateUser(this.userId, this.user).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/users']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to update user';
          this.submitting = false;
          console.error('Error updating user:', err);
        }
      });
    } else {
      this.userService.createUser(this.user).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/users']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to create user';
          this.submitting = false;
          console.error('Error creating user:', err);
        }
      });
    }
  }
}
