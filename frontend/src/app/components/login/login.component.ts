import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials: LoginRequest = {
    email: '',
    password: ''
  };
  
  error: string | null = null;
  submitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.submitting = true;
    this.error = null;

    this.authService.login(this.credentials).subscribe({
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
        this.error = err.error?.message || 'Login failed. Please check your credentials.';
        this.submitting = false;
      }
    });
  }
}
