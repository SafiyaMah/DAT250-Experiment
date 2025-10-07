import { writable } from 'svelte/store'

export const route = writable(window.location.hash.slice(1) || '/')

export function goto(path){
    if (!path.startsWith('/')) path = '/' + path
    window.location.hash = path
}

window.addEventListener('hashchange', () => {
    route.set(window.location.hash.slice(1) || '/')
})
