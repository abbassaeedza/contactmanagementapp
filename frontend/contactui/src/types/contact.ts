export type EmailType = 'WORK' | 'PERSONAL' | 'OTHER';
export type PhoneType = 'WORK' | 'HOME' | 'PERSONAL' | 'OTHER';

export interface ContactEmailDto {
  emailtype: EmailType;
  emailvalue: string;
}

export interface ContactPhoneDto {
  phonetype: PhoneType;
  phonevalue: string;
}

/** Contact as returned in list/search (paginated or search results). */
export interface ContactSummary {
  id: string;
  title: string | null;
  firstname: string;
  lastname: string;
}

export interface ContactDetailResponse extends ContactSummary {
  emails: ContactEmailDto[];
  phones: ContactPhoneDto[];
}

export interface ContactRequest {
  title: string;
  firstname: string;
  lastname: string;
  emails: ContactEmailDto[];
  phones: ContactPhoneDto[];
}

export interface SpringPage<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
