// Records the README GIF: add an item with its dependencies, open it, go back to the library.
// Usage: node capture.mjs <app url> <output .gif>, in the runner (run.sh capture). It registers items, so the
// organized snapshot has to be loaded again before the next run (run.sh does that)
import { chromium } from 'playwright';
import { execFileSync } from 'node:child_process';
import { mkdirSync, mkdtempSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';

// Settings
const ITEM_LINK = 'https://steamcommunity.com/sharedfiles/filedetails/?id=2995193002'; // Calamity: Wrath of the Gods
const ITEM_TITLE = 'Calamity: Wrath of the Gods';
const NEW_ITEMS = 4;             // the item and its dependencies that the showcase data doesn't have yet
const VIEWPORT = { width: 1440, height: 900 };
const TIMEZONE = 'UTC';          // fixed, so dates read the same wherever it is recorded
const LOCALE = 'en-US';
const PACE = 0.75;               // multiplies every pause and cursor step
const GIF_FPS = 12;
const GIF_WIDTH = 1080;
const MAX_FRAME = 2.5;           // seconds a still frame may last
const CUT_PAUSE = 0.5;           // seconds shown where a wait is cut out
const WAIT_LIMIT = 180_000;      // ms for a cut-out wait before giving up

const [base, output] = process.argv.slice(2);
const ITEM_ID = new URL(ITEM_LINK).searchParams.get('id');
const wait = (page, ms) => page.waitForTimeout(Math.round(ms * PACE));

// Headless browsers draw no cursor: this one follows the mouse events and survives reloads
const CURSOR_SCRIPT = `(() => {
  const install = () => {
    if (document.getElementById('__cursor')) return;
    const c = document.createElement('div');
    c.id = '__cursor';
    c.innerHTML = '<svg width="26" height="26" viewBox="0 0 24 24"><path d="M4 2.5l6.6 18.2 2.5-7.3 7.4-2.6z" fill="#fff" stroke="#111" stroke-width="1.4" stroke-linejoin="round"/></svg>';
    Object.assign(c.style, { position: 'fixed', left: '-40px', top: '-40px', zIndex: 2147483647, pointerEvents: 'none',
      transform: 'translate(-4px, -3px)', transition: 'scale 90ms', filter: 'drop-shadow(0 1px 2px rgba(0,0,0,.5))' });
    const saved = JSON.parse(sessionStorage.getItem('__cursor') || 'null');
    if (saved) { c.style.left = saved.x + 'px'; c.style.top = saved.y + 'px'; }
    document.documentElement.appendChild(c);
    addEventListener('mousemove', (e) => {
      c.style.left = e.clientX + 'px'; c.style.top = e.clientY + 'px';
      sessionStorage.setItem('__cursor', JSON.stringify({ x: e.clientX, y: e.clientY }));
    }, true);
    addEventListener('mousedown', () => { c.style.scale = '0.82'; }, true);
    addEventListener('mouseup', () => { c.style.scale = '1'; }, true);
  };
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', install); else install();
})();`;

// Seeded, so every run moves the same way
let seed = 7;
const rnd = () => ((seed = (seed * 16807) % 2147483647) / 2147483647);
const ease = (t) => (t < 0.5 ? 4 * t * t * t : 1 - (-2 * t + 2) ** 3 / 2);
let mouse = { x: 820, y: 560 };

// A slightly bent curve with ease-in-out, like a hand
async function moveTo(page, locator) {
  const box = await locator.boundingBox();
  const tx = box.x + box.width * (0.5 + (rnd() - 0.5) * 0.3);
  const ty = box.y + box.height * (0.5 + (rnd() - 0.5) * 0.3);
  const dx = tx - mouse.x, dy = ty - mouse.y;
  const dist = Math.hypot(dx, dy) || 1;
  const bend = (rnd() - 0.5) * dist * 0.4;
  const [nx, ny] = [-dy / dist, dx / dist];
  const c1 = [mouse.x + dx * 0.3 + nx * bend, mouse.y + dy * 0.3 + ny * bend];
  const c2 = [mouse.x + dx * 0.75 + nx * bend * 0.35, mouse.y + dy * 0.75 + ny * bend * 0.35];
  const steps = Math.max(16, Math.min(55, Math.round(dist / 16)));
  for (let i = 1; i <= steps; i++) {
    const t = ease(i / steps), u = 1 - t;
    const x = u ** 3 * mouse.x + 3 * u * u * t * c1[0] + 3 * u * t * t * c2[0] + t ** 3 * tx;
    const y = u ** 3 * mouse.y + 3 * u * u * t * c1[1] + 3 * u * t * t * c2[1] + t ** 3 * ty;
    await page.mouse.move(x, y);
    await wait(page, 12);
  }
  mouse = { x: tx, y: ty };
}

async function click(page, locator) {
  await moveTo(page, locator);
  await wait(page, 140);
  await page.mouse.down();
  await wait(page, 80);
  await page.mouse.up();
}

async function settle(page) {
  await page.waitForLoadState('networkidle');
  // Only images on screen: lazy ones further down never load
  await page.waitForFunction(() => [...document.images]
    .filter((i) => i.getBoundingClientRect().top < window.innerHeight)
    .every((i) => i.complete));
  await page.waitForTimeout(400);
}

// True when the first `count` items of the search all have a preview that loads
function previewsReady(page, searchQuery, count) {
  return page.evaluate(async ([query, count]) => {
    const res = await fetch('/api/items/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + localStorage.getItem('access_token') },
      body: JSON.stringify({ search_query: query, page: 0 }),
    });
    const items = (await res.json()).items.slice(0, count);
    if (items.length < count || !items.every((i) => i.preview_url)) return false;
    // The link appears before the file is stored
    const loads = await Promise.all(items.map((i) => fetch(i.preview_url).then((r) => r.ok, () => false)));
    return loads.every(Boolean);
  }, [searchQuery, count]);
}

