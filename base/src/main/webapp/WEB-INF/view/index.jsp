<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Spring Boot File Upload</title>
</head>
<body>
<h1>Spring Boot - File Upload With JSP</h1>
<form method="POST" action="/upload"
	enctype="multipart/form-data">
	<input type="file" name="file" /><br /> <br /> <input type="submit"
		value="Submit" />
</form>
</body>
</html>