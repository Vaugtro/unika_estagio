package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.validation.internal.CNPJValidator;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
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
import org.apache.wicket.validation.validator.StringValidator;

import org.springframework.dao.DataIntegrityViolationException;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        EnderecoCreateFormModel initialEndereco = new EnderecoCreateFormModel();
        initialEndereco.setPrincipal(true);
        formModel.getEnderecos().add(initialEndereco);

        Form<ClienteJuridicoCreateFormModel> form = new Form<>("form", new CompoundPropertyModel<>(formModel));
        form.setOutputMarkupId(true);

        TextField<String> cnpjField = new TextField<>("cnpj", String.class);
        cnpjField.setRequired(true);
        cnpjField.add(StringValidator.lengthBetween(ValidationConstants.CNPJ_LENGTH_FORMATTED_MIN, ValidationConstants.CNPJ_LENGTH_FORMATTED_MAX));
        cnpjField.add(new CNPJValidator());
        cnpjField.add(new AttributeModifier("placeholder", "00.000.000/0000-00"));
        cnpjField.add(new AttributeModifier("data-mask", "00.000.000/0000-00"));
        Label cnpjFeedback = ValidationFeedback.createFeedbackLabel("cnpjFeedback", cnpjField);
        ValidationFeedback.attachRealTimeValidation(cnpjField, cnpjFeedback);
        form.add(cnpjField);
        form.add(cnpjFeedback);

        TextField<String> razaoSocialField = new TextField<>("razaoSocial", String.class);
        razaoSocialField.setRequired(true);
        razaoSocialField.add(StringValidator.lengthBetween(ValidationConstants.RAZAO_SOCIAL_MIN, ValidationConstants.RAZAO_SOCIAL_MAX));
        razaoSocialField.add(new AttributeModifier("placeholder", "Razão Social"));
        Label razaoSocialFeedback = ValidationFeedback.createFeedbackLabel("razaoSocialFeedback", razaoSocialField);
        ValidationFeedback.attachRealTimeValidation(razaoSocialField, razaoSocialFeedback);
        form.add(razaoSocialField);
        form.add(razaoSocialFeedback);

        TextField<String> ieField = new TextField<>("inscricaoEstadual", String.class);
        ieField.setRequired(true);
        ieField.add(StringValidator.maximumLength(ValidationConstants.INSCRICAO_ESTADUAL_MAX));
        ieField.add(new AttributeModifier("placeholder", "Inscrição Estadual"));
        Label ieFeedback = ValidationFeedback.createFeedbackLabel("ieFeedback", ieField);
        ValidationFeedback.attachRealTimeValidation(ieField, ieFeedback);
        form.add(ieField);
        form.add(ieFeedback);

        TextField<String> emailField = new TextField<>("email", String.class);
        emailField.add(StringValidator.maximumLength(ValidationConstants.EMAIL_MAX));
        emailField.add(new AttributeModifier("placeholder", "E-mail"));
        Label emailFeedback = ValidationFeedback.createFeedbackLabel("emailFeedback", emailField);
        ValidationFeedback.attachRealTimeValidation(emailField, emailFeedback);
        form.add(emailField);
        form.add(emailFeedback);

        TextField<String> dataCriacaoEmpresaField = new TextField<>("dataCriacaoEmpresa", String.class);
        dataCriacaoEmpresaField.setRequired(true);
        dataCriacaoEmpresaField.add(new AttributeModifier("data-mask", "99/99/9999"));
        dataCriacaoEmpresaField.add(new AttributeModifier("placeholder", "DD/MM/YYYY"));
        Label dataCriacaoEmpresaFeedback = ValidationFeedback.createFeedbackLabel("dataCriacaoEmpresaFeedback", dataCriacaoEmpresaField);
        ValidationFeedback.attachRealTimeValidation(dataCriacaoEmpresaField, dataCriacaoEmpresaFeedback);
        form.add(dataCriacaoEmpresaField);
        form.add(dataCriacaoEmpresaFeedback);

        form.add(new EnderecoCreateTablePanel("enderecosContainer", formModel.getEnderecos()));

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteJuridicoCreateFormModel model = (ClienteJuridicoCreateFormModel) form.getModelObject();

                List<EnderecoCreateRequest> enderecosDTO = new ArrayList<>();
                for (EnderecoCreateFormModel endForm : model.getEnderecos()) {
                    String cepClean = endForm.getCep() != null ? endForm.getCep().replaceAll("\\D", "") : null;
                    String telefoneClean = endForm.getTelefone() != null ? endForm.getTelefone().replaceAll("\\D", "") : null;
                    enderecosDTO.add(new EnderecoCreateRequest(
                            endForm.getLogradouro(),
                            endForm.getNumero(),
                            cepClean,
                            endForm.getBairro(),
                            telefoneClean,
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
                        dataCriacao = LocalDate.parse(dataCriacaoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

                try {
                    clienteJuridicoService.create(dto);
                } catch (DataIntegrityViolationException e) {
                    ValidationFeedback.showToast(target, "error",
                            "Já existe um cliente com esses dados (CNPJ ou email duplicado).");
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
