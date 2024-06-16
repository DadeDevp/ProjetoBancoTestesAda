//package br.ada.caixa.config.security;
//
//import br.ada.caixa.respository.UsuarioRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//
//@Service
//public class UserServiceImpl implements UserDetailsService {
//
//    final UsuarioRepository usuarioRepository;
//
//    public UserServiceImpl(UsuarioRepository usuarioRepository) {
//        this.usuarioRepository = usuarioRepository;
//    }
//
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//
//        var usuario = usuarioRepository.findByEmail(email);
//
//        if (Objects.isNull(usuario)) {
//            throw new UsernameNotFoundException(email);
//        }
//
//        return new UserPrincipalDetails(usuario);
//    }
//
//}
