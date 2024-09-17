package br.com.sharebox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.model.TokenModel;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.service.AuthService;
import br.com.sharebox.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
    private AuthService authService;

	@PostMapping("/login")
	private UsuarioModel login(@RequestBody UsuarioModel usuarioModel) {
		UsuarioModel usuarioLogado = this.usuarioService.login(usuarioModel.getLogin(), usuarioModel.getSenha());
		if (usuarioLogado != null) {
			String token = authService.gerarToken(usuarioLogado.getLogin(), usuarioLogado.getSenha());
			TokenModel tokenModel = new TokenModel();
			tokenModel.setValue(token);
			tokenModel.setIsValid(true);
			usuarioLogado.setToken(tokenModel);
		}
//		this.authService.pegarUsuarioESenhaDoToken(token);
		return usuarioLogado;
	}
	
}
