import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { clearToken } from '../lib/auth';
import {
  getContactPage,
  searchContacts,
  getContact,
  createContact,
  updateContact,
  deleteContact,
} from '../api/contacts';
import type {
  ContactSummary,
  ContactDetailResponse,
  ContactRequest,
} from '../types/contact';
import ContactForm, {
  contactRequestEmpty,
  contactDetailToRequest,
  sanitizeContactRequest,
} from '../components/ContactForm';
import { EmailTypeBadge, PhoneTypeBadge } from '../components/TypeBadge';
import { IconPencil, IconTrash, IconClose } from '../components/Icons';
import { useTheme } from '../lib/theme';

const PAGE_SIZE = 10;
const SEARCH_DEBOUNCE_MS = 500;

export default function ContactsPage() {
  const navigate = useNavigate();
  const { theme, setTheme } = useTheme();
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [page, setPage] = useState(0);
  const [paginatedData, setPaginatedData] = useState<{
    content: ContactSummary[];
    totalPages: number;
    totalElements: number;
  } | null>(null);
  const [searchResults, setSearchResults] = useState<ContactSummary[] | null>(
    null,
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Debounce search
  useEffect(() => {
    const t = setTimeout(
      () => setDebouncedQuery(searchQuery.trim()),
      SEARCH_DEBOUNCE_MS,
    );
    return () => clearTimeout(t);
  }, [searchQuery]);

  const fetchPage = useCallback(async (pageNum: number) => {
    setLoading(true);
    setError('');
    try {
      const res = await getContactPage(pageNum, PAGE_SIZE);
      setPaginatedData({
        content: res.content,
        totalPages: res.totalPages,
        totalElements: res.totalElements,
      });
      setSearchResults(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load contacts.');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchSearch = useCallback(async (query: string) => {
    if (!query) {
      setSearchResults(null);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const list = await searchContacts(query);
      setSearchResults(Array.isArray(list) ? list : []);
      setPaginatedData(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Search failed.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (debouncedQuery) {
      fetchSearch(debouncedQuery);
    } else {
      fetchPage(page);
    }
  }, [debouncedQuery, page, fetchPage, fetchSearch]);

  useEffect(() => {
    if (!debouncedQuery) setPage(0);
  }, [debouncedQuery]);

  useEffect(() => {
    if (!error) return;
    const t = setTimeout(() => setError(''), 5000);
    return () => clearTimeout(t);
  }, [error]);

  const contacts = debouncedQuery
    ? (searchResults ?? [])
    : (paginatedData?.content ?? []);
  const totalPages = paginatedData?.totalPages ?? 0;
  const totalElements = paginatedData?.totalElements ?? 0;
  const isPaginated = !debouncedQuery && paginatedData;

  const [detailContact, setDetailContact] =
    useState<ContactDetailResponse | null>(null);
  const [addOpen, setAddOpen] = useState(false);
  const [updateContactId, setUpdateContactId] = useState<string | null>(null);
  const [updateForm, setUpdateForm] = useState<ContactRequest | null>(null);
  const [deleteContactId, setDeleteContactId] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const refresh = useCallback(() => {
    if (debouncedQuery) fetchSearch(debouncedQuery);
    else fetchPage(page);
  }, [debouncedQuery, page, fetchSearch, fetchPage]);

  function handleLogout() {
    clearToken();
    navigate('/');
  }

  function openDetail(id: string) {
    setActionLoading(true);
    getContact(id)
      .then(setDetailContact)
      .catch(() => setError('Failed to load contact.'))
      .finally(() => setActionLoading(false));
  }

  function closeDetail() {
    setDetailContact(null);
  }

  function openAdd() {
    setAddOpen(true);
  }

  function closeAdd() {
    setAddOpen(false);
  }

  const [addForm, setAddForm] = useState<ContactRequest>(contactRequestEmpty());

  function handleAddSave() {
    setActionLoading(true);
    createContact(sanitizeContactRequest(addForm))
      .then(() => {
        closeAdd();
        setAddForm(contactRequestEmpty());
        refresh();
      })
      .catch((err) =>
        setError(
          err instanceof Error ? err.message : 'Failed to create contact.',
        ),
      )
      .finally(() => setActionLoading(false));
  }

  function openUpdate(c: ContactDetailResponse) {
    setUpdateContactId(c.id);
    setUpdateForm(contactDetailToRequest(c));
  }

  function closeUpdate() {
    setUpdateContactId(null);
    setUpdateForm(null);
  }

  function handleUpdateSave() {
    if (!updateContactId || !updateForm) return;
    setActionLoading(true);
    updateContact(updateContactId, sanitizeContactRequest(updateForm))
      .then(() => {
        closeUpdate();
        if (detailContact?.id === updateContactId) {
          getContact(updateContactId).then(setDetailContact);
        }
        refresh();
      })
      .catch((err) =>
        setError(
          err instanceof Error ? err.message : 'Failed to update contact.',
        ),
      )
      .finally(() => setActionLoading(false));
  }

  function openDelete(id: string) {
    setDeleteContactId(id);
  }

  function closeDelete() {
    setDeleteContactId(null);
  }

  function handleDeleteConfirm() {
    if (!deleteContactId) return;
    setActionLoading(true);
    deleteContact(deleteContactId)
      .then(() => {
        closeDelete();
        if (detailContact?.id === deleteContactId) closeDetail();
        refresh();
      })
      .catch((err) =>
        setError(
          err instanceof Error ? err.message : 'Failed to delete contact.',
        ),
      )
      .finally(() => setActionLoading(false));
  }

  const displayName = (c: ContactSummary) => {
    const parts = [c.firstname, c.lastname].filter(Boolean);
    const name = parts.join(' ');
    return c.title ? `${c.title} ${name}`.trim() : name || '—';
  };

  const isEmpty = !loading && contacts.length === 0;

  return (
    <div className='min-h-screen flex flex-col'>
      <header className='bg-white dark:bg-slate-800 border-b border-slate-200/80 dark:border-slate-700 shrink-0'>
        <div className='max-w-2xl mx-auto px-4 py-3 flex items-center justify-between gap-4'>
          <h1 className='text-base font-semibold text-slate-800 dark:text-slate-100 flex items-center gap-2'>
            Contacts
          </h1>
          <div className='flex items-center gap-2'>
            <button
              type='button'
              onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
              className='p-2 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-700 dark:text-slate-400'
              aria-label={
                theme === 'dark'
                  ? 'Switch to light mode'
                  : 'Switch to dark mode'
              }
            >
              {theme === 'dark' ? (
                <svg
                  className='w-4 h-4'
                  fill='none'
                  stroke='currentColor'
                  viewBox='0 0 24 24'
                >
                  <path
                    strokeLinecap='round'
                    strokeLinejoin='round'
                    d='M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773-4.227-1.591 1.591M5.25 12H3m4.227-4.773-1.591-1.591M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z'
                  />
                </svg>
              ) : (
                <svg
                  className='w-4 h-4'
                  fill='none'
                  stroke='currentColor'
                  viewBox='0 0 24 24'
                >
                  <path
                    strokeLinecap='round'
                    strokeLinejoin='round'
                    d='M21.752 15.002A9.72 9.72 0 0 1 18 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 0 0 3 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 0 0 9.002-5.998Z'
                  />
                </svg>
              )}
            </button>
            <button
              type='button'
              onClick={() => navigate('/profile')}
              className='text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 py-1 px-2'
            >
              Profile
            </button>
            <button
              type='button'
              onClick={handleLogout}
              className='text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 py-1 px-2'
            >
              Log out
            </button>
          </div>
        </div>
      </header>

      <main className='flex-1 max-w-2xl w-full mx-auto px-4 py-6'>
        <div className='flex flex-col sm:flex-row gap-3 mb-6'>
          <input
            type='search'
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder='Search by first or last name...'
            className='flex-1 px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none text-slate-800 dark:text-slate-200 placeholder:text-slate-400 dark:placeholder:text-slate-500'
          />
          <button
            type='button'
            onClick={openAdd}
            className='px-4 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 shrink-0 transition'
          >
            Create contact
          </button>
        </div>

        {error && (
          <div className='mb-4 p-3 pr-10 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm border border-red-100 dark:border-red-800 relative'>
            {error}
            <button
              type='button'
              onClick={() => setError('')}
              className='absolute top-2 right-2 p-1 text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 rounded'
              aria-label='Close'
            >
              <IconClose className='w-4 h-4' />
            </button>
          </div>
        )}

        {loading && (
          <p className='text-slate-500 dark:text-slate-400 text-sm py-6'>
            Loading…
          </p>
        )}

        {!loading && contacts.length > 0 && (
          <ul className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 overflow-hidden divide-y divide-slate-100 dark:divide-slate-700'>
            {contacts.map((c, i) => (
              <li
                key={c.id}
                className='animate-slide-up'
                style={{ animationDelay: `${i * 30}ms` }}
              >
                <button
                  type='button'
                  onClick={() => openDetail(c.id)}
                  className='w-full text-left px-4 py-3.5 hover:bg-slate-50/80 dark:hover:bg-slate-700/50 transition text-slate-800 dark:text-slate-200'
                >
                  {displayName(c)}
                </button>
              </li>
            ))}
          </ul>
        )}

        {isEmpty && (
          <div className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 p-10 text-center'>
            <p className='text-4xl mb-3'>📭</p>
            <p className='font-medium text-slate-700 dark:text-slate-200'>
              Nothing here yet
            </p>
            <p className='text-sm text-slate-500 dark:text-slate-400 mt-1'>
              {debouncedQuery
                ? "No contacts matched your search. Maybe they're hiding?"
                : 'Your contact list is emptier than a Monday morning inbox. Add someone!'}
            </p>
          </div>
        )}

        {isPaginated && (
          <div className='mt-6 flex items-center justify-between text-sm text-slate-500 dark:text-slate-400'>
            <span>
              Page {page + 1} of {Math.max(1, totalPages)} ({totalElements}{' '}
              total)
            </span>
            <div className='flex gap-2'>
              <button
                type='button'
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className='px-3 py-1.5 rounded-lg border border-slate-200 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed'
              >
                Previous
              </button>
              <button
                type='button'
                onClick={() =>
                  setPage((p) => Math.min(Math.max(0, totalPages - 1), p + 1))
                }
                disabled={totalPages <= 1 || page >= totalPages - 1}
                className='px-3 py-1.5 rounded-lg border border-slate-200 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed'
              >
                Next
              </button>
            </div>
          </div>
        )}
      </main>

      {detailContact && (
        <DetailModal
          contact={detailContact}
          onClose={closeDetail}
          onUpdate={() => openUpdate(detailContact)}
          onDelete={() => openDelete(detailContact.id)}
        />
      )}

      {addOpen && (
        <Modal title='Add contact' onClose={closeAdd}>
          <ContactForm value={addForm} onChange={setAddForm} />
          <div className='mt-6 flex justify-end'>
            <button
              type='button'
              onClick={handleAddSave}
              disabled={actionLoading}
              className='px-5 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50'
            >
              {actionLoading ? 'Saving…' : 'Save'}
            </button>
          </div>
        </Modal>
      )}

      {updateContactId && updateForm && (
        <Modal title='Update contact' onClose={closeUpdate}>
          <ContactForm value={updateForm} onChange={setUpdateForm} />
          <div className='mt-6 flex justify-end'>
            <button
              type='button'
              onClick={handleUpdateSave}
              disabled={actionLoading}
              className='flex items-center gap-2 px-5 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50'
            >
              <IconPencil className='w-4 h-4' />
              {actionLoading ? 'Updating…' : 'Update'}
            </button>
          </div>
        </Modal>
      )}

      {deleteContactId && (
        <Modal title='Delete contact' onClose={closeDelete}>
          <p className='text-slate-600 dark:text-slate-300 text-sm'>
            Are you sure you want to delete this contact? This cannot be undone.
          </p>
          <div className='mt-6 flex justify-end'>
            <button
              type='button'
              onClick={handleDeleteConfirm}
              disabled={actionLoading}
              className='flex items-center gap-2 px-5 py-2.5 rounded-lg bg-red-600 text-white text-sm font-medium hover:bg-red-700 disabled:opacity-50'
            >
              <IconTrash className='w-4 h-4' />
              {actionLoading ? 'Deleting…' : 'Delete'}
            </button>
          </div>
        </Modal>
      )}

      {actionLoading && detailContact === null && (
        <div className='fixed inset-0 z-20 flex items-center justify-center bg-slate-900/20 dark:bg-black/30 backdrop-blur-[2px]'>
          <span className='text-sm font-medium text-slate-700 dark:text-slate-200 bg-white dark:bg-slate-800 px-4 py-2 rounded-lg shadow-sm'>
            Loading…
          </span>
        </div>
      )}
    </div>
  );
}

function Modal({
  title,
  onClose,
  children,
}: {
  title: string;
  onClose: () => void;
  children: React.ReactNode;
}) {
  return (
    <div
      className='fixed inset-0 z-10 flex items-center justify-center p-4 bg-slate-900/30 dark:bg-black/50 backdrop-blur-[2px]'
      onClick={onClose}
    >
      <div
        className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 shadow-lg p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto animate-modal-in'
        onClick={(e) => e.stopPropagation()}
      >
        <div className='flex items-center justify-between gap-4 mb-5'>
          <h2 className='text-base font-semibold text-slate-800 dark:text-slate-100'>
            {title}
          </h2>
          <button
            type='button'
            onClick={onClose}
            className='p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 rounded shrink-0'
            aria-label='Close'
          >
            <IconClose className='w-4 h-4' />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

function DetailModal({
  contact,
  onClose,
  onUpdate,
  onDelete,
}: {
  contact: ContactDetailResponse;
  onClose: () => void;
  onUpdate: () => void;
  onDelete: () => void;
}) {
  const displayName =
    [contact.title, contact.firstname, contact.lastname]
      .filter(Boolean)
      .join(' ') || '—';

  return (
    <div
      className='fixed inset-0 z-10 flex items-center justify-center p-4 bg-slate-900/30 dark:bg-black/50 backdrop-blur-[2px]'
      onClick={onClose}
    >
      <div
        className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 shadow-lg p-6 w-full max-w-md animate-modal-in'
        onClick={(e) => e.stopPropagation()}
      >
        <div className='flex items-center justify-between gap-4 mb-5'>
          <h2 className='text-base font-semibold text-slate-800 dark:text-slate-100'>
            {displayName}
          </h2>
          <button
            type='button'
            onClick={onClose}
            className='p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 rounded shrink-0'
            aria-label='Close'
          >
            <IconClose className='w-4 h-4' />
          </button>
        </div>

        <div className='space-y-4'>
          {contact.emails.length > 0 && (
            <div>
              <p className='text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-2'>
                Emails
              </p>
              <ul className='space-y-2'>
                {contact.emails.map((e, i) => (
                  <li
                    key={i}
                    className='grid grid-cols-[5.5rem_1fr] gap-3 items-center'
                  >
                    <EmailTypeBadge type={e.emailtype} />
                    <span className='text-slate-800 dark:text-slate-200 text-sm truncate'>
                      {e.emailvalue}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          )}
          {contact.phones.length > 0 && (
            <div>
              <p className='text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-2'>
                Phones
              </p>
              <ul className='space-y-2'>
                {contact.phones.map((p, i) => (
                  <li
                    key={i}
                    className='grid grid-cols-[5.5rem_1fr] gap-3 items-center'
                  >
                    <PhoneTypeBadge type={p.phonetype} />
                    <span className='text-slate-800 dark:text-slate-200 text-sm truncate'>
                      {p.phonevalue}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          )}
          {contact.emails.length === 0 && contact.phones.length === 0 && (
            <p className='text-slate-500 dark:text-slate-400 text-sm'>
              No emails or phones added.
            </p>
          )}
        </div>

        <div className='mt-6 flex gap-3 justify-end'>
          <button
            type='button'
            onClick={onDelete}
            className='flex items-center gap-2 px-4 py-2 rounded-lg border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 text-sm font-medium hover:bg-red-50 dark:hover:bg-red-900/20'
          >
            <IconTrash className='w-4 h-4' />
            Delete
          </button>
          <button
            type='button'
            onClick={onUpdate}
            className='flex items-center gap-2 px-4 py-2 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500'
          >
            <IconPencil className='w-4 h-4' />
            Update
          </button>
        </div>
      </div>
    </div>
  );
}
