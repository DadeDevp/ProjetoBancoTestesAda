package br.ada.caixa.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositoRequestDto {

    @NotNull
    private Long numeroConta;
    @NotNull
    private BigDecimal valor;


    @Override
    public String toString() {
        return "DepositoRequestDto{" +
                "numeroConta='" + numeroConta + '\'' +
                ", valor=" + valor +
                '}';
    }
}
