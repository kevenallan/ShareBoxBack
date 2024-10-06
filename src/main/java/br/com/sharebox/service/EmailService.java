package br.com.sharebox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.sharebox.model.UsuarioModel;

@Service
public class EmailService {

	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
	private AuthService authService;
	
	public void enviarEmail(UsuarioModel usuario) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(usuario.getEmail());
		message.setSubject("Redefinição de Senha - ShareBox");
		String linkRedefinicaoSenha = "https://sharebox-1155c.web.app/alterar-sua-senha?token=" + this.authService.gerarTokenRedefinicaoSenha(usuario.getId());
		StringBuilder conteudoBuilder = new StringBuilder();
		conteudoBuilder.append("Olá ").append(usuario.getNome()).append(",\n\n")
		               .append("Recebemos um pedido para redefinir a sua senha da sua conta no ShareBox. ")
		               .append("Se você não solicitou essa redefinição, pode ignorar este email.\n\n")
		               .append("Para redefinir sua senha, clique no link abaixo:\n")
		               .append(linkRedefinicaoSenha).append("\n\n")
		               .append("Este link será válido por 5 minutos. Após este período, você precisará solicitar uma nova redefinição.\n\n")
		               .append("Se você tiver alguma dúvida ou precisar de ajuda, sinta-se à vontade para entrar em contato com nossa equipe de suporte.\n\n")
		               .append("Atenciosamente,\n")
		               .append("Equipe ShareBox");
		message.setText(conteudoBuilder.toString());
		mailSender.send(message);
    }
	
}
