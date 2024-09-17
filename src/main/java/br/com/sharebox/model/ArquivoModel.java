package br.com.sharebox.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Arquivo")
@Data
public class ArquivoModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	private String nome;
	private byte[] arquivo;
	private String extensao;
	@Column(name = "prefixo_base64")
	private String prefixoBase64;
	private Long tamanho;
	private String descricao;
	private Long idUsuario;
	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;
	@Column(name = "data_ultima_modificacao")
	private LocalDateTime dataUltimaModificacao;

}
