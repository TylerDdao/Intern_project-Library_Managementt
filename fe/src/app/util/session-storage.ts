import { User } from "../models/user";
import { Announcement } from '../models/announcement';

export function getUser(): User | null {
  if (typeof sessionStorage === 'undefined') {
    return null;
  }

  const user = sessionStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}

export function saveUser(user:User):void{
    sessionStorage.setItem("user", JSON.stringify(user));
}


const ANNOUNCEMENT_KEY = 'announcements';

export function saveAnnouncements(announcements: Announcement[]): void {
  if (typeof sessionStorage === 'undefined') return;

  sessionStorage.setItem(
    ANNOUNCEMENT_KEY,
    JSON.stringify(announcements)
  );
}

export function getAnnouncementsFromStorage(): Announcement[] {
  if (typeof sessionStorage === 'undefined') {
    return [];
  }

  const data = sessionStorage.getItem(ANNOUNCEMENT_KEY);

  if (!data) {
    return [];
  }

  try {
    return JSON.parse(data) as Announcement[];
  } catch {
    sessionStorage.removeItem(ANNOUNCEMENT_KEY);
    return [];
  }
}

export function clearAnnouncements(): void {
  if (typeof sessionStorage === 'undefined') return;
  sessionStorage.removeItem('announcements');
  sessionStorage.removeItem('announcementsLoaded');
}   

export function removeAuthInfo():void{
  if(typeof sessionStorage === "undefined") return
  sessionStorage.removeItem('token');
  sessionStorage.removeItem('user');
  sessionStorage.removeItem('authorities');
}