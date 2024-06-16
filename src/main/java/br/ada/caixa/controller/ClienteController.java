package br.ada.caixa.controller;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.dto.response.SaldoResponseDto;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.service.cliente.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listarTodos(
            @RequestParam(required = false) String tipoCliente) {
        if (Objects.isNull(tipoCliente)) {
            return ResponseEntity.ok(clienteService.listarTodos());
        } else {
            return ResponseEntity.ok(clienteService.listarTodos(TipoCliente.valueOf(tipoCliente)));
        }
    }

    @PostMapping("/pf")
    public ResponseEntity<RegistrarClienteResponseDto> registrarPF(@RequestBody RegistrarClientePFRequestDto clienteDto) {
        var response = clienteService.registrarPF(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pj")
    public ResponseEntity<RegistrarClienteResponseDto> registrarPJ(@RequestBody RegistrarClientePJRequestDto clienteDto) {
        var response = clienteService.registrarPJ(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
