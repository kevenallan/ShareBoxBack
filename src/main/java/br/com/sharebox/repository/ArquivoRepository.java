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
import java.util.Comparator;
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
				String nome = blob.getName().split("/")[1];
				String[] nomeExtensao = nome.split("\\.");
				int indicePontoExtensao = nome.lastIndexOf(".");
				String nomeArquivo = nome.substring(0, indicePontoExtensao);

				arquivo.setNome(nomeArquivo);
				arquivo.setExtensao(nomeExtensao[nomeExtensao.length - 1]);
				arquivo.setMimeType(blob.getContentType());

				String tamanhoFormatado = formatarTamanhoArquivo(blob.getSize());
				arquivo.setTamanho(tamanhoFormatado);

				arquivo.setDataCriacao(
						LocalDateTime.ofInstant(Instant.ofEpochMilli(blob.getCreateTime()), ZoneId.systemDefault()));

				try (ReadChannel reader = blob.reader()) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					WritableByteChannel channel = Channels.newChannel(outputStream);
					ByteBuffer buffer = ByteBuffer.allocate(64 * 1024); // 64KB por buffer

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
			arquivoList.sort(Comparator.comparing(ArquivoModel::getDataCriacao).reversed());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			BlobId blobId = BlobId.of(this.firebaseService.getBucketName(),
					this.authService.uuidUsuarioLogado + "/" + nomeArquivo);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

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
		String pathArquivo;
		if (nomeArquivo.contains("/")) {
			pathArquivo = nomeArquivo;
		} else {
			pathArquivo = this.authService.uuidUsuarioLogado + "/" + nomeArquivo;
		}

		Blob blob = storage.get(BlobId.of(this.firebaseService.getBucketName(), pathArquivo));
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

	public void deletarPasta(String idUsuario) throws FileNotFoundException, IOException {
		Storage storage = this.firebaseService.initStorage();
		Bucket bucket = storage.get(this.firebaseService.getBucketName());

		// Listar arquivos na "pasta" usando o prefixo
		Iterable<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(idUsuario + "/")).iterateAll();

		// Excluir cada arquivo encontrado
		for (Blob blob : blobs) {
			blob.delete();
		}
	}

}
