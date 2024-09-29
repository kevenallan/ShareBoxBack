package br.com.sharebox.repository;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

import br.com.sharebox.exception.CustomException;
import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.service.AuthService;
import br.com.sharebox.service.FirebaseService;

@Component
public class ArquivoRepository extends Repository {
	
	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private AuthService authService;
	
	public List<ArquivoModel> listar() throws FileNotFoundException, IOException {
		List<ArquivoModel> arquivoList = new ArrayList<>();
		
        Storage storage = this.firebaseService.initStorage();
        Bucket bucket = storage.get(this.firebaseService.getBucketName());

        // Lista arquivos dentro da pasta especificada
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(this.authService.uuidUsuarioLogado));


        // Itera sobre os arquivos listados na pasta
        for (Blob blob : blobs.iterateAll()) {
        	ArquivoModel arquivo = new ArquivoModel();
        	arquivo.setNome(blob.getName().split("/")[1].split("\\.")[0]);
        	
        	arquivo.setExtensao(blob.getName().split("/")[1].split("\\.")[1]);
        	arquivo.setMimeType(blob.getContentType());

        	String tamanhoFormatado = formatarTamanhoArquivo(blob.getSize());
        	arquivo.setTamanho(tamanhoFormatado);
        	
        	arquivo.setDataCriacao(LocalDateTime.ofInstant(Instant.ofEpochMilli(blob.getCreateTime()), ZoneId.systemDefault()));

        	// Converte o arquivo para Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blob.downloadTo(outputStream);
            byte[] fileBytes = outputStream.toByteArray();
            String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
        	arquivo.setBase64(base64Encoded);

        	arquivoList.add(arquivo);
        }

        return arquivoList;
    }
	
	public String formatarTamanhoArquivo(long tamanhoEmBytes) {
	    if (tamanhoEmBytes < 1024) {
	        return tamanhoEmBytes + " B";
	    } else if (tamanhoEmBytes < 1024 * 1024) {
	        return (tamanhoEmBytes / 1024) + " KB";
	    } else if (tamanhoEmBytes < 1024 * 1024 * 1024) {
	        return (tamanhoEmBytes / (1024 * 1024)) + " MB";
	    } else {
	        return (tamanhoEmBytes / (1024 * 1024 * 1024)) + " GB";
	    }
	}
	
	@SuppressWarnings("deprecation")
	public void upload(MultipartFile file, String nomeArquivo) throws InterruptedException, ExecutionException {

        try {
            BlobId blobId = BlobId.of(this.firebaseService.getBucketName(), this.authService.uuidUsuarioLogado + "/" + nomeArquivo);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            try (InputStream inputStream = file.getInputStream()) {
            	Storage storage = this.firebaseService.initStorage();
                storage.create(blobInfo, inputStream);
            }

        } catch (Exception e) {
        	throw new CustomException("Erro ao tentar fazer o upload do arquivo. Tente novamente.");
        }
	}
	
	public Blob getArquivo(String nomeArquivo) throws FileNotFoundException, IOException {
		Storage storage = this.firebaseService.initStorage();
		Blob blob = storage.get(BlobId.of(this.firebaseService.getBucketName(), this.authService.uuidUsuarioLogado + "/" + nomeArquivo ));
		return blob;
	}
	
	public void deletar(String nomeArquivo) throws FileNotFoundException, IOException {
        // Obtenha o bucket do Storage
		Storage storage = this.firebaseService.initStorage();
		Bucket bucket = storage.get(this.firebaseService.getBucketName());

		String pathArquivo = this.authService.uuidUsuarioLogado + "/" + nomeArquivo;
        // Referencie o arquivo e delete
        Blob blob = bucket.get(pathArquivo);
        if (blob != null && blob.exists()) {
            blob.delete();
        } else {
           throw new CustomException("Arquivo não encontrado.");
        }
    }

}
