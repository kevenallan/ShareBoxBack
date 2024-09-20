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
	
	public List<ArquivoModel> listar(String pathArquivo) throws FileNotFoundException, IOException {
		return this.arquivoRepository.listar(pathArquivo);
    }
	
	public void upload(MultipartFile file, String nomeArquivo, String usuario) throws InterruptedException, ExecutionException {
        this.arquivoRepository.upload(file, nomeArquivo, usuario);
	}
	
	public byte[] getArquivo(String nomeArquivo, String login) throws FileNotFoundException, IOException {
		Blob blob = this.arquivoRepository.getArquivo(nomeArquivo, login);
		if (blob != null) {
			return blob.getContent();
		}
		return null;
	}
	
	public void deletar(String nomeArquivo, String usuario) throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		String pathArquivo = usuario + "/" + nomeArquivo;
        this.arquivoRepository.deletar(pathArquivo);
	}
}
