package br.ada.caixa.service;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.service.cliente.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ClienteService service;

    @Test
    void registrarPFTest() {
        //given
        final RegistrarClientePFRequestDto clientePFRequestDto = mock(RegistrarClientePFRequestDto.class);
        final var cliente = mock(Cliente.class);
        final var documenteCliente = "12345";

        given(cliente.getDocumento()).willReturn(documenteCliente);
        given(modelMapper.map(clientePFRequestDto, Cliente.class)).willReturn(cliente);
        given(clienteRepository.save(cliente)).willReturn(cliente);

        //when
        RegistrarClienteResponseDto actual = service.registrarPF(clientePFRequestDto);

        //then
        verify(clienteRepository).save(cliente);
        assertNotNull(actual.getSaldoResponseDto());
        assertEquals(documenteCliente, actual.getDocumento());
        assertInstanceOf(RegistrarClienteResponseDto.class, actual);

    }

    @Test
    void registrarPJTest() {
        //given
        final RegistrarClientePJRequestDto clientePJRequestDto = mock(RegistrarClientePJRequestDto.class);
        final var cliente = mock(Cliente.class);
        final var documenteCliente = "12345";

        given(cliente.getDocumento()).willReturn(documenteCliente);
        given(modelMapper.map(clientePJRequestDto, Cliente.class)).willReturn(cliente);
        given(clienteRepository.save(cliente)).willReturn(cliente);

        //when
        RegistrarClienteResponseDto actual = service.registrarPJ(clientePJRequestDto);

        //then
        verify(clienteRepository).save(cliente);
        assertNotNull(actual.getSaldoResponseDto());
        assertEquals(documenteCliente, actual.getDocumento());
        assertInstanceOf(RegistrarClienteResponseDto.class, actual);
    }

    @Test
    void listarTodosTest() {
        //given
        Cliente cliente1 = mock(Cliente.class);

        given(cliente1.getTipo()).willReturn(TipoCliente.PF);

        ClienteResponseDto clienteDto = new ClienteResponseDto();
        clienteDto.setTipo(cliente1.getTipo().name());

        given(modelMapper.map(cliente1, ClienteResponseDto.class)).willReturn(clienteDto);

        List<Cliente> clientesList = List.of(cliente1);
        given(clienteRepository.findAll()).willReturn(clientesList);

        //when
        List<ClienteResponseDto> actual = service.listarTodos();

        //then
        verify(clienteRepository).findAll();
        assertNotNull(actual);
        assertInstanceOf(ArrayList.class, actual);
        assertEquals(clientesList.size(), actual.size());
        assertTrue(actual.stream().allMatch(item -> item instanceof ClienteResponseDto));

    }

    @Test
    void listarTodosPorTipoClienteTest() {
        //given
        Cliente clientePF = mock(Cliente.class);
        Cliente clientePJ = mock(Cliente.class);

        given(clientePF.getTipo()).willReturn(TipoCliente.PF);
        given(clientePJ.getTipo()).willReturn(TipoCliente.PJ);

        ClienteResponseDto clienteDto1 = new ClienteResponseDto();
        ClienteResponseDto clienteDto2 = new ClienteResponseDto();

        given(modelMapper.map(clientePF, ClienteResponseDto.class)).willReturn(clienteDto1);
        given(modelMapper.map(clientePJ, ClienteResponseDto.class)).willReturn(clienteDto2);

        List<Cliente> clientesList = List.of(clientePF, clientePJ);
        given(clienteRepository.findAllByTipo(TipoCliente.PF)).willReturn(List.of(clientePF));
        given(clienteRepository.findAllByTipo(TipoCliente.PJ)).willReturn(List.of(clientePJ));

        //when
        List<ClienteResponseDto> actualPF = service.listarTodos(TipoCliente.PF);
        List<ClienteResponseDto> actualPJ = service.listarTodos(TipoCliente.PJ);

        //then
        verify(clienteRepository).findAllByTipo(TipoCliente.PF);
        assertNotNull(actualPF);
        assertInstanceOf(ArrayList.class, actualPF);
        assertEquals(1, actualPF.size());
        assertTrue(actualPF.stream().allMatch(item -> item instanceof ClienteResponseDto));

        verify(clienteRepository).findAllByTipo(TipoCliente.PJ);
        assertNotNull(actualPF);
        assertInstanceOf(ArrayList.class, actualPJ);
        assertEquals(1, actualPF.size());
        assertTrue(actualPJ.stream().allMatch(item -> item instanceof ClienteResponseDto));
    }
}