<%-- 
    Document   : main
    Created on : May 25, 2017, 2:32:50 PM
    Author     : prog
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Olá, <h:outputText value="#{loginBean.login}" /></h1>
        <h:form>
        <div><h:commandButton value="Sair" action="logout"/></div>
        </h:form>
        
        <h:dataTable value="#{loginBean.arquis}" var="item">
            
            <h:column>
                
                <f:facet name="header">Nome do Arquivo</f:facet>
                #{item.getNome()}
                <h:commandLink action="#{loginBean.atualizar(item)}" value="#{item.getNome()}" />
                
                <!--h:form>
                <h:commandButton value="#{item.getNome()}" action="#{loginBean.atualizar(item)}" />
                </h:form-->
               
            </h:column>
            <h:column>
                <f:facet name="header">Tamanho</f:facet>
                #{item.getIsPasta()}
            </h:column>
        </h:dataTable>
    </body>
</html>
