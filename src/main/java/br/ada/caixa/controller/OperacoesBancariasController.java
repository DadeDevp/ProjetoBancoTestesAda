package br.ada.caixa.controller;

import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.InvestimentoRequestDto;
import br.ada.caixa.dto.request.SaqueRequestDto;
import br.ada.caixa.dto.request.TransferenciaRequestDto;
import br.ada.caixa.dto.response.SaldoResponseDto;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import br.ada.caixa.service.operacoesbancarias.saldo.SaldoService;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import br.ada.caixa.service.operacoesbancarias.transferencia.TransferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operacoes")
@RequiredArgsConstructor
public class OperacoesBancariasController {

    private final DepositoService depositoService;
    private final SaqueService saqueService;
    private final TransferenciaService transferenciaService;
    private final SaldoService saldoService;
    private final InvestimentoService investimentoService;
    private final ContaService contaService;

    @PostMapping("/depositar")
    public ResponseEntity<Void> depositar(@RequestBody DepositoRequestDto depositoRequestDto) {
        depositoService.depositar(depositoRequestDto.getNumeroConta(), depositoRequestDto.getValor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sacar")
    public ResponseEntity<Void> sacar(@RequestBody SaqueRequestDto saqueRequestDto) {
        saqueService.sacar(saqueRequestDto.getNumeroConta(), saqueRequestDto.getValor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferencia(@RequestBody TransferenciaRequestDto transferenciaRequestDto) {
        transferenciaService.transferir(transferenciaRequestDto.getNumeroContaOrigem(),
                transferenciaRequestDto.getNumeroContaDestino(),
                transferenciaRequestDto.getValor());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saldo/{numeroConta}")
    public ResponseEntity<SaldoResponseDto> consultarSaldo(@PathVariable Long numeroConta) {
        var saldoResponseDto = new SaldoResponseDto();
        saldoResponseDto.setNumeroConta(numeroConta);
        saldoResponseDto.setSaldo(saldoService.consultarSaldo(numeroConta));
        return ResponseEntity.status(HttpStatus.OK).body(saldoResponseDto);
    }

    @PostMapping("/investimento")
    public ResponseEntity<SaldoResponseDto> investir
            (@RequestBody InvestimentoRequestDto investimentoRequestDto) {
        var contaInvestimento = investimentoService.investir(investimentoRequestDto.getDocumentoCliente(), investimentoRequestDto.getValor());

        var saldoResponseDto = new SaldoResponseDto();
        saldoResponseDto.setNumeroConta(contaInvestimento.getNumero());
        saldoResponseDto.setSaldo(contaInvestimento.getSaldo());
        return ResponseEntity.status(HttpStatus.OK).body(saldoResponseDto);
    }

    //Regra: cliente PJ nao pode ter conta poupanca
    @PostMapping("/abrir-conta-poupanca/{cpf}")
    public ResponseEntity<SaldoResponseDto> abrirContaPoupanca(@PathVariable String cpf) {
        var contaPoupanca = contaService.abrirContaPoupanca(cpf);

        var saldoResponseDto = new SaldoResponseDto();
        saldoResponseDto.setNumeroConta(contaPoupanca.getNumero());
        saldoResponseDto.setSaldo(contaPoupanca.getSaldo());
        return ResponseEntity.status(HttpStatus.OK).body(saldoResponseDto);
    }

}
