package br.ada.caixa.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequestDto {

    private Long numeroContaOrigem;
    private Long numeroContaDestino;
    private BigDecimal valor;

    @Override
    public String toString() {
        return "TransfereRequestDto{" +
                "numeroContaOrigem='" + numeroContaOrigem + '\'' +
                ", numeroContaDestino='" + numeroContaDestino + '\'' +
                ", valor=" + valor +
                '}';
    }
}
