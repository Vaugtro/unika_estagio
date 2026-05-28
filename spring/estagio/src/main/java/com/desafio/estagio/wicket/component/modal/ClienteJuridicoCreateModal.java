package com.desafio.estagio.wicket.component.modal;

import com.desafio.estagio.dto.clientejuridico.ClienteJuridicoCreateRequest;
import com.desafio.estagio.service.ClienteJuridicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.validation.internal.CNPJValidator;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.builder.FormFieldBundle;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.shared.EnderecoCreateTablePanel;
import com.desafio.estagio.wicket.mapper.ClienteJuridicoDtoMapper;
import com.desafio.estagio.wicket.model.ClienteJuridicoCreateFormModel;
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

import java.io.Serial;

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

        FormFieldBundle cnpjBundle = FormFieldBuilder.create(String.class)
                .id("cnpj").required()
                .minLength(ValidationConstants.CNPJ_LENGTH_FORMATTED_MIN)
                .maxLength(ValidationConstants.CNPJ_LENGTH_FORMATTED_MAX)
                .validator(new CNPJValidator())
                .placeholder("00.000.000/0000-00")
                .dataMask("00.000.000/0000-00")
                .feedbackLabel("cnpjFeedback")
                .realTimeValidation()
                .build();
        form.add(cnpjBundle.field(), cnpjBundle.feedbackLabel());

        FormFieldBundle razaoSocialBundle = FormFieldBuilder.create(String.class)
                .id("razaoSocial").required()
                .minLength(ValidationConstants.RAZAO_SOCIAL_MIN)
                .maxLength(ValidationConstants.RAZAO_SOCIAL_MAX)
                .placeholder("Razão Social")
                .feedbackLabel("razaoSocialFeedback")
                .realTimeValidation()
                .build();
        form.add(razaoSocialBundle.field(), razaoSocialBundle.feedbackLabel());

        FormFieldBundle ieBundle = FormFieldBuilder.create(String.class)
                .id("inscricaoEstadual").required()
                .maxLength(ValidationConstants.INSCRICAO_ESTADUAL_MAX)
                .placeholder("Inscrição Estadual")
                .feedbackLabel("ieFeedback")
                .realTimeValidation()
                .build();
        form.add(ieBundle.field(), ieBundle.feedbackLabel());

        FormFieldBundle emailBundle = FormFieldBuilder.create(String.class)
                .id("email")
                .maxLength(ValidationConstants.EMAIL_MAX)
                .placeholder("E-mail")
                .feedbackLabel("emailFeedback")
                .realTimeValidation()
                .build();
        form.add(emailBundle.field(), emailBundle.feedbackLabel());

        FormFieldBundle dataCriacaoBundle = FormFieldBuilder.create(String.class)
                .id("dataCriacaoEmpresa").required()
                .dataMask("99/99/9999")
                .placeholder("DD/MM/YYYY")
                .feedbackLabel("dataCriacaoEmpresaFeedback")
                .realTimeValidation()
                .build();
        form.add(dataCriacaoBundle.field(), dataCriacaoBundle.feedbackLabel());

        form.add(new EnderecoCreateTablePanel("enderecosContainer", formModel.getEnderecos()));

        form.add(new AjaxButton("submit", form) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteJuridicoCreateFormModel model = (ClienteJuridicoCreateFormModel) form.getModelObject();
                ClienteJuridicoCreateRequest dto = ClienteJuridicoDtoMapper.toCreateRequest(model);
                Boolean success = ErrorHandler.handleServiceCall(() -> {
                    clienteJuridicoService.create(dto);
                    return Boolean.TRUE;
                }, target);
                if (Boolean.TRUE.equals(success)) {
                    setResponsePage(getPage());
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
        response.render(JavaScriptHeaderItem.forReference(JavaScriptUtils.getMaskLibraryReference()));
        response.render(JavaScriptHeaderItem.forReference(JavaScriptUtils.getMaskInitReference()));
    }
}
