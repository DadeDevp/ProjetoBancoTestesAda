package br.ada.caixa.respository;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    List<Conta> findContasByClienteAndTipo(Cliente cliente, TipoConta tipoConta);

    Optional<Conta> findByNumero(Long numero);

    boolean existsByNumero(Long numero);

}
