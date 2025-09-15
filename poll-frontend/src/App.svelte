<script>
  import CreateUserComponent from './CreateUserComponent.svelte';
  import CreatePollComponent from './CreatePollComponent.svelte';
  import VoteComponent from './VoteComponent.svelte';
</script>

<main style="font-family:system-ui, Arial; margin:2rem; display:grid; gap:1.5rem">
  <h1>Poll App</h1>
  <CreateUserComponent />
  <CreatePollComponent />
  <VoteComponent />
</main>
