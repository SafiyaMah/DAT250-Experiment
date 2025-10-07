// src/api.js
const BASE = import.meta.env?.DEV ? 'http://localhost:8080' : '';

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

async function request(method, path, body) { // GET/POST/DELETE, URL path, optional body
  const init = { method }; // manage options for the fetch req

  if (body !== undefined && body !== null) {
    init.headers = { 'Content-Type': 'application/json' };
    init.body = JSON.stringify(body);
  }

  let res;
  try {
    res = await fetch(`${BASE}${path}`, init); // localhost.../api/...
  } catch (e) {
    throw new ApiError(0, e?.message || 'Network error'); // no HTTP status
  }

  if (!res.ok) {
    let msg = await res.text().catch(() => '');
    try { 
      const j = JSON.parse(msg);
      msg = j.message || j.error || msg;
    } catch {} // leave msg as is
    throw new ApiError(res.status, msg || `${res.status} ${res.statusText}`);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

// api requests
export const api = {
  get: (p) => request('GET', p),
  post: (p, b) => request('POST', p, b),
  put: (p, b) => request('PUT', p, b),
  del: (p) => request('DELETE', p),
};

// Users
export const Users = {
  list: () => api.get('/api/users'),
  create: (payload => api.post('/api/users', payload)), // {username, email}
  getByUsername : (username) => api.get(`/api/users?username=${encodeURIComponent(username)}`)
};

// Polls
export const Polls = {
  list: () => api.get(`/api/polls`),
  get: (id) => api.get(`/api/polls/${id}`),
  create: (payload, creatorId) => api.post(`/api/polls?creatorId=${encodeURIComponent(creatorId)}`, payload), // {question, validUntil, aPublic, optionCaptions[]}
  delete: (id) => api.del(`/api/polls/${id}`),
  addOption: (pollId, caption) => api.post(`/api/polls/${pollId}/options`, { caption }),
  vote: ({pollId, voteOptionId, voterId}) => api.post(`/api/polls/${encodeURIComponent(pollId)}/votes?voteOptionId=${encodeURIComponent(voteOptionId)}&voterId=${encodeURIComponent(voterId)}`, null),
  results : (id) => api.get(`/api/polls/${id}/results`)
};

// Votes
export const Votes = {
  cast: (pollId, optionId, voterId) => api.post('/api/votes', { pollId, optionId, voterId })
};