import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { clearToken } from '../lib/auth';
import { getUser, updateUser, changePassword, deleteUser } from '../api/user';
import type {
  UserResponse,
  UserRequest,
  ChangePassRequest,
} from '../types/user';
import { isEmailOrPhone, getUsernameType } from '../utils/validation';
import { isPasswordStrong, PASSWORD_REQUIREMENTS } from '../utils/validation';
import { useTheme } from '../lib/theme';
import { IconClose, IconPencil, IconTrash } from '../components/Icons';

export default function ProfilePage() {
  const navigate = useNavigate();
  const { theme, setTheme } = useTheme();
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editError, setEditError] = useState('');
  const [changePasswordError, setChangePasswordError] = useState('');
  const [editOpen, setEditOpen] = useState(false);
  const [changePassOpen, setChangePassOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    getUser()
      .then(setUser)
      .catch((err) =>
        setError(
          err instanceof Error ? err.message : 'Failed to load profile.',
        ),
      )
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!error) return;
    const t = setTimeout(() => setError(''), 5000);
    return () => clearTimeout(t);
  }, [error]);

  function handleLogout() {
    clearToken();
    navigate('/');
  }

  function handleEditSave(payload: UserRequest) {
    setActionLoading(true);
    setEditError('');
    updateUser(payload)
      .then(() => {
        setEditOpen(false);
        setEditError('');
        clearToken();
        navigate('/');
      })
      .catch((err) =>
        setEditError(
          err instanceof Error ? err.message : 'Failed to update profile.',
        ),
      )
      .finally(() => setActionLoading(false));
  }

  function handleChangePassword(payload: ChangePassRequest) {
    setActionLoading(true);
    setChangePasswordError('');
    changePassword(payload)
      .then(() => {
        setChangePassOpen(false);
        setChangePasswordError('');
        clearToken();
        navigate('/');
      })
      .catch((err) =>
        setChangePasswordError(
          err instanceof Error ? err.message : 'Failed to change password.',
        ),
      )
      .finally(() => setActionLoading(false));
  }

  function handleDeleteConfirm() {
    setActionLoading(true);
    setError('');
    deleteUser()
      .then(() => {
        setDeleteOpen(false);
        clearToken();
        navigate('/');
      })
      .catch((err) => {
        setError(
          err instanceof Error ? err.message : 'Failed to delete account.',
        );
      })
      .finally(() => setActionLoading(false));
  }

  const displayName = user
    ? [user.firstname, user.lastname].filter(Boolean).join(' ') || user.username
    : '—';

  return (
    <div className='min-h-screen flex flex-col'>
      <header className='bg-white dark:bg-slate-800 border-b border-slate-200/80 dark:border-slate-700 shrink-0'>
        <div className='max-w-2xl mx-auto px-4 py-3 flex items-center justify-between gap-4'>
          <h1 className='text-base font-semibold text-slate-800 dark:text-slate-100'>
            Profile
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
              onClick={() => navigate('/contacts')}
              className='text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'
            >
              Back to contacts
            </button>
          </div>
        </div>
      </header>

      <main className='flex-1 max-w-2xl w-full mx-auto px-4 py-6'>
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

        {!loading && user && (
          <div className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 p-6 space-y-6'>
            <div>
              <p className='text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-1.5'>
                Name
              </p>
              <p className='text-slate-800 dark:text-slate-100 text-xl font-semibold'>
                {displayName}
              </p>
            </div>
            <div>
              <p className='text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-1'>
                Username
              </p>
              <p className='text-slate-800 dark:text-slate-200 text-sm'>
                {user.username}
              </p>
            </div>

            <div className='flex flex-wrap gap-3 pt-2'>
              <button
                type='button'
                onClick={() => setEditOpen(true)}
                className='flex items-center gap-2 px-4 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500'
              >
                <IconPencil className='w-4 h-4' />
                Edit profile
              </button>
              <button
                type='button'
                onClick={() => setChangePassOpen(true)}
                className='flex items-center gap-2 px-4 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 text-slate-700 dark:text-slate-200 text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-700/50'
              >
                Change password
              </button>
              <button
                type='button'
                onClick={() => setDeleteOpen(true)}
                className='flex items-center gap-2 px-4 py-2.5 rounded-lg border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 text-sm font-medium hover:bg-red-50 dark:hover:bg-red-900/20'
              >
                <IconTrash className='w-4 h-4' />
                Delete account
              </button>
              <button
                type='button'
                onClick={handleLogout}
                className='px-4 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 text-slate-600 dark:text-slate-300 text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-700/50'
              >
                Logout
              </button>
            </div>
          </div>
        )}

        {!loading && !user && !error && (
          <div className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 p-6'>
            <p className='text-slate-500 dark:text-slate-400 text-sm'>
              Unable to load profile.
            </p>
          </div>
        )}
      </main>

      {editOpen && user && (
        <EditProfileModal
          user={user}
          onClose={() => {
            setEditOpen(false);
            setEditError('');
          }}
          onSave={handleEditSave}
          loading={actionLoading}
          apiError={editError}
        />
      )}

      {changePassOpen && (
        <ChangePasswordModal
          onClose={() => {
            setChangePassOpen(false);
            setChangePasswordError('');
          }}
          onReset={handleChangePassword}
          loading={actionLoading}
          apiError={changePasswordError}
        />
      )}

      {deleteOpen && (
        <DeleteConfirmModal
          onClose={() => setDeleteOpen(false)}
          onConfirm={handleDeleteConfirm}
          loading={actionLoading}
        />
      )}

      {actionLoading && !editOpen && !changePassOpen && !deleteOpen && (
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

const inputBase =
  'w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-800 dark:text-slate-200 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 dark:focus:border-slate-500 outline-none transition';

function EditProfileModal({
  user,
  onClose,
  onSave,
  loading,
  apiError,
}: {
  user: UserResponse;
  onClose: () => void;
  onSave: (payload: UserRequest) => void;
  loading: boolean;
  apiError: string;
}) {
  const [username, setUsername] = useState(user.username);
  const [firstname, setFirstname] = useState(user.firstname);
  const [lastname, setLastname] = useState(user.lastname);
  const [formError, setFormError] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError('');
    if (!isEmailOrPhone(username.trim())) {
      setFormError(
        'Username must be a valid email or phone number (e.g. +1234567890).',
      );
      return;
    }
    const type = getUsernameType(username.trim());
    const email = type === 'email' ? username.trim() : '';
    const phone = type === 'phone' ? username.trim() : '';
    onSave({
      email,
      phone,
      firstname: firstname.trim(),
      lastname: lastname.trim(),
    });
  }

  return (
    <Modal title='Edit profile' onClose={onClose}>
      <p className='text-slate-600 dark:text-slate-400 text-sm mb-4 p-3 rounded-lg bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800'>
        Changing your username will log you out. You will need to sign in again.
      </p>
      <form onSubmit={handleSubmit} className='space-y-4'>
        {(formError || apiError) && (
          <div className='p-3 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm border border-red-100 dark:border-red-800'>
            {formError || apiError}
          </div>
        )}
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            Username (email or phone)
          </label>
          <input
            type='text'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className={inputBase}
            placeholder='you@example.com or +1234567890'
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            First name
          </label>
          <input
            type='text'
            value={firstname}
            onChange={(e) => setFirstname(e.target.value)}
            className={inputBase}
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            Last name
          </label>
          <input
            type='text'
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
            className={inputBase}
          />
        </div>
        <div className='mt-6 flex justify-end'>
          <button
            type='submit'
            disabled={loading}
            className='px-4 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50'
          >
            {loading ? 'Saving…' : 'Save'}
          </button>
        </div>
      </form>
    </Modal>
  );
}

function ChangePasswordModal({
  onClose,
  onReset,
  loading,
  apiError,
}: {
  onClose: () => void;
  onReset: (payload: ChangePassRequest) => void;
  loading: boolean;
  apiError: string;
}) {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [formError, setFormError] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError('');
    if (!oldPassword.trim()) {
      setFormError('Enter your current password.');
      return;
    }
    if (!newPassword.trim()) {
      setFormError('Enter a new password.');
      return;
    }
    if (!isPasswordStrong(newPassword)) {
      setFormError(PASSWORD_REQUIREMENTS);
      return;
    }
    onReset({ oldpassword: oldPassword, newpassword: newPassword });
  }

  return (
    <Modal title='Change password' onClose={onClose}>
      <p className='text-slate-600 dark:text-slate-400 text-sm mb-4 p-3 rounded-lg bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800'>
        Changing your password will log you out. You will need to sign in again.
      </p>
      <form onSubmit={handleSubmit} className='space-y-4'>
        {(formError || apiError) && (
          <div className='p-3 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm border border-red-100 dark:border-red-800'>
            {formError || apiError}
          </div>
        )}
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            Current password
          </label>
          <input
            type='password'
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            className={inputBase}
            placeholder='••••••••'
            autoComplete='current-password'
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            New password
          </label>
          <input
            type='password'
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            className={inputBase}
            placeholder='••••••••'
            autoComplete='new-password'
          />
          <p className='mt-1.5 text-xs text-slate-500 dark:text-slate-400'>
            Min 8 characters, include uppercase, lowercase, number, and special
            character.
          </p>
        </div>
        <div className='mt-6 flex justify-end'>
          <button
            type='submit'
            disabled={loading}
            className='px-4 py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50'
          >
            {loading ? 'Resetting…' : 'Reset'}
          </button>
        </div>
      </form>
    </Modal>
  );
}

function DeleteConfirmModal({
  onClose,
  onConfirm,
  loading,
}: {
  onClose: () => void;
  onConfirm: () => void;
  loading: boolean;
}) {
  return (
    <Modal title='Delete account' onClose={onClose}>
      <p className='text-slate-600 dark:text-slate-300 text-sm mb-6'>
        Are you sure you want to delete your account? This cannot be undone and
        all your data will be removed.
      </p>
      <div className='flex justify-end'>
        <button
          type='button'
          onClick={onConfirm}
          disabled={loading}
          className='px-4 py-2.5 rounded-lg bg-red-600 text-white text-sm font-medium hover:bg-red-700 disabled:opacity-50'
        >
          {loading ? 'Deleting…' : 'Confirm'}
        </button>
      </div>
    </Modal>
  );
}
