package br.com.sharebox.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.sharebox.model.ArquivoModel;

import java.util.List;

@Aspect
@Component
public class ArquivoAspect {

    private static final Logger logger = LoggerFactory.getLogger(ArquivoAspect.class);

    // Intercepta o método 'listar' após sua execução e captura o retorno
    @AfterReturning(pointcut = "execution(* br.com.sharebox.repository.ArquivoRepository.listar(..))", returning = "resultado")
    public void rastrearNomesArquivos(List<ArquivoModel> resultado) {
    	if (resultado != null && !resultado.isEmpty()) {
            for (ArquivoModel arquivo : resultado) {
                logger.info("Arquivo processado: Nome - {}, Extensão - {}, Tamanho - {}", 
                            arquivo.getNome(), arquivo.getExtensao(), arquivo.getTamanho());
            }
        } else {
            logger.warn("Nenhum arquivo foi retornado na listagem.");
        }
    }
}