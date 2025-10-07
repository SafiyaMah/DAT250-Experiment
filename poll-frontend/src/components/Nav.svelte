<script>
import { session, logout } from '../lib/session.js'
import { goto } from '../lib/router.js'
import { get } from 'svelte/store'

function doLogout() {
    logout()
    goto('/')
}
</script>

<nav class="card">
<div class="links">
    <a href="#/">Welcome</a>
    <a href="#/polls">Polls</a>
    <a href="#/polls/new">Create poll</a>
</div>
<div>
    {#if $session}
    <span class="pill">👤 {$session.username}</span>
    <button class="btn secondary" on:click={doLogout}>Log out</button>
    {:else}
    <a class="btn" href="#/auth">Log in / Sign up</a>
    {/if}
</div>
</nav>