const itemPreviewReady = (page) => previewsReady(page, 'original:' + ITEM_ID, 1);
const newPreviewsReady = (page) => previewsReady(page, '', NEW_ITEMS);

// A loop, not waitForFunction: that one takes an async check's promise as true at once
async function waitUntil(page, check, what) {
  const deadline = Date.now() + WAIT_LIMIT;
  while (Date.now() < deadline) {
    if (await check(page)) return;
    await page.waitForTimeout(1000);
  }
  throw new Error(`Gave up waiting: ${what}`);
}

mkdirSync(dirname(output), { recursive: true });
const frameDir = mkdtempSync(join(tmpdir(), 'showcase-frames-'));

const browser = await chromium.launch();
const context = await browser.newContext({ viewport: VIEWPORT, colorScheme: 'dark', timezoneId: TIMEZONE, locale: LOCALE });
await context.addInitScript(CURSOR_SCRIPT);
const page = await context.newPage();
await page.goto(base + '/login');
await page.getByPlaceholder('Username or email').fill('admin');
await page.getByPlaceholder('Password').fill('Admin123!dev');
await page.getByRole('button', { name: 'Sign in', exact: true }).click();
await page.waitForURL('**/library**');
await settle(page);
await page.mouse.move(mouse.x, mouse.y);

// The browser's screencast sends a frame only when the page changes
const frames = [];
let recording = false;
const cdp = await context.newCDPSession(page);
cdp.on('Page.screencastFrame', async ({ data, metadata, sessionId }) => {
  if (recording) {
    const file = join(frameDir, String(frames.length).padStart(5, '0') + '.jpg');
    writeFileSync(file, Buffer.from(data, 'base64'));
    frames.push({ file, time: metadata.timestamp });
  }
  await cdp.send('Page.screencastFrameAck', { sessionId }).catch(() => {});
});
const record = async () => {
  recording = true;
  await cdp.send('Page.startScreencast', { format: 'jpeg', quality: 92, everyNthFrame: 1 });
};
const pause = async () => {
  recording = false;
  frames.push({ cut: true });
  await cdp.send('Page.stopScreencast');
};
// Goes on at once if the check passes; otherwise the wait is left out of the GIF
const cutUntil = async (check, message) => {
  if (await check(page)) return;
  console.log(message + ': the wait is cut out');
  await pause();
  await waitUntil(page, check, message);
  await record();
  await wait(page, 300);
};

const newItem = page.getByRole('main').getByRole('link').filter({ hasText: ITEM_TITLE }).first();

await record();
await wait(page, 1000);
await click(page, page.getByRole('button', { name: 'Add items' }));
await wait(page, 700);
const links = page.getByRole('dialog').getByRole('textbox');
await click(page, links);
await wait(page, 350);
await links.fill(ITEM_LINK);
await wait(page, 700);
await click(page, page.getByText('Also add required items'));
await wait(page, 500);
await click(page, page.getByRole('button', { name: /^Add \d+ item/ }));
// Steam may be slow. "Close" comes with the results and stays (the toast doesn't)
const close = page.getByRole('dialog').getByText('Close', { exact: true });
await wait(page, 700);
await cutUntil(() => close.isVisible(), 'Steam is slow to answer');
await wait(page, 2000);
await click(page, close);
await wait(page, 800);

await cutUntil(itemPreviewReady, 'Item preview not stored yet');
await click(page, newItem);
await page.waitForURL('**/items/**');
await settle(page);
await wait(page, 1000);
await cutUntil(newPreviewsReady, 'Dependency previews not stored yet');

await click(page, page.getByRole('main').getByRole('link', { name: 'Library', exact: true }));
await page.waitForURL('**/library**');
await settle(page);
await wait(page, 900);
await moveTo(page, newItem);
await wait(page, 2300);
await pause();
await browser.close();

// Each frame lasts until the next one, at most MAX_FRAME; a cut lasts CUT_PAUSE
const list = [];
for (let i = 0; i < frames.length; i++) {
  const f = frames[i];
  if (f.cut) continue;
  const next = frames[i + 1];
  const duration = !next ? 1.5 : next.cut ? CUT_PAUSE : Math.min(next.time - f.time, MAX_FRAME);
  list.push(`file '${f.file}'\nduration ${duration.toFixed(3)}`);
}
list.push(list.at(-1).split('\n')[0]);
writeFileSync(join(frameDir, 'list.txt'), list.join('\n'));
// Two passes: a palette made from the frames keeps the GIF small and clean
execFileSync('ffmpeg', ['-y', '-loglevel', 'error', '-f', 'concat', '-safe', '0', '-i', join(frameDir, 'list.txt'),
  '-vf', `fps=${GIF_FPS},scale=${GIF_WIDTH}:-1:flags=lanczos,split[a][b];[a]palettegen=stats_mode=diff[p];[b][p]paletteuse=dither=bayer:bayer_scale=4`,
  output]);
rmSync(frameDir, { recursive: true, force: true });
console.log('Saved', output);
