package br.com.sharebox.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sharebox.dto.LoginDTO;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private AuthService authService;

    public UsuarioModel cadastrar(UsuarioModel usuario) throws Exception {
        return this.usuarioRepository.cadastrar(usuario);
     }

    public LoginDTO login(UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {

		UsuarioModel usuarioLogado =  this.usuarioRepository.login(usuarioModel.getUsuario(), usuarioModel.getSenha());

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setUsuarioModel(usuarioLogado);
		loginDTO.setToken(this.authService.gerarToken(usuarioLogado.getId()));				
		return loginDTO;
    }
    
    public void esqueceuSuaSenha(String email) throws Exception {
    	this.usuarioRepository.buscarUsuarioPorEmail(email);
    }
	
}
