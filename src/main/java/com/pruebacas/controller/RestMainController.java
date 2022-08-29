package com.pruebacas.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class RestMainController {


    
  @RequestMapping("inicio")
	public String inicio() {
		return "Bienvenido!!";
	}  
        
  @RequestMapping("admin")
	public String admin() {
		return "Bienvenido ADMIN!!";
	}      
        
        
            
   @RequestMapping(value="logout", method = RequestMethod.GET)
    public void logoutPage (HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){    
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    response.sendRedirect("http://172.17.8.9:8080/cas-server-webapp-3.1.1/login");
}     
              
    
    
    

}
