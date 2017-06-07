<%-- 
    Document   : index
    Created on : May 25, 2017, 2:39:22 PM
    Author     : prog
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">
    <head>
        <title>Facelet Title</title>
    </head>
    <body>
        <%
        System.out.print("index.jsp");
        %>
        <h1>Login</h1>
        <h:form>
            <div>Login:</div>
            <div><h:inputText value="#{loginBean.login}" /></div>
            <div>Senha:</div>
            <div><h:inputSecret value="#{loginBean.senha}" /></div>
            <div><h:commandButton action="#{loginBean.processar}" value="Entrar" /></div>
        </h:form>
        <div><h:outputText value="#{loginBean.mensagem}" /></div>
    </body>
</html>
