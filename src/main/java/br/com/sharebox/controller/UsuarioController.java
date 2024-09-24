package br.com.sharebox.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.dto.LoginDTO;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/cadastrar")
    public void cadastrar(@RequestBody UsuarioModel usuarioModel) {
		try {
			this.usuarioService.cadastrar(usuarioModel);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@PostMapping("/login")
    public LoginDTO login(@RequestBody UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {
		return this.usuarioService.login(usuarioModel.getUsuario(), usuarioModel.getSenha());
    }
}
