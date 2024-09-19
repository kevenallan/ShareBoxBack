package br.com.sharebox.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/")
    public void teste() {
		try {
			UsuarioModel usuario = new UsuarioModel();
			usuario.setNome("Dev");
			usuario.setUsuario("dev");
			usuario.setSenha("dev");
			this.usuarioService.cadastrar(usuario);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@PostMapping("/login")
    public UsuarioModel login(@RequestBody UsuarioModel usuarioModel) {
		try {
			return this.usuarioService.login(usuarioModel.getUsuario(), usuarioModel.getSenha());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
