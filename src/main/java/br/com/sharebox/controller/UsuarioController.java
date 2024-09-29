package br.com.sharebox.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.dto.LoginDTO;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/cadastrar")
    public LoginDTO cadastrar(@RequestBody UsuarioModel usuarioModel) throws Exception {
		try {
			return this.usuarioService.cadastrar(usuarioModel);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
	
	@PostMapping("/login")
    public LoginDTO login(@RequestBody UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {
		return this.usuarioService.login(usuarioModel);
    }
	
	@GetMapping("/esqueceu-sua-senha")
    public ResponseEntity<String> esqueceuSuaSenha(@RequestParam("email") String email) throws Exception {
		 String mensagem =  this.usuarioService.esqueceuSuaSenha(email);
		 return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }
}
