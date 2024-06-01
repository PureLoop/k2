<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error Page</title>
</head>
<body>
    <h1>An error occurred</h1>
    <p>${errorMessage}</p>
    <a href="<%= request.getContextPath() %>/index.jsp">Go to Home</a>
</body>
</html>