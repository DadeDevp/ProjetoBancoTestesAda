package br.ada.caixa.controller;

import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.InvestimentoRequestDto;
import br.ada.caixa.dto.request.SaqueRequestDto;
import br.ada.caixa.dto.request.TransferenciaRequestDto;
import br.ada.caixa.dto.response.SaldoResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoOperacaoPF;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoOperacaoPJ;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import br.ada.caixa.service.operacoesbancarias.saldo.SaldoService;
import br.ada.caixa.service.operacoesbancarias.saque.OperacaoSaqueSaldoPJ;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import br.ada.caixa.service.operacoesbancarias.transferencia.TransferenciaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OperacoesBancariasControllerITTest {

    private String url;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @SpyBean
    private ContaRepository contaRepository;

    @SpyBean
    private SaqueService saqueService;

    @SpyBean
    private DepositoService depositoService;

    @SpyBean
    private TransferenciaService transferenciaService;

    @SpyBean
    private InvestimentoService investimentoService;

    @SpyBean
    private SaldoService saldoService;

    @SpyBean
    private ContaService contaService;

    @SpyBean
    private OperacaoSaqueSaldoPJ operacaoSaqueSaldoPJ;

    @Autowired
    private ClienteRepository clienteRepository;


    @BeforeEach
    void setUp() {
        //SET URL
        url = "http://localhost:" + port + "/operacoes";

        //CRIAR CLIENTES
        var cliente1 = Cliente.builder()
                .documento("123456889")
                .nome("Teste 1")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente2 = Cliente.builder()
                .documento("1234567891")
                .nome("Teste 2")
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PJ)
                .createdAt(LocalDate.now())
                .build();

        clienteRepository.saveAllAndFlush(List.of(cliente1, cliente2));

        //CRIAR CONTAS
        var contaCorrente1 = Conta.builder()
                .numero(1L)
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente1.getDocumento()).get())
                .cliente(cliente1)
                .createdAt(LocalDate.now())
                .build();

        var contaCorrente2 = Conta.builder()
                .numero(2L)
                .saldo(BigDecimal.TEN)
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente2.getDocumento()).get())
                .cliente(cliente2)
                .createdAt(LocalDate.now())
                .build();

        contaRepository.saveAllAndFlush(List.of(contaCorrente1, contaCorrente2));

    }

    @AfterEach
    void tearDown() {
        contaRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
    }

    @Test
    void postDepositarTest() {
        //given
        final var valor = BigDecimal.valueOf(100.50);
        final var numeroConta = 1L;
        DepositoRequestDto depositoRequestDto =
                DepositoRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/depositar", depositoRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo())).isZero();
        assertEquals(0, valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo()));
        verify(depositoService).depositar(any(), eq(valor));
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void postSacarClienteComSaldoSuficiente() {
        //given
        final var valor = BigDecimal.valueOf(5.50);
        final var numeroConta = 2L;
        SaqueRequestDto saqueRequestDto =
                SaqueRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);

        //then
        verify(saqueService).sacar(any(), eq(valor));
        verify(operacaoSaqueSaldoPJ).executar(any(Conta.class), eq(valor));
        verify(contaRepository).save(any(Conta.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postSacarClienteSemSaldoSuficiente() {
        //given
        final var valor = BigDecimal.valueOf(1000);
        final var numeroConta = 2L;
        SaqueRequestDto saqueRequestDto =
                SaqueRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);

        //then
        verify(saqueService).sacar(any(), eq(valor));
        verify(operacaoSaqueSaldoPJ).executar(any(Conta.class), eq(valor));
        verify(contaRepository, never()).save(any(Conta.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postTransferenciaClienteComSaldoSuficiente() {
        //given
        final var valor = BigDecimal.valueOf(5.50);
        final var numeroContaOrigem = 2L;
        final var numeroContaDestino = 1L;

        TransferenciaRequestDto transferenciaRequestDto =
                TransferenciaRequestDto.builder()
                        .numeroContaOrigem(numeroContaOrigem)
                        .numeroContaDestino(numeroContaDestino)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/transferir", transferenciaRequestDto, Void.class);

        //then
        verify(transferenciaService).transferir(any(), any(), eq(valor));
        verify(saqueService).sacar(any(), eq(valor));
        verify(depositoService).depositar(any(), eq(valor));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postTransferenciaClienteSemSaldoSuficiente() {
        //given
        final var valor = BigDecimal.valueOf(5.50);
        final var numeroContaOrigem = 1L;
        final var numeroContaDestino = 2L;

        TransferenciaRequestDto transferenciaRequestDto =
                TransferenciaRequestDto.builder()
                        .numeroContaOrigem(numeroContaOrigem)
                        .numeroContaDestino(numeroContaDestino)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/transferir", transferenciaRequestDto, Void.class);

        //then
        verify(transferenciaService).transferir(any(), any(), eq(valor));
        verify(saqueService).sacar(any(), eq(valor));
        verify(depositoService, never()).depositar(any(), eq(valor));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getConsultarSaldo() {
        //given
        final var numeroConta = 2L;
        final var saldo = contaRepository.findByNumero(numeroConta).get().getSaldo();

        //when
        var response = restTemplate.getForEntity(url + "/saldo/{numeroConta}", SaldoResponseDto.class, numeroConta);
        var dtoResponse = response.getBody();

        //then
        verify(saldoService).consultarSaldo(any());
        assertInstanceOf(SaldoResponseDto.class, response.getBody());
        assertEquals(numeroConta, dtoResponse.getNumeroConta());
        assertEquals(saldo, dtoResponse.getSaldo());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postInvestirPF() {
        //given
        final var cpf = "123456889";
        final var cliente = clienteRepository.findByDocumento(cpf).get();
        final var valor = BigDecimal.valueOf(100.50);
        final var valorPosInvestimento = valor.multiply(InvestimentoOperacaoPF.RENDIMENTO_INVESTIMENTO);

        InvestimentoRequestDto investimentoRequestDto =
                InvestimentoRequestDto.builder()
                        .documentoCliente(cpf)
                        .valor(valor)
                        .build();

        //when
        var response = restTemplate.postForEntity(url + "/investimento", investimentoRequestDto, SaldoResponseDto.class);
        var responseDto = response.getBody();

        //then
        verify(investimentoService).investir(any(),any());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseDto.getNumeroConta()).isNotNull();
        assertThat(responseDto.getNumeroConta()).isEqualTo(contaRepository.findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO).get(0).getNumero());
        assertInstanceOf(SaldoResponseDto.class, response.getBody());
        assertThat(responseDto.getSaldo()).isEqualTo(valorPosInvestimento);

    }

    @Test
    void postInvestirPJ() {
        //given
        final var cpf = "1234567891";
        final var cliente = clienteRepository.findByDocumento(cpf).get();
        final var valor = BigDecimal.valueOf(100.50);
        final var valorPosInvestimento = valor.multiply(InvestimentoOperacaoPJ.RENDIMENTO_INVESTIMENTO);

        InvestimentoRequestDto investimentoRequestDto =
                InvestimentoRequestDto.builder()
                        .documentoCliente(cpf)
                        .valor(valor)
                        .build();

        //when
        var response = restTemplate.postForEntity(url + "/investimento", investimentoRequestDto, SaldoResponseDto.class);
        var responseDto = response.getBody();

        //then
        verify(investimentoService).investir(any(),any());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseDto.getNumeroConta()).isNotNull();
        assertThat(responseDto.getNumeroConta()).isEqualTo(contaRepository.findContasByClienteAndTipo(cliente, TipoConta.CONTA_INVESTIMENTO).get(0).getNumero());
        assertInstanceOf(SaldoResponseDto.class, response.getBody());
        assertThat(responseDto.getSaldo()).isEqualTo(valorPosInvestimento);

    }

    @Test
    void postAbrirContaPoupancaClienteExistente() {
        //given
        final var cpf = "123456889";
        final var cliente = clienteRepository.findByDocumento(cpf).get();

        //when
        var response = restTemplate.postForEntity(url + "/abrir-conta-poupanca/{cpf}", null, SaldoResponseDto.class, cpf);

        var responseDto = response.getBody();

        //then
        verify(contaService).abrirContaPoupanca(any());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseDto.getNumeroConta()).isNotNull();
        assertThat(responseDto.getNumeroConta()).isEqualTo(contaRepository.findContasByClienteAndTipo(cliente, TipoConta.CONTA_POUPANCA).get(0).getNumero());
        assertInstanceOf(SaldoResponseDto.class, response.getBody());
        assertThat(responseDto.getSaldo()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void postAbrirContaPoupancaClienteNaoExiste() {
        // given
        final var cpf = "999999999999999";

        // when
        ResponseEntity<Map> response = restTemplate.postForEntity(url + "/abrir-conta-poupanca/{cpf}", null, Map.class, cpf);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("error")).isEqualTo("Cliente nao encontrado com o CPF informado!");
    }
}