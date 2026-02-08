import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiPost } from '../api/client';
import { setToken } from '../lib/auth';
import type { LoginResponse, SignupResponseDto } from '../types/auth';
import { isEmailOrPhone, getUsernameType } from '../utils/validation';
import { IconAuth, IconClose } from '../components/Icons';

type Mode = 'login' | 'signup';

export default function AuthPage() {
  const navigate = useNavigate();
  const [mode, setMode] = useState<Mode>('login');
  const [showForgotModal, setShowForgotModal] = useState(false);

  // Login state
  const [loginUsername, setLoginUsername] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Signup state
  const [signupUsername, setSignupUsername] = useState('');
  const [signupFirstname, setsignupFirstname] = useState('');
  const [signupLastname, setsignupLastname] = useState('');
  const [signupPassword, setSignupPassword] = useState('');

  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);

  function clearMessages() {
    setError('');
    setSuccessMessage('');
  }

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    clearMessages();
    if (!loginUsername.trim()) {
      setError('Please enter your username (email or phone).');
      return;
    }
    if (!loginPassword) {
      setError('Please enter your password.');
      return;
    }
    setLoading(true);
    try {
      const res = await apiPost<LoginResponse>('/auth/login', {
        username: loginUsername.trim(),
        password: loginPassword,
      });
      setToken(res.jwt);
      navigate('/contacts');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed.');
    } finally {
      setLoading(false);
    }
  }

  async function handleSignup(e: React.FormEvent) {
    e.preventDefault();
    clearMessages();
    if (!isEmailOrPhone(signupUsername)) {
      setError(
        'Username must be a valid email or phone number (e.g. +1234567890).',
      );
      return;
    }
    if (!signupFirstname.trim()) {
      setError('Please enter your first name.');
      return;
    }
    if (!signupLastname.trim()) {
      setError('Please enter your last name.');
      return;
    }
    if (!signupPassword) {
      setError('Please enter a password.');
      return;
    }
    const type = getUsernameType(signupUsername);
    const email = type === 'email' ? signupUsername.trim() : '';
    const phone = type === 'phone' ? signupUsername.trim() : '';
    setLoading(true);
    try {
      await apiPost<SignupResponseDto>('/auth/signup', {
        email,
        phone,
        firstname: signupFirstname.trim(),
        lastname: signupLastname.trim(),
        password: signupPassword,
      });
      setSuccessMessage('Account created. Please log in.');
      setMode('login');
      setSignupUsername('');
      setsignupFirstname('');
      setsignupLastname('');
      setSignupPassword('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Sign up failed.');
    } finally {
      setLoading(false);
    }
  }

  function switchToLogin() {
    setMode('login');
    clearMessages();
  }

  function switchToSignup() {
    setMode('signup');
    clearMessages();
  }

  return (
    <div className='min-h-screen flex flex-col items-center justify-center px-4'>
      <div className='w-full max-w-[400px] animate-modal-in'>
        <div className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 shadow-sm p-8'>
          <h1 className='text-xl font-semibold text-slate-800 dark:text-slate-100 text-center mb-6 flex items-center justify-center gap-2'>
            <IconAuth className='w-6 h-6 text-slate-600 dark:text-slate-400' />
            Contact Manager
          </h1>

          <div className='flex rounded-lg bg-slate-100 dark:bg-slate-700 p-0.5 mb-6'>
            <button
              type='button'
              onClick={switchToLogin}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-colors ${
                mode === 'login'
                  ? 'bg-white dark:bg-slate-600 text-slate-800 dark:text-slate-100 shadow-sm'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
              }`}
            >
              Log in
            </button>
            <button
              type='button'
              onClick={switchToSignup}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-colors ${
                mode === 'signup'
                  ? 'bg-white dark:bg-slate-600 text-slate-800 dark:text-slate-100 shadow-sm'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
              }`}
            >
              Sign up
            </button>
          </div>

          {error && (
            <div className='mb-4 p-3 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-sm border border-red-100 dark:border-red-800'>
              {error}
            </div>
          )}
          {successMessage && (
            <div className='mb-4 p-3 rounded-lg bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 text-sm border border-emerald-100 dark:border-emerald-800'>
              {successMessage}
            </div>
          )}

          {mode === 'login' ? (
            <form onSubmit={handleLogin} className='space-y-5'>
              <div>
                <label
                  htmlFor='login-username'
                  className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'
                >
                  Username (email or phone)
                </label>
                <input
                  id='login-username'
                  type='text'
                  value={loginUsername}
                  onChange={(e) => setLoginUsername(e.target.value)}
                  className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                  placeholder='you@example.com'
                  autoComplete='username'
                />
              </div>
              <div>
                <label
                  htmlFor='login-password'
                  className='block text-sm font-medium text-slate-600 mb-1.5'
                >
                  Password
                </label>
                <input
                  id='login-password'
                  type='password'
                  value={loginPassword}
                  onChange={(e) => setLoginPassword(e.target.value)}
                  className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                  placeholder='••••••••'
                  autoComplete='current-password'
                />
                <button
                  type='button'
                  onClick={() => setShowForgotModal(true)}
                  className='mt-1.5 text-sm text-slate-500 hover:text-slate-700'
                >
                  Forgot password?
                </button>
              </div>
              <button
                type='submit'
                disabled={loading}
                className='w-full py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50 disabled:cursor-not-allowed transition'
              >
                {loading ? 'Logging in…' : 'Log in'}
              </button>
            </form>
          ) : (
            <form onSubmit={handleSignup} className='space-y-5'>
              <div>
                <label
                  htmlFor='signup-username'
                  className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'
                >
                  Username (email or phone)
                </label>
                <input
                  id='signup-username'
                  type='text'
                  value={signupUsername}
                  onChange={(e) => setSignupUsername(e.target.value)}
                  className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                  placeholder='you@example.com'
                  autoComplete='username'
                />
              </div>
              <div className='grid grid-cols-2 gap-4'>
                <div>
                  <label
                    htmlFor='signup-firstname'
                    className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'
                  >
                    First name
                  </label>
                  <input
                    id='signup-firstname'
                    type='text'
                    value={signupFirstname}
                    onChange={(e) => setsignupFirstname(e.target.value)}
                    className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                    placeholder='John'
                    autoComplete='given-name'
                  />
                </div>
                <div>
                  <label
                    htmlFor='signup-lastname'
                    className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'
                  >
                    Last name
                  </label>
                  <input
                    id='signup-lastname'
                    type='text'
                    value={signupLastname}
                    onChange={(e) => setsignupLastname(e.target.value)}
                    className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                    placeholder='Doe'
                    autoComplete='family-name'
                  />
                </div>
              </div>
              <div>
                <label
                  htmlFor='signup-password'
                  className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'
                >
                  Password
                </label>
                <input
                  id='signup-password'
                  type='password'
                  value={signupPassword}
                  onChange={(e) => setSignupPassword(e.target.value)}
                  className='w-full px-3.5 py-2.5 rounded-lg border border-slate-200 dark:border-slate-600 bg-slate-50/50 dark:bg-slate-700/50 focus:bg-white dark:focus:bg-slate-700 focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:text-slate-200'
                  placeholder='••••••••'
                  autoComplete='new-password'
                />
              </div>
              <button
                type='submit'
                disabled={loading}
                className='w-full py-2.5 rounded-lg bg-slate-800 dark:bg-slate-600 text-white text-sm font-medium hover:bg-slate-700 dark:hover:bg-slate-500 disabled:opacity-50 disabled:cursor-not-allowed transition'
              >
                {loading ? 'Creating account…' : 'Sign up'}
              </button>
            </form>
          )}
        </div>
      </div>

      {showForgotModal && (
        <ForgotPasswordModal onClose={() => setShowForgotModal(false)} />
      )}
    </div>
  );
}

function ForgotPasswordModal({ onClose }: { onClose: () => void }) {
  return (
    <div
      className='fixed inset-0 z-10 flex items-center justify-center p-4 bg-slate-900/30 dark:bg-black/50 backdrop-blur-[2px]'
      onClick={onClose}
    >
      <div
        className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 shadow-lg p-6 w-full max-w-sm animate-modal-in'
        onClick={(e) => e.stopPropagation()}
      >
        <div className='flex items-start justify-between gap-4'>
          <div>
            <h2 className='text-base font-semibold text-slate-800 dark:text-slate-100 mb-1.5'>
              Forgot password?
            </h2>
            <p className='text-slate-600 dark:text-slate-300 text-sm'>
              Password reset functionality will be added here later.
            </p>
          </div>
          <button
            type='button'
            onClick={onClose}
            className='p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 rounded shrink-0'
            aria-label='Close'
          >
            <IconClose className='w-4 h-4' />
          </button>
        </div>
      </div>
    </div>
  );
}
