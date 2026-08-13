export type AnnouncementType = 'info' | 'warning' | 'error';

export interface Announcement {
  id: number;
  type: AnnouncementType;
  subject_vi: string;
  content_vi: string;
  subject_en?: string;
  content_en?: string;
  subject_fr?: string;
  content_fr?: string;
  link?: string;
  linkText_vi ?: string;
  linkText_en ?: string;
  linkText_fr ?: string;
  isActive: boolean;
  locations: string[]
}