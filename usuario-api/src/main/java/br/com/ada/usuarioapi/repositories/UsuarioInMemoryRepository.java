//package br.com.ada.usuarioapi.repositories;
//
//import br.com.ada.usuarioapi.model.Usuario;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public class UsuarioInMemoryRepository {
//    private final List<Usuario> listUsuario = new ArrayList<>();
//
//    public Usuario save(Usuario usuario){
//
//        listUsuario.add(usuario);
//        return usuario;
//    }
//
//    public Optional<Usuario> get(UUID uuid){
//        return listUsuario.stream().filter(usuario -> usuario.getUsuarioId().equals(uuid)).findFirst();
//    }
//    public List<Usuario> getAll(){
//        return listUsuario;
//    }
//}
