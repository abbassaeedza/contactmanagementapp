const iconClass = 'w-4 h-4 shrink-0';

export function IconAuth({
  className = 'w-5 h-5 shrink-0',
}: {
  className?: string;
}) {
  return (
    <svg
      className={className}
      fill='none'
      stroke='currentColor'
      viewBox='0 0 24 24'
      strokeWidth={1.8}
    >
      <path
        strokeLinecap='round'
        strokeLinejoin='round'
        d='M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z'
      />
    </svg>
  );
}

export function IconClose({ className = iconClass }: { className?: string }) {
  return (
    <svg
      className={className}
      fill='none'
      stroke='currentColor'
      viewBox='0 0 24 24'
      strokeWidth={2}
    >
      <path
        strokeLinecap='round'
        strokeLinejoin='round'
        d='M6 18 18 6M6 6l12 12'
      />
    </svg>
  );
}
