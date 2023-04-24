package br.com.ada.jogosapi.repositories;
import br.com.ada.jogosapi.model.Jogo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogoRepository extends ReactiveMongoRepository<Jogo, String> {

}
