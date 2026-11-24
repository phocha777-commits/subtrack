const CACHE='subtrack-static-v3';
const ASSETS=['./','./index.html','./manifest.webmanifest','./icon.svg','./icons/icon-192.png','./icons/icon-512.png','./capacitor-core.js','./privacy.html'];
self.addEventListener('install',e=>{self.skipWaiting();e.waitUntil(caches.open(CACHE).then(c=>c.addAll(ASSETS)))});
self.addEventListener('activate',e=>e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim())));
// 화면(HTML)은 네트워크 우선 → 수정본이 바로 반영, 오프라인이면 캐시
self.addEventListener('fetch',e=>{
  if(e.request.method!=='GET')return;
  const isPage=e.request.mode==='navigate';
  if(isPage){e.respondWith(fetch(e.request).then(r=>{const c=r.clone();caches.open(CACHE).then(x=>x.put('./index.html',c));return r}).catch(()=>caches.match('./index.html')));return}
  e.respondWith(caches.match(e.request).then(hit=>hit||fetch(e.request)));
});
