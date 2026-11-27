---
title: "Guía: usar la cámara y configurar HTTPS"
---

Los navegadores móviles **solo permiten la cámara en contexto seguro**: HTTPS (o `localhost`). Con `http://192.168.x.x` la cámara no funciona.

# 1. Certificado para Tomcat 10.1

```
keytool -genkeypair -alias eventope -keyalg RSA -keysize 2048 -validity 365 ^
  -storetype PKCS12 -keystore eventope.p12 -storepass cambiar123 ^
  -dname "CN=192.168.1.50" -ext "san=ip:192.168.1.50,dns:localhost"
```
Cambia `192.168.1.50` por la IP de la PC (`ipconfig`) y la contraseña por una propia. No subas el `.p12` al repositorio.

# 2. Conector en `conf/server.xml`

```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           SSLEnabled="true" maxThreads="150" scheme="https" secure="true">
  <SSLHostConfig>
    <Certificate certificateKeystoreFile="conf/eventope.p12"
                 certificateKeystorePassword="cambiar123" type="RSA"/>
  </SSLHostConfig>
</Connector>
```
Reinicia Tomcat y abre `https://IP:8443/` desde la PC. (Validado localmente con Tomcat 10.1.)

# 3. Que el celular confíe en el certificado

- Opción recomendada: **mkcert** (`mkcert -install`, `mkcert 192.168.1.50`), e instalar la CA raíz (`rootCA.pem`) en el celular.
- Opción rápida: con certificado autofirmado, el navegador mostrará una advertencia; aceptarla («Avanzado → continuar») permite probar. Android/Chrome puede seguir bloqueando la cámara en algunos casos; si pasa, usa mkcert o un túnel HTTPS.
- PC y celular deben estar en la misma red Wi‑Fi; permitir el puerto 8443 en el firewall de Windows.

# 4. Cookie de sesión

Con HTTPS activo, cambiar en `web.xml` `<secure>false</secure>` por `true` en `cookie-config` (coordinar con el equipo; con HTTP puro dejaría de funcionar el login).

# 5. Usar la cámara (guardia)

1. Iniciar sesión como personal de validación en `https://IP:8443/personal/login`.
2. Abrir el panel de validación y pulsar el botón de cámara.
3. Aceptar el permiso de cámara del navegador.
4. Apuntar al QR; ingresar el DNI cuando se pida y confirmar.
5. Si no hay conexión: la pantalla ofrece reintentar. Un resultado «ANTERIOR RECUPERADO» (ámbar) **no autoriza otro ingreso**.

# 6. Si la cámara no abre

| Síntoma | Causa probable | Solución |
|---|---|---|
| No pide permiso | Página en HTTP | Usar HTTPS |
| «Permiso denegado» | Se rechazó antes | Candado de la barra → Permisos → Cámara → Permitir; recargar |
| Pantalla negra | Otra app usa la cámara | Cerrar la otra app |
| Advertencia de certificado | Autofirmado | Instalar CA de mkcert |
| No carga la página | Firewall / otra red | Abrir 8443, misma Wi‑Fi |
| iPhone | Debe usarse Safari (o navegador con WebKit) con HTTPS de confianza | Instalar el perfil de la CA en Ajustes → General → Información → Certificados |
