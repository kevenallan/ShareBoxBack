package br.com.sharebox.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.com.sharebox.exception.CustomException;
import br.com.sharebox.model.UsuarioModel;

@Component
public class UsuarioRepository extends Repository {
	
	private final String COLLECTION_USUARIO = "USUARIO"; 
	
    // Método para salvar um usuário no Firestore
    public UsuarioModel cadastrar(UsuarioModel usuarioModel) throws Exception {
        
    	//VERIFICAR USUARIO E EMAIL
    	this.buscarUsuarioPorUsuarioEEmail(usuarioModel.getUsuario(), usuarioModel.getEmail());
//    	if (usuarioValido != null) {
//    		return null;
//    	}
    	// Obtenha a instância do Firestore
        Firestore db = getConectionFirestoreDataBase();

        // Gere um ID para o documento (ou use o ID do objeto se existir)
        String idDocumento = UUID.randomUUID().toString();
        DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(idDocumento);

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nome", usuarioModel.getNome());
        usuarioData.put("email", usuarioModel.getEmail());
        usuarioData.put("usuario", usuarioModel.getUsuario());
        usuarioData.put("senha", usuarioModel.getSenha());
        
        // Insira os dados do objeto no Firestore
        ApiFuture<WriteResult> result = docRef.set(usuarioData);

        result.get();
        
        // Agora, valide se o documento realmente foi criado
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            // Se o documento foi encontrado, retorne o usuário
        	UsuarioModel usuarioCadastrado = document.toObject(UsuarioModel.class);
        	usuarioCadastrado.setId(idDocumento);
            return usuarioCadastrado;
        } else {
            // Se não foi encontrado, lance uma exceção ou retorne null
            throw new RuntimeException("Falha ao cadastrar o usuário. O documento não foi encontrado.");
        }
    }
	
    public UsuarioModel login(String usuario, String senha) throws InterruptedException, ExecutionException {
    	Firestore db = getConectionFirestoreDataBase();
  
        Query query = db.collection(COLLECTION_USUARIO)
                        .whereEqualTo("usuario", usuario)
                        .whereEqualTo("senha", senha);
        
        // Executa a consulta
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        // Obtém os resultados
        List<QueryDocumentSnapshot> documentos = querySnapshot.get().getDocuments();

        // Verifica se encontrou algum documento
        if (!documentos.isEmpty()) {
        	QueryDocumentSnapshot documento = documentos.get(0);
            
            // Converte o documento para o objeto UsuarioModel
            UsuarioModel usuarioModel = documento.toObject(UsuarioModel.class);
            
            // Define a chave do documento (ID) no UsuarioModel
            usuarioModel.setId(documento.getId());
            
            return usuarioModel;
        }

        // Retorna nulo se não encontrou o usuário
        return null;
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
//            return "Usuario já em uso";
        	 throw new Exception("Usuario já em uso");
        }
        
        // Consulta pelo campo "email"
        Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);
       
        ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

        List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

        // Caso não tenha encontrado pelo "usuario", tenta pelo "email"
        if (!documentosEmail.isEmpty()) {
        	throw new Exception("Email já em uso");
        }
    }
    
    public UsuarioModel buscarUsuarioPorEmail(String email) throws Exception  {
        Firestore dbFirestore = getConectionFirestoreDataBase();
        
        // Consulta pelo campo "email"
        Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);
       
        ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

        List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();
        UsuarioModel usuarioModel = null;

        // Caso não tenha encontrado pelo "usuario", tenta pelo "email"      	
        if (documentosEmail.isEmpty()) {
        	throw new CustomException("Usuário inválido");
            
        }
        QueryDocumentSnapshot documento = documentosEmail.get(0);
        
        // Converte o documento para o objeto UsuarioModel
        usuarioModel = documento.toObject(UsuarioModel.class);    
        return usuarioModel;
       
    }

}
