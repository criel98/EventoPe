/* Indicador de notificaciones sin leer en la cabecera. Solo lee un número del servidor; sin él la página funciona igual. */
(() => {
 'use strict';
 const marca = document.getElementById('contador-notificaciones');
 if (!marca) return;
 fetch(marca.dataset.url, {credentials: 'same-origin', cache: 'no-store', headers: {'Accept': 'application/json'}})
  .then(r => r.ok && r.headers.get('content-type')?.includes('application/json') ? r.json() : null)
  .then(d => {
   if (!d || !Number.isInteger(d.noLeidas) || d.noLeidas < 1) return;
   marca.textContent = d.noLeidas > 99 ? '99+' : String(d.noLeidas);
   marca.hidden = false;
   marca.setAttribute('aria-label', d.noLeidas + ' notificaciones sin leer');
  })
  .catch(() => {});
})();
