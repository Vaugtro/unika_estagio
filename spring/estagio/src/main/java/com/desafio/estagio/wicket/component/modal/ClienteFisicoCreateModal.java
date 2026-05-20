package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
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

public class ClienteFisicoCreateModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoCreateModal(String id) {
        super(id);

        ClienteFisicoCreateFormModel formModel = new ClienteFisicoCreateFormModel();
        formModel.getEnderecos().add(new EnderecoCreateFormModel());

        Form<ClienteFisicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(formModel));
        form.setOutputMarkupId(true);

        TextField<String> cpfField = new TextField<>("cpf", String.class);
        cpfField.setRequired(true);
        cpfField.add(StringValidator.lengthBetween(11, 14));
        cpfField.add(new AttributeModifier("placeholder", "000.000.000-00"));
        cpfField.add(new AttributeModifier("data-mask", "000.000.000-00"));
        Label cpfFeedback = ValidationFeedback.createFeedbackLabel("cpfFeedback", cpfField);
        ValidationFeedback.attachRealTimeValidation(cpfField, cpfFeedback);
        form.add(cpfField);
        form.add(cpfFeedback);

        TextField<String> nomeField = new TextField<>("nome", String.class);
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(3, 150));
        nomeField.add(new AttributeModifier("placeholder", "Nome completo"));
        Label nomeFeedback = ValidationFeedback.createFeedbackLabel("nomeFeedback", nomeField);
        ValidationFeedback.attachRealTimeValidation(nomeField, nomeFeedback);
        form.add(nomeField);
        form.add(nomeFeedback);

        TextField<String> rgField = new TextField<>("rg", String.class);
        rgField.setRequired(true);
        rgField.add(StringValidator.lengthBetween(8, 9));
        rgField.add(new AttributeModifier("placeholder", "RG"));
        Label rgFeedback = ValidationFeedback.createFeedbackLabel("rgFeedback", rgField);
        ValidationFeedback.attachRealTimeValidation(rgField, rgFeedback);
        form.add(rgField);
        form.add(rgFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        TextField<String> dataNascimentoField = new TextField<String>("dataNascimento") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("type", "date");
            }
        };
        dataNascimentoField.setRequired(true);
        Label dataNascimentoFeedback = ValidationFeedback.createFeedbackLabel("dataNascimentoFeedback", dataNascimentoField);
        ValidationFeedback.attachRealTimeValidation(dataNascimentoField, dataNascimentoFeedback);
        form.add(dataNascimentoField);
        form.add(dataNascimentoFeedback);

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
                ClienteFisicoCreateFormModel model = (ClienteFisicoCreateFormModel) form.getModelObject();

                List<EnderecoWithinClienteCreateRequest> enderecosDTO = new ArrayList<>();
                for (EnderecoCreateFormModel endForm : model.getEnderecos()) {
                    enderecosDTO.add(new EnderecoWithinClienteCreateRequest(
                            endForm.getLogradouro(),
                            endForm.getNumero(),
                            endForm.getCep(),
                            endForm.getBairro(),
                            endForm.getTelefone(),
                            endForm.getEstado(),
                            endForm.getCidade(),
                            endForm.getPrincipal() != null && endForm.getPrincipal(),
                            endForm.getComplemento()
                    ));
                }

                String dataNascimentoStr = model.getDataNascimento();
                LocalDate dataNascimento = null;
                if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                    try {
                        dataNascimento = LocalDate.parse(dataNascimentoStr);
                    } catch (DateTimeParseException e) {
                        ValidationFeedback.showToast(target, "error", "Data de nascimento inválida.");
                        return;
                    }
                }

                String rgClean = model.getRg() != null ? model.getRg().replaceAll("\\D", "") : null;

                ClienteFisicoCreateRequest dto = new ClienteFisicoCreateRequest(
                        model.getCpf(),
                        model.getNome(),
                        rgClean,
                        model.getEmail(),
                        dataNascimento,
                        enderecosDTO
                );

                clienteFisicoService.create(dto);
                model.setCpf(null);
                model.setNome(null);
                model.setRg(null);
                model.setEmail(null);
                model.setDataNascimento(null);
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
