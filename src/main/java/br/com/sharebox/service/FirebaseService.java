package br.com.sharebox.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseService {

	@Value("${firebase.storage.bucket-name}")
    private String bucketName;
	
	@Value("${firebase.sharebox.config}")
    private String firebaseShareboxConfig;
	
	public String getBucketName() {
		return this.bucketName;
	}
	
    public Firestore getConectionFirestoreDataBase() {
        return FirestoreClient.getFirestore();
    }

    public Storage initStorage() throws FileNotFoundException, IOException {
		 return StorageOptions.newBuilder()
		            .setCredentials(GoogleCredentials.fromStream(new FileInputStream(firebaseShareboxConfig)))
		            .build()
		            .getService();
    }

    public void getCapacidadeStorage() throws FileNotFoundException, IOException {
        Storage storage = this.initStorage();
        
        Bucket bucket = storage.get(getBucketName());
        
        if (bucket != null) {
            System.out.println("Bucket: " + bucket.getName());
            
            long totalSize = 0;
            // Lista todos os blobs (objetos) no bucket
            Page<Blob> blobs = storage.list(getBucketName());
            
            for (Blob blob : blobs.iterateAll()) {
                totalSize += blob.getSize(); // Soma o tamanho de cada blob
            }
            
            // Converte o tamanho de bytes para gigabytes
            double totalSizeGB = totalSize / (1024.0 * 1024.0 * 1024.0); // 1 GB = 1024^3 bytes
            
            System.out.println("Total Size (bytes): " + totalSize);
            System.out.printf("Total Size: %.10f GB\n", totalSizeGB);
        } else {
            System.out.println("Bucket not found.");
        }
    }

    
}
