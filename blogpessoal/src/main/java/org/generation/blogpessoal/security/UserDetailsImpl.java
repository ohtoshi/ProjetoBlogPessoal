package org.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.generation.blogpessoal.model.Usuario;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;
	private List<GrantedAuthority> authorities; // permissão de administrador
	
	
	public UserDetailsImpl(Usuario usuario) { // puxando informações de usuário e senha
		this.userName = usuario.getUsuario();
		this.password = usuario.getSenha();
	}

	public UserDetailsImpl() {  } //para teste com usuário e senha, método construtor vazio
	
	// métodos padrão basic security
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override // informação para considerar apenas a senha
	public String getPassword() {
		return password;
	}
	
	@Override // informação para considerar apenas o usuário
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
