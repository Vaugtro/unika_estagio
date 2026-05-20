package com.desafio.estagio.wicket.component.btn;

import com.desafio.estagio.dto.clientefisico.ClienteFisicoListResponse;
import com.desafio.estagio.dto.clientefisico.ClienteFisicoUpdateRequest;
import com.desafio.estagio.service.ClienteFisicoService;
import com.desafio.estagio.wicket.models.ClienteFisicoModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serial;

public class EditarClienteFisicoBtn extends AjaxButton {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String BTN_ID = "editarBtn";

    @SpringBean
    private ClienteFisicoService clienteFisicoService;

    public EditarClienteFisicoBtn(Form<?> form) {
        super(BTN_ID, form);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

        ClienteFisicoModel model = (ClienteFisicoModel) form.getModelObject();

        ClienteFisicoUpdateRequest updateRequest = new ClienteFisicoUpdateRequest(model.getNome(), model.getEmail(), model.getEstaAtivo());

        clienteFisicoService.update(model.getId(), updateRequest);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        target.add(form);
    }
}
