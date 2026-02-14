import type { EmailType, PhoneType } from '../types/contact';

const EMAIL_COLORS: Record<EmailType, string> = {
  WORK: 'bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300',
  PERSONAL:
    'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300',
  OTHER: 'bg-slate-200 text-slate-700 dark:bg-slate-600 dark:text-slate-300',
};

const PHONE_COLORS: Record<PhoneType, string> = {
  WORK: 'bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300',
  HOME: 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300',
  PERSONAL:
    'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300',
  OTHER: 'bg-slate-200 text-slate-700 dark:bg-slate-600 dark:text-slate-300',
};

const BADGE_FIXED = 'w-[5.5rem] text-center';

export function EmailTypeBadge({ type }: { type: EmailType }) {
  return (
    <span
      className={`inline-block px-2 py-0.5 rounded text-xs font-medium shrink-0 ${BADGE_FIXED} ${EMAIL_COLORS[type]}`}
    >
      {type}
    </span>
  );
}

export function PhoneTypeBadge({ type }: { type: PhoneType }) {
  return (
    <span
      className={`inline-block px-2 py-0.5 rounded text-xs font-medium shrink-0 ${BADGE_FIXED} ${PHONE_COLORS[type]}`}
    >
      {type}
    </span>
  );
}
