package br.ada.caixa.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ClientePFResponseDto {

    private String cpf;
    private String nome;
    private LocalDate dataNascimento;

    private List<ContaResponseDto> contas;

}
