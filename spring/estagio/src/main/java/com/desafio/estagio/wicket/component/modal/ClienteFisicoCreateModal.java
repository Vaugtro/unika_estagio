package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoCreateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.validation.internal.CPFValidator;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.builder.FormFieldBundle;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel;
import com.desafio.estagio.wicket.mapper.ClienteFisicoDtoMapper;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.io.Serial;
import java.time.format.DateTimeParseException;

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

        FormFieldBundle cpfBundle = FormFieldBuilder.create(String.class)
                .id("cpf").required().placeholder("000.000.000-00").dataMask("000.000.000-00")
                .minLength(ValidationConstants.CPF_LENGTH_FORMATTED_MIN)
                .maxLength(ValidationConstants.CPF_LENGTH_FORMATTED_MAX)
                .validator(new CPFValidator()).feedbackLabel("cpfFeedback").realTimeValidation().build();
        form.add(cpfBundle.field(), cpfBundle.feedbackLabel());

        // Nome field
        FormFieldBundle nomeBundle = FormFieldBuilder.create(String.class)
                .id("nome").required().placeholder("Nome completo")
                .minLength(ValidationConstants.NOME_MIN).maxLength(ValidationConstants.NOME_MAX)
                .feedbackLabel("nomeFeedback").realTimeValidation().build();
        form.add(nomeBundle.field(), nomeBundle.feedbackLabel());

        // RG field — custom digit-count validator
        FormFieldBundle rgBundle = FormFieldBuilder.create(String.class)
                .id("rg").required()
                .placeholder("RG").dataMask("99.999.999-9")
                .pattern("^\\d{1,2}\\.?\\d{1,3}\\.?\\d{1,3}-?\\d$")
                .minLength(ValidationConstants.RG_LENGTH_MIN)
                .maxLength(ValidationConstants.RG_LENGTH_MAX)
                .validator(new IValidator<String>() {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void validate(IValidatable<String> validatable) {
                        String value = validatable.getValue();
                        if (value != null) {
                            long digitCount = value.chars().filter(Character::isDigit).count();
                            if (digitCount < ValidationConstants.RG_LENGTH_MIN || digitCount > ValidationConstants.RG_LENGTH_MAX)
                                validatable.error(new ValidationError("RG deve ter entre " + ValidationConstants.RG_LENGTH_MIN + " e " + ValidationConstants.RG_LENGTH_MAX + " dígitos."));
                        }
                    }
                }).feedbackLabel("rgFeedback").realTimeValidation().build();
        form.add(rgBundle.field(), rgBundle.feedbackLabel());

        // Email field
        FormFieldBundle emailBundle = FormFieldBuilder.create(String.class)
                .id("email").placeholder("E-mail")
                .maxLength(ValidationConstants.EMAIL_MAX)
                .feedbackLabel("emailFeedback").realTimeValidation().build();
        form.add(emailBundle.field(), emailBundle.feedbackLabel());

        // Data de Nascimento field
        FormFieldBundle dataNascimentoBundle = FormFieldBuilder.create(String.class)
                .id("dataNascimento").required().placeholder("DD/MM/YYYY").dataMask("99/99/9999")
                .feedbackLabel("dataNascimentoFeedback").realTimeValidation().build();
        form.add(dataNascimentoBundle.field(), dataNascimentoBundle.feedbackLabel());

        form.add(new EnderecoCreateTablePanel("enderecosContainer", formModel.getEnderecos()));

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteFisicoCreateFormModel model = (ClienteFisicoCreateFormModel) form.getModelObject();
                ClienteFisicoCreateRequest dto;
                try {
                    dto = ClienteFisicoDtoMapper.toCreateRequest(model);
                } catch (DateTimeParseException e) {
                    ValidationFeedback.showToast(target, "error", "Data de nascimento inválida.");
                    return;
                }
                Boolean success = ErrorHandler.handleServiceCall(() -> {
                    clienteFisicoService.create(dto);
                    return true;
                }, target);
                if (Boolean.TRUE.equals(success)) setResponsePage(getPage());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });
        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JavaScriptUtils.getMaskLibraryReference()));
        response.render(JavaScriptHeaderItem.forReference(JavaScriptUtils.getMaskInitReference()));
    }
}
