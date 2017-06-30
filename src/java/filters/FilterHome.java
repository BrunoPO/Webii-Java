/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import User.Client;
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
public class FilterHome implements Filter{
    public void init (FilterConfig config) throws ServletException{
    // TODO Auto-generatedmethodstub
    }
    public void destroy() {
    //  TODO  Auto-generatedmethodstub
    }
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, java.io.IOException{
        HttpSession session= ((HttpServletRequest) request).getSession(false);
        if(session!= null && session.getAttribute("cli") != null && session.getAttribute("cli") instanceof Client){
            chain.doFilter(request, response);
        } else{
            ((HttpServletResponse) response).sendRedirect("Login.xhtml");
        }
    }
}
