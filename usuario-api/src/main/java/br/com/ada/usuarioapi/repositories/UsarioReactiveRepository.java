package br.com.ada.usuarioapi.repositories;
import br.com.ada.usuarioapi.model.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsarioReactiveRepository extends ReactiveMongoRepository<Usuario, String> {

}
