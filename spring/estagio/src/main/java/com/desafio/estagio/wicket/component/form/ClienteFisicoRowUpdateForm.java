package com.desafio.estagio.wicket.component.form;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.validation.ValidationConstants;
import com.desafio.estagio.wicket.builder.FormFieldBuilder;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.mapper.ClienteFisicoDtoMapper;
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
import org.apache.wicket.Component;
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
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;

public class ClienteFisicoRowUpdateForm extends Form<ClienteFisicoUpdateFormModel> {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final ValidationStyleBehavior VALIDATION_STYLE_INSTANCE = new ValidationStyleBehavior();
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

        var nomeBundle = FormFieldBuilder.create(String.class)
            .id("nome")
            .required()
            .validator(StringValidator.lengthBetween(ValidationConstants.NOME_MIN, ValidationConstants.NOME_MAX))
            .validator(new PatternValidator("[^\\d]+"))
            .build();
        nomeBundle.field().add(VALIDATION_STYLE_INSTANCE);
        nomeBundle.field().setOutputMarkupId(true);
        add(nomeBundle.field());

        var emailBundle = FormFieldBuilder.create(String.class)
            .id("email")
            .validator(EmailAddressValidator.getInstance())
            .validator(StringValidator.maximumLength(ValidationConstants.EMAIL_MAX))
            .build();
        emailBundle.field().add(VALIDATION_STYLE_INSTANCE);
        emailBundle.field().setOutputMarkupId(true);
        add(emailBundle.field());

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
                ErrorHandler.handleServiceCall(target, form, () -> {
                    clienteFisicoService.update(model.getId(), ClienteFisicoDtoMapper.toUpdateRequest(model));

                    form.setDefaultModelObject(new ClienteFisicoUpdateFormModel(
                            clienteFisicoService.findById(model.getId())));

                    ValidationFeedback.showToast(target, "success", "Cliente atualizado com sucesso!");
                    target.add(form);
                    JavaScriptUtils.reloadLucideIconsSafe(target);
                });
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
                JavaScriptUtils.reloadLucideIconsSafe(target);
            }
        };
    }

    /**
     * Behavior that adds {@code is-invalid} CSS class during render
     * when the component has feedback messages (validation errors).
     */
    private static final class ValidationStyleBehavior extends Behavior {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            if (!component.getFeedbackMessages().isEmpty()) {
                String cls = tag.getAttribute("class");
                tag.put("class", cls != null ? cls + " is-invalid" : "is-invalid");
            }
        }
    }
}
