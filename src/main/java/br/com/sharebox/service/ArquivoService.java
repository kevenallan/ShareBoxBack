package br.com.sharebox.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;

import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.repository.ArquivoRepository;

@Service
public class ArquivoService {

	@Autowired
	private ArquivoRepository arquivoRepository;

	public List<ArquivoModel> listar() throws FileNotFoundException, IOException {
		return this.arquivoRepository.listar();
	}

	public void upload(MultipartFile[] files) throws InterruptedException, ExecutionException {
		for (MultipartFile file : files) {
			this.arquivoRepository.upload(file, file.getOriginalFilename());
		}
	}

	public void update(MultipartFile file, String nomeArquivo, String nomeArquivoAntigo)
			throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		this.arquivoRepository.deletar(nomeArquivoAntigo);
		this.arquivoRepository.upload(file, nomeArquivo);
	}

	public byte[] getArquivo(String nomeArquivo) throws FileNotFoundException, IOException {
		Blob blob = this.arquivoRepository.getArquivo(nomeArquivo);
		if (blob != null) {
			return blob.getContent();
		}
		return null;
	}

	public void deletar(List<String> nomesArquivos)
			throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		for (String nomeArquivo : nomesArquivos) {
			this.arquivoRepository.deletar(nomeArquivo);
		}
	}

	public void deletarPasta(String idUsuario) throws FileNotFoundException, IOException {
		this.arquivoRepository.deletarPasta(idUsuario);
	}
}
