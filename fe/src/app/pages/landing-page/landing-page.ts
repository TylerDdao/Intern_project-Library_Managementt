import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';

interface Feature {
  callNumber: string;
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
})
export class LandingPage {
  // TODO: replace with your real repository URL
  readonly githubUrl = 'https://github.com/TylerDdao/TT_Viettel_Software-Library_Management.git';
  readonly apiDocUrl = environment.apiUrl + "/swagger-ui/index.html#/"
  readonly features: Feature[] = [
    {
      callNumber: '020.285',
      icon: 'menu_book',
      title: 'Catalog & search',
      description:
        'Browse the full collection by title, author, or genre, with cover art and live copy counts.',
    },
    {
      callNumber: '025.6',
      icon: 'sync_alt',
      title: 'Borrowing & returns',
      description:
        'Borrow, track due dates, and get automatic email reminders before a book falls overdue.',
    },
    {
      callNumber: '028.9',
      icon: 'forum',
      title: 'Reader discussions',
      description:
        'Post about what you\u2019re reading and see what the rest of the library is talking about.',
    },
    {
      callNumber: '351.82',
      icon: 'admin_panel_settings',
      title: 'Role-based admin tools',
      description:
        'A dashboard for librarians to manage books, borrows, users, and permissions in one place.',
    },
    {
      callNumber: '418.02',
      icon: 'translate',
      title: 'Three languages',
      description:
        'The full interface is available in Vietnamese, English, and French.',
    },
    {
      callNumber: '025.171',
      icon: 'summarize',
      title: 'Exportable reports',
      description:
        'Pull users, books, borrows, or system logs into an Excel sheet whenever you need one.',
    },
  ];
}