import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SignupRequest } from '../../models/auth.model';
import { Role } from '../../models/user.model';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupData: SignupRequest = {
    name: '',
    email: '',
    password: '',
    role: Role.USER
  };
  
  roles = Object.values(Role);
  error: string | null = null;
  submitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.submitting = true;
    this.error = null;

    this.authService.signup(this.signupData).subscribe({
      next: (response) => {
        this.submitting = false;
        // Redirect based on role
        if (response.role === 'ADMIN') {
          this.router.navigate(['/users']);
        } else {
          this.router.navigate(['/my-meetings']);
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Signup failed. Please try again.';
        this.submitting = false;
      }
    });
  }
}
