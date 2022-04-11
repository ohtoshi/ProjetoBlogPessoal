package org.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.generation.blogpessoal.model.Usuario;
import org.generation.blogpessoal.model.UsuarioLogin;
import org.generation.blogpessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario){
		
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) { // se existe usuário, retorne o usuário
			return Optional.empty();
		}
		
		usuario.setSenha(criptografarSenha(usuario.getSenha())); // criptografa a senha
		
		return Optional.of(usuarioRepository.save(usuario)); // salvar a senha criptografada no DB
	}
		
	private boolean compararSenhas(String senhaDigitada, String senhaDoBanco) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.matches(senhaDigitada, senhaDoBanco);
	}
	
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //recebe a senha e criptografa
		
		return encoder.encode(senha); // retorna a senha criptografada
	}
	
	private String geradorBasicToken(String usuario, String senha) {
		
		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));  // tecnologia para gerar token
		
		return "Basic " + new String(tokenBase64); // necessário pois estamos utilizando o Basic Security
	}
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
        if (usuarioRepository.findById(usuario.getId()).isPresent()) { // busca usuário por ID
            Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario()); // busca o usuário completo pelo ID
            if (buscaUsuario.isPresent()) { // se ele está presente
                 if (buscaUsuario.get().getId() != usuario.getId()) // se o ID é o mesmo do objeto
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
            }
            usuario.setSenha(criptografarSenha(usuario.getSenha()));
            return Optional.of(usuarioRepository.save(usuario));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!", null);
    }
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){ // verificar se user existe
		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if(usuario.isPresent()) {
			if(compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) { // compara se a senha digitada é a mesma senha cadastrada
				usuarioLogin.get().setId(usuario.get().getId());  // atribuições
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(geradorBasicToken
						(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());

				return usuarioLogin;
			}
		}
		
		return Optional.empty();
	}
}
