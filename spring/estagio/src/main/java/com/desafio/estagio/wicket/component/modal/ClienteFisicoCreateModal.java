package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.validation.internal.CPFValidator;
import com.desafio.estagio.wicket.builder.AttributeModifierBuilder;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel;
import com.desafio.estagio.wicket.mapper.ClienteFisicoDtoMapper;
import com.desafio.estagio.wicket.model.ClienteFisicoCreateFormModel;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import wicket.js.WicketJsAnchor;

import java.io.Serial;

public class ClienteFisicoCreateModal extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final ResourceReference MASKS_JS = new JavaScriptResourceReference(WicketJsAnchor.class, "masks.js");

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

        var cpfBundle = FormFieldBuilder.create(String.class).id("cpf").required()
                .validator(StringValidator.lengthBetween(ValidationConstants.CPF_LENGTH_FORMATTED_MIN, ValidationConstants.CPF_LENGTH_FORMATTED_MAX))
                .validator(new CPFValidator()).placeholder("000.000.000-00")
                .attribute("data-mask", "000.000.000-00").feedbackLabel("cpfFeedback").realTimeValidation().build();
        cpfBundle.field().add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                AttributeModifierBuilder.on(cpfBundle.field()).dataAttr("mask", "000.000.000-00").build();
            }
        });
        form.add(cpfBundle.field());
        form.add(cpfBundle.feedbackLabel());

        var nomeBundle = FormFieldBuilder.create(String.class).id("nome").required()
                .validator(StringValidator.lengthBetween(ValidationConstants.NOME_MIN, ValidationConstants.NOME_MAX))
                .placeholder("Nome completo").feedbackLabel("nomeFeedback").realTimeValidation().build();
        form.add(nomeBundle.field());
        form.add(nomeBundle.feedbackLabel());

        var rgBundle = FormFieldBuilder.create(String.class).id("rg").required()
                .validator(new IValidator<String>() {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void validate(IValidatable<String> validatable) {
                        String value = validatable.getValue();
                        if (value != null) {
                            long digitCount = value.chars().filter(Character::isDigit).count();
                            if (digitCount < ValidationConstants.RG_LENGTH_MIN || digitCount > ValidationConstants.RG_LENGTH_MAX) {
                                validatable.error(new ValidationError("RG deve ter entre " + ValidationConstants.RG_LENGTH_MIN + " e " + ValidationConstants.RG_LENGTH_MAX + " dígitos."));
                            }
                        }
                    }
                })
                .validator(new PatternValidator("^\\d{1,2}\\.?\\d{1,3}\\.?\\d{1,3}-?\\d$"))
                .placeholder("RG").attribute("data-mask", "99.999.999-9")
                .feedbackLabel("rgFeedback").realTimeValidation().build();
        form.add(rgBundle.field());
        form.add(rgBundle.feedbackLabel());

        var emailBundle = FormFieldBuilder.create(String.class).id("email")
                .validator(StringValidator.maximumLength(ValidationConstants.EMAIL_MAX))
                .placeholder("E-mail").feedbackLabel("emailFeedback").realTimeValidation().build();
        form.add(emailBundle.field());
        form.add(emailBundle.feedbackLabel());

        var dataNascimentoBundle = FormFieldBuilder.create(String.class).id("dataNascimento").required()
                .placeholder("DD/MM/YYYY").attribute("data-mask", "99/99/9999")
                .feedbackLabel("dataNascimentoFeedback").realTimeValidation().build();
        form.add(dataNascimentoBundle.field());
        form.add(dataNascimentoBundle.feedbackLabel());

        form.add(new EnderecoCreateTablePanel("enderecosContainer", formModel.getEnderecos()));

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteFisicoCreateFormModel model = (ClienteFisicoCreateFormModel) form.getModelObject();
                try {
                    var dto = ClienteFisicoDtoMapper.toCreateRequest(model);
                    ErrorHandler.handleServiceCall(target, form, () -> {
                        clienteFisicoService.create(dto);
                        setResponsePage(getPage());
                    });
                } catch (IllegalArgumentException e) {
                    ValidationFeedback.showToast(target, "error", "Data de nascimento inválida.");
                }
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
        response.render(JavaScriptHeaderItem.forReference(MASKS_JS));
    }
}
