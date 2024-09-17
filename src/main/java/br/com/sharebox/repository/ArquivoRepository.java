package br.com.sharebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sharebox.model.ArquivoModel;

@Repository
public interface ArquivoRepository extends JpaRepository<ArquivoModel, Long> {
	

}
