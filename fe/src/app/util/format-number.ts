import { LanguageService } from "../services/language-service/language-service";

export function formatNumber(num: number): string {
    if (num >= 1_000_000) {
        return (num / 1_000_000).toFixed(num % 1_000_000 === 0 ? 0 : 1) + 'M';
    } else if (num >= 1_000) {
        return (num / 1_000).toFixed(num % 1_000 === 0 ? 0 : 1) + 'K';
    }
    return num.toString();
}

export function formatTime(time: string | Date, lang: string = 'en'): string {
    const date = typeof time === 'string' ? new Date(time) : time;
    return date.toLocaleDateString(lang, {
        year: 'numeric',
        month: 'long',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
}