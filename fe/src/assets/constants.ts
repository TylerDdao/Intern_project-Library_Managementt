import { Announcement } from "../app/models/announcement";
import { environment } from "../environments/environment";

export const errorImage = `${environment.apiUrl}/book-covers/default.jpg`

export const webAnnouncements = [
    // {
    //     id: 1,
    //     type: 'info',
    //     subject_vi: "Ý kiến của bạn rất quan trọng!",
    //     content_vi: "Chúng tôi rất mong nhận được phản hồi từ bạn. Vui lòng gửi ý kiến cho chúng tôi qua liên kết bên dưới!",

    //     subject_en: 'Your feedback matters!',
    //     content_en: 'We are excited to hear your feedback. Please send it to us via the link below!',

    //     link: 'https://forms.gle/8agsuPwmFonKSzPb6',
    //     linkText_vi: 'Biểu mẫu góp ý',
    //     linkText_en: 'Feedback Form',
    //     isActive: true,
    //     locations: []
    // },
    // {
    //     id: 2,
    //     type: 'warning',
    //     subject_vi: 'Website đang phát triển — Một số trang hiện chưa khả dụng',
    //     content_vi: 'Website này vẫn đang trong quá trình phát triển nên một số trang chưa thể truy cập vào lúc này. Chúng tôi sẽ hoàn thiện sớm nhất có thể!',

    //     subject_en: 'Site Under Development — Some pages are not available',
    //     content_en: 'This website is still under active development so some pages are not available to access at the moment. We will finish them as soon as possible!',
    //     isActive: true,
    //     locations: []
    // },
] as Announcement[];

interface Page{
  name: string,
  path: string
}

export const allPages: Page[] = [
    { name: 'navBar.user.Home-page', path: '/home' },
    { name: 'navBar.user.Browse-books', path: '/books'},
    { name: 'navBar.user.Browse-posts', path: '/posts'},
    { name: 'navBar.user.My-borrows', path: '/my-borrows'},
    { name: 'navBar.user.My-posts', path: '/my-posts' },
    { name: 'navBar.admin.Dashboard', path: '/dashboard'},
    { name: 'navBar.admin.Books-management', path: '/books-management'},
    { name: 'navBar.admin.Borrows-management', path: '/borrows-management'},
    { name: 'navBar.admin.Users-management', path: '/users-management'},
    { name: 'navBar.admin.Logs-management', path: '/logs-management'},
    { name: 'navBar.admin.Announcements-management', path: '/announcements-management'},

    { name: 'login.Login', path: '/login'},
    { name: 'signup.Sign/-up', path: '/signup'},
    { name: 'settings.Settings', path: '/settings'},
  ];