package br.ada.caixa.service.operacoesbancarias.saque;

import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Order(1)
@Service
public class OperacaoSaqueSaldoPF implements OperacaoSaque {

    @Override
    public void executar(Conta conta, BigDecimal valorSaque) {

        if (!(conta.getCliente().getTipo().equals(TipoCliente.PF))) {
            return;
        }

        OperacaoSaque.super.validarSaldo(conta, valorSaque);
        conta.setSaldo(conta.getSaldo().subtract(valorSaque));
    }

}
