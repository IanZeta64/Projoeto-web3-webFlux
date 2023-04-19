package br.com.ada.apostaapi.repositories;

import br.com.ada.apostaapi.model.Aposta;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApostaInMemoryRepository {

    private static final List<Aposta> listAposta = new ArrayList<>();


    public Aposta save(Aposta aposta){

        listAposta.add(aposta);
        return aposta;
    }

    public Optional<Aposta> get(UUID uuid){
        return listAposta.stream().filter(aposta -> aposta.getApostaId().equals(uuid)).findFirst();
    }
    public List<Aposta> getAll(){
        return listAposta;
    }
}
