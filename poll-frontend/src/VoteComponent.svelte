<script>
  import { api } from './api';

  let pollId = '';
  let voter = '';
  let poll = null;
  let options = [];
  let count = 0;
  let msg = '';
  let votingId = null;

  // Make any value a nice caption string
  const toCaption = (val) => {
    if (val === null || val === undefined) return '';
    const s = String(val).trim();
    return s;
  };

  async function loadPoll() {
    msg = '';
    if (!pollId) { msg = 'Enter a poll id'; return; }
    try {
      // 1) poll
      poll = await api.get(`/api/polls/${pollId}`);

      // 2) full options (normalize + sort)
      const full = await api.get(`/api/polls/${pollId}/options`);
      options = full
        .map(o => ({
          ...o,
          caption: toCaption(o.caption) || (o.id != null ? `Option ${o.id}` : 'Option')
        }))
        .sort((a, b) =>
          (a.presentationOrder ?? 0) - (b.presentationOrder ?? 0) ||
          (a.id ?? 0) - (b.id ?? 0)
        );

      // 3) vote count
      const c = await api.get(`/api/polls/${pollId}/votes/count`);
      count = c?.count ?? 0;
    } catch (e) {
      poll = null; options = []; count = 0;
      msg = e.message || 'Failed to fetch';
      console.error('loadPoll failed', e);
    }
  }

  function getOptionId(o) { return o && (o.id ?? o); }

  async function voteFor(o) {
    const optionId = getOptionId(o);
    if (!pollId) { msg = 'Enter a poll id'; return; }
    if (!voter)  { msg = 'Enter your username'; return; }
    if (!optionId) { msg = 'Could not determine option id'; return; }
    if (votingId) return;

    votingId = optionId;
    try {
      await api.post(
        `/api/polls/${pollId}/votes?username=${encodeURIComponent(voter)}&voteOptionId=${optionId}`
      );
      msg = 'Your vote has been recorded!';
      const c = await api.get(`/api/polls/${pollId}/votes/count`);
      count = c?.count ?? count;

      setTimeout(() => resetUI(), 1000);
    } catch (e) {
      msg = e.message || 'Vote failed';
    } finally {
      votingId = null;
    }
  }

  function resetUI() {
    pollId = '';
    voter = '';
    poll = null;
    options = [];
    count = 0;
    msg = '';
  }
</script>

<h2>Vote</h2>

<div style="display:flex; gap:.5rem; flex-wrap:wrap; margin-bottom:.5rem">
  <input placeholder="poll id" bind:value={pollId} />
  <input placeholder="your username" bind:value={voter} />
  <button on:click={loadPoll}>Load poll</button>
  {#if poll}<button on:click={resetUI}>Clear</button>{/if}
</div>

{#if poll}
  <h3 style="margin:.5rem 0">{poll.question}</h3>
  <p style="margin:.25rem 0"><strong>Total votes:</strong> {count}</p>

  <ul>
    {#each options as o}
      <li style="margin:.25rem 0">
        {o.caption}
        <button on:click={() => voteFor(o)} style="margin-left:.5rem">Vote</button>
      </li>
    {/each}
  </ul>
{/if}

<p style="color:#555; margin-top:.5rem">{msg}</p>
