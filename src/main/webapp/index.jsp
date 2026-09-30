<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Join Email List</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

    <h1>Join our email list</h1>
    <p>Enter your details below to join us!</p>

    <form action="emailList" method="post">
        <!-- Thông báo lỗi nếu trùng Email -->
        <div class="message">${message}</div>

        <div class="form-row">
            <label>Email:</label>
            <input type="email" name="email" value="${user.email}" required placeholder="vd: han@gmail.com">
        </div>

        <div class="form-row">
            <label>First Name:</label>
            <input type="text" name="firstName" value="${user.firstName}" required placeholder="First name">
        </div>

        <div class="form-row">
            <label>Last Name:</label>
            <input type="text" name="lastName" value="${user.lastName}" required placeholder="Last name">
        </div>

        <div class="btn-container">
            <input type="submit" value="Join Now">
        </div>
    </form>

</body>
</html>