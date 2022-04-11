package org.generation.blogpessoal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired // escopo local
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure (AuthenticationManagerBuilder auth) throws Exception{ // armazenar o usuário em memória
	// auth = alias (apelido da classe)
		auth.userDetailsService(userDetailsService);
		
		auth.inMemoryAuthentication()
		.withUser("root")
		.password(passwordEncoder().encode("root"))
		.authorities("ROLE_USER"); // para atribuir o usuário padrão root root
	}
	
	@Bean // escopo global
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		
		http.authorizeHttpRequests() // para que a aplicação não fique bloqueada
		.antMatchers("/usuarios/logar").permitAll() // permitir para logar
		.antMatchers("/usuarios/cadastrar").permitAll() // permitir para cadastrar
		.antMatchers("/usuarios/atualizar").permitAll() // permitir para atualizar cadastro antes do login
		.antMatchers(HttpMethod.OPTIONS).permitAll()
		.anyRequest().authenticated() // qualquer requisição precisa de login (autenticação)
		.and().httpBasic()
		.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		// para que o token não seja infinito, para que peça login esporadicamente
		.and().cors() // auxiliar para Crossorigins
		.and().csrf().disable(); // para conseguir fazer put e delete, sem outro nível de autorização
 	}
}
