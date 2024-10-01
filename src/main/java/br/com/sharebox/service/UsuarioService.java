package br.com.sharebox.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.sharebox.dto.LoginDTO;
import br.com.sharebox.exception.CustomException;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private EmailService emailService;

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
    	UsuarioModel usuarioEncontrado = this.usuarioRepository.buscarUsuarioPorEmail(email);
    	if (usuarioEncontrado != null) {
    		this.emailService.enviarEmail(usuarioEncontrado);
    	}
    }
    
    public void alterarSenha(String novaSenha, String token) throws Exception {
    	try {
    	    String idUsuario = this.authService.extractUserId(token);
    	    this.usuarioRepository.atualizarSenha(idUsuario, novaSenha);
    	} catch (JWTVerificationException e) {
    	    // Tratar erro de verificação, por exemplo, token inválido ou expirado
    	    throw new CustomException("A validade desse link expirou. Por favor solicite uma nova redefinição de senha.");
    	}
    }
    
    public void atualizarUsuario(UsuarioModel usuarioModel) throws Exception {
    	try {
    	    this.usuarioRepository.atualizarUsuario(this.authService.uuidUsuarioLogado, usuarioModel);
    	} catch (JWTVerificationException e) {
    	    // Tratar erro de verificação, por exemplo, token inválido ou expirado
    	    throw new CustomException("A validade desse link expirou. Por favor solicite uma nova redefinição de senha.");
    	}
    }
    
    public UsuarioModel getDadosUsuario() {
    	try {
			return this.usuarioRepository.getDadosUsuario(this.authService.uuidUsuarioLogado);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Erro ao pegar os dados do usuário logado.");
		}
    	
    }
	
}
