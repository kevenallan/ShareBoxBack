package br.com.sharebox.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

import br.com.sharebox.exception.CustomException;
import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.model.ResponseModel;
import br.com.sharebox.service.ArquivoService;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {

	@Autowired
	private ArquivoService arquivoService;

	@Value("${link.back}")
	private String linkBack;

	@GetMapping("/listar")
	public ResponseEntity<ResponseModel<?>> listar() throws FileNotFoundException, IOException {
		List<ArquivoModel> arquivos = this.arquivoService.listar();
		ResponseModel<?> response = new ResponseModel<>(null, arquivos);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseModel<?>> uploadFile(@RequestParam("files") MultipartFile[] files) {
		this.arquivoService.upload(files);
		return new ResponseEntity<>(new ResponseModel<>("Arquivo(s) adicionado(s)", null), HttpStatus.OK);
	}

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

	@GetMapping("/buscar")
	public ResponseEntity<byte[]> buscarArquivo(@RequestParam("nomeArquivo") String nomeArquivo)
			throws FileNotFoundException, IOException {
		byte[] arquivo = this.arquivoService.getArquivo(nomeArquivo);
		return new ResponseEntity<>(arquivo, HttpStatus.OK);
	}

	@DeleteMapping("/deletar")
	public ResponseEntity<ResponseModel<?>> deletar(@RequestParam("nomesArquivos") List<String> nomesArquivos)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		this.arquivoService.deletar(nomesArquivos);
		ResponseModel<?> response = new ResponseModel<>("Arquivo(s) deletado(s) com sucesso.", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Rota para fazer o upload do arquivo ZIP
	@PostMapping("/upload-zip-link")
	public ResponseEntity<ResponseModel<?>> uploadZip(@RequestParam("file") MultipartFile file) {
		try {
			String nomeZip = "sharebox-" + UUID.randomUUID() + ".zip";
			// Cria um diretório temporário
			Path tempDir = Files.createTempDirectory("shraboxZipTemp");
			Path tempFile = tempDir.resolve(nomeZip);

			// Salva o arquivo ZIP no diretório temporário
			Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

			// Gera o link de download
			String downloadUrl = linkBack + "/arquivo/download-link?fileName=" + nomeZip;

			// Retorna o link para ser compartilhado
			ResponseModel<?> response = new ResponseModel<>("Link copiado com sucesso", downloadUrl);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Rota para download do arquivo ZIP
	@GetMapping("/download-link")
	public ResponseEntity<Resource> downloadZip(@RequestParam("fileName") String filename) throws IOException {
		try {
			// Localiza o arquivo no diretório temporário
			Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir, "shraboxZipTemp*")) {
				for (Path dir : stream) {
					Path targetFile = dir.resolve(filename);
					if (Files.exists(targetFile)) {
						Resource resource = new UrlResource(targetFile.toUri());
						return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
					}
				}
			}
			throw new CustomException("Arquivo indisponível.");

		} catch (MalformedURLException e) {
			throw new CustomException("Link inválida, solicite um novo link.");
		}
	}

}
