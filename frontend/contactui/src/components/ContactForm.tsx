import type {
  ContactRequest,
  ContactEmailDto,
  ContactPhoneDto,
  EmailType,
  PhoneType,
} from '../types/contact';
import { isEmail, isPhone } from '../utils/validation';
import { IconPlus, IconTrash } from './Icons';

const EMAIL_TYPES: EmailType[] = ['WORK', 'PERSONAL', 'OTHER'];
const PHONE_TYPES: PhoneType[] = ['WORK', 'HOME', 'PERSONAL', 'OTHER'];

interface ContactFormProps {
  value: ContactRequest;
  onChange: (value: ContactRequest) => void;
}

export default function ContactForm({ value, onChange }: ContactFormProps) {
  function update(part: Partial<ContactRequest>) {
    onChange({ ...value, ...part });
  }

  function addEmail() {
    update({
      emails: [...value.emails, { emailtype: 'PERSONAL', emailvalue: '' }],
    });
  }

  function removeEmail(index: number) {
    update({
      emails: value.emails.filter((_, i) => i !== index),
    });
  }

  function updateEmail(index: number, row: ContactEmailDto) {
    const next = [...value.emails];
    next[index] = row;
    update({ emails: next });
  }

  function addPhone() {
    update({
      phones: [...value.phones, { phonetype: 'PERSONAL', phonevalue: '' }],
    });
  }

  function removePhone(index: number) {
    update({
      phones: value.phones.filter((_, i) => i !== index),
    });
  }

  function updatePhone(index: number, row: ContactPhoneDto) {
    const next = [...value.phones];
    next[index] = row;
    update({ phones: next });
  }

  const inputBase =
    'rounded-lg border bg-white focus:border-slate-400 focus:ring-2 focus:ring-slate-200/50 outline-none transition text-slate-800 dark:focus:border-slate-500 dark:focus:ring-slate-500/30';
  const inputInvalid =
    'border-red-400 focus:border-red-500 focus:ring-red-200/50 dark:border-red-500 dark:focus:ring-red-500/30';

  return (
    <div className='space-y-4'>
      <div>
        <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
          Title
        </label>
        <input
          type='text'
          value={value.title}
          onChange={(e) => update({ title: e.target.value })}
          className={`w-full px-3.5 py-2 ${inputBase} border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200`}
          placeholder='e.g. Mr, Mrs, Dr'
        />
      </div>
      <div className='grid grid-cols-2 gap-4'>
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            First name
          </label>
          <input
            type='text'
            value={value.firstname}
            onChange={(e) => update({ firstname: e.target.value })}
            className={`w-full px-3.5 py-2 ${inputBase} border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200`}
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-slate-600 dark:text-slate-400 mb-1.5'>
            Last name
          </label>
          <input
            type='text'
            value={value.lastname}
            onChange={(e) => update({ lastname: e.target.value })}
            className={`w-full px-3.5 py-2 ${inputBase} border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200`}
          />
        </div>
      </div>

      <div>
        <div className='flex items-center justify-between mb-2'>
          <span className='text-sm font-medium text-slate-600 dark:text-slate-400'>
            Emails
          </span>
          <button
            type='button'
            onClick={addEmail}
            className='flex items-center gap-1.5 text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 p-1 rounded'
          >
            <IconPlus className='w-4 h-4' />
            <span>Add email</span>
          </button>
        </div>
        <div className='space-y-2'>
          {value.emails.map((row, i) => (
            <div
              key={i}
              className='grid grid-cols-[5.5rem_1fr_2rem] gap-2 items-center'
            >
              <select
                value={row.emailtype}
                onChange={(e) =>
                  updateEmail(i, {
                    ...row,
                    emailtype: e.target.value as EmailType,
                  })
                }
                className={`px-1 py-2 text-xs ${inputBase} border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200`}
              >
                {EMAIL_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
              <input
                type='text'
                value={row.emailvalue}
                onChange={(e) =>
                  updateEmail(i, { ...row, emailvalue: e.target.value })
                }
                className={`px-3.5 py-2 ${inputBase} ${
                  row.emailvalue.trim() && !isEmail(row.emailvalue)
                    ? inputInvalid
                    : 'border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200'
                }`}
                placeholder='email@example.com'
              />
              <button
                type='button'
                onClick={() => removeEmail(i)}
                className='p-1.5 text-slate-400 hover:text-red-600 dark:hover:text-red-400 rounded'
                aria-label='Remove email'
              >
                <IconTrash className='w-4 h-4' />
              </button>
            </div>
          ))}
        </div>
      </div>

      <div>
        <div className='flex items-center justify-between mb-2'>
          <span className='text-sm font-medium text-slate-600 dark:text-slate-400'>
            Phones
          </span>
          <button
            type='button'
            onClick={addPhone}
            className='flex items-center gap-1.5 text-sm text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 p-1 rounded'
          >
            <IconPlus className='w-4 h-4' />
            <span>Add phone</span>
          </button>
        </div>
        <div className='space-y-2'>
          {value.phones.map((row, i) => (
            <div
              key={i}
              className='grid grid-cols-[5.5rem_1fr_2rem] gap-2 items-center'
            >
              <select
                value={row.phonetype}
                onChange={(e) =>
                  updatePhone(i, {
                    ...row,
                    phonetype: e.target.value as PhoneType,
                  })
                }
                className={`px-1 py-2 text-xs ${inputBase} border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200`}
              >
                {PHONE_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
              <input
                type='text'
                value={row.phonevalue}
                onChange={(e) =>
                  updatePhone(i, { ...row, phonevalue: e.target.value })
                }
                className={`px-3.5 py-2 ${inputBase} ${
                  row.phonevalue.trim() && !isPhone(row.phonevalue)
                    ? inputInvalid
                    : 'border-slate-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200'
                }`}
                placeholder='+1234567890'
              />
              <button
                type='button'
                onClick={() => removePhone(i)}
                className='p-1.5 text-slate-400 hover:text-red-600 dark:hover:text-red-400 rounded'
                aria-label='Remove phone'
              >
                <IconTrash className='w-4 h-4' />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export function contactRequestEmpty(): ContactRequest {
  return {
    title: '',
    firstname: '',
    lastname: '',
    emails: [],
    phones: [],
  };
}

export function contactDetailToRequest(d: {
  title: string | null;
  firstname: string;
  lastname: string;
  emails: ContactEmailDto[];
  phones: ContactPhoneDto[];
}): ContactRequest {
  return {
    title: d.title ?? '',
    firstname: d.firstname,
    lastname: d.lastname,
    emails: d.emails.map((e) => ({ ...e })),
    phones: d.phones.map((p) => ({ ...p })),
  };
}

/** Removes emails/phones with empty value so they are not sent in the request body. */
export function sanitizeContactRequest(req: ContactRequest): ContactRequest {
  return {
    ...req,
    emails: req.emails.filter((e) => e.emailvalue.trim() !== ''),
    phones: req.phones.filter((p) => p.phonevalue.trim() !== ''),
  };
}
