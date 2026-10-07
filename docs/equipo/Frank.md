# Frank — cuenta del cliente y acceso mínimo del guardia

Resultado esperado: clientes pueden registrarse, entrar, salir y editar sus propios datos; el guardia puede autenticarse para entrar a la única pantalla de Glenn. No construir gestión del personal ni perfil de guardia.

## Tareas en orden

1. En `src/main/java/pe/edu/utp/eventope/`, crear `dao/UsuarioClienteDAO.java` y `dao/PersonalDAO.java`. Consultas parametrizadas por email/username. Cliente y personal son tablas diferentes: no confundir ids iguales. Crear `service/ServicioAutenticacion.java`; usar `PasswordUtil.verificar(char[], String)` y comprobar ACTIVO y rol. Para registro usar `PasswordUtil.crear(char[])`; no crear un segundo algoritmo.
2. Crear `controller/LoginServlet.java` GET/POST `/login`, `PersonalLoginServlet.java` GET/POST `/personal/login` y `LogoutServlet.java` POST `/logout`. En login correcto renovar id de sesión y guardar `SesionUsuario` bajo `sesionUsuario`; renovar CSRF y quitar identidad/dispositivo anteriores cuando corresponda. Cliente va a catálogo/mis entradas; guardia VALIDACION va a `/seguridad/panel`. Error de credenciales genérico, sin exponer si existe la cuenta. No aceptar rol o id de usuario del formulario como identidad.
3. Crear `RegistroServlet.java`, `service/ServicioCuenta.java`, `dao/PerfilUsuarioDAO.java`, `dao/PreferenciasDAO.java`. Registro de cliente/perfil/preferencias en una transacción, comprobar email y DNI únicos y devolver error entendible si la BD detecta duplicado. DNI: ocho dígitos; contraseña: 12–128 caracteres según utilidad. Crear `PerfilServlet.java` para consultar/actualizar solo el usuario de sesión. Preferencias son opcionales; no impedir login por estar vacías. No implementar cambio de contraseña para esta primera integración salvo que se acuerde expresamente.
4. Crear JSP bajo `src/main/webapp/WEB-INF/views/auth/` (`login.jsp`, `registro.jsp`, `personal-login.jsp`) y `cuenta/perfil.jsp`. GET crea CSRF y todos los POST lo envían. Escapar texto del usuario, mostrar errores junto al campo y conservar datos no sensibles; nunca rellenar de nuevo la contraseña.

## Dependencias

Entrega primero login de cliente y guardia: permite conectar el trabajo de los otros tres. La sesión común, filtros y utilidades ya están creados; no reemplazarlos. Coordinar con Glenn la asignación segura de dispositivo en sesión: la credencial demo se obtiene de configuración local del servidor, no se publica en JavaScript ni en GitHub. No basta con asignar dispositivo id 1 a cualquier usuario que entre.

Los filtros requieren HTTPS para que el navegador envíe su cookie Secure. Coordinar con el integrador el entorno Tomcat HTTPS desde el principio; consultar README. No afirmar que el login falla por la contraseña si la cookie no se está enviando.

## Pruebas y checklist

- [ ] Registro válido crea cliente, perfil y preferencias; un fallo revierte todo.
- [ ] Email/DNI repetidos e intentos simultáneos no crean duplicados.
- [ ] Contraseña incorrecta o cuenta inactiva no abre sesión.
- [ ] Cambia el id de sesión al entrar y desaparece al salir.
- [ ] Cliente no entra a seguridad y guardia no entra a cuenta/compras del cliente.
- [ ] Modificar id/rol oculto o URL no permite editar otra cuenta.
- [ ] POST sin CSRF se rechaza; formulario correcto funciona con acentos.
- [ ] Pantallas legibles a 360 px y en escritorio; mensajes sin datos internos.
- [ ] Crear pruebas de autenticación/registro en `src/test/java/pe/edu/utp/eventope/CuentaMySqlTest.java` con BD exclusiva.
- [ ] Entregar capturas login/registro/perfil y comprobación de permisos para 3.8.

Nota: el historial de compras lo entrega Glenn dentro de “Mis entradas”, consultando las compras propias; acuerda un enlace desde la cuenta, sin duplicar esa consulta.
