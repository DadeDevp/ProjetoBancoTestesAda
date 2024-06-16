package br.ada.caixa.service.operacoesbancarias.investimento;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InvestimentoService {

    @Qualifier("investimentoOperacaoPF")
    private final InvestimentoOperacao investimentoOperacaoPF;
    @Qualifier("investimentoOperacaoPJ")
    private final InvestimentoOperacao investimentoOperacaoPJ;

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public Conta investir(String documentoCliente, BigDecimal valor) {
        var clienteOptional = clienteRepository.findByDocumento(documentoCliente);
        var cliente = clienteOptional.orElseThrow(() -> new ValidacaoException("Cliente nao existe"));
        var contaInvestimento = getSingleContaInvestimento(cliente);
        getOperacaoTipoCliente(cliente).executar(contaInvestimento, valor);
        return contaRepository.save(contaInvestimento);
    }

    private Conta getSingleContaInvestimento(final Cliente cliente) {
        var contas =
                contaRepository
                        .findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO);

        if (contas.size() > 1) {
            throw new ValidacaoException("Cliente possui mais de uma conta investimento");
        }
        Conta contaInvestimento;
        if (contas.isEmpty()) {
            contaInvestimento = new Conta();
            contaInvestimento.setTipo(TipoConta.CONTA_INVESTIMENTO);
            contaInvestimento.setNumero(gerarNumeroDeContaUnico());
            contaInvestimento.setCliente(cliente);
            contaInvestimento.setSaldo(BigDecimal.ZERO);
        } else {
            contaInvestimento = contas.get(0);
        }
        return contaInvestimento;
    }

    private InvestimentoOperacao getOperacaoTipoCliente(Cliente cliente) {
        if (cliente.getTipo().equals(TipoCliente.PF)) {
            return investimentoOperacaoPF;
        } else if (cliente.getTipo().equals(TipoCliente.PJ)) {
            return investimentoOperacaoPJ;
        } else {
            throw new ValidacaoException("Operacao investimento nao encontrada!");
        }
    }

    private Long gerarNumeroDeContaUnico() {
        Long numero;
        Random random = new Random();
        do {
            numero = 1000000000L + random.nextLong(); // Gera um número de 10 dígitos
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }

}
