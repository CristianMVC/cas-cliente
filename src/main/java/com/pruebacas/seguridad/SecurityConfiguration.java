package com.pruebacas.seguridad;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;



import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/* private static final String CAS_URL_LOGIN = "cas.service.login";
	private static final String CAS_URL_LOGOUT = "cas.service.logout";
	private static final String CAS_URL_PREFIX = "cas.url.prefix";
	private static final String CAS_SERVICE_URL = "app.service.security";
	private static final String APP_SERVICE_HOME = "app.service.home";
	private static final String APP_ADMIN_USER_NAME = "app.admin.userName"; */

        /* Setea el admin */
	@Bean
	public Set<String> adminList() {
		Set<String> admins = new HashSet<String>();
		String adminUserName = "admin";

		admins.add("admin");
		if (adminUserName != null && !adminUserName.isEmpty()) {
			admins.add(adminUserName);
		}
		return admins;
	}
    
    
                @Bean
	        public SingleSignOutFilter singleSignOutFilter() {
		       SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		//singleSignOutFilter.setCasServerUrlPrefix(env.getRequiredProperty(CAS_URL_PREFIX));
		      return singleSignOutFilter;
	        }
                
        
              
                
                  /* Carga  la función casAuthenticationFilter(), los filtros y el manejo de sessión. 
                     Configura la seguridad de spring */
                @Override
                protected void configure(HttpSecurity http) throws Exception {
                          http.addFilter(casAuthenticationFilter()).exceptionHandling()
                          .authenticationEntryPoint(casAuthenticationEntryPoint()).and().addFilter(casAuthenticationFilter())
                                  .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
                          
                          
		   http.authorizeRequests() 
		     .anyRequest().authenticated();
                          
                      
                }
    
    
                /* Registra a la función encargada de llamar al servidor de autenticación */
                @Override
                protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                          auth.authenticationProvider(casAuthenticationProvider());
                }
                
                
                /* URL por defecto donde se verifican lso tickets j_spring_cas_security_check */
                @Bean
                public ServiceProperties serviceProperties() {
                       ServiceProperties serviceProperties = new ServiceProperties();
                       serviceProperties.setService("http://localhost:8080/j_spring_cas_security_check");
                       serviceProperties.setSendRenew(false);
                       return serviceProperties;
                }

                
                
              /* Registra datos de usuario, el j_spring_cas_security_check que verifica el ticket enviado desde el servidor cas
                y el ticketValidator que se encarga de comunicarse con el servidor cas una vez verificado el ticket
                */
                @Bean
                public CasAuthenticationProvider casAuthenticationProvider() {
                       CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
                       casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
                       casAuthenticationProvider.setServiceProperties(serviceProperties());
                        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
                       casAuthenticationProvider.setKey("cas");
                       return casAuthenticationProvider;
                }

                
                /* Carga datos de usuario */
               @Bean
	        public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
		   return new CustomUserDetailsService(adminList());
	       }
                
          
                

               @Bean
               public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
                      return new Cas20ServiceTicketValidator("http://172.17.8.9:8080/cas-server-webapp-3.1.1/");
                }

                @Bean
                public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
                       CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
                       casAuthenticationFilter.setAuthenticationManager(authenticationManager());
                       casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
                       return casAuthenticationFilter;
                }
                
                
                @Bean
	        public SessionAuthenticationStrategy sessionStrategy() {
		       SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
		        return sessionStrategy;
	        }
                 
                /* Configura punto de entrada al servidor cas */
                @Bean
                public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
                       CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
                       casAuthenticationEntryPoint.setLoginUrl("http://172.17.8.9:8080/cas-server-webapp-3.1.1/login");
                       casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
                return casAuthenticationEntryPoint;
                }
    
}
