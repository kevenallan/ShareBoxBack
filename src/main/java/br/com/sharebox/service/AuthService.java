package br.com.sharebox.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import br.com.sharebox.model.UsuarioModel;

@Service
public class AuthService {

	@Autowired
	private UsuarioService usuarioService;
	
	@Value("${jwt.secret}")
    private String secretKey; // Configure a chave secreta no application.properties

	 public String gerarToken(String usuario, String senha) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String token = JWT.create()
		    .withSubject(usuario)                   // Adiciona o "subject" (usuário)
			.withClaim("senha", senha)              // Adiciona a senha como uma "claim"
			.withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))               // Define a data de emissão
			.sign(algorithm);                       // Assina o token com o algoritmo HMAC256
		
		return token;
	 }

	 public Boolean isTokenValid(String token) throws InterruptedException, ExecutionException {
        // Cria um verificador para validar o token usando a chave secreta
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();

        // Decodifica o token
        DecodedJWT jwt = verifier.verify(token);

        // Pega os valores do token
        String usuario = jwt.getSubject(); // "sub" no JWT, que representa o usuário
        String senha = jwt.getClaim("senha").asString(); // O claim "senha"
        
        UsuarioModel usuarioLogado = this.usuarioService.login(usuario, senha);
        if (usuarioLogado != null) {
        	return true;
        }
        return false;
    }
}
