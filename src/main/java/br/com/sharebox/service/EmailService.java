package br.com.sharebox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
    private JavaMailSender mailSender;
	
	public void enviarEmail(String para) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(para);
		message.setSubject("Redefinição de senha");
		message.setText("Teste email sharebox");
		mailSender.send(message);
    }
	
}
