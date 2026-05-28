package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.wicket.WicketTestBase;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EnderecoCreateTablePanelTest extends WicketTestBase {

    @Test
    @DisplayName("Panel renders without errors with one endereco row")
    void panelRendersWithoutErrors() {
        List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
        enderecos.add(new EnderecoCreateFormModel());

        tester.startComponentInPage(new EnderecoCreateTablePanel("panel", enderecos));
        tester.assertNoErrorMessage();
    }

    @Test
    @DisplayName("Panel renders without rows when enderecos list is empty")
    void renderEmptyList() {
        List<EnderecoCreateFormModel> enderecos = new ArrayList<>();

        tester.startComponentInPage(new EnderecoCreateTablePanel("panel", enderecos));
        tester.assertNoErrorMessage();
        tester.assertComponent("panel:addEndereco", AjaxLink.class);
    }

    @Test
    @DisplayName("ListView and all field components exist in endereco row")
    void rowComponentsExist() {
        List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
        enderecos.add(new EnderecoCreateFormModel());
        tester.startComponentInPage(new EnderecoCreateTablePanel("panel", enderecos));

        tester.assertComponent("panel:enderecosRow", ListView.class);

        // Input fields
        tester.assertComponent("panel:enderecosRow:0:logradouro", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:numero", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:bairro", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:cep", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:cidade", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:estado", DropDownChoice.class);
        tester.assertComponent("panel:enderecosRow:0:telefone", TextField.class);
        tester.assertComponent("panel:enderecosRow:0:complemento", TextField.class);

        // Feedback labels
        tester.assertComponent("panel:enderecosRow:0:logradouroFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:numeroFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:bairroFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:cepFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:cidadeFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:estadoFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:telefoneFeedback", Label.class);
        tester.assertComponent("panel:enderecosRow:0:complementoFeedback", Label.class);

        // Checkbox and action buttons
        tester.assertComponent("panel:enderecosRow:0:principal", CheckBox.class);
        tester.assertComponent("panel:enderecosRow:0:removeBtn", AjaxLink.class);
        tester.assertComponent("panel:addEndereco", AjaxLink.class);
    }

    @Test
    @DisplayName("Clicking addEndereco creates a new row")
    void addEnderecoCreatesRow() {
        List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
        enderecos.add(new EnderecoCreateFormModel());
        tester.startComponentInPage(new EnderecoCreateTablePanel("panel", enderecos));

        // Initially 1 row
        tester.assertComponent("panel:enderecosRow:0:logradouro", TextField.class);

        // Click add button
        tester.executeAjaxEvent("panel:addEndereco", "click");
        tester.assertNoErrorMessage();

        // Now 2 rows
        tester.assertComponent("panel:enderecosRow:1:logradouro", TextField.class);
    }

    @Test
    @DisplayName("Clicking removeBtn removes the row")
    void removeBtnRemovesRow() {
        List<EnderecoCreateFormModel> enderecos = new ArrayList<>();
        enderecos.add(new EnderecoCreateFormModel());
        enderecos.add(new EnderecoCreateFormModel());
        tester.startComponentInPage(new EnderecoCreateTablePanel("panel", enderecos));

        // Initially 2 rows
        tester.assertComponent("panel:enderecosRow:0:logradouro", TextField.class);
        tester.assertComponent("panel:enderecosRow:1:logradouro", TextField.class);

        // Click remove on first row
        tester.executeAjaxEvent("panel:enderecosRow:0:removeBtn", "click");
        tester.assertNoErrorMessage();

        // After removal: only 1 row remains (the former second item shifted to index 0)
        tester.assertComponent("panel:enderecosRow:0:logradouro", TextField.class);
    }
}
