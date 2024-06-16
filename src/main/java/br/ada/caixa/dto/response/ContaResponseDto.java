package br.ada.caixa.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ContaResponseDto {

    private String numero;
    private BigDecimal saldo;
    private String tipo;

}
