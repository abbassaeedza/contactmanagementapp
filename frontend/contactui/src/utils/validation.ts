const EMAIL_REGEX = /^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/;
const PHONE_REGEX = /^((\+\d{1,3})|0)?\(?\d{3}\)?\d{3}\d{4}$/;

export function isEmail(value: string): boolean {
  return EMAIL_REGEX.test(value.trim());
}

export function isPhone(value: string): boolean {
  return PHONE_REGEX.test(value.trim());
}

/** Username must be either a valid email or a valid phone (one of the two). */
export function isEmailOrPhone(value: string): boolean {
  const trimmed = value.trim();
  return trimmed.length > 0 && (isEmail(trimmed) || isPhone(trimmed));
}

/** Returns 'email' or 'phone' based on regex; use after isEmailOrPhone. */
export function getUsernameType(value: string): 'email' | 'phone' {
  return isEmail(value) ? 'email' : 'phone';
}

/** Password strength: min 8 chars, at least one uppercase, one lowercase, one digit, one special character. */
const PASSWORD_MIN_LENGTH = 8;
const PASSWORD_HAS_UPPER = /[A-Z]/;
const PASSWORD_HAS_LOWER = /[a-z]/;
const PASSWORD_HAS_DIGIT = /\d/;
const PASSWORD_HAS_SPECIAL = /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/;

export function isPasswordStrong(password: string): boolean {
  if (password.length < PASSWORD_MIN_LENGTH) return false;
  if (!PASSWORD_HAS_UPPER.test(password)) return false;
  if (!PASSWORD_HAS_LOWER.test(password)) return false;
  if (!PASSWORD_HAS_DIGIT.test(password)) return false;
  if (!PASSWORD_HAS_SPECIAL.test(password)) return false;
  return true;
}

export const PASSWORD_REQUIREMENTS =
  'At least 8 characters, one uppercase, one lowercase, one digit, and one special character (!@#$%^&* etc.).';
