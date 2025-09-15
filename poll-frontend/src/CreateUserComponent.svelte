<script>
  import { api } from './api';
  let username = '';
  let email = '';
  let msg = '';

  async function createUser() {
    msg = '';
    if (!username.trim()) { msg = 'Username required'; return; }
    try {
      const u = await api.post('/api/users', { username: username.trim(), email: email.trim() });
      msg = `✅ Created user: ${u.username}`;
      setTimeout(() => { username = ''; email = ''; msg = ''; }, 1000);
    } catch (e) { msg = e.message; }
  }
</script>

<h2>Create User</h2>
<div style="display:flex; gap:.5rem; flex-wrap:wrap">
  <input placeholder="username" bind:value={username} />
  <input placeholder="email (optional)" bind:value={email} />
  <button on:click={createUser}>Create</button>
</div>
<p style="color:#555">{msg}</p>
