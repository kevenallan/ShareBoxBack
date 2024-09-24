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

    public LoginDTO cadastrar(UsuarioModel usuario) throws InterruptedException, ExecutionException {
        this.usuarioRepository.cadastrar(usuario);
        LoginDTO login = this.login(usuario.getUsuario(), usuario.getSenha());
        if (login != null) {
     	   return login;
        }
        return null;
        
     }

    public LoginDTO login(String usuario, String senha) throws InterruptedException, ExecutionException {

		UsuarioModel usuarioModel =  this.usuarioRepository.login(usuario, senha);
		if (usuarioModel != null) {
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setUsuarioModel(usuarioModel);
			loginDTO.setToken(this.authService.gerarToken(usuarioModel.getUsuario(), usuarioModel.getSenha()));				
			return loginDTO;
		}
		return null;
    }
	
}
