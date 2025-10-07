<script>
import { Users } from '../api.js'
import { login as setSession } from '../lib/session.js'
import { goto } from '../lib/router.js'

let loginName = ''
let loggingIn = false
let loginError = ''

let newUser = {
  username: '',
  email: ''
}

let creating = false
let createError = ''

async function doLogin() {
  loginError = '';
  const name = loginName.trim()
  if (!name) {
    loginError = 'Please enter a username';
    return
  }
  try {
    loggingIn = true
    // GET username
    const user = await Users.getByUsername(name)
    setSession(user);
    goto('/polls');
  } catch (e) {
    loginError = 'User not found'
  } finally {
    loggingIn = false
  }
}

async function createUser() {
  createError = '';
  const username = newUser.username.trim()
  const email = newUser.email.trim()

  if (!username) {
    createError = 'Choose a username';
    return
  }
  if (!email) {
    createError = 'Add an email';
    return
  }
  try {
    creating = true;
    // POST {username, email}
    const created = await Users.create({ username, email })
    setSession(created)
    goto('/polls')
  } catch (e) {
    console.error('Create user failed:', e)
    // if duplicate
    if (e?.status === 409){
      createError = 'That username is taken. Try another.';
    }
    else {
      createError = 'Could not create user';
    }
  } finally {
    creating = false
  }
}

function clearLogin() {loginName = ''}
function clearCreate() {newUser = {username: '', email: ''}}
</script>

<!-- Login -->
<div class="shell">
  <div class="card stack narrow">
    <h2>Log in</h2>

    <div class="stack">
      <label for="loginName">Username</label>
      <input
        id="loginName"
        bind:value={loginName}
        placeholder="yourname"
        autocomplete="username"
      />
    </div>

    {#if loginError}
      <p class="error">{loginError}</p>
    {/if}

    <div style="display:flex; gap:10px;">
      <button class="btn" on:click={doLogin} disabled={loggingIn}>
        {loggingIn ? 'Logging in…' : 'Log in'}
      </button>
      <button class="btn secondary" on:click={clearLogin}>Clear</button>
    </div>
  </div>
</div>

<!-- Create user -->
<div class="shell">
  <div class="card stack">
    <h2>Create a user</h2>

    <div class="row">
      <div class="stack">
        <label for="newUsername">Username</label>
        <input
          id="newUsername"
          bind:value={newUser.username}
          placeholder="yourname"
          autocomplete="username"
        />
      </div>
      <div class="stack">
        <label for="newEmail">Email</label>
        <input
          id="newEmail"
          type="email"
          bind:value={newUser.email}
          placeholder="you@example.com"
          autocomplete="email"
        />
      </div>
    </div>
    {#if createError}
      <p class="error">{createError}</p>
    {/if}
    <div style="display:flex; gap:10px; margin-top:12px;">
      <button class="btn" on:click={createUser} disabled={creating}>
        {creating ? 'Creating…' : 'Create user'}
      </button>
      <button class="btn secondary" on:click={clearCreate}>Clear</button>
    </div>
  </div>
</div>