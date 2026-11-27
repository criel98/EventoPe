/* Renovación del boleto a partir del reloj del servidor. */
document.addEventListener('DOMContentLoaded', () => {
    const zona = document.getElementById('qr-contenedor');
    if (!zona) return;
    const dibujo = document.getElementById('qr-codigo');
    const contador = document.getElementById('qr-contador');
    const barra = document.getElementById('qr-barra');
    const alerta = document.getElementById('qr-alerta');
    const boton = document.getElementById('btn-recargar-qr');
    let ocupado = false, fin = 0, reintento, detenido = false;
    function limpiar() { dibujo.replaceChildren(); barra.style.width = '0%'; fin = 0; }
    function mensaje(texto) { alerta.textContent = texto; alerta.style.display = texto ? 'block' : 'none'; }
    async function actualizar() {
        if (ocupado || detenido || document.hidden) return;
        ocupado = true;
        clearTimeout(reintento);
        limpiar();
        contador.textContent = 'Actualizando código…';
        boton.style.display = 'none';
        const inicio = performance.now();
        const abortar = new AbortController();
        const timeout = setTimeout(() => abortar.abort(), 10000);
        try {
            const respuesta = await fetch(zona.dataset.endpoint, {
                method: 'POST', credentials: 'same-origin', cache: 'no-store',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: new URLSearchParams({ entradaId: zona.dataset.entradaId, csrf: zona.dataset.csrf }),
                signal: abortar.signal
            });
            if (respuesta.redirected || !respuesta.headers.get('content-type')?.includes('application/json')) {
                detenido = true;
                throw new Error('La sesión terminó. Vuelve a iniciar sesión y abre tu boleto.');
            }
            const datos = await respuesta.json();
            if (!respuesta.ok || !datos.ok) {
                detenido = [400, 401, 403, 409].includes(respuesta.status);
                throw new Error(datos.error || 'El servidor no puede generar el código en este momento.');
            }
            const vigencia = Date.parse(datos.expiraEn) - Date.parse(datos.horaServidor);
            // Descontar toda la demora de red evita prolongar la validez del servidor.
            const disponible = vigencia - (performance.now() - inicio);
            if (!Number.isFinite(disponible) || typeof datos.contenido !== 'string') throw new Error('Respuesta de código inválida.');
            if (disponible <= 250) {
                reintento = setTimeout(actualizar, 500);
                return;
            }
            new QRCode(dibujo, { text: datos.contenido, width: 224, height: 224,
                colorDark: '#111827', colorLight: '#ffffff', correctLevel: QRCode.CorrectLevel.M });
            fin = performance.now() + disponible;
            mensaje('');
            contar();
        } catch (error) {
            limpiar();
            contador.textContent = 'Código no disponible';
            mensaje(error.name === 'AbortError' ? 'El servidor está demorando. Comprueba tu conexión.' :
                error instanceof TypeError ? 'Sin conexión. El código se actualizará al reconectar.' : error.message);
            boton.style.display = detenido ? 'none' : 'inline-flex';
            if (!detenido) reintento = setTimeout(actualizar, 4000);
        } finally { clearTimeout(timeout); ocupado = false; }
    }
    function contar() {
        if (!fin) return;
        const restante = fin - performance.now();
        if (restante <= 0) {
            limpiar();
            contador.textContent = 'Renovando código…';
            actualizar();
            return;
        }
        contador.textContent = 'Se renueva en ' + Math.ceil(restante / 1000) + ' s';
        barra.style.width = Math.max(0, Math.min(100, restante / 300)) + '%';
    }
    const intervalo = setInterval(contar, 200);
    boton.addEventListener('click', actualizar);
    window.addEventListener('online', actualizar);
    window.addEventListener('offline', () => { limpiar(); mensaje('Sin conexión. Conéctate para mostrar un código vigente.'); });
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) { limpiar(); clearTimeout(reintento); }
        else actualizar();
    });
    window.addEventListener('pagehide', () => { clearInterval(intervalo); clearTimeout(reintento); limpiar(); });
    actualizar();
});
