package br.ada.caixa.service.conta;

import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public Conta abrirContaPoupanca(String cpf) {
        //Regra: cliente PJ nao pode ter conta poupanca
        return clienteRepository.findByDocumento(cpf)
                .map(clientePF -> {
                    var contaPoupanca = new Conta();
                    contaPoupanca.setTipo(TipoConta.CONTA_POUPANCA);
                    contaPoupanca.setNumero(gerarNumeroDeContaUnico());
                    contaPoupanca.setCliente(clientePF);
                    contaPoupanca.setSaldo(BigDecimal.ZERO);
                    return contaRepository.save(contaPoupanca);
                })
                .orElseThrow(() -> new ValidacaoException("Cliente nao encontrado com o CPF informado!"));
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
