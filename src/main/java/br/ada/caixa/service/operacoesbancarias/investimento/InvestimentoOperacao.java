package br.ada.caixa.service.operacoesbancarias.investimento;

import br.ada.caixa.entity.Conta;

import java.math.BigDecimal;

public interface InvestimentoOperacao {

    void executar(Conta contaInvestimento, BigDecimal valor);

}
