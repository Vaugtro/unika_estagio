package com.desafio.estagio.wicket.page.clientes;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoResponse;
import com.desafio.estagio.model.formatter.CNPJFormatter;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.WicketTestBase;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteJuridicoDetalhePageTest extends WicketTestBase {

    private ClienteJuridicoService mockService;

    @BeforeEach
    void setupMocks() {
        mockService = mock(ClienteJuridicoService.class);
        ClienteJuridicoResponse cliente = ClienteJuridicoResponse.builder()
                .id(1L)
                .cnpj("12345678000190")
                .razaoSocial("Empresa Exemplo LTDA")
                .inscricaoEstadual("123456789")
                .email("contato@empresa.com.br")
                .dataCriacaoEmpresa(LocalDate.of(2020, 1, 15))
                .estaAtivo(true)
                .build();
        when(mockService.findById(anyLong())).thenReturn(cliente);
        configureMock(ClienteJuridicoService.class, mockService);
    }

    @Test
    void renderPageWithValidId() {
        tester.startPage(ClienteJuridicoDetalhePage.class,
                new PageParameters().add("clienteId", 1L));
        tester.assertRenderedPage(ClienteJuridicoDetalhePage.class);
        tester.assertNoErrorMessage();

        tester.assertLabel("clienteId", "1");
        tester.assertLabel("clienteCnpj", CNPJFormatter.format("12345678000190"));
        tester.assertLabel("clienteRazaoSocial", "Empresa Exemplo LTDA");
        tester.assertLabel("clienteInscricaoEstadual", "123456789");
        tester.assertLabel("clienteEmail", "contato@empresa.com.br");
        tester.assertLabel("clienteDataCriacaoEmpresa",
                LocalDate.of(2020, 1, 15).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tester.assertLabel("clienteStatus", "Ativo");
    }

    @Test
    void verifyEnderecoListViewPanelRendered() {
        tester.startPage(ClienteJuridicoDetalhePage.class,
                new PageParameters().add("clienteId", 1L));
        tester.assertRenderedPage(ClienteJuridicoDetalhePage.class);
        tester.assertNoErrorMessage();

        tester.assertVisible("enderecoPanel");
    }

    @Test
    void renderInactiveClientShowsInactiveStatus() {
        when(mockService.findById(anyLong())).thenReturn(
                ClienteJuridicoResponse.builder()
                        .id(2L)
                        .cnpj("22345678000190")
                        .razaoSocial("Outra Empresa LTDA")
                        .inscricaoEstadual("987654321")
                        .email("outra@empresa.com.br")
                        .dataCriacaoEmpresa(LocalDate.of(2019, 6, 20))
                        .estaAtivo(false)
                        .build()
        );

        tester.startPage(ClienteJuridicoDetalhePage.class,
                new PageParameters().add("clienteId", 2L));
        tester.assertRenderedPage(ClienteJuridicoDetalhePage.class);

        tester.assertLabel("clienteStatus", "Inativo");
    }
}
