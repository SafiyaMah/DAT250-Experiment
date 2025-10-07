import { writable, derived} from 'svelte/store'

const KEY = 'pollapp.session'

function load() {
    try { 
        const raw = localStorage.getItem(KEY)
        return raw ? JSON.parse(raw) : null
    } catch { 
        return null 
    }
}

export const session = writable(load()); // {id, username, email}

session.subscribe(val => {
    try { 
        if (val !== null) {
            localStorage.setItem(KEY, JSON.stringify(val));
        } else {
            localStorage.removeItem(KEY);
        }
    } catch {
        // ignore storage errors
    }
});

// Helpers
export function login(user) { 
    // expect shape: {id, username, email}
    session.set(user) 
}

export function logout() { 
    session.set(null) 
}

export const isAuthed = derived(session, ($s) => !!$s); // true/false depends on wether session has a user
export const userId = derived(session, ($s) => $s?.id ?? null); // current user's id from the session