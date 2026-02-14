import { useNavigate } from 'react-router-dom';

export default function ProfilePage() {
  const navigate = useNavigate();

  return (
    <div className='min-h-screen flex flex-col'>
      <header className='bg-white dark:bg-slate-800 border-b border-slate-200/80 dark:border-slate-700 shrink-0'>
        <div className='max-w-2xl mx-auto px-4 py-3 flex items-center justify-between gap-4'>
          <h1 className='text-base font-semibold text-slate-800 dark:text-slate-100'>
            Profile
          </h1>
          <button
            type='button'
            onClick={() => navigate('/contacts')}
            className='text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'
          >
            Back to contacts
          </button>
        </div>
      </header>
      <main className='flex-1 max-w-2xl w-full mx-auto px-4 py-6'>
        <div className='bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700 p-6'>
          <p className='text-slate-600 dark:text-slate-300 text-sm'>
            Profile page — coming soon.
          </p>
        </div>
      </main>
    </div>
  );
}
