package br.com.ada.apostaapi.repositories;

import br.com.ada.apostaapi.model.Aposta;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApostaRepository extends ReactiveMongoRepository<Aposta, String> {

}


