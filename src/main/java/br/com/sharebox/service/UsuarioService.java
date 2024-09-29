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

    public LoginDTO cadastrar(UsuarioModel usuario) throws Exception {
        UsuarioModel usuarioCadastrado = this.usuarioRepository.cadastrar(usuario);
//        if (usuarioCadastrado == null) {
//        	return null;
//        }

        LoginDTO login = this.login(usuarioCadastrado);
        if (login != null) {
     	   return login;
        }
        return null;
        
     }

    public LoginDTO login(UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {

		UsuarioModel usuarioLogado =  this.usuarioRepository.login(usuarioModel.getUsuario(), usuarioModel.getSenha());
		if (usuarioLogado != null) {
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setUsuarioModel(usuarioLogado);
			loginDTO.setToken(this.authService.gerarToken(usuarioLogado.getId()));				
			return loginDTO;
		}
		return null;
    }
    
    public void esqueceuSuaSenha(String email) throws Exception {
    	this.usuarioRepository.buscarUsuarioPorEmail(email);
    }
	
}
