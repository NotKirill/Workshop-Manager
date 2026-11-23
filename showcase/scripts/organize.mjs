// Adds the personal library data from organize.json
// Usage: node organize.mjs <app url> <organize.json>
import { readFileSync } from 'node:fs';
import { signIn } from './api.mjs';

const [base, planFile] = process.argv.slice(2);
const { _format, ...plan } = JSON.parse(readFileSync(planFile, 'utf8'));
const call = await signIn(base);

const games = new Map((await call('GET', '/api/games')).map((g) => [g.name, g.id]));
const items = new Map();
for (let page = 0, pages = 1; page < pages; page++) {
  const res = await call('POST', '/api/items/search', { search_query: '', page });
  res.items.forEach((i) => items.set(i.original_id, i.id));
  pages = res.total_pages;
}

for (const [game, spec] of Object.entries(plan)) {
  const gameId = games.get(game);
  const create = async (kind, name) => (await call('POST', '/api/' + kind, { name, game_id: gameId })).id;
  const groups = new Map();
  for (const name of spec.groups) groups.set(name, await create('groups', name));
  const tags = new Map();
  for (const [originalId, [rating, tagNames, groupNames, note, title]] of Object.entries(spec.items)) {
    const id = items.get(originalId);
    if (id === undefined) {
      console.log('    not in the library, skipped:', originalId);
      continue;
    }
    for (const name of tagNames) {
      if (!tags.has(name)) tags.set(name, await create('tags', name));
    }
    if (tagNames.length) await call('PUT', `/api/items/${id}/tags`, { ids: tagNames.map((n) => tags.get(n)) });
    if (groupNames.length) await call('PUT', `/api/items/${id}/groups`, { ids: groupNames.map((n) => groups.get(n)) });
    if (rating) await call('PUT', `/api/items/${id}/rating`, { score: rating });
    if (note || title) {
      await call('PUT', `/api/items/${id}`, { title, description: null, user_commentary: note, auto_backup: false });
    }
  }
  console.log(game.padEnd(24), `${Object.keys(spec.items).length} items, ${tags.size} tags, ${groups.size} groups`);
}
