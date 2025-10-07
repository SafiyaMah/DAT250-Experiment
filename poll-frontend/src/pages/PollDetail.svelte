<script>
import { onMount } from 'svelte'
import { Polls, Votes } from '../api.js'
import { session } from '../lib/session.js'

export let params = {} // router injects { id }
let poll
let loading = true
let error = ''
let casting = false
let counts = new Map()
let newCaption = ''

async function load(){
    loading = true
    error = ''
    try { 
        poll = await Polls.get(params.id) 
        const res = await Polls.results(params.id)
        counts = new Map(res.map(r => [r.optionId, r.votes]))
    } catch (e) { 
        error = e.message 
    }finally { 
        loading = false 
    }
}
onMount(load)

async function cast(optionId){
    if (!$session) { 
        alert('Log in first'); 
    return }
    casting = true
    try {
    await Votes.cast(poll.id, optionId, $session.id)
    await load()
    } catch (e) { 
        if (e.status === 409) {
            alert("You've already voted on this poll.")
        }
        else {
            alert(e.message) 
        }
    } finally { 
        casting = false }
}

async function addNew(){
    const cap = (newCaption || '').trim()
    if (!cap) return
    try {
        await Polls.addOption(poll.id, cap)
        newCaption = ''
        await load() 
    } catch (e) { alert(e.message) }
}
</script>

<div class="shell">
    <div class="card stack">
        {#if loading}<p class="muted">Loading…</p>{/if}
        {#if error}<p style="color:white">{error}</p>{/if}
        {#if poll}
        <h2>{poll.question}</h2>
        {#if (poll?.voteOptions?.length ?? 0) > 0}
        <ul class="opts">
            {#each (poll?.voteOptions ?? []) as opt (opt.id)}
            <li class="opt">
                <span>{opt.caption}</span>
                <span class="muted">{counts.get(opt.id) ?? 0}</span>
                <button class="btn sm" on:click={() => cast(opt.id)} disabled={casting}>Vote</button>
            </li>
            {/each}
        </ul>
        {:else}
        <p class="muted" style="margin-top:12px;">No options yet.</p>
        {/if}
        {#if $session && poll?.creator?.id === $session.id}
        <div class="row" style="margin-top:12px;">
            <input bind:value={newCaption} placeholder="Add another option..." />
            <button class="btn" on:click={addNew} disabled={!newCaption?.trim()}>Add</button>
        </div>
        {/if}
        <a class="btn secondary" href="#/polls" style="margin-top:8px">← Back to polls</a>
        {/if}
    </div>
</div>

<style>
.opts { 
    list-style: none; 
    padding: 0; 
    margin-top: 12px; 
    display: grid; 
    gap: 8px; 
}
.opt  { 
    display: grid; 
    grid-template-columns: 1fr auto auto; 
    align-items: center; 
    gap: 12px; 
    padding: 10px 12px; 
    border-radius: 12px; 
    background: #ff69b41a; 
}
.btn.sm { 
    padding: 6px 12px; 
    border-radius: 10px; 
    }
</style>