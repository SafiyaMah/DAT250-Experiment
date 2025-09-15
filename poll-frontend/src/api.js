// src/api.js
const BASE = import.meta.env?.DEV ? 'http://localhost:8080' : '';

async function request(method, path, body) {
  const init = { method };

  // Only set JSON header when sending a body (avoids CORS preflight on GET)
  if (body !== undefined && body !== null) {
    init.headers = { 'Content-Type': 'application/json' };
    init.body = JSON.stringify(body);
  }

  let res;
  try {
    res = await fetch(`${BASE}${path}`, init);
  } catch (e) {
    // Network/CORS error happens before we get a response
    throw new Error(e?.message || 'Network error');
  }

  if (!res.ok) {
    let err = await res.text().catch(() => '');
    try { const j = JSON.parse(err); err = j.message || j.error || err; } catch {}
    throw new Error(err || `${res.status} ${res.statusText}`);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  get: (p) => request('GET', p),
  post: (p, b) => request('POST', p, b),
  put: (p, b) => request('PUT', p, b),
  del: (p) => request('DELETE', p),
};
