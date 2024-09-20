package br.com.sharebox.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

import br.com.sharebox.model.ArquivoModel;

@Service
public class ArquivoService {

	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	public List<ArquivoModel> listar(String pasta) throws FileNotFoundException, IOException {
		List<ArquivoModel> arquivoList = new ArrayList<>();
		
        Storage storage = this.firebaseService.initStorage();
        Bucket bucket = storage.get(this.firebaseService.getBucketName());

        // Lista arquivos dentro da pasta especificada
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(pasta));


        // Itera sobre os arquivos listados na pasta
        for (Blob blob : blobs.iterateAll()) {
        	ArquivoModel arquivo = new ArquivoModel();
        	arquivo.setNome(blob.getName().split("/")[1]);
        	arquivo.setExtensao(blob.getContentType());
        	arquivo.setDataCriacao(LocalDateTime.ofInstant(Instant.ofEpochMilli(blob.getCreateTime()), ZoneId.systemDefault()));
        	arquivoList.add(arquivo);
        }

        return arquivoList;
    }
	
	public void upload(MultipartFile file, String nomeArquivo, String usuario) throws InterruptedException, ExecutionException {

        try {
            BlobId blobId = BlobId.of(this.firebaseService.getBucketName(), usuario + "/" + nomeArquivo);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            try (InputStream inputStream = file.getInputStream()) {
            	Storage storage = this.firebaseService.initStorage();
                storage.create(blobInfo, inputStream);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public byte[] getArquivo(String nomeArquivo, String login) throws FileNotFoundException, IOException {
		Storage storage = this.firebaseService.initStorage();
		Blob blob = storage.get(BlobId.of(this.firebaseService.getBucketName(), login + "/" + nomeArquivo ));
		if(blob != null) {
			return blob.getContent();
		}	
		return null;
		
	}
	

}
