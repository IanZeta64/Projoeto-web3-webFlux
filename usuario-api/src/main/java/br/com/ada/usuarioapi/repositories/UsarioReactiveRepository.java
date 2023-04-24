package br.com.ada.usuarioapi.repositories;

import br.com.ada.usuarioapi.model.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@EnableReactiveMongoRepositories
public interface UsarioReactiveRepository extends ReactiveMongoRepository<Usuario, String> {

}
