<script>
  import { route, goto } from './lib/router.js'
  import { session } from './lib/session.js'
  import Nav from './components/Nav.svelte'
  import Welcome from './pages/Welcome.svelte'
  import LoginOrCreate from './pages/LoginOrCreate.svelte'
  import Polls from './pages/Polls.svelte'
  import CreatePoll from './pages/CreatePoll.svelte'
  import PollDetail from './pages/PollDetail.svelte'

  function match(path, pattern){
    const a = path.split('/').filter(Boolean)
    const b = pattern.split('/').filter(Boolean)
    if (a.length !== b.length) return null
    const params = {}
    for (let i = 0; i < b.length; i++) {
      if (b[i].startsWith(':')) params[b[i].slice(1)] = a[i]
      else if (a[i] !== b[i]) return null
    }
    return params
  }
  
  // to compute params reactively whenever route changes
  $: pollParams = match($route, '/polls/:id');
  // expose current session for debugging from __session in devtools
  $: if (typeof window !== 'undefined') {
    window['__session'] = $session;  
  }
</script>

<div class="shell">
  <Nav />
</div>

{#key $route}
  {#if $route === '/'}
    <Welcome />
  {:else if $route === '/auth'}
    <LoginOrCreate />
  {:else if $route === '/polls'}
    <Polls />
  {:else if $route === '/polls/new'}
    <CreatePoll />
  {:else if pollParams}
    <PollDetail params={pollParams} />
  {:else}
    <div class="shell"><div class="card"><h3>Not found</h3><a href="#/">Go home</a></div></div>
  {/if}
{/key}