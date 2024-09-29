package br.com.sharebox.model;

import com.google.cloud.firestore.annotation.Exclude;

import lombok.Data;

@Data
public class UsuarioModel {

	@Exclude
	private String id;
	private String nome;
	private String email;
	private String usuario;
	private String senha;
	
}
