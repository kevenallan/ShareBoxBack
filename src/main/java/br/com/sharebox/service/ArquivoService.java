package br.com.sharebox.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sharebox.model.ArquivoModel;
import br.com.sharebox.repository.ArquivoRepository;

@Service
public class ArquivoService {

	@Autowired
	private ArquivoRepository arquivoRepository;
	
	public List<ArquivoModel> listar() {
		return this.arquivoRepository.findAll();
	}
	
	public void upload(ArquivoModel arquivoModel) {
		this.arquivoRepository.save(arquivoModel);
	}
	
	@SuppressWarnings("deprecation")
	public ArquivoModel getArquivoById(Long id) {
		return this.arquivoRepository.getById(id);
	}
}
