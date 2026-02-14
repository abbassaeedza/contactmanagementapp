import { apiGet, apiPost, apiPut, apiDelete } from './client';
import type {
  SpringPage,
  ContactSummary,
  ContactDetailResponse,
  ContactRequest,
} from '../types/contact';

export function getContactPage(page: number, size: number) {
  return apiGet<SpringPage<ContactSummary>>('/contact', { page, size });
}

export function searchContacts(query: string) {
  return apiGet<ContactSummary[]>('/contact/s', { query });
}

export function getContact(contactId: string) {
  return apiGet<ContactDetailResponse>(`/contact/${contactId}`);
}

export function createContact(body: ContactRequest) {
  return apiPost<ContactDetailResponse>('/contact', body);
}

export function updateContact(contactId: string, body: ContactRequest) {
  return apiPut<ContactDetailResponse>(`/contact/${contactId}`, body);
}

export function deleteContact(contactId: string) {
  return apiDelete(`/contact/${contactId}`);
}
