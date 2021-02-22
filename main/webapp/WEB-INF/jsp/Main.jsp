<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>존버닷컴</title>
</head>
<body>
    <h1>존버닷컴</h1>
    <form action = "localhost:8080/hello" name = "test" method = "POST">
        <input type = "text" name = "text" placeholder="상품의 URL입력"/>
        <input type = "button" value = "등록"/>
    </form>
</body>
</html>