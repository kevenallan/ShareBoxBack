package br.com.sharebox.repository;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.ReadChannel;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;

import br.com.sharebox.exception.CustomException;
import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.model.UsuarioModel;
import br.com.sharebox.service.FirebaseService;

@Component
public class UsuarioRepository extends Repository {

	private final String COLLECTION_USUARIO = "USUARIO";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FirebaseService firebaseService;

	// Método para salvar um usuário no Firestore
	public UsuarioModel cadastrar(UsuarioModel usuarioModel) throws Exception {

		// VERIFICAR USUARIO E EMAIL
		this.buscarUsuarioPorUsuarioEEmail(usuarioModel.getUsuario(), usuarioModel.getEmail());

		// Obtenha a instância do Firestore
		Firestore db = getConectionFirestoreDataBase();

		// Gere um ID para o documento (ou use o ID do objeto se existir)
		String idDocumento = UUID.randomUUID().toString();
		DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(idDocumento);

		String senhaSemCriptografia = usuarioModel.getSenha();
		String senhaComCriptografia = passwordEncoder.encode(usuarioModel.getSenha());

		Map<String, Object> usuarioData = new HashMap<>();
		usuarioData.put("nome", usuarioModel.getNome());
		usuarioData.put("email", usuarioModel.getEmail());
		usuarioData.put("usuario", usuarioModel.getUsuario());
		usuarioData.put("senha", senhaComCriptografia);

		// Insira os dados do objeto no Firestore
		ApiFuture<WriteResult> result = docRef.set(usuarioData);

		result.get();

		// Agora, valide se o documento realmente foi criado
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();

		if (document.exists()) {
			// Se o documento foi encontrado, retorne o usuário
			UsuarioModel usuarioCadastrado = getUsuarioModel(document);
			usuarioCadastrado.setSenha(senhaSemCriptografia);
			return usuarioCadastrado;
		} else {
			// Se não foi encontrado, lance uma exceção ou retorne null
			throw new CustomException("Falha ao cadastrar o usuário.");
		}
	}

	public UsuarioModel cadastrarUsuarioGoogle(UsuarioModel usuarioModel) throws Exception {

		Firestore db = getConectionFirestoreDataBase();

		String idDocumento = usuarioModel.getId();
		DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(idDocumento);

		Map<String, Object> usuarioData = new HashMap<>();
		usuarioData.put("nome", usuarioModel.getNome());
		usuarioData.put("email", usuarioModel.getEmail());

		// Insira os dados do objeto no Firestore
		ApiFuture<WriteResult> result = docRef.set(usuarioData);

		result.get();

		// Agora, valide se o documento realmente foi criado
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();

		if (document.exists()) {
			// Se o documento foi encontrado, retorne o usuário
			UsuarioModel usuarioCadastrado = getUsuarioModel(document);
			return usuarioCadastrado;
		} else {
			// Se não foi encontrado, lance uma exceção ou retorne null
			throw new CustomException("Falha ao cadastrar o usuário.");
		}
	}

	public UsuarioModel login(String usuario, String senha) throws InterruptedException, ExecutionException {
		Firestore db = getConectionFirestoreDataBase();

		Query query = db.collection(COLLECTION_USUARIO).whereEqualTo("usuario", usuario);

		// Executa a consulta
		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		// Obtém os resultados
		List<QueryDocumentSnapshot> documentos = querySnapshot.get().getDocuments();

		// Verifica se o documento esta vazio
		if (documentos.isEmpty()) {
			throw new CustomException("Usuário inválido.");
		}

		QueryDocumentSnapshot documento = documentos.get(0);

		// Converte o documento para o objeto UsuarioModel
		UsuarioModel usuarioModel = getUsuarioModel(documento);

		// VALIDAR SENHA CRIPTOGRAFADA
		if (!passwordEncoder.matches(senha, usuarioModel.getSenha())) {
			throw new CustomException("Senha inválida.");
		}

		return usuarioModel;

	}

	public void buscarUsuarioPorUsuarioEEmail(String usuario, String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		// Consulta pelo campo "usuario"
		Query queryUsuario = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("usuario", usuario);

		// Executa as consultas em paralelo
		ApiFuture<QuerySnapshot> futureUsuario = queryUsuario.get();

		// Obtém os resultados das consultas
		List<QueryDocumentSnapshot> documentosUsuario = futureUsuario.get().getDocuments();

		// Verifica se encontrou algum documento pelo campo "usuario"
		if (!documentosUsuario.isEmpty()) {
			throw new CustomException("Este nome de usuario já está em uso. Tente outro.");
		}

		// Consulta pelo campo "email"
		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		// Caso não tenha encontrado pelo "usuario", tenta pelo "email"
		if (!documentosEmail.isEmpty()) {
			throw new CustomException("Este e-mail já está em uso. Tente outro.");
		}
	}

	public UsuarioModel buscarUsuarioPorEmail(String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		// Consulta pelo campo "email"
		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		if (documentosEmail.isEmpty()) {
			throw new CustomException("E-mail inválido");

		}
		QueryDocumentSnapshot documento = documentosEmail.get(0);

		return getUsuarioModel(documento);

	}

	public UsuarioModel buscarUsuarioGooglePorEmail(String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		if (documentosEmail.isEmpty()) {
			return null;

		}
		QueryDocumentSnapshot documento = documentosEmail.get(0);

		return getUsuarioModel(documento);

	}

	public void atualizarSenha(String idUsuario, String novaSenha) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		// Obter a referência do documento pelo idUsuario
		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		// Verificar se o documento existe
		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			String senhaAtual = documentSnapshot.getString("senha");
			if (passwordEncoder.matches(novaSenha, senhaAtual)) {
				throw new CustomException("A nova senha não pode ser igual à senha atual.");
			}

			// Atualizar a senha
			ApiFuture<WriteResult> futureUpdate = docRef.update("senha", passwordEncoder.encode(novaSenha));
			futureUpdate.get(); // Aguardar a conclusão da atualização
		} else {
			throw new CustomException("Erro na atualização da senha.");
		}
	}

	public UsuarioModel getDadosUsuario(String idUsuario) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			return getUsuarioModel(documentSnapshot);
		} else {
			throw new CustomException("Erro ao buscar os dados do usuário.");
		}
	}

	public void atualizarUsuario(String idUsuario, UsuarioModel usuarioAtualizado) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		// Obter a referência do documento pelo idUsuario
		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		// Verificar se o documento existe
		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			// TODO - PRECISA ATUALIZAR TODOS OS CAMPOS?
			String senhaAtual = documentSnapshot.getString("senha");
			ApiFuture<WriteResult> futureUpdate = null;
			if (passwordEncoder.matches(usuarioAtualizado.getSenha(), senhaAtual)
					|| usuarioAtualizado.getSenha().equals(senhaAtual)) {
				futureUpdate = docRef.update("nome", usuarioAtualizado.getNome(), "email", usuarioAtualizado.getEmail(),
						"usuario", usuarioAtualizado.getUsuario());
			} else {
				futureUpdate = docRef.update("nome", usuarioAtualizado.getNome(), "email", usuarioAtualizado.getEmail(),
						"usuario", usuarioAtualizado.getUsuario(), "senha",
						passwordEncoder.encode(usuarioAtualizado.getSenha()));
			}

			futureUpdate.get(); // Aguardar a conclusão da atualização
		} else {
			throw new CustomException("Erro na atualização do usuário.");
		}
	}

	public void atualizarUsuarioGoogle(String idUsuario, UsuarioModel usuarioAtualizado) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			ApiFuture<WriteResult> futureUpdate = null;

			futureUpdate = docRef.update("nome", usuarioAtualizado.getNome());

			futureUpdate.get();
		} else {
			throw new CustomException("Erro na atualização do nome do usuário.");
		}
	}

	public void deletarUsuarioPorId(String documentId) {
		try {
			Firestore dbFirestore = getConectionFirestoreDataBase();
			dbFirestore.collection(COLLECTION_USUARIO).document(documentId).delete().get();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CustomException("Erro ao tentar deletar o usuário");
		}
	}

	private UsuarioModel getUsuarioModel(DocumentSnapshot documentSnapshot) {
		UsuarioModel usuarioModel = documentSnapshot.toObject(UsuarioModel.class);
		usuarioModel.setId(documentSnapshot.getId());
		return usuarioModel;
	}

	public void compartilharArquivos(UsuarioModel usuarioAtualizado) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(usuarioAtualizado.getId());

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {

			ApiFuture<WriteResult> futureUpdate = null;

			futureUpdate = docRef.update("arquivosCompartilhados", usuarioAtualizado.getArquivosCompartilhados());

			futureUpdate.get(); // Aguardar a conclusão da atualização
		} else {
			throw new CustomException("Erro na atualização do usuário.");
		}
	}

	public List<ArquivoModel> listarArquivosCompartilhados(String documentId) throws Exception {
		UsuarioModel usuario = this.getDadosUsuario(documentId);
		List<BlobId> listaBlobId = new ArrayList<>();

		if (usuario.getArquivosCompartilhados() == null || usuario.getArquivosCompartilhados().isEmpty()) {
			return new ArrayList<>();
		}

		usuario.getArquivosCompartilhados()
				.forEach(arquivo -> listaBlobId.add(BlobId.of(this.firebaseService.getBucketName(), arquivo)));

		Storage storage = this.firebaseService.initStorage();
		List<Blob> lista = storage.get(listaBlobId);

		return this.converterParaArquivoModel(lista);
	}

	private List<ArquivoModel> converterParaArquivoModel(List<Blob> lista) {
		List<ArquivoModel> arquivoList = new ArrayList<>();
		try {
			for (Blob blob : lista) {
				ArquivoModel arquivo = new ArquivoModel();
				String nome = blob.getName().split("/")[1];
				String[] nomeExtensao = nome.split("\\.");
				int indicePontoExtensao = nome.lastIndexOf(".");
				String nomeArquivo = nome.substring(0, indicePontoExtensao);

				arquivo.setPathArquivo(blob.getName());
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

}
