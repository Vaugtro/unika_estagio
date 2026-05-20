package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ClienteJuridicoCreateModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteJuridicoService clienteJuridicoService;

    public ClienteJuridicoCreateModal(String id) {
        super(id);

        ClienteJuridicoCreateFormModel formModel = new ClienteJuridicoCreateFormModel();
        formModel.getEnderecos().add(new EnderecoCreateFormModel());

        Form<ClienteJuridicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(formModel));
        form.setOutputMarkupId(true);

        TextField<String> cnpjField = new TextField<>("cnpj", String.class);
        cnpjField.setRequired(true);
        cnpjField.add(StringValidator.lengthBetween(14, 18));
        cnpjField.add(new AttributeModifier("placeholder", "00.000.000/0000-00"));
        cnpjField.add(new AttributeModifier("data-mask", "00.000.000/0000-00"));
        Label cnpjFeedback = ValidationFeedback.createFeedbackLabel("cnpjFeedback", cnpjField);
        ValidationFeedback.attachRealTimeValidation(cnpjField, cnpjFeedback);
        form.add(cnpjField);
        form.add(cnpjFeedback);

        TextField<String> razaoSocialField = new TextField<>("razaoSocial", String.class);
        razaoSocialField.setRequired(true);
        razaoSocialField.add(StringValidator.lengthBetween(3, 150));
        razaoSocialField.add(new AttributeModifier("placeholder", "Razão Social"));
        Label razaoSocialFeedback = ValidationFeedback.createFeedbackLabel("razaoSocialFeedback", razaoSocialField);
        ValidationFeedback.attachRealTimeValidation(razaoSocialField, razaoSocialFeedback);
        form.add(razaoSocialField);
        form.add(razaoSocialFeedback);

        TextField<String> ieField = new TextField<>("inscricaoEstadual", String.class);
        ieField.setRequired(true);
        ieField.add(new AttributeModifier("placeholder", "Inscrição Estadual"));
        Label ieFeedback = ValidationFeedback.createFeedbackLabel("ieFeedback", ieField);
        ValidationFeedback.attachRealTimeValidation(ieField, ieFeedback);
        form.add(ieField);
        form.add(ieFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        TextField<String> dataCriacaoEmpresaField = new TextField<String>("dataCriacaoEmpresa") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("type", "date");
            }
        };
        dataCriacaoEmpresaField.setRequired(true);
        Label dataCriacaoEmpresaFeedback = ValidationFeedback.createFeedbackLabel("dataCriacaoEmpresaFeedback", dataCriacaoEmpresaField);
        ValidationFeedback.attachRealTimeValidation(dataCriacaoEmpresaField, dataCriacaoEmpresaFeedback);
        form.add(dataCriacaoEmpresaField);
        form.add(dataCriacaoEmpresaFeedback);

        WebMarkupContainer enderecosContainer = new WebMarkupContainer("enderecosContainer");
        enderecosContainer.setOutputMarkupId(true);
        form.add(enderecosContainer);

        List<EnderecoCreateFormModel> enderecos = formModel.getEnderecos();
        ListView<EnderecoCreateFormModel> enderecosView = new ListView<>("enderecosRow", enderecos) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<EnderecoCreateFormModel> item) {
                item.setModel(new CompoundPropertyModel<>(item.getModelObject()));

                TextField<String> logradouroField = new TextField<>("logradouro", String.class);
                logradouroField.setRequired(true);
                logradouroField.add(new AttributeModifier("placeholder", "Logradouro"));
                item.add(logradouroField);

                TextField<Long> numeroField = new TextField<>("numero", Long.class);
                numeroField.setRequired(true);
                numeroField.add(new AttributeModifier("placeholder", "Nº"));
                item.add(numeroField);

                TextField<String> bairroField = new TextField<>("bairro", String.class);
                bairroField.setRequired(true);
                bairroField.add(new AttributeModifier("placeholder", "Bairro"));
                item.add(bairroField);

                TextField<String> cepField = new TextField<>("cep", String.class);
                cepField.setRequired(true);
                cepField.add(new AttributeModifier("placeholder", "CEP"));
                cepField.add(new AttributeModifier("data-mask", "00000-000"));
                item.add(cepField);

                TextField<String> cidadeField = new TextField<>("cidade", String.class);
                cidadeField.setRequired(true);
                cidadeField.add(new AttributeModifier("placeholder", "Cidade"));
                item.add(cidadeField);

                TextField<String> estadoField = new TextField<>("estado", String.class);
                estadoField.setRequired(true);
                estadoField.add(StringValidator.exactLength(2));
                estadoField.add(new AttributeModifier("placeholder", "UF"));
                item.add(estadoField);

                TextField<String> telefoneField = new TextField<>("telefone", String.class);
                telefoneField.setRequired(true);
                telefoneField.add(new AttributeModifier("placeholder", "Telefone"));
                telefoneField.add(new AttributeModifier("data-mask", "(00) 00000-0000"));
                item.add(telefoneField);

                TextField<String> complementoField = new TextField<>("complemento", String.class);
                complementoField.add(new AttributeModifier("placeholder", "Complemento"));
                item.add(complementoField);

                CheckBox principalField = new CheckBox("principal");
                item.add(principalField);

                AjaxLink<Void> removeBtn = new AjaxLink<>("removeBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (enderecos.size() > 1) {
                            enderecos.remove(item.getIndex());
                            target.add(enderecosContainer);
                        }
                    }
                };
                item.add(removeBtn);
            }
        };
        enderecosContainer.add(enderecosView);

        AjaxLink<Void> addEnderecoBtn = new AjaxLink<>("addEndereco") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                enderecos.add(new EnderecoCreateFormModel());
                target.add(enderecosContainer);
            }
        };
        enderecosContainer.add(addEnderecoBtn);

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteJuridicoCreateFormModel model = (ClienteJuridicoCreateFormModel) form.getModelObject();

                List<EnderecoCreateRequest> enderecosDTO = new ArrayList<>();
                for (EnderecoCreateFormModel endForm : model.getEnderecos()) {
                    enderecosDTO.add(new EnderecoCreateRequest(
                            endForm.getLogradouro(),
                            endForm.getNumero(),
                            endForm.getCep(),
                            endForm.getBairro(),
                            endForm.getTelefone(),
                            endForm.getEstado(),
                            endForm.getCidade(),
                            endForm.getPrincipal() != null && endForm.getPrincipal(),
                            endForm.getComplemento(),
                            null
                    ));
                }

                String dataCriacaoStr = model.getDataCriacaoEmpresa();
                LocalDate dataCriacao = null;
                if (dataCriacaoStr != null && !dataCriacaoStr.isBlank()) {
                    try {
                        dataCriacao = LocalDate.parse(dataCriacaoStr);
                    } catch (DateTimeParseException e) {
                        ValidationFeedback.showToast(target, "error", "Data de criação inválida.");
                        return;
                    }
                }

                ClienteJuridicoCreateRequest dto = new ClienteJuridicoCreateRequest(
                        model.getCnpj(),
                        model.getRazaoSocial(),
                        model.getInscricaoEstadual(),
                        model.getEmail(),
                        dataCriacao,
                        enderecosDTO
                );

                clienteJuridicoService.create(dto);
                model.setCnpj(null);
                model.setRazaoSocial(null);
                model.setInscricaoEstadual(null);
                model.setEmail(null);
                model.setDataCriacaoEmpresa(null);
                model.getEnderecos().clear();
                model.getEnderecos().add(new EnderecoCreateFormModel());

                ValidationFeedback.showToast(target, "success", "Cliente criado com sucesso!");
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
                StringBuilder errors = new StringBuilder();
                form.getFeedbackMessages().messages(msg -> msg.getLevel() == org.apache.wicket.feedback.FeedbackMessage.ERROR)
                        .forEach(msg -> {
                            if (!errors.isEmpty()) errors.append("<br>");
                            errors.append(msg.getMessage());
                        });
                if (!errors.isEmpty()) {
                    ValidationFeedback.showToast(target, "error", errors.toString());
                }
            }
        });

        add(form);
    }
}
