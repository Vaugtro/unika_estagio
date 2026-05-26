package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoWithinClienteCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.validation.internal.CPFValidator;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import org.springframework.dao.DataIntegrityViolationException;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        EnderecoCreateFormModel initialEndereco = new EnderecoCreateFormModel();
        initialEndereco.setPrincipal(true);
        formModel.getEnderecos().add(initialEndereco);

        Form<ClienteFisicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(formModel));
        form.setOutputMarkupId(true);

        TextField<String> cpfField = new TextField<>("cpf", String.class);
        cpfField.setRequired(true);
        cpfField.add(StringValidator.lengthBetween(ValidationConstants.CPF_LENGTH_FORMATTED_MIN, ValidationConstants.CPF_LENGTH_FORMATTED_MAX));
        cpfField.add(new CPFValidator());
        cpfField.add(new AttributeModifier("placeholder", "000.000.000-00"));
        cpfField.add(new AttributeModifier("data-mask", "000.000.000-00"));
        Label cpfFeedback = ValidationFeedback.createFeedbackLabel("cpfFeedback", cpfField);
        ValidationFeedback.attachRealTimeValidation(cpfField, cpfFeedback);
        form.add(cpfField);
        form.add(cpfFeedback);

        TextField<String> nomeField = new TextField<>("nome", String.class);
        nomeField.setRequired(true);
        nomeField.add(StringValidator.lengthBetween(ValidationConstants.NOME_MIN, ValidationConstants.NOME_MAX));
        nomeField.add(new AttributeModifier("placeholder", "Nome completo"));
        Label nomeFeedback = ValidationFeedback.createFeedbackLabel("nomeFeedback", nomeField);
        ValidationFeedback.attachRealTimeValidation(nomeField, nomeFeedback);
        form.add(nomeField);
        form.add(nomeFeedback);

        TextField<String> rgField = new TextField<>("rg", String.class);
        rgField.setRequired(true);
        rgField.add(StringValidator.lengthBetween(ValidationConstants.RG_LENGTH_MIN, ValidationConstants.RG_LENGTH_MAX));
        rgField.add(new AttributeModifier("placeholder", "RG"));
        rgField.add(new AttributeModifier("data-mask", "99.999.999-9"));
        rgField.add(new PatternValidator("^\\d{1,2}\\.?\\d{1,3}\\.?\\d{1,3}-?\\d$"));
        Label rgFeedback = ValidationFeedback.createFeedbackLabel("rgFeedback", rgField);
        ValidationFeedback.attachRealTimeValidation(rgField, rgFeedback);
        form.add(rgField);
        form.add(rgFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(StringValidator.maximumLength(ValidationConstants.EMAIL_MAX));
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        TextField<String> dataNascimentoField = new TextField<>("dataNascimento", String.class);
        dataNascimentoField.setRequired(true);
        dataNascimentoField.add(new AttributeModifier("data-mask", "99/99/9999"));
        dataNascimentoField.add(new AttributeModifier("placeholder", "DD/MM/YYYY"));
        Label dataNascimentoFeedback = ValidationFeedback.createFeedbackLabel("dataNascimentoFeedback", dataNascimentoField);
        ValidationFeedback.attachRealTimeValidation(dataNascimentoField, dataNascimentoFeedback);
        form.add(dataNascimentoField);
        form.add(dataNascimentoFeedback);

        form.add(new EnderecoCreateTablePanel("enderecosContainer", formModel.getEnderecos()));

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteFisicoCreateFormModel model = (ClienteFisicoCreateFormModel) form.getModelObject();

                List<EnderecoWithinClienteCreateRequest> enderecosDTO = new ArrayList<>();
                for (EnderecoCreateFormModel endForm : model.getEnderecos()) {
                    String cepClean = endForm.getCep() != null ? endForm.getCep().replaceAll("\\D", "") : null;
                    String telefoneClean = endForm.getTelefone() != null ? endForm.getTelefone().replaceAll("\\D", "") : null;
                    enderecosDTO.add(new EnderecoWithinClienteCreateRequest(
                            endForm.getLogradouro(),
                            endForm.getNumero(),
                            cepClean,
                            endForm.getBairro(),
                            telefoneClean,
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
                        dataNascimento = LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

                try {
                    clienteFisicoService.create(dto);
                } catch (DataIntegrityViolationException e) {
                    ValidationFeedback.showToast(target, "error",
                            "Já existe um cliente com esses dados (CPF ou email duplicado).");
                    return;
                } catch (RuntimeException e) {
                    ValidationFeedback.showToast(target, "error",
                            "Erro inesperado ao criar cliente. Tente novamente.");
                    return;
                }
                setResponsePage(getPage());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });

        add(form);
    }
}
