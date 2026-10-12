<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acceso Personal - EventoPe</title>
</head>
<body>
    <div style="max-width: 400px; margin: 50px auto; font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <h2 style="text-align: center; color: #333;">Acceso Personal / Guardia</h2>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div style="color: #721c24; background: #f8d7da; padding: 10px; margin-bottom: 15px; border-radius: 4px; border: 1px solid #f5c6cb;">
                <%= error %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/personal/login" method="POST">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div style="margin-bottom: 15px;">
                <label for="username" style="display: block; margin-bottom: 5px; font-weight: bold;">Usuario:</label>
                <input type="text" id="username" name="username" 
                       value="<%= request.getAttribute("usernameIngresado") != null ? request.getAttribute("usernameIngresado") : "" %>" 
                       required style="width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px;">
            </div>

            <div style="margin-bottom: 15px;">
                <label for="password" style="display: block; margin-bottom: 5px; font-weight: bold;">Contraseña:</label>
                <input type="password" id="password" name="password" required style="width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px;">
            </div>

            <button type="submit" style="width: 100%; padding: 10px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;">Ingresar como Personal</button>
        </form>
    </div>
</body>
</html>
 
