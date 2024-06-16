package br.ada.caixa.service.cliente;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.dto.response.SaldoResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public RegistrarClienteResponseDto registrarPF(RegistrarClientePFRequestDto clienteDto) {
        final var cliente = modelMapper.map(clienteDto, Cliente.class);
        cliente.setTipo(TipoCliente.PF);
        cliente.setStatus(StatusCliente.ATIVO);
        return registrar(cliente);
    }

    public RegistrarClienteResponseDto registrarPJ(RegistrarClientePJRequestDto clienteDto) {
        final var cliente = modelMapper.map(clienteDto, Cliente.class);
        cliente.setTipo(TipoCliente.PJ);
        cliente.setStatus(StatusCliente.ATIVO);
        return registrar(cliente);
    }

    private RegistrarClienteResponseDto registrar(Cliente cliente) {
        cliente = clienteRepository.save(cliente);
        final var conta = criarConta(cliente);

        final SaldoResponseDto saldoResponseDto = SaldoResponseDto.builder()
                .saldo(conta.getSaldo())
                .numeroConta(conta.getNumero())
                .build();

        return RegistrarClienteResponseDto.builder()
                .documento(cliente.getDocumento())
                .saldoResponseDto(saldoResponseDto)
                .build();
    }

    private static Conta criarConta(final Cliente cliente) {
        final var contaCorrente = new Conta();
        contaCorrente.setCliente(cliente);
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setTipo(TipoConta.CONTA_CORRENTE);
        return contaCorrente;
    }

    public List<ClienteResponseDto> listarTodos(TipoCliente tipoCliente) {
        List<Cliente> clientes = clienteRepository.findAllByTipo(tipoCliente);
        return clientes.stream().map(cliente -> {
            ClienteResponseDto clienteResponseDto = modelMapper.map(cliente, ClienteResponseDto.class);
            clienteResponseDto.setTipo(cliente.getTipo().name());
            return clienteResponseDto;
        }).collect(Collectors.toList());
    }

    public List<ClienteResponseDto> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream().map(cliente -> {
            ClienteResponseDto clienteResponseDto = modelMapper.map(cliente, ClienteResponseDto.class);
            clienteResponseDto.setTipo(cliente.getTipo().name());
            return clienteResponseDto;
        }).collect(Collectors.toList());
    }
}
