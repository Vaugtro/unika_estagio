package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.wicket.WicketTestBase;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnderecoCreateTablePanelTest extends WicketTestBase {

    private static final String PANEL_PATH = "panel";

    @Test
    void rendersPanelWithNoErrors() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        tester.assertNoErrorMessage();
        tester.assertComponent(PANEL_PATH, EnderecoCreateTablePanel.class);
    }

    @Test
    void rendersAllWicketIdsInEnderecoRow() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        String rowPath = PANEL_PATH + ":enderecosRow:0";

        // Text fields
        tester.assertComponent(rowPath + ":logradouro", TextField.class);
        tester.assertComponent(rowPath + ":numero", TextField.class);
        tester.assertComponent(rowPath + ":bairro", TextField.class);
        tester.assertComponent(rowPath + ":cep", TextField.class);
        tester.assertComponent(rowPath + ":cidade", TextField.class);
        tester.assertComponent(rowPath + ":estado", TextField.class);
        tester.assertComponent(rowPath + ":telefone", TextField.class);
        tester.assertComponent(rowPath + ":complemento", TextField.class);

        // Feedback labels
        tester.assertComponent(rowPath + ":logradouroFeedback", Label.class);
        tester.assertComponent(rowPath + ":numeroFeedback", Label.class);
        tester.assertComponent(rowPath + ":bairroFeedback", Label.class);
        tester.assertComponent(rowPath + ":cepFeedback", Label.class);
        tester.assertComponent(rowPath + ":cidadeFeedback", Label.class);
        tester.assertComponent(rowPath + ":estadoFeedback", Label.class);
        tester.assertComponent(rowPath + ":telefoneFeedback", Label.class);
        tester.assertComponent(rowPath + ":complementoFeedback", Label.class);

        // Checkbox
        tester.assertComponent(rowPath + ":principal", CheckBox.class);

        // Remove button
        tester.assertComponent(rowPath + ":removeBtn", AjaxLink.class);
    }

    @Test
    void rendersAddEnderecoButton() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        tester.assertComponent(PANEL_PATH + ":addEndereco", AjaxLink.class);
    }

    @Test
    void startsWithOneRowWhenOneModelProvided() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        var listView = (ListView<?>) tester.getComponentFromLastRenderedPage(
                PANEL_PATH + ":enderecosRow");
        assertEquals(1, listView.size());
    }

    @Test
    void addsRowWhenAddEnderecoClicked() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        assertEquals(1, enderecos.size());

        tester.executeAjaxEvent(PANEL_PATH + ":addEndereco", "click");

        assertEquals(2, enderecos.size());
        var listView = (ListView<?>) tester.getComponentFromLastRenderedPage(
                PANEL_PATH + ":enderecosRow");
        assertEquals(2, listView.size());
    }

    @Test
    void removesRowWhenRemoveBtnClickedWithMultipleRows() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        assertEquals(2, enderecos.size());

        tester.executeAjaxEvent(PANEL_PATH + ":enderecosRow:0:removeBtn", "click");

        assertEquals(1, enderecos.size());
        var listView = (ListView<?>) tester.getComponentFromLastRenderedPage(
                PANEL_PATH + ":enderecosRow");
        assertEquals(1, listView.size());
    }

    @Test
    void doesNotRemoveLastRemainingRow() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        assertEquals(1, enderecos.size());

        tester.executeAjaxEvent(PANEL_PATH + ":enderecosRow:0:removeBtn", "click");

        // Since enderecos.size() == 1, the remove guard prevents removal
        assertEquals(1, enderecos.size());
        var listView = (ListView<?>) tester.getComponentFromLastRenderedPage(
                PANEL_PATH + ":enderecosRow");
        assertEquals(1, listView.size());
    }

    @Test
    void rendersMultipleRowsWhenMultipleModelsProvided() {
        var enderecos = new ArrayList<EnderecoCreateFormModel>();
        enderecos.add(new EnderecoCreateFormModel());
        enderecos.add(new EnderecoCreateFormModel());
        enderecos.add(new EnderecoCreateFormModel());

        var panel = new EnderecoCreateTablePanel(PANEL_PATH, enderecos);
        tester.startComponentInPage(panel);

        var listView = (ListView<?>) tester.getComponentFromLastRenderedPage(
                PANEL_PATH + ":enderecosRow");
        assertEquals(3, listView.size());

        // Verify second and third rows also have fields
        tester.assertComponent(PANEL_PATH + ":enderecosRow:1:logradouro", TextField.class);
        tester.assertComponent(PANEL_PATH + ":enderecosRow:2:logradouro", TextField.class);
    }
}
