/* Lectura local del QR; autorización e identidad verificadas en el servidor. */
(() => {
 'use strict';
 const $ = id => document.getElementById(id);
 const form = $('form-validar'); if (!form) return;
 const video = $('camara-video'), canvas = $('camara-canvas'), resultado = $('resultado-contenedor');
 let stream, frame, ocupado = false, pendiente = null, pausado = false;
 function aviso(texto) { $('camara-estado').textContent = texto; }
 function detener() {
  cancelAnimationFrame(frame); stream?.getTracks().forEach(t => t.stop()); stream = null;
  video.srcObject = null; $('btn-camara').textContent = 'Activar cámara';
 }
 function mostrar(titulo, texto, permitido, reintento) {
  resultado.replaceChildren(); resultado.style.display = 'block'; resultado.setAttribute('role', 'status');
  const tarjeta = document.createElement('div'); tarjeta.className = 'tarjeta';
  tarjeta.style.borderLeft = '6px solid ' + (permitido ? '#166438' : '#971b1b');
  tarjeta.style.background = permitido ? '#eafaf1' : '#fdeeed';
  const h = document.createElement('h2'); h.textContent = titulo;
  const p = document.createElement('p'); p.textContent = texto;
  const b = document.createElement('button'); b.type = 'button';
  b.textContent = reintento ? 'Reintentar la misma lectura' : 'Siguiente asistente';
  b.addEventListener('click', () => {
   if (reintento) enviar();
   else { pendiente = null; pausado = false; resultado.style.display = 'none'; $('input-dni').value = ''; $('input-manual-qr').value = ''; $('input-dni').focus(); escanear(); }
  });
  tarjeta.append(h,p,b); resultado.append(tarjeta); resultado.scrollIntoView({block:'nearest'});
 }
 async function enviar() {
  if (ocupado || !pendiente) return;
  ocupado = true; pausado = true; cancelAnimationFrame(frame);
  aviso('Comprobando entrada…');
  const controlador = new AbortController(); const timeout = setTimeout(() => controlador.abort(), 10000);
  try {
   const res = await fetch(form.action, {method:'POST', credentials:'same-origin', cache:'no-store', body:pendiente, signal:controlador.signal});
   if (res.redirected || !res.headers.get('content-type')?.includes('application/json')) {
    pendiente = null; mostrar('Sesión no disponible','Vuelve a iniciar sesión como personal de seguridad.',false,false); return;
   }
   const data = await res.json();
   if (!res.ok || !data.ok) {
    if (res.status >= 500) mostrar('No se pudo consultar',data.error || 'Inténtalo otra vez.',false,true);
    else { pendiente = null; mostrar('Validación no completada',data.error || 'Revisa los datos de la lectura.',false,false); }
    return;
   }
   $('seguridad-solicitud-id').value = data.siguienteSolicitudId;
   const motivos = {VALIDO:'Boleto vigente. Puede ingresar.',YA_UTILIZADA:'Este boleto ya fue utilizado. No se permite otro ingreso.',ANULADA:'El boleto está anulado.',IDENTIDAD_NO_COINCIDE:'El DNI no coincide con el asistente registrado.',EVENTO_INCORRECTO:'El boleto pertenece a otro evento.',QR_VENCIDO_O_FUERA_DE_INTERVALO:'El QR venció. Solicita mostrar el código actualizado.',QR_ALTERADO:'El código no es válido.',QR_MALFORMADO:'No se reconoce el formato del QR.',QR_DESCONOCIDO:'No se encontró una entrada asociada al QR.'};
   const recuperado = data.recuperado ? ' Resultado recuperado de la misma solicitud; no autoriza otro ingreso.' : '';
   mostrar(data.permitido ? 'ACCESO PERMITIDO' : 'ACCESO DENEGADO',(motivos[data.motivo] || data.motivo) + recuperado,data.permitido,false);
   pendiente = null;
  } catch (_) { mostrar('Sin respuesta del servidor','Comprueba tu conexión. El reintento conserva exactamente la misma lectura y el mismo DNI.',false,true); }
  finally { clearTimeout(timeout); ocupado = false; aviso('Revisa el resultado antes del siguiente asistente.'); }
 }
 function leer(contenido) {
  if (ocupado || pausado) return;
  const dni = $('input-dni').value.trim();
  if (!/^\d{8}$/.test(dni)) { aviso('Primero escribe los 8 dígitos del DNI presentado por el asistente.'); return; }
  pendiente = new URLSearchParams(new FormData(form));
  pendiente.set('dni',dni); pendiente.set('contenido',contenido); enviar();
 }
 let ultimoFrame = 0;
 function escanear(ahora = 0) {
  if (!stream || pausado || ocupado) return;
  if (video.readyState >= 2 && ahora - ultimoFrame >= 160) {
   ultimoFrame = ahora;
   const ancho = Math.min(640, video.videoWidth);
   canvas.width = ancho; canvas.height = Math.round(video.videoHeight * ancho / video.videoWidth);
   const ctx = canvas.getContext('2d', {willReadFrequently:true});
   ctx.drawImage(video,0,0,canvas.width,canvas.height);
   const imagen = ctx.getImageData(0,0,canvas.width,canvas.height);
   const qr = window.jsQR(imagen.data,imagen.width,imagen.height,{inversionAttempts:'dontInvert'});
   if (qr) leer(qr.data);
  }
  if (!pausado) frame = requestAnimationFrame(escanear);
 }
 $('btn-camara').addEventListener('click', async () => {
  if (stream) { detener(); aviso('Cámara detenida.'); return; }
  if (!navigator.mediaDevices?.getUserMedia) { aviso('La cámara necesita HTTPS o localhost y permiso del navegador.'); return; }
  try {
   stream = await navigator.mediaDevices.getUserMedia({video:{facingMode:{ideal:'environment'},width:{ideal:640}},audio:false});
   video.srcObject = stream; video.muted = true; await video.play();
   $('btn-camara').textContent = 'Detener cámara'; aviso('Escribe el DNI y apunta al QR vigente.'); escanear();
  } catch (_) { detener(); aviso('No se pudo abrir la cámara. Revisa el permiso y que no esté en uso.'); }
 });
 $('btn-validar-manual').addEventListener('click', () => {
  const texto = $('input-manual-qr').value.trim();
  if (!texto) { aviso('Introduce el contenido de un QR para la prueba manual.'); return; }
  leer(texto);
 });
 window.addEventListener('pagehide',detener);
 document.addEventListener('visibilitychange', () => { if(document.hidden) detener(); });
})();
