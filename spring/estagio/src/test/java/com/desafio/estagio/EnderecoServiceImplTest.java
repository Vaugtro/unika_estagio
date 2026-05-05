package com.desafio.estagio;

import com.desafio.estagio.mvc.model.dto.EnderecoDTO;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.ClienteFisico;
import com.desafio.estagio.mvc.model.entity.ClienteFisicoEntity;
import com.desafio.estagio.mvc.model.entity.EnderecoEntity;
import com.desafio.estagio.mvc.model.mapper.EnderecoMapper;
import com.desafio.estagio.mvc.service.EnderecoServiceImpl;
import com.desafio.estagio.repository.ClienteRepository;
import com.desafio.estagio.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceImplTest {

    @Mock
    private EnderecoRepository<EnderecoEntity> enderecoRepository;

    @Mock
    private ClienteRepository<ClienteFisicoEntity> clienteRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EnderecoServiceImpl enderecoService;

    private EnderecoDTO.Request request;
    private EnderecoDTO.Response response;
    private EnderecoEntity entity;
    private ClienteFisicoEntity cliente;

    @BeforeEach
    void setUp() {
        cliente = new ClienteFisicoEntity();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        request = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        entity = new EnderecoEntity();
        entity.setId(1L);
        entity.setLogradouro("Rua das Flores");
        entity.setNumero(123L);
        entity.setCliente(cliente);
        entity.setPrincipal(true);

        response = new EnderecoDTO.Response(
                1L, "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", "Apto 42",
                true, null, null, null
        );
    }

    @Test
    void createForCliente_ShouldCreateAddress_WhenValidRequest() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(enderecoMapper.toEntity(request)).thenReturn(entity);
        when(enderecoRepository.save(any(EnderecoEntity.class))).thenReturn(entity);
        when(enderecoMapper.toResponse(entity)).thenReturn(response);

        EnderecoDTO.Response result = enderecoService.createForCliente(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.logradouro()).isEqualTo("Rua das Flores");
        verify(enderecoRepository).save(any(EnderecoEntity.class));
    }

    @Test
    void createForCliente_ShouldThrowException_WhenClienteNotFound() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.createForCliente(999L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void findById_ShouldReturnAddress_WhenExists() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enderecoMapper.toResponse(entity)).thenReturn(response);

        EnderecoDTO.Response result = enderecoService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Endereço não encontrado");
    }

    @Test
    void findAllByClienteId_ShouldReturnListOfAddresses() {
        List<EnderecoEntity> entities = List.of(entity);
        when(enderecoRepository.findByClienteId(1L)).thenReturn(entities);
        when(enderecoMapper.toResponse(entity)).thenReturn(response);

        List<EnderecoDTO.Response> results = enderecoService.findAllByClienteId(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(1L);
    }

    @Test
    void update_ShouldUpdateAddress_WhenValidRequest() {
        EnderecoDTO.Request updateRequest = new EnderecoDTO.Request(
                "Rua Nova", 456L, "87654321", "Jardins",
                "11987654321", "São Paulo", "SP", false, "Sala 10", null
        );

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enderecoRepository.save(any(EnderecoEntity.class))).thenReturn(entity);

        EnderecoDTO.Response updatedResponse = new EnderecoDTO.Response(
                1L, "Rua Nova", 456L, "87654321", "Jardins",
                "11987654321", "São Paulo", "SP", "Sala 10",
                false, null, null, null
        );
        when(enderecoMapper.toResponse(entity)).thenReturn(updatedResponse);

        EnderecoDTO.Response result = enderecoService.update(1L, updateRequest);

        assertThat(result.logradouro()).isEqualTo("Rua Nova");
        assertThat(result.numero()).isEqualTo(456L);
        verify(enderecoMapper).updateEntityFromDTO(eq(updateRequest), eq(entity));
    }

    @Test
    void setAsPrincipal_ShouldSetAddressAsPrincipal() {
        EnderecoEntity otherAddress = new EnderecoEntity();
        otherAddress.setId(2L);
        otherAddress.setPrincipal(false);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enderecoRepository.findByClienteId(1L)).thenReturn(List.of(entity, otherAddress));
        when(enderecoRepository.save(any(EnderecoEntity.class))).thenReturn(entity);
        when(enderecoMapper.toResponse(entity)).thenReturn(response);

        EnderecoDTO.Response result = enderecoService.setAsPrincipal(1L);

        assertThat(result.principal()).isTrue();
        verify(enderecoRepository, atLeast(1)).save(any(EnderecoEntity.class));
    }

    @Test
    void delete_ShouldRemoveAddress_WhenExists() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(enderecoRepository).deleteById(1L);

        enderecoService.delete(1L);

        verify(enderecoRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldSetNewPrincipal_WhenDeletingPrincipalAddress() {
        entity.setPrincipal(true);
        EnderecoEntity otherAddress = new EnderecoEntity();
        otherAddress.setId(2L);
        otherAddress.setPrincipal(false);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enderecoRepository.findByClienteId(1L)).thenReturn(List.of(entity, otherAddress));
        doNothing().when(enderecoRepository).deleteById(1L);

        enderecoService.delete(1L);

        assertThat(otherAddress.getPrincipal()).isTrue();
        verify(enderecoRepository, times(1)).save(otherAddress);
    }
}