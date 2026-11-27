# Prueba de cámara en celular real por HTTPS

- Probado por: Glenn
- Fecha: 27/11/2026
- Celular: iPhone 12 Pro
- iOS: (V-26)
- Navegadores: Brave y Safari
- URL HTTPS usada: https://192.168.1.11:8443/personal/login
- Certificado: autofirmado (la advertencia se aceptó manualmente)
- Servidor: Tomcat 10.1.62, MySQL 8.0.46

| # | Prueba | Resultado esperado | Resultado obtenido | Captura |
|---|---|---|---|---|
| 1 | Cámara por HTTPS | Abre y pide permiso | (✔ ) | EVIDENCIA/_.jpeg |
| 2 | QR válido con DNI correcto | Acceso permitido | (✔ ) | EVIDENCIA/_.jpeg |
| 3 | Mismo QR otra vez | Denegado, ya utilizado | (✔ ) | EVIDENCIA/_.jpeg |
| 4 | Sin conexión y reintento | Resultado anterior recuperado | (✔ ) | EVIDENCIA/_.jpeg |
| 5 | Permiso de cámara denegado | Mensaje de falta de permiso | (✔ ) | EVIDENCIA/_.jpeg |


