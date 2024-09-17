package br.com.sharebox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public UsuarioModel login(String login, String senha) {
		return this.usuarioRepository.finbByLoginAndSenha(login, senha);
	}
}
