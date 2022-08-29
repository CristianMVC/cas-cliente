/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pruebacas.seguridad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Cristian
 */
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	private Set<String> admins;

	public CustomUserDetailsService() {
		super();
	}

	/**
	 * @param admins
	 */
	public CustomUserDetailsService(Set<String> admins) {
		super();
		this.admins = admins;
	}

        /* Se verifica los datos de usuario enviados por el servidor cas y se crean los permisos. 
           Verifica si el usuario es un administrador o un usurio común */
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
           
          
		String login = token.getPrincipal().toString();
		String lowercaseLogin = login.toLowerCase();
             
		log.debug("Authenticating '{}'", login);
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
                
                System.out.println(lowercaseLogin);
                System.out.println(admins);
                  
		if (admins != null && admins.contains(lowercaseLogin)) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
		} else {
			grantedAuthorities.add(new GrantedAuthority() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getAuthority() {
					return AuthoritiesConstants.USER;
				}
			});
		}

		return new AppUserDetails(lowercaseLogin, grantedAuthorities);
	}
}

