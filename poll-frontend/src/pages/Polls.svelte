<script>
import { Polls } from '../api.js'
import { session } from '../lib/session.js'
import { onMount } from 'svelte'

let data = []
let loading = true
let error = ''

async function load(){
    loading = true; error = ''
    try { 
        data = await Polls.list() 
    } catch (e) { 
        error = e.message || 'Failed to load polls'
    } finally { 
        loading = false 
    }
}
onMount(load)

async function del(p){
    if (!confirm('Delete this poll?')) return
    try { 
        await Polls.delete(p.id)
        await load() 
    } catch (e) { 
        alert(e.message || 'Delete failed') 
    }
}

$: visible = ($session) ? data.filter(p => p.aPublic || p.creator?.id === $session.id) : data.filter(p => p.aPublic)

</script>

<div class="shell stack">
    <div class="card">
        <h2>Polls</h2>
        {#if loading}
        <p class="muted">Loading…</p>
        {:else if error}
        <p class="error" style="color:white">{error}</p>
        {:else if visible.length === 0}
        <p class="muted">No polls yet. <a href="#/polls/new">Create one</a>.</p>
        {:else}
        <ul class="clean">
            {#each visible as p (p.id)}
            <li class="card">
                <div class="stack">
                    <div style="display:flex;justify-content:space-between;align-items:center;">
                        <strong>Poll Question:</strong>&nbsp;{p.question}
                        <div style="display:flex; gap:8px;">
                            <a class="btn" href={`#/polls/${p.id}`}>Open</a>
                            <button class="btn secondary" on:click={() => del(p)} disabled={!$session || $session.id !== p.creator?.id}>Delete</button>
                        </div>
                    </div>
                    <div class="muted">
                        Creator: {p.creator?.username ?? 'unknown'}
                    </div>
                </div>
            </li>
            {/each}
        </ul>
        {/if}
    </div>
</div>