package com.desafio.estagio;

import com.desafio.estagio.dto.ClienteFisicoDTO;
import com.desafio.estagio.dto.EnderecoDTO;
import com.desafio.estagio.mapper.ClienteFisicoMapper;
import com.desafio.estagio.model.ClienteFisicoEntity;
import com.desafio.estagio.model.enums.TipoCliente;
import com.desafio.estagio.repository.ClienteFisicoRepository;
import com.desafio.estagio.service.ClienteFisicoServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteFisicoServiceImplTest {

    @Mock
    private ClienteFisicoRepository repository;

    @Mock
    private ClienteFisicoMapper mapper;

    @InjectMocks
    private ClienteFisicoServiceImpl service;

    private ClienteFisicoDTO.Request request;
    private ClienteFisicoEntity entity;
    private ClienteFisicoDTO.Response response;
    private EnderecoDTO.Request enderecoRequest;

    @BeforeEach
    void setUp() {
        enderecoRequest = new EnderecoDTO.Request(
                "Rua das Flores", 123L, "01234567", "Centro",
                "11912345678", "São Paulo", "SP", true, "Apto 42", null
        );

        request = new ClienteFisicoDTO.Request(
                TipoCliente.FISICA,
                "joao@email.com",
                "12345678901",
                "João Silva",
                "123456789",
                true,
                LocalDate.of(1990, 1, 15),
                List.of(enderecoRequest)
        );

        entity = new ClienteFisicoEntity();
        entity.setId(1L);
        entity.setNome("João Silva");
        entity.setCpf("12345678901");
        entity.setEmail("joao@email.com");

        response = new ClienteFisicoDTO.Response(
                1L,
                TipoCliente.FISICA,
                "joao@email.com",
                "123.456.789-01",
                "João Silva",
                "123456789",
                true,
                LocalDate.of(1990, 1, 15),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void create_ShouldReturnCreatedClient() {
        when(repository.existsByCpf(anyString())).thenReturn(false);
        when(mapper.toEntity(any(ClienteFisicoDTO.Request.class))).thenReturn(entity);
        when(repository.save(any(ClienteFisicoEntity.class))).thenReturn(entity);
        when(mapper.toResponse(any(ClienteFisicoEntity.class))).thenReturn(response);

        ClienteFisicoDTO.Response result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("João Silva");
        verify(repository, times(1)).save(any(ClienteFisicoEntity.class));
    }

    @Test
    void create_ShouldThrowException_WhenCpfAlreadyExists() {
        when(repository.existsByCpf(anyString())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("CPF já cadastrado");
    }

    @Test
    void getById_ShouldReturnClient_WhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ClienteFisicoDTO.Response result = service.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void findAll_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClienteFisicoEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toResponse(any(ClienteFisicoEntity.class))).thenReturn(response);

        Page<ClienteFisicoDTO.Response> result = service.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
    }

    @Test
    void update_ShouldReturnUpdatedClient() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(ClienteFisicoEntity.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ClienteFisicoDTO.Response result = service.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void delete_ShouldDeleteClient_WhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    void inativarCliente_ShouldSetActiveToFalse() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(ClienteFisicoEntity.class))).thenReturn(entity);

        service.inativarCliente(1L);

        assertThat(entity.getEstaAtivo()).isFalse();
        verify(repository, times(1)).save(entity);
    }

    @Test
    void ativarCliente_ShouldSetActiveToTrue() {
        entity.setEstaAtivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(ClienteFisicoEntity.class))).thenReturn(entity);

        service.ativarCliente(1L);

        assertThat(entity.getEstaAtivo()).isTrue();
        verify(repository, times(1)).save(entity);
    }
}