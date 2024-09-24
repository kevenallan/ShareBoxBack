package br.com.sharebox.repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import br.com.sharebox.model.UsuarioModel;

@Component
public class UsuarioRepository extends Repository {
	
	private final String COLLECTION_USUARIO = "USUARIO"; 
	
    // Método para salvar um usuário no Firestore
    public void cadastrar(UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {
        // Obtenha a instância do Firestore
        Firestore db = getConectionFirestoreDataBase();

        // Gere um ID para o documento (ou use o ID do objeto se existir)
        DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(usuarioModel.getUsuario());

        // Insira os dados do objeto no Firestore
//        ApiFuture<WriteResult> result = docRef.set(usuarioModel);
        docRef.set(usuarioModel);

        // Retorna a hora em que o documento foi atualizado
//        return result.get().getUpdateTime().toString();
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
            // Converte o primeiro documento encontrado no objeto UsuarioModel
            return documentos.get(0).toObject(UsuarioModel.class);
        }

        // Retorna nulo se não encontrou o usuário
        return null;
    }
	
}
