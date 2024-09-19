package br.com.sharebox.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.Firestore;

import br.com.sharebox.service.FirebaseService;

@Component
public class Repository {

	@Autowired
	private FirebaseService firebaseService;
	
    public Firestore getConectionFirestoreDataBase() {
        return this.firebaseService.getConectionFirestoreDataBase();
    }
}
