package br.com.ada.jogosapi.repositories;

import br.com.ada.jogosapi.model.Jogo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JogoInMemoryRepository {
    private static final List<Jogo> listJogos = new ArrayList<>();


    public Jogo save(Jogo jogo){

            listJogos.add(jogo);
            return jogo;
    }

    public Optional<Jogo> get(UUID uuid){
        return listJogos.stream().filter(jogo -> jogo.getUuid().equals(uuid)).findFirst();
    }
    public List<Jogo> getAll(){
        return listJogos;
    }
}
