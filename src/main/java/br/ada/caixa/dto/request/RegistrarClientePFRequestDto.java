package br.ada.caixa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarClientePFRequestDto {

    private String cpf;
    private String nome;
    private LocalDate dataNascimento;

}
