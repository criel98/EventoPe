---
title: "Pruebas del módulo de incidencias y notificaciones"
---

# Resultados reales (27/11/2026)

| Prueba | Resultado | Evidencia |
|---|---|---|
| Suite JUnit completa | 115 encontradas, 113 correctas, 0 fallos, 2 no ejecutadas (`CompraMySqlTest`, opcional) | `evidencias/resultado_junit.txt` |
| Pruebas nuevas (`GlennIncidenciasNotificacionesTest`) | 36 correctas | idem |
| Guion HTTP contra Tomcat + BD reales | 75 de 75 | `evidencias/resultado_pruebas_http.txt` |

Entorno: Java 17, Tomcat 10.1, **MariaDB 10.11** (no MySQL 8). Repetir con `mvn clean verify` en MySQL 8 antes de entregar.

# Matriz 5/5/5/5 de QR

| Grupo | Cantidad | Resultado esperado | Resultado |
|---|---|---|---|
| Válidos | 5 | Permiten ingreso | 5 permitidos |
| Vencidos | 5 | Deniegan, incidencia BAJA | 5 denegados |
| Ya usados | 5 | Deniegan, incidencia ALTA | 5 denegados |
| Alterados | 5 | Deniegan, incidencia ALTA | 5 denegados |

Solo los válidos permiten ingreso.

# Otros casos cubiertos

- **Notificaciones ajenas:** un cliente no ve ni marca avisos de otro (404).
- **Permisos erróneos:** sin sesión (redirige/401), cliente y guardia (403), SOPORTE no puede autorizar ingreso manual, cuenta desactivada con sesión vieja.
- **CSRF** ausente o inválido: rechazado.
- **Ingreso manual:** entrada anulada no se recupera; entrada usada no se duplica; motivos de fraude rechazados; 10 intentos concurrentes con dos administradores = 1 solo ingreso; rollback ante fallo inducido.
- **Falta de conexión:** reintento con el mismo `solicitud_id` devuelve el resultado previo marcado como recuperado.
- **Fallo del aviso:** un error al notificar no cancela la compra ni el ingreso (savepoint).
- **Zona horaria:** regresión para que la fecha de la bandeja coincida con Lima.

Nota: el texto de `resultado_junit.txt` puede mostrar un WARNING esperado del test que induce un fallo a propósito.

# Cómo repetirlas

```
set EVENTOPE_TEST_DB_URL=jdbc:mysql://localhost:3306/eventope_base_test
mvn clean verify
```
(El esquema `eventope_base_test` se crea con `sql/01_esquema.sql`; usa tu propia BD, no la de otros.)
