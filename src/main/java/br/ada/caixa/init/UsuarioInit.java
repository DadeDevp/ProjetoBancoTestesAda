//package br.ada.caixa.init;
//
//import br.ada.caixa.entity.Usuario;
//import br.ada.caixa.respository.UsuarioRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Slf4j
////@Component
//public class UsuarioInit implements InitializingBean {
//
//    private final UsuarioRepository usuarioRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public UsuarioInit(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
//        this.usuarioRepository = usuarioRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//
//    @Override
//    public void afterPropertiesSet() {
//
//        if (usuarioRepository.count() == 0) {
//            final var usuario = new Usuario();
//            usuario.setEmail("admin@test.com");
//            usuario.setNome("User admin");
//            usuario.setSenha(passwordEncoder.encode("12345"));
//            usuarioRepository.save(usuario);
//        }
//
//    }
//
//}
