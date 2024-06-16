package br.ada.caixa.service.operacoesbancarias.investimento;

import br.ada.caixa.entity.Conta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("investimentoOperacaoPF")
public class InvestimentoOperacaoPF implements InvestimentoOperacao {

    public static final BigDecimal RENDIMENTO_INVESTIMENTO = BigDecimal.valueOf(1.01);

    @Override
    public void executar(Conta contaInvestimento, BigDecimal valor) {
        valor = valor.multiply(RENDIMENTO_INVESTIMENTO);
        contaInvestimento.setSaldo(contaInvestimento.getSaldo().add(valor));
    }
}
