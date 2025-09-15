<script>
  import { api } from './api';
  let creator = '';
  let question = '';
  let validUntil = '';
  let aPublic = true;

  let optionCaption = '';
  let options = [];
  let msg = '';

  function addOption() {
    const c = optionCaption.trim();
    if (!c) return;
    options = [...options, { caption: c, presentationOrder: options.length + 1 }];
    optionCaption = '';
  }

  async function createPoll() {
    msg = '';
    if (!creator || !question || !validUntil || options.length === 0) {
      msg = 'creator, question, validUntil and at least one option are required';
      return;
    }
    try {
      const poll = await api.post(`/api/polls?username=${encodeURIComponent(creator)}`, {
        question, aPublic, validUntil
      });
      for (const opt of options) {
        await api.post(`/api/polls/${poll.id}/options`, opt);
      }
      msg = `Created poll #${poll.id}`;
      setTimeout(() => {
        creator = ''; question = ''; validUntil = ''; aPublic = true; options = []; msg = '';
      }, 1200);
    } catch (e) { msg = e.message; }
  }
</script>

<h2>Create Poll</h2>
<div style="display:grid; gap:.5rem; grid-template-columns:repeat(2,minmax(0,1fr))">
  <input placeholder="creator username" bind:value={creator} />
  <input type="date" bind:value={validUntil} />
  <input style="grid-column:1 / span 2" placeholder="question" bind:value={question} />
  <label style="display:flex; align-items:center; gap:.5rem">
    <input type="checkbox" bind:checked={aPublic} /> Public
  </label>
</div>

<h3>Options</h3>
<div style="display:flex; gap:.5rem; flex-wrap:wrap">
  <input placeholder="Add option…" bind:value={optionCaption}
         on:keydown={(e)=> e.key==='Enter' && addOption()} />
  <button on:click={addOption}>Add</button>
</div>
<ol>
  {#each options as o}
    <li>[{o.presentationOrder}] {o.caption}</li>
  {/each}
</ol>

<button on:click={createPoll}>Create Poll</button>
<p style="color:#555">{msg}</p>
