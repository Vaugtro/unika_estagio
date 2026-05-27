package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.builder.FormFieldBundle;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.ClienteFisicoUpdateFormModel;
import com.desafio.estagio.wicket.page.clientes.ClienteFisicoDetalhePage;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import java.io.Serial;

public class ClienteFisicoRowUpdateForm extends Form<ClienteFisicoUpdateFormModel> {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Behavior VALIDATION_STYLE = new Behavior() {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            if (!component.getFeedbackMessages().isEmpty()) {
                String cls = tag.getAttribute("class");
                tag.put("class", cls != null ? cls + " is-invalid" : "is-invalid");
            }
        }
    };

    @Getter
    private final Item<ClienteFisicoListResponse> parentItem;
    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public ClienteFisicoRowUpdateForm(String id, ClienteFisicoListResponse cliente, Item<ClienteFisicoListResponse> parentItem) {
        super(id);
        this.parentItem = parentItem;
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        IModel<ClienteFisicoUpdateFormModel> detachedModel = new LoadableDetachableModel<>() {
            @Override
            protected ClienteFisicoUpdateFormModel load() {
                return new ClienteFisicoUpdateFormModel(clienteFisicoService.findById(cliente.id()));
            }
        };

        setModel(new CompoundPropertyModel<>(detachedModel));

        add(new Label("id"));
        add(new Label("cpf"));

        FormFieldBundle nomeField = FormFieldBuilder.create(String.class)
                .id("nome")
                .required()
                .minLength(ValidationConstants.NOME_MIN)
                .maxLength(ValidationConstants.NOME_MAX)
                .pattern("[^\\d]+")
                .validationStyle(VALIDATION_STYLE)
                .build();
        add(nomeField.field());

        FormFieldBundle emailField = FormFieldBuilder.create(String.class)
                .id("email")
                .maxLength(ValidationConstants.EMAIL_MAX)
                .validator(EmailAddressValidator.getInstance())
                .validationStyle(VALIDATION_STYLE)
                .build();
        add(emailField.field());

        IModel<String> statusTextModel = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                return model != null && model.getEstaAtivo() ? "Ativo" : "Inativo";
            }
        };

        Label btnStatus = new Label("status", statusTextModel);
        btnStatus.setOutputMarkupId(true);

        AjaxLink<Void> toggleBtn = getToggleBtn();
        toggleBtn.add(btnStatus);

        toggleBtn.add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "btn btn-sm btn-success" : "btn btn-sm btn-danger";
            }
        }));

        toggleBtn.add(new AttributeModifier("title", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean isAtivo = model != null && model.getEstaAtivo();
                return isAtivo ? "Inativar" : "Ativar";
            }
        }));

        add(toggleBtn);

        BookmarkablePageLink<Void> detalhesLink = new BookmarkablePageLink<>("detalhesBtn", ClienteFisicoDetalhePage.class,
                new PageParameters().set("clienteId", cliente.id()));
        detalhesLink.add(new AttributeModifier("class", "btn btn-sm btn-outline-info"));
        add(detalhesLink);

        AjaxButton editButton = getEditButton();
        add(editButton);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        String oldName = tag.getName();
        tag.setName("form");
        super.onComponentTag(tag);
        tag.setName(oldName);
    }

    private AjaxButton getEditButton() {
        AjaxButton editButton = new AjaxButton("editarBtn", this) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ClienteFisicoUpdateFormModel model = (ClienteFisicoUpdateFormModel) form.getModelObject();
                if (model == null) {
                    ValidationFeedback.showToast(target, "error", "Modelo de dados não encontrado");
                    return;
                }
                ClienteFisicoUpdateRequest updateRequest = new ClienteFisicoUpdateRequest(
                        model.getNome(), model.getEmail(), model.getEstaAtivo()
                );
                Boolean success = ErrorHandler.handleServiceCall(() -> {
                    clienteFisicoService.update(model.getId(), updateRequest);
                    return true;
                }, target);
                if (Boolean.TRUE.equals(success)) {
                    form.setDefaultModelObject(new ClienteFisicoUpdateFormModel(
                            clienteFisicoService.findById(model.getId())));
                    ValidationFeedback.showToast(target, "success", "Cliente atualizado com sucesso!");
                    target.add(form);
                    JavaScriptUtils.createIconsSafe(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };

        editButton.setOutputMarkupId(true);
        editButton.setDefaultFormProcessing(true);
        return editButton;
    }

    private AjaxLink<Void> getToggleBtn() {
        return new AjaxLink<>("statusBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ClienteFisicoUpdateFormModel model = ClienteFisicoRowUpdateForm.this.getModelObject();
                boolean newStatus = !model.getEstaAtivo();
                if (model.getEstaAtivo()) {
                    clienteFisicoService.inactivate(model.getId());
                } else {
                    clienteFisicoService.activate(model.getId());
                }
                model.setEstaAtivo(newStatus);
                target.add(ClienteFisicoRowUpdateForm.this);
                JavaScriptUtils.createIconsSafe(target);
            }
        };
    }
}
