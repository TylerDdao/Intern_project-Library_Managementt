export type AnnouncementType = 'info' | 'warning' | 'error';

export interface Announcement {
  id: number;
  type: AnnouncementType;
  subjectVi: string;
  contentVi: string;
  subjectEn?: string;
  contentEn?: string;
  subjectFr?: string;
  contentFr?: string;
  link?: string;
  linkTextVi ?: string;
  linkTextEn ?: string;
  linkTextFr ?: string;
  active: boolean;
  locations: string[]
}