package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.model.formatter.TelefoneFormatter;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.service.FileService;
import com.desafio.estagio.service.FileService.ImportResult;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.component.modal.ExportModal;
import com.desafio.estagio.wicket.component.modal.ImportModal;
import com.desafio.estagio.wicket.mapper.EnderecoDtoMapper;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ErrorHandler;
import com.desafio.estagio.wicket.util.JavaScriptUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.InputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class EnderecoListViewPanel extends Panel {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Long clienteId;
    private final List<EnderecoCreateFormModel> modalEnderecos = new ArrayList<>();
    private final Form<?> modalForm;
    private final Label enderecoModalLabel;
    private final WebMarkupContainer enderecosContainer;
    @SpringBean
    private EnderecoService enderecoService;
    @SpringBean
    private FileService fileService;

    public EnderecoListViewPanel(String id, Long clienteId) {
        super(id);
        this.clienteId = clienteId;
        setOutputMarkupId(true);

        // --- Modal form ---
        modalForm = new Form<>("modalForm");
        modalForm.setOutputMarkupId(true);

        enderecoModalLabel = new Label("enderecoModalLabel", Model.of("Novo Endereço"));
        enderecoModalLabel.setOutputMarkupId(true);
        modalForm.add(enderecoModalLabel);

        modalForm.add(new EnderecoCreateTablePanel("enderecoTablePanel", modalEnderecos));

        add(modalForm);

        // --- Enderecos table ---
        enderecosContainer = new WebMarkupContainer("enderecosContainer");
        enderecosContainer.setOutputMarkupId(true);
        add(enderecosContainer);

        LoadableDetachableModel<List<EnderecoResponse>> enderecosModel = new LoadableDetachableModel<>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected List<EnderecoResponse> load() {
                return enderecoService.findAllByClienteId(clienteId);
            }
        };

        ListView<EnderecoResponse> enderecosView = new ListView<>("enderecoRow", enderecosModel) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<EnderecoResponse> item) {
                EnderecoResponse end = item.getModelObject();

                item.add(new Label("logradouro", end.logradouro() != null ? end.logradouro() : ""));
                item.add(new Label("numero", end.numero() != null ? end.numero().toString() : ""));
                item.add(new Label("bairro", end.bairro() != null ? end.bairro() : ""));
                item.add(new Label("cep", end.cep() != null ? end.cep() : ""));
                item.add(new Label("cidade", end.cidade() != null ? end.cidade() : ""));
                item.add(new Label("estado", end.estado() != null ? end.estado() : ""));
                item.add(new Label("telefone", end.telefone() != null ? TelefoneFormatter.format(end.telefone()) : ""));

                Long endId = end.id();
                boolean isPrincipal = Boolean.TRUE.equals(end.principal());

                Label btnPrincipalLabel = new Label("principalLabel", isPrincipal ? "Sim" : "Não");
                btnPrincipalLabel.setOutputMarkupId(true);

                AjaxLink<Void> principalBtn = new AjaxLink<>("setAsPrincipalBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ErrorHandler.handleServiceCall(() -> {
                            enderecoService.setAsPrincipal(endId);
                            target.add(enderecosContainer);
                            ValidationFeedback.showToast(target, "success",
                                    "Endereço definido como principal!");
                            JavaScriptUtils.createIcons(target);
                        }, target);
                    }
                };

                principalBtn.add(btnPrincipalLabel);
                principalBtn.add(new AttributeModifier("class",
                        isPrincipal ? "btn btn-sm btn-success" : "btn btn-sm btn-outline-success"));
                principalBtn.add(new AttributeModifier("title",
                        isPrincipal ? "Principal" : "Definir como principal"));

                if (isPrincipal) {
                    principalBtn.add(new AttributeModifier("disabled", "disabled"));
                } else if (getList().size() <= 1) {
                    principalBtn.setEnabled(false);
                    principalBtn.add(new AttributeModifier("title", "Endereço único — já é principal"));
                }

                item.add(principalBtn);

                item.add(new AjaxLink<Void>("editarBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        EnderecoResponse end = item.getModelObject();
                        modalEnderecos.clear();
                        modalForm.getFeedbackMessages().clear();
                        EnderecoCreateFormModel formModel = new EnderecoCreateFormModel();
                        formModel.setId(end.id());
                        formModel.setLogradouro(end.logradouro());
                        formModel.setNumero(end.numero());
                        formModel.setBairro(end.bairro());
                        formModel.setCep(end.cep());
                        formModel.setCidade(end.cidade());
                        formModel.setEstado(end.estado());
                        formModel.setTelefone(end.telefone());
                        formModel.setPrincipal(Boolean.TRUE.equals(end.principal()));
                        formModel.setComplemento(end.complemento());
                        modalEnderecos.add(formModel);
                        enderecoModalLabel.setDefaultModelObject("Editar Endereço");
                        target.add(modalForm);
                        JavaScriptUtils.callAbrirModalEndereco(target);
                    }
                });

                item.add(new AjaxLink<Void>("excluirBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ErrorHandler.handleDelete(() -> {
                            enderecoService.delete(endId);
                            target.add(enderecosContainer);
                            ValidationFeedback.showToast(target, "success", "Endereço excluído com sucesso!");
                            JavaScriptUtils.createIcons(target);
                        }, target, "endereço");
                    }
                });
            }
        };
        enderecosContainer.add(enderecosView);

        // --- AjaxButton for modal form ---
        modalForm.add(new AjaxButton("salvarEnderecoBtn", modalForm) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (modalEnderecos.isEmpty()) return;

                EnderecoCreateFormModel endForm = modalEnderecos.get(0);

                ErrorHandler.handleServiceCall(() -> {
                    if (endForm.getId() != null) {
                        enderecoService.update(endForm.getId(), EnderecoDtoMapper.toUpdateRequest(endForm));
                        ValidationFeedback.showToast(target, "success", "Endereço atualizado com sucesso!");
                    } else {
                        enderecoService.createForCliente(clienteId,
                                EnderecoDtoMapper.toWithinClienteCreateRequest(endForm));
                        ValidationFeedback.showToast(target, "success", "Endereço adicionado com sucesso!");
                    }

                    modalEnderecos.clear();
                    target.add(enderecosContainer);
                    target.add(modalForm);
                    JavaScriptUtils.callFecharModalEndereco(target);
                    JavaScriptUtils.createIcons(target);
                }, target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
                target.add(form);
            }
        });

        // --- Adicionar Endereço button ---
        add(new AjaxLink<Void>("adicionarEnderecoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEnderecos.clear();
                modalForm.getFeedbackMessages().clear();
                EnderecoCreateFormModel end = new EnderecoCreateFormModel();
                end.setPrincipal(false);
                modalEnderecos.add(end);
                enderecoModalLabel.setDefaultModelObject("Novo Endereço");
                target.add(modalForm);
                JavaScriptUtils.callAbrirModalEndereco(target);
            }
        });

        // --- Export and Import modals ---
        add(new ExportModal("exportModal", "exportEnderecoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getPdfData() {
                return fileService.pdfEnderecos(clienteId);
            }

            @Override
            protected String getPdfName() {
                return "enderecos.pdf";
            }

            @Override
            protected byte[] getXlsxData() {
                return fileService.xlsxEnderecos(clienteId);
            }

            @Override
            protected String getXlsxName() {
                return "enderecos.xlsx";
            }
        });

        add(new ImportModal("importModal", "importEnderecoModal") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getTemplateData() {
                return fileService.templateEnderecosImport();
            }

            @Override
            protected String getTemplateFileName() {
                return "template-enderecos.xlsx";
            }

            @Override
            protected ImportResult importData(InputStream is) throws Exception {
                return fileService.importEnderecos(clienteId, is);
            }

            @Override
            protected String getSuccessMessage() {
                return "endereço(s) importado(s) com sucesso!";
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(
                new UrlResourceReference(org.apache.wicket.request.Url.parse(
                        "https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.min.js"))
        ));
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(JavaScriptUtils.class, "js/mask-init.js")
        ));
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(JavaScriptUtils.class, "js/endereco-modal.js")
        ));
    }
}
