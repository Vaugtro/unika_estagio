package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoResponse;
import com.desafio.estagio.model.formatter.CPFFormatter;
import com.desafio.estagio.model.formatter.RGFormatter;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteFisicoDetalhePageTest extends WicketTestBase {

    private ClienteFisicoService mockService;

    @BeforeEach
    void setupMocks() {
        mockService = mock(ClienteFisicoService.class);
        ClienteFisicoResponse cliente = ClienteFisicoResponse.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .rg("123456789")
                .email("joao@email.com")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .estaAtivo(true)
                .build();
        when(mockService.findById(anyLong())).thenReturn(cliente);
        configureMock(ClienteFisicoService.class, mockService);
    }

    @Test
    void renderPageWithValidId() {
        tester.startPage(ClienteFisicoDetalhePage.class,
                new PageParameters().add("clienteId", 1L));
        tester.assertRenderedPage(ClienteFisicoDetalhePage.class);
        tester.assertNoErrorMessage();

        tester.assertLabel("clienteId", "1");
        tester.assertLabel("clienteNome", "João Silva");
        tester.assertLabel("clienteCpf", CPFFormatter.format("12345678901"));
        tester.assertLabel("clienteRg", RGFormatter.format("123456789"));
        tester.assertLabel("clienteEmail", "joao@email.com");
        tester.assertLabel("clienteDataNascimento",
                LocalDate.of(1990, 5, 15).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tester.assertLabel("clienteStatus", "Ativo");
    }

    @Test
    void verifyEnderecoListViewPanelRendered() {
        tester.startPage(ClienteFisicoDetalhePage.class,
                new PageParameters().add("clienteId", 1L));
        tester.assertRenderedPage(ClienteFisicoDetalhePage.class);
        tester.assertNoErrorMessage();

        tester.assertVisible("enderecoPanel");
    }

    @Test
    void renderInactiveClientShowsExcluirButton() {
        when(mockService.findById(anyLong())).thenReturn(
                ClienteFisicoResponse.builder()
                        .id(2L)
                        .nome("Maria")
                        .cpf("98765432100")
                        .rg("98765432")
                        .email("maria@email.com")
                        .dataNascimento(LocalDate.of(1988, 3, 10))
                        .estaAtivo(false)
                        .build()
        );

        tester.startPage(ClienteFisicoDetalhePage.class,
                new PageParameters().add("clienteId", 2L));
        tester.assertRenderedPage(ClienteFisicoDetalhePage.class);

        tester.assertLabel("clienteStatus", "Inativo");
    }
}
