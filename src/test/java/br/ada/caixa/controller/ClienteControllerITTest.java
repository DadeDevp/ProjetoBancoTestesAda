package br.ada.caixa.controller;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteControllerITTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository repository;

    private String url;


    @BeforeEach
    void setup() {

        url = "http://localhost:" + port + "/clientes";

        var cliente1 = Cliente.builder()
                .documento("123456889")
                .nome("Teste 1")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente2 = Cliente.builder()
                .documento("123456789")
                .nome("Teste 2")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente3 = Cliente.builder()
                .documento("123456779")
                .nome("Teste 3")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        repository.saveAll(List.of(cliente1, cliente2, cliente3));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAllInBatch();

    }

    @Test
    void getAllTest() {
        // given
        long expected = repository.count();

        // when
        var response =
                restTemplate.getForEntity(url, ClienteResponseDto[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().length);
    }

    @Test
    void postPFTest() {
        // given
        final var dataNascimento = LocalDate.now().minusYears(10);
        final var cpf = "99999999";
        final var nome = "Teste Post";
        final RegistrarClientePFRequestDto clienteDto =
                RegistrarClientePFRequestDto.builder()
                        .cpf(cpf)
                        .nome(nome)
                        .dataNascimento(dataNascimento)
                        .build();

        // when
        var response =
                restTemplate.postForEntity(url + "/pf",
                        clienteDto,
                        RegistrarClienteResponseDto.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final var dtoResponse = response.getBody();
        assertEquals(cpf, dtoResponse.getDocumento());

        final var entity = repository.findByDocumento(cpf);
        assertEquals(nome, entity.get().getNome());
    }

    @Test
    void postPJTest() {
        // given
        final var cnpj = "1234";
        final var nomeFantasia = "Teste Post PJ";
        final var razaoSocial = "Teste Post PJ";
        final RegistrarClientePJRequestDto clienteDto =
                RegistrarClientePJRequestDto.builder()
                        .cnpj(cnpj)
                        .nomeFantasia(nomeFantasia)
                        .razaoSocial(razaoSocial)
                        .build();

        // when
        var response =
                restTemplate.postForEntity(url + "/pj",
                        clienteDto,
                        RegistrarClienteResponseDto.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final var dtoResponse = response.getBody();
        assertEquals(cnpj, dtoResponse.getDocumento());

        final var entity = repository.findByDocumento(cnpj);
        assertEquals(nomeFantasia, entity.get().getNome());
    }
}