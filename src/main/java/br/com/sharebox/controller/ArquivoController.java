package br.com.sharebox.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.service.ArquivoService;
import br.com.sharebox.service.FirebaseService;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {

	@Autowired
	private ArquivoService arquivoService;
	
	@Autowired
	private FirebaseService firebaseService;

	@GetMapping("/listar")
	public List<ArquivoModel> listar(@RequestParam("usuario") String usuario) throws FileNotFoundException, IOException{
		this.firebaseService.getCapacidadeStorage();
		return this.arquivoService.listar(usuario);
	}
	
	@PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("nome") String nomeArquivo,
            @RequestParam("usuario") String usuario) throws InterruptedException, ExecutionException {
		this.arquivoService.upload(file, nomeArquivo, usuario);
    }

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("nomeArquivo") String nomeArquivo, @RequestParam("usuario") String usuario) throws FileNotFoundException, IOException {

		byte[] arquivo = this.arquivoService.getArquivo(nomeArquivo, usuario);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDisposition(ContentDisposition.builder("attachment").filename(nomeArquivo).build());
	    return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
	}
	
	@GetMapping("/buscar")
	public byte[] buscarArquivo(@RequestParam("nomeArquivo") String nomeArquivo, @RequestParam("usuario") String usuario) throws FileNotFoundException, IOException {
		byte[] arquivo = this.arquivoService.getArquivo(nomeArquivo, usuario);
		return arquivo;	
	}
	
	@DeleteMapping("/deletar")
	public void deletar(@RequestParam("nomeArquivo") String nomeArquivo, @RequestParam("usuario") String usuario) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		this.arquivoService.deletar(nomeArquivo, usuario);
	}

}
