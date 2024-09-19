package br.com.sharebox.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

    public void cadastrar(UsuarioModel usuario) throws InterruptedException, ExecutionException {
       this.usuarioRepository.cadastrar(usuario);
    }

    public UsuarioModel login(String usuario, String senha) throws InterruptedException, ExecutionException {
    	return this.usuarioRepository.login(usuario, senha);
    }
	
}
