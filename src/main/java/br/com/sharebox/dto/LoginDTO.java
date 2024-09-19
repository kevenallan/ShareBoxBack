package br.com.sharebox.dto;

import br.com.sharebox.model.UsuarioModel;
import lombok.Data;

@Data
public class LoginDTO {

	private UsuarioModel usuarioModel;
	private String token;
	
}
