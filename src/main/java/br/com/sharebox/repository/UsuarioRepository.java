package br.com.sharebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sharebox.model.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

	@Query(value = "SELECT * FROM USUARIO WHERE LOGIN = :login AND SENHA = :senha", nativeQuery = true)
	public UsuarioModel finbByLoginAndSenha(String login, String senha);
}
