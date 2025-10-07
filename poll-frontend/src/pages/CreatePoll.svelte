<script>
import { Polls } from '../api.js'
import { session } from '../lib/session.js'
import { goto } from '../lib/router.js'

let question = ''
let validUntil = '' 
let aPublic = true
let aPublicStr = aPublic ? 'true' : 'false'
let options = ['', '']
let error = ''
let saving = false

function addOption()  { 
    options = [...options, ''] 
}

function removeOption(i) { 
    options = options.filter((_, idx) => idx !== i) 
}

// to keep boolean in sync with the selected string in UI
$: aPublic = aPublicStr === 'true';

async function submit(){
    error = ''

    const q = (question ?? '').trim()
    if (!q) {
        error = 'Please enter a question.';
        return
    }

    if (!validUntil) {
        error = 'Please pick a date.';
        return
    }

    const opts = (options || [])
    .map(s => (s ?? '').trim())
    .filter(Boolean)

    if (opts.length < 2) {
        error = 'Add at least two options.';
        return
    }

    const user = $session;
    if (!user?.id) {
        error = 'You must be logged in.';
        return
    }

    const payload = {
        question: q, 
        validUntil, 
        aPublic,
        options: opts
        }

    try {
        saving = true
        await Polls.create(payload, user.id) // creatorId in query param
        goto('/polls')
    }   catch (e) {
        error = e?.message || 'Could not create poll'
    }   finally {
        saving = false
    }
}
</script>

<div class="shell">
<div class="card stack">
    <h2>Create poll</h2>
    <div class="stack">
    <label>Question</label>
    <input bind:value={question} placeholder="Enter a question..." />
    </div>
    <div class="row">
    <div class="stack">
        <label>Valid until</label>
        <input type="date" bind:value={validUntil}/>
    </div>
    <div class="stack">
        <label>Public</label>
        <select bind:value={aPublicStr}>
        <option value="true">Yes</option>
        <option value="false">No</option>
        </select>
    </div>
    </div>
    <hr/>
    <h3>Options</h3>
    <div class="stack">
    {#each options as opts, i}
        <div class="row">
        <input bind:value={options[i]} placeholder={`Option ${i+1}`} />
        <button class="btn secondary" on:click={() => removeOption(i)} disabled={options.length <= 2}>Remove</button>
        </div>
    {/each}
    <button class="btn secondary" on:click={addOption}>+ Add option</button>
    </div>
    {#if error}<p style="color:white">{error}</p>{/if}
    <div>
    <button class="btn" on:click={submit} disabled={saving}>{saving ? 'Creating…' : 'Create poll'}</button>
    </div>
</div>
</div>