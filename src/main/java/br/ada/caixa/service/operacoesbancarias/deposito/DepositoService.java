package br.ada.caixa.service.operacoesbancarias.deposito;

import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ContaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepositoService {

    private final ContaRepository contaRepository;

    public DepositoService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void depositar(Long numeroConta, BigDecimal valor) {
        contaRepository.findByNumero(numeroConta)
                .ifPresentOrElse(conta -> {
                    conta.setSaldo(conta.getSaldo().add(valor));
                    contaRepository.save(conta);
                }, () -> { throw new ValidacaoException("Conta inválida!"); });
    }

}
