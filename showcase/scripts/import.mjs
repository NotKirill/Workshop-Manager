// Registers the ids from ids.json with their dependencies
// Usage: node import.mjs <app url> <ids.json>
import { readFileSync } from 'node:fs';
import { signIn } from './api.mjs';

const [base, idsFile] = process.argv.slice(2);
const plan = JSON.parse(readFileSync(idsFile, 'utf8'));
const call = await signIn(base);

async function register(label, ids) {
  const { results } = await call('POST', '/api/items/register-external', {
    items: [{ modspace: 'STEAM', original_ids: ids }],
    include_dependencies: true,
  });
  const counts = {};
  for (const r of results) counts[r.status] = (counts[r.status] ?? 0) + 1;
  console.log(label.padEnd(24), JSON.stringify(counts));
  for (const r of results.filter((r) => !['REGISTERED', 'ALREADY_REGISTERED'].includes(r.status))) {
    console.log('   ', r.original_id, r.status, r.reason ?? '');
  }
}

for (const [game, ids] of Object.entries(plan.games)) {
  await register(game, ids);
}
for (const id of [...plan.lead].reverse()) {
  await register('lead ' + id, [id]);
}
