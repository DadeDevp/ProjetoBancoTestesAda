//package br.ada.caixa.config.security;
//
//import br.ada.caixa.entity.Cliente;
//import br.ada.caixa.entity.Usuario;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//public class UserPrincipalDetails implements UserDetails {
//
//    final Usuario usuario;
//
//    public UserPrincipalDetails(Usuario usuario) {
//        this.usuario = usuario;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
//    }
//
//    @Override
//    public String getPassword() {
//        return usuario.getSenha();
//    }
//
//    @Override
//    public String getUsername() {
//        return usuario.getEmail();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    public String getEmail() {
//        return usuario.getEmail();
//    }
//}
