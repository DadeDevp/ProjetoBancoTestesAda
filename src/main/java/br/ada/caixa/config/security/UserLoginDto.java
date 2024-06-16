package br.ada.caixa.config.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    private String email;
    private String senha;

}
