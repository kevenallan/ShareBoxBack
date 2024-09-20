package br.com.sharebox.model;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ArquivoModel {
	
	private String nome;
	private MultipartFile file;
	private String base64;
	private String extensao;
	private String mimeType;
	private Long tamanho;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataUltimaModificacao;

}
