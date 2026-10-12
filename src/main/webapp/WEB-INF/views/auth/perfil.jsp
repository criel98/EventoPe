<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - EventoPe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body>
    <div class="container" style="max-width: 600px; margin: 40px auto; padding: 20px;">
        <h2>Mi Perfil en EventoPe</h2>

        <% 
            java.util.Map<String, Object> perfilCompleto = (java.util.Map<String, Object>) request.getAttribute("perfilCompleto");
            pe.edu.utp.eventope.model.UsuarioCliente cliente = perfilCompleto != null ? (pe.edu.utp.eventope.model.UsuarioCliente) perfilCompleto.get("cliente") : null;
            pe.edu.utp.eventope.model.PerfilUsuario perfil = perfilCompleto != null ? (pe.edu.utp.eventope.model.PerfilUsuario) perfilCompleto.get("perfil") : null;
        %>

        <% if (cliente != null) { %>
            <div class="card" style="background: #f9f9f9; padding: 20px; border-radius: 8px; border: 1px solid #ddd; margin-top: 20px;">
                <h3>Datos Personales</h3>
                <p><strong>Nombres y Apellidos:</strong> <%= cliente.getNombres() %> <%= cliente.getApellidos() %></p>
                <p><strong>DNI:</strong> <%= cliente.getDni() %></p>
                <p><strong>Correo Electrónico:</strong> <%= cliente.getEmail() %></p>
                <p><strong>Teléfono:</strong> <%= cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado" %></p>

                <% if (perfil != null) { %>
                    <hr style="margin: 15px 0; border: 0; border-top: 1px solid #ccc;">
                    <h3>Información Adicional</h3>
                    <p><strong>País:</strong> <%= perfil.getPais() %></p>
                    <p><strong>Fecha de Nacimiento:</strong> <%= perfil.getFechaNacimiento() %></p>
                <% } %>
            </div>
        <% } else { %>
            <p style="color: red; margin-top: 20px;">No se pudo cargar la información del perfil.</p>
        <% } %>

        <div style="margin-top: 25px;">
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger" style="color: red; text-decoration: none;">Cerrar Sesión</a>
        </div>
    </div>
</body>
</html> 
