package br.com.sharebox.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.service.ArquivoService;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {

	@Autowired
	private ArquivoService arquivoService;
	
	@GetMapping("/listar")
	private List<ArquivoModel> listar(){
		return this.arquivoService.listar();
	}
	
	@PostMapping("/upload")
	private void upload(@RequestBody ArquivoModel arquivoModel) {
		System.out.println("upload back");
		this.arquivoService.upload(arquivoModel);
	}
	
	@GetMapping("/download/{fileId}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
		ArquivoModel arquivo = this.arquivoService.getArquivoById(fileId);
	    byte[] fileData = arquivo.getArquivo();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDisposition(ContentDisposition.builder("attachment").filename("arquivo.jpg").build());
	    return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
	}
}
