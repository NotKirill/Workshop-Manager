// Signs in as the dev admin; returns call(method, path, body) for the app's API
export async function signIn(base) {
  let token = null;
  const call = async (method, path, body) => {
    const res = await fetch(base + path, {
      method,
      headers: { 'Content-Type': 'application/json', ...(token && { Authorization: 'Bearer ' + token }) },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    if (!res.ok) {
      throw new Error(`${method} ${path}: HTTP ${res.status} ${await res.text()}`);
    }
    const text = await res.text();
    return text ? JSON.parse(text) : null;
  };
  token = (await call('POST', '/api/auth/login', { login: 'admin', password: 'Admin123!dev' })).access_token;
  return call;
}
