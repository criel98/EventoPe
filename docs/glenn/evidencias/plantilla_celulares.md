# Evidencia de pruebas en celular real (Glenn)

- **Probado por:** Glenn
- **Fecha:** 27/11/2026
- **Dispositivo:** iPhone 12 Pro
- **Sistema:** iOS 26
- **Navegadores:** Safari y Brave
- **URL probada:** https://192.168.1.11:8443/personal/login (HTTPS, certificado autofirmado)
- **Servidor:** Tomcat 10.1.62 · **Base de datos:** MySQL 8.0.46

| # | Prueba | Resultado obtenido | Captura |
|---|--------|--------------------|---------|
| 1 | Cámara por HTTPS | ✔ Correcto: la cámara abre y enfoca el QR | [3.jpeg](3.jpeg) |
| 2 | QR válido con DNI correcto | ✔ Correcto: "ACCESO PERMITIDO" | [3.jpeg](3.jpeg) |
| 3 | Mismo QR otra vez | ✔ Correcto: "ACCESO DENEGADO, este boleto ya fue utilizado" | [5.jpeg](5.jpeg) |
| 4 | Sin conexión y reintento | Parcial: se muestra "Sin respuesta del servidor" y el botón "Reintentar la misma lectura"; el resultado del reintento no quedó capturado | [6.jpeg](6.jpeg) |
| 5 | Permiso de cámara denegado | No realizada | — |

## Pruebas adicionales

| Prueba | Resultado obtenido | Captura |
|--------|--------------------|---------|
| QR de un boleto de otro evento | ✔ Correcto: "ACCESO DENEGADO, el boleto pertenece a otro evento" | [2.jpeg](2.jpeg) |
| QR del cliente en pantalla (se renueva solo) | ✔ Correcto | [1.jpeg](1.jpeg) |
| Tras el ingreso, el boleto del cliente pasa a UTILIZADA y llegan notificaciones | ✔ Correcto: estado UTILIZADA y 9 notificaciones sin leer | [4.jpeg](4.jpeg) |
