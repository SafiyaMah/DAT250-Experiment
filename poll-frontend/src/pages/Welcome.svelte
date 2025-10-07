<script>
import { goto } from '../lib/router.js'
</script>

<div class="shell stack">
<div class="card stack">
    <h1>Welcome to DAT250 Poll System</h1>
    <p class="muted">Project by Safiya — create polls, add options, and vote.</p>
    <div style="display:flex; gap:10px;">
    <button class="btn" on:click={() => goto('/auth')}>Get started</button>
    <a class="btn secondary" href="#/polls">Browse polls</a>
    </div>
</div>
</div>
