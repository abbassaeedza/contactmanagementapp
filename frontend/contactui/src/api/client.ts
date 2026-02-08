const baseUrl = import.meta.env.VITE_API_PATH;

function getAuthHeader(): Record<string, string> {
  const token = localStorage.getItem('auth_token');
  if (!token) return {};
  return { Authorization: `Bearer ${token}` };
}

function buildUrl(path: string, params?: Record<string, string | number>): string {
  const pathPart = path.startsWith('/') ? path : `/${path}`;
  let url = `${baseUrl}${pathPart}`;
  if (params && Object.keys(params).length > 0) {
    const search = new URLSearchParams();
    for (const [k, v] of Object.entries(params)) {
      search.set(k, String(v));
    }
    url += `?${search.toString()}`;
  }
  return url;
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text();
    let message = text;
    try {
      const json = JSON.parse(text);
      if (json.message) message = json.message;
      else if (json.error) message = json.error;
    } catch {
      // use text as message
    }
    throw new Error(message || `Request failed: ${res.status}`);
  }
  const contentType = res.headers.get('content-type');
  if (contentType?.includes('application/json')) {
    return res.json() as Promise<T>;
  }
  return undefined as unknown as T;
}

export async function apiGet<T>(path: string, params?: Record<string, string | number>): Promise<T> {
  const url = buildUrl(path, params);
  const res = await fetch(url, {
    method: 'GET',
    headers: getAuthHeader(),
  });
  return handleResponse<T>(res);
}

export async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const url = buildUrl(path);
  const res = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
    },
    body: JSON.stringify(body),
  });
  return handleResponse<T>(res);
}

export async function apiPut<T>(path: string, body: unknown): Promise<T> {
  const url = buildUrl(path);
  const res = await fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
    },
    body: JSON.stringify(body),
  });
  return handleResponse<T>(res);
}

export async function apiDelete(path: string): Promise<void> {
  const url = buildUrl(path);
  const res = await fetch(url, {
    method: 'DELETE',
    headers: getAuthHeader(),
  });
  if (!res.ok && res.status !== 204) {
    const text = await res.text();
    let message = text;
    try {
      const json = JSON.parse(text);
      if (json.message) message = json.message;
      else if (json.error) message = json.error;
    } catch {
      // use text as message
    }
    throw new Error(message || `Request failed: ${res.status}`);
  }
}
