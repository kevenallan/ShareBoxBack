package br.com.sharebox.repository;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
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
	
	private static final Logger log = LoggerFactory.getLogger(ArquivoRepository.class);

	
	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private AuthService authService;
	
	public List<ArquivoModel> listar() {
		List<ArquivoModel> arquivoList = new ArrayList<>();
		try {
	        Storage storage = this.firebaseService.initStorage();
	        Bucket bucket = storage.get(this.firebaseService.getBucketName());

	        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(this.authService.uuidUsuarioLogado));

	        for (Blob blob : blobs.iterateAll()) {
	        	ArquivoModel arquivo = new ArquivoModel();
	        	arquivo.setNome(blob.getName().split("/")[1].split("\\.")[0]);
	        	
	        	arquivo.setExtensao(blob.getName().split("/")[1].split("\\.")[1]);
	        	arquivo.setMimeType(blob.getContentType());

	        	String tamanhoFormatado = formatarTamanhoArquivo(blob.getSize());
	        	arquivo.setTamanho(tamanhoFormatado);
	        	
	        	arquivo.setDataCriacao(LocalDateTime.ofInstant(Instant.ofEpochMilli(blob.getCreateTime()), ZoneId.systemDefault()));

	        	// Converte o arquivo para Base64
//	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	            blob.downloadTo(outputStream);
//	            byte[] fileBytes = outputStream.toByteArray();
//	            String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
//	        	arquivo.setBase64(base64Encoded);
	        	
	        	try (ReadChannel reader = blob.reader()) {
	        		log.info("Convertendo o Arquivo em BYTE[]");
	        	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        	    WritableByteChannel channel = Channels.newChannel(outputStream);
	        	    ByteBuffer buffer = ByteBuffer.allocate(64 * 1024);  // 64KB por buffer

	        	    while (reader.read(buffer) > 0) {
	        	        buffer.flip();
	        	        channel.write(buffer);
	        	        buffer.clear();
	        	    }
	        	    // Processar os bytes como necessário
	        	    byte[] fileBytes = outputStream.toByteArray();
	        	    arquivo.setBytes(fileBytes);
	        	} catch (Exception e) {
	        		log.error("----->ERRO AO CONVERTER O ARQUIVO EM BYTE[]");
	        		e.printStackTrace();
	        	}
	        	
	        	
	        	arquivoList.add(arquivo);
	        }
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		log.info("RETURN");
        return arquivoList;
    }
	
	public String formatarTamanhoArquivo(long tamanhoEmBytes) {
	    if (tamanhoEmBytes < 1024) {
	        return tamanhoEmBytes + " B";
	    } else if (tamanhoEmBytes < 1024 * 1024) {
	        return String.format("%.1f KB", tamanhoEmBytes / 1024.0);
	    } else if (tamanhoEmBytes < 1024 * 1024 * 1024) {
	        return String.format("%.1f MB", tamanhoEmBytes / (1024.0 * 1024));
	    } else {
	        return String.format("%.1f GB", tamanhoEmBytes / (1024.0 * 1024 * 1024));
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
