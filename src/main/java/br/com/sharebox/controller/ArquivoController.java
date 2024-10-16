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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.model.ResponseModel;
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
	public ResponseEntity<ResponseModel<?>> listar() throws FileNotFoundException, IOException {
		this.firebaseService.getCapacidadeStorage();
		List<ArquivoModel> arquivos = this.arquivoService.listar();
		ResponseModel<?> response = new ResponseModel<>(null, arquivos);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseModel<?>> uploadFile(@RequestParam("files") MultipartFile[] files)
			throws InterruptedException, ExecutionException {
		this.arquivoService.upload(files);
		return new ResponseEntity<>(new ResponseModel<>("Arquivo(s) adicionado(s)", null), HttpStatus.OK);
	}

	// PRECISA MUDAR PARA RESPONSEMODEL?
	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("nomeArquivo") String nomeArquivo)
			throws FileNotFoundException, IOException {

		byte[] arquivo = this.arquivoService.getArquivo(nomeArquivo);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(nomeArquivo).build());
		return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<ResponseModel<?>> updateFile(@RequestParam("file") MultipartFile file,
			@RequestParam("nome") String nomeArquivo, @RequestParam("nomeArquivoAntigo") String nomeArquivoAntigo)
			throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		this.arquivoService.update(file, nomeArquivo, nomeArquivoAntigo);
		ResponseModel<?> response = new ResponseModel<>("Arquivo atualizado com sucesso.", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// PRECISA MUDAR PARA RESPONSEMODEL?
	@GetMapping("/buscar")
	public ResponseEntity<byte[]> buscarArquivo(@RequestParam("nomeArquivo") String nomeArquivo)
			throws FileNotFoundException, IOException {
		byte[] arquivo = this.arquivoService.getArquivo(nomeArquivo);
		return new ResponseEntity<>(arquivo, HttpStatus.OK);
	}

	@DeleteMapping("/deletar")
	public ResponseEntity<ResponseModel<?>> deletar(@RequestParam("nomeArquivo") String nomeArquivo)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		this.arquivoService.deletar(nomeArquivo);
		ResponseModel<?> response = new ResponseModel<>("Arquivo deletado com sucesso.", null);
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

}
