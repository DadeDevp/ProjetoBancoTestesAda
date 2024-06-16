package br.ada.caixa.config;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClientePFResponseDto;
import br.ada.caixa.dto.response.ClientePJResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.TipoCliente;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public ModelMapper getModelMapper() {

        var modelMapper = new ModelMapper();

        modelMapper.typeMap(RegistrarClientePFRequestDto.class, Cliente.class)
                .addMapping(RegistrarClientePFRequestDto::getCpf, Cliente::setDocumento);

        modelMapper.typeMap(Cliente.class, ClientePFResponseDto.class)
                        .addMapping(Cliente::getDocumento, ClientePFResponseDto::setCpf);

        modelMapper.typeMap(RegistrarClientePJRequestDto.class, Cliente.class)
                .addMapping(RegistrarClientePJRequestDto::getCnpj, Cliente::setDocumento);

        modelMapper.typeMap(Cliente.class, ClientePJResponseDto.class)
                .addMapping(Cliente::getDocumento, ClientePJResponseDto::setCnpj);

        return modelMapper;
    }

}
