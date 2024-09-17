package br.com.sharebox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.model.TokenModel;
import br.com.sharebox.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
    private AuthService authService;
	
	@PostMapping("/validar-token")
	private Boolean validarToken(@RequestBody TokenModel tokenModel) {
		return this.authService.isTokenValid(tokenModel.getValue());
	}
}
