package br.ada.caixa.service;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ContaService service;

    private Cliente clientePF;
    private Cliente clientePJ;

    @Test
    void abrirContaPoupancaClienteExiste(){
        //given
        final Conta expected = mock(Conta.class);
        Cliente cliente = mock(Cliente.class);
        final String documentoCliente = "1234567890";

        given(clienteRepository.findByDocumento(documentoCliente)).willReturn(Optional.of(cliente));
        given(contaRepository.save(any(Conta.class))).willReturn(expected);
        given(expected.getTipo()).willReturn(TipoConta.CONTA_POUPANCA);
        given(expected.getSaldo()).willReturn(BigDecimal.ZERO);
        given(expected.getCliente()).willReturn(cliente);


        //when
        Conta actual = service.abrirContaPoupanca(documentoCliente);

        //then
        assertNotNull(actual, "A conta gerada não deve ser nula");
        assertEquals(BigDecimal.ZERO, actual.getSaldo(), "O saldo inicial deve ser ZERO");
        assertNotNull(actual.getNumero(), "O número da conta deve ser gerado");
        assertSame(expected,actual, "O objeto da conta gerada deve ser o mesmo que a mockada");
        assertEquals(TipoConta.CONTA_POUPANCA,actual.getTipo(), "a conta gerada deve ser do tipo poupança");
        assertSame(cliente, actual.getCliente(), "O cliente da conta deve ser o cliente fornecido");
        verify(clienteRepository).findByDocumento(documentoCliente);
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void abrirContaPoupancaClienteNaoExiste(){
        //given
        Cliente clientePJ = mock(Cliente.class);
        final String documentoCliente = "9999999";
        given(clienteRepository.findByDocumento(documentoCliente)).willReturn(Optional.empty());

        //when
        Executable action = () -> service.abrirContaPoupanca(documentoCliente);

        //then
        assertThrows(ValidacaoException.class, action, "Deve lançar uma exceção para cliente que nao existe");
    }
}
