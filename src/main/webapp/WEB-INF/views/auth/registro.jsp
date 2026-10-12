<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - EventoPe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body>
    <div class="container">
        <h2>Crear Cuenta en EventoPe</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger" style="color: red; margin-bottom: 15px;">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/registro" method="POST">
            <!-- Protección CSRF corregida para CsrfFilter -->
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">

            <div class="form-group">
                <label for="nombres">Nombres:</label>
                <input type="text" id="nombres" name="nombres" required class="form-control">
            </div>

            <div class="form-group">
                <label for="apellidos">Apellidos:</label>
                <input type="text" id="apellidos" name="apellidos" required class="form-control">
            </div>

            <div class="form-group">
                <label for="dni">DNI:</label>
                <input type="text" id="dni" name="dni" maxlength="8" required class="form-control">
            </div>

            <div class="form-group">
                <label for="email">Correo Electrónico:</label>
                <input type="email" id="email" name="email" required class="form-control">
            </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required class="form-control">
            </div>

            <div class="form-group">
                <label for="telefono">Teléfono:</label>
                <input type="text" id="telefono" name="telefono" class="form-control">
            </div>

            <div class="form-group">
                <label for="pais">País:</label>
                <input type="text" id="pais" name="pais" value="Perú" class="form-control">
            </div>

            <div class="form-group">
                <label for="fechaNacimiento">Fecha de Nacimiento:</label>
                <input type="date" id="fechaNacimiento" name="fechaNacimiento" class="form-control">
            </div>

            <div class="form-group">
                <label for="generoMusical">Género Musical Favorito:</label>
                <input type="text" id="generoMusical" name="generoMusical" class="form-control">
            </div>

            <div class="form-group">
                <label for="artistaFavorito">Artista/Banda Favorita:</label>
                <input type="text" id="artistaFavorito" name="artistaFavorito" class="form-control">
            </div>

            <button type="submit" class="btn btn-primary" style="margin-top: 15px;">Registrarse</button>
        </form> 

        <p style="margin-top: 20px;">
            ¿Ya tienes una cuenta? <a href="${pageContext.request.contextPath}/login">Inicia sesión aquí</a>
        </p>
    </div>
</body>
</html>
