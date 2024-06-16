package br.ada.caixa.respository;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByDocumento(String documento);

    List<Cliente> findAllByTipo(TipoCliente tipoCliente);

}
