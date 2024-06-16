package br.ada.caixa.service.operacoesbancarias.saque;

import br.ada.caixa.entity.Conta;
import br.ada.caixa.exceptions.ValidacaoException;

import java.math.BigDecimal;

@FunctionalInterface
public interface OperacaoSaque {

    void executar(Conta conta, BigDecimal valorSaque);

    default void validarSaldo(Conta conta, BigDecimal valorSaque) {
        if (valorSaque.compareTo(conta.getSaldo()) > 0) {
            throw new ValidacaoException("Saldo insuficiente!");
        }
    }
}
