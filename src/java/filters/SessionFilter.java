/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import Arquivo.Arquivo;
import User.Client;
import beans.Tokens;
import java.util.ArrayList;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *
 * @author prog
 */
public class SessionFilter implements Filter{
    public void init (FilterConfig config) throws ServletException{
    // TODO Auto-generatedmethodstub
    }
    public void destroy() {
    //  TODO  Auto-generatedmethodstub
    }
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, java.io.IOException{
        System.out.println("Passou pelo filtro");
        HttpSession session= ((HttpServletRequest) request).getSession(false);
        String login = request.getParameter("user");
        String senha = request.getParameter("pwd");
        if(session!= null && session.getAttribute("cli") != null && session.getAttribute("cli") instanceof Client){
            ((HttpServletResponse) response).sendRedirect("Home.xhtml");
        } else{
            Client cli = new Client();
            System.out.println("Login: "+login);
            System.out.println("Senha: "+senha);// &&login!=null && senha != null
            if(login!=null && senha != null){
                if(cli.Conferir(login, senha)){
                    session.setAttribute("cli",cli);
                    chain.doFilter(request, response);
                }else{
                    session.setAttribute("loginBean.mensagem","usuario ou senha invalido");
                    ((HttpServletResponse) response).sendRedirect("Login.xhtml");
                }
            }
            chain.doFilter(request, response);
        }
        
    }
}
