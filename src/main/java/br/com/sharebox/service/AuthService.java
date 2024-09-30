package br.com.sharebox.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Service
public class AuthService {
	
	@Value("${jwt.secret}")
    private String secretKey; // Configure a chave secreta no application.properties
	
	private int jwtExpirationMs = 3600000; // - Expira em 1 hora
	private int jwtExpirationRedefinirSenha = 300000; //5 minutos
	
	public String uuidUsuarioLogado = "";

	 public String gerarToken(String uuid) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String token = JWT.create()
		    .withSubject(uuid)                   // Adiciona o "subject" (usuário)
//			.withClaim("senha", senha)              // Adiciona a senha como uma "claim"
			.withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))               // Define a data de emissão
			.withExpiresAt(new Date((new java.util.Date()).getTime() + jwtExpirationMs))
			.sign(algorithm);                       // Assina o token com o algoritmo HMAC256
		
		return token;
	 }
	 
	 public String gerarTokenRedefinicaoSenha(String uuid) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String token = JWT.create()
		    .withSubject(uuid)
			.withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
			.withExpiresAt(new Date((new java.util.Date()).getTime() + jwtExpirationRedefinirSenha))
			.sign(algorithm);
		
		return token;
	 }

	// Validar token JWT
	public DecodedJWT validateToken(String token) throws JWTVerificationException {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
	    JWTVerifier verifier = JWT.require(algorithm)
	                              .build();
	    return verifier.verify(token);
	}

	// Extrair o ID do usuário do token
	public String extractUserId(String token) {
	    return validateToken(token).getSubject();
	}
}
