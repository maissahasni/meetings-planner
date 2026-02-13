import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { UserFormComponent } from './components/user-form/user-form.component';
import { MeetingListComponent } from './components/meeting-list/meeting-list.component';
import { MeetingFormComponent } from './components/meeting-form/meeting-form.component';
import { AgendaListComponent } from './components/agenda-list/agenda-list.component';
import { MyMeetingsComponent } from './components/my-meetings/my-meetings.component';
import { MyAgendasComponent } from './components/my-agendas/my-agendas.component';
import { AdminMeetingsComponent } from './components/admin-meetings/admin-meetings.component';
import { AdminAgendasComponent } from './components/admin-agendas/admin-agendas.component';
import { AdminMeetingFormComponent } from './components/admin-meeting-form/admin-meeting-form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  
  // Admin routes
  { path: 'users', component: UserListComponent },
  { path: 'users/new', component: UserFormComponent },
  { path: 'users/edit/:id', component: UserFormComponent },
  { path: 'admin/meetings', component: AdminMeetingsComponent },
  { path: 'admin/meetings/new', component: AdminMeetingFormComponent },
  { path: 'admin/meetings/edit/:id', component: AdminMeetingFormComponent },
  { path: 'admin/agendas', component: AdminAgendasComponent },
  
  // User routes
  { path: 'my-meetings', component: MyMeetingsComponent },
  { path: 'my-agendas', component: MyAgendasComponent },
  { path: 'meetings/new', component: MeetingFormComponent },
  { path: 'meetings/edit/:id', component: MeetingFormComponent },
  
  // Legacy routes (kept for compatibility)
  { path: 'meetings', component: MeetingListComponent },
  { path: 'agendas', component: AgendaListComponent }
];
