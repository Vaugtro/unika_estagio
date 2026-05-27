package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;
import java.util.List;

public class EnderecoRowActionPanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private EnderecoService enderecoService;

    public EnderecoRowActionPanel(String id, EnderecoResponse end, List<EnderecoResponse> allEnderecos,
                                   WebMarkupContainer enderecosContainer,
                                   List<EnderecoCreateFormModel> modalEnderecos,
                                   Form<?> modalForm,
                                   Label enderecoModalLabel) {
        super(id);

        Long endId = end.id();
        boolean isPrincipal = Boolean.TRUE.equals(end.principal());

        // --- Set as Principal button ---
        Label btnPrincipalLabel = new Label("principalLabel", isPrincipal ? "Sim" : "Não");
        btnPrincipalLabel.setOutputMarkupId(true);

        AjaxLink<Void> principalBtn = new AjaxLink<>("setAsPrincipalBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ErrorHandler.handleServiceCall(target, null, () -> {
                    enderecoService.setAsPrincipal(endId);
                    target.add(enderecosContainer);
                    ValidationFeedback.showToast(target, "success",
                            "Endereço definido como principal!");
                    JavaScriptUtils.reloadLucideIcons(target);
                });
            }
        };

        principalBtn.add(btnPrincipalLabel);
        principalBtn.add(new AttributeModifier("class",
                isPrincipal ? "btn btn-sm btn-success" : "btn btn-sm btn-outline-success"));
        principalBtn.add(new AttributeModifier("title",
                isPrincipal ? "Principal" : "Definir como principal"));

        if (isPrincipal) {
            principalBtn.add(new AttributeModifier("disabled", "disabled"));
        } else if (allEnderecos.size() <= 1) {
            principalBtn.setEnabled(false);
            principalBtn.add(new AttributeModifier("title", "Endereço único — já é principal"));
        }

        add(principalBtn);

        // --- Edit button ---
        add(new AjaxLink<Void>("editarBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEnderecos.clear();
                modalForm.getFeedbackMessages().clear();
                EnderecoCreateFormModel formModel = new EnderecoCreateFormModel();
                formModel.setId(end.id());
                formModel.setLogradouro(end.logradouro());
                formModel.setNumero(end.numero());
                formModel.setBairro(end.bairro());
                formModel.setCep(end.cep());
                formModel.setCidade(end.cidade());
                formModel.setEstado(end.estado());
                formModel.setTelefone(end.telefone());
                formModel.setPrincipal(Boolean.TRUE.equals(end.principal()));
                formModel.setComplemento(end.complemento());
                modalEnderecos.add(formModel);
                enderecoModalLabel.setDefaultModelObject("Editar Endereço");
                target.add(modalForm);
                target.appendJavaScript("abrirModalEndereco();");
            }
        });

        // --- Delete button ---
        add(new AjaxLink<Void>("excluirBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ErrorHandler.handleDelete(target, () -> {
                    enderecoService.delete(endId);
                    target.add(enderecosContainer);
                    JavaScriptUtils.reloadLucideIcons(target);
                });
            }
        });
    }
}
