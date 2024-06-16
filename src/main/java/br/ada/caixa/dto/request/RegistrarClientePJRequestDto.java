package br.ada.caixa.dto.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarClientePJRequestDto {

    private String cnpj;
    private String nomeFantasia;
    private String razaoSocial;

}
