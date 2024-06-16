package br.ada.caixa.service.operacoesbancarias.investimento;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestimentoServiceTest {

    @Mock
    private InvestimentoOperacao investimentoOperacaoPF;
    @Mock
    private InvestimentoOperacao investimentoOperacaoPJ;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private InvestimentoService service;

    @Test
    @Disabled
    @DisplayName("dado um cliente PF com uma conta investimento unica, " +
            "quando investido um valor, " +
            "entao a conta é atualizada no banco de dados")
    void investirContaPFEncontradaTest() {
        // given
        final Conta expected = mock(Conta.class);
        final Cliente cliente = mock(Cliente.class);

        final Random random = new Random();
        final String documentoCliente = "some document";
        final BigDecimal valor =
                BigDecimal
                        .valueOf(random.nextDouble())
                        .setScale(2, RoundingMode.HALF_UP);


        given(clienteRepository.findByDocumento(documentoCliente))
                .willReturn(Optional.of(cliente));

        given(contaRepository
                      .findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO))
                .willReturn(List.of(expected));
        given(contaRepository.save(expected))
                .willReturn(expected);

        given(cliente.getTipo())
                .willReturn(TipoCliente.PF);

        // when
        Conta actual = service.investir(documentoCliente, valor);

        // then
        assertEquals(expected, actual);
        verify(investimentoOperacaoPF, atLeast(1)).executar(actual, valor);
        verify(contaRepository, atLeast(1)).save(actual);
    }

    @Test
    @DisplayName("Tentar investir mas o cliente não é encontrado")
    void investirClienteNaoEncontradoTest() {
        // given
        final var expected = ValidacaoException.class;
        final Random random = new Random();
        final String documentoCliente = "some document";
        final BigDecimal valor =
                BigDecimal
                        .valueOf(random.nextDouble())
                        .setScale(2, RoundingMode.HALF_UP);

        given(clienteRepository.findByDocumento(documentoCliente))
                .willReturn(Optional.empty());

        // when
        // then
        assertThrows(expected, () -> service.investir(documentoCliente, valor) );
        verify(investimentoOperacaoPF, atLeast(0)).executar(any(Conta.class), eq(valor));
        verify(contaRepository, atLeast(0)).save(any());
    }

    @Test
    @DisplayName("Tentar investir mas o cliente tem mais de uma conta investimento")
    void investirClienteMaisDeUmaContaTest() {
        // given
        final Cliente cliente = mock(Cliente.class);
        final var expected = ValidacaoException.class;

        final Conta contaInvestimento1 = mock(Conta.class);
        final Conta contaInvestimento2 = mock(Conta.class);

        contaInvestimento1.setTipo(TipoConta.CONTA_INVESTIMENTO);
        contaInvestimento2.setTipo(TipoConta.CONTA_INVESTIMENTO);

        List<Conta> contas = Arrays.asList(contaInvestimento1,contaInvestimento2);

        final Random random = new Random();
        final String documentoCliente = "some document";
        final BigDecimal valor =
                BigDecimal
                        .valueOf(random.nextDouble())
                        .setScale(2, RoundingMode.HALF_UP);

        given(clienteRepository.findByDocumento(documentoCliente))
                .willReturn(Optional.of(cliente));

        given(contaRepository
                .findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO))
                .willReturn(contas);

        // when
        // then
        assertThrows(expected, () -> service.investir(documentoCliente, valor) );
        verify(investimentoOperacaoPF, atLeast(0)).executar(any(), eq(valor));
        verify(contaRepository, atLeast(0)).save(any());
    }

    @Test
    @DisplayName("dado um cliente Pj com uma conta investimento unica, " +
            "quando investido um valor, " +
            "entao a conta é atualizada no banco de dados")
    void investirContaPJEncontradaTest() {
        // given
        final Conta expected = mock(Conta.class);
        final Cliente cliente = mock(Cliente.class);

        final Random random = new Random();
        final String documentoCliente = "some document";
        final BigDecimal valor =
                BigDecimal
                        .valueOf(random.nextDouble())
                        .setScale(2, RoundingMode.HALF_UP);


        given(clienteRepository.findByDocumento(documentoCliente))
                .willReturn(Optional.of(cliente));

        given(contaRepository
                .findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO))
                .willReturn(List.of(expected));
        given(contaRepository.save(expected))
                .willReturn(expected);

        given(cliente.getTipo())
                .willReturn(TipoCliente.PJ);

        // when
        Conta actual = service.investir(documentoCliente, valor);

        // then
        assertEquals(expected, actual);
        verify(investimentoOperacaoPJ, atMost(1)).executar(actual, valor);
        verify(contaRepository, atMost(1)).save(actual);
    }



    /*
    1 - Variacao qdo o cliente nao é encontrado
    2 - Testar qdo o cliente tem + de 1 conta investimento ?
    3 - Testar qdo o cliente nao possui conta investimento.
    4 - E se o cliente for PJ?
     */
}