package com.desafio.estagio.wicket.component.shared;

import com.desafio.estagio.dto.endereco.EnderecoCreateRequest;
import com.desafio.estagio.dto.endereco.EnderecoResponse;
import com.desafio.estagio.dto.endereco.EnderecoUpdateRequest;
import com.desafio.estagio.service.EnderecoService;
import com.desafio.estagio.service.ExportService;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import com.desafio.estagio.wicket.model.EnderecoCreateFormModel;
import com.desafio.estagio.wicket.util.ByteArrayResourceStream;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.springframework.dao.DataIntegrityViolationException;

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
    private ExportService exportService;

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
                item.add(new Label("telefone", end.telefone() != null ? end.telefone() : ""));
                item.add(new Label("principalLabel", Boolean.TRUE.equals(end.principal()) ? "Sim" : "Não"));

                Long endId = end.id();

                item.add(new AjaxLink<Void>("editarBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        EnderecoResponse end = item.getModelObject();
                        modalEnderecos.clear();
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
                        target.appendJavaScript("abrirModalEndereco();");
                    }
                });

                item.add(new AjaxLink<Void>("excluirBtn") {
                    @Serial
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            enderecoService.delete(endId);
                            target.add(enderecosContainer);
                            ValidationFeedback.showToast(target, "success", "Endereço excluído com sucesso!");
                            target.appendJavaScript("lucide.createIcons();");
                        } catch (Exception e) {
                            ValidationFeedback.showToast(target, "error", e.getMessage());
                        }
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
                String cepClean = endForm.getCep() != null ? endForm.getCep().replaceAll("\\D", "") : null;
                String telefoneClean = endForm.getTelefone() != null ? endForm.getTelefone().replaceAll("\\D", "") : null;

                try {
                    if (endForm.getId() != null) {
                        enderecoService.update(endForm.getId(), new EnderecoUpdateRequest(
                                endForm.getLogradouro(),
                                endForm.getNumero(),
                                cepClean,
                                endForm.getBairro(),
                                telefoneClean,
                                endForm.getEstado(),
                                endForm.getCidade(),
                                endForm.getPrincipal(),
                                endForm.getComplemento()
                        ));
                        ValidationFeedback.showToast(target, "success", "Endereço atualizado com sucesso!");
                    } else {
                        enderecoService.create(new EnderecoCreateRequest(
                                endForm.getLogradouro(),
                                endForm.getNumero(),
                                cepClean,
                                endForm.getBairro(),
                                telefoneClean,
                                endForm.getEstado(),
                                endForm.getCidade(),
                                endForm.getPrincipal(),
                                endForm.getComplemento(),
                                clienteId
                        ));
                        ValidationFeedback.showToast(target, "success", "Endereço adicionado com sucesso!");
                    }

                    modalEnderecos.clear();
                    target.add(enderecosContainer);
                    target.add(modalForm);
                    target.appendJavaScript("fecharModalEndereco(); lucide.createIcons();");
                } catch (DataIntegrityViolationException e) {
                    ValidationFeedback.showToast(target, "error",
                            "Já existe um endereço principal para este cliente. Desmarque o endereço principal atual primeiro.");
                } catch (Exception e) {
                    ValidationFeedback.showToast(target, "error",
                            e.getMessage() != null ? e.getMessage() : "Erro ao salvar endereço.");
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });

        // --- Adicionar Endereço button ---
        add(new AjaxLink<Void>("adicionarEnderecoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalEnderecos.clear();
                EnderecoCreateFormModel end = new EnderecoCreateFormModel();
                end.setPrincipal(false);
                modalEnderecos.add(end);
                enderecoModalLabel.setDefaultModelObject("Novo Endereço");
                target.add(modalForm);
                target.appendJavaScript("abrirModalEndereco();");
            }
        });

        // --- Export buttons ---
        add(buildEnderecoExportLink("exportEnderecosPdfBtn", "enderecos.pdf", "application/pdf", true));
        add(buildEnderecoExportLink("exportEnderecosXlsxBtn", "enderecos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", false));

        // --- Import form ---
        add(buildEnderecoImportForm());

        // --- Template download ---
        add(buildEnderecoTemplateLink("downloadEnderecoTemplateBtn"));
    }

    private Link<Void> buildEnderecoExportLink(String id, String filename, String mimeType, boolean pdf) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = pdf ? exportService.pdfEnderecos(clienteId) : exportService.xlsxEnderecos(clienteId);
                IResourceStream stream = new ByteArrayResourceStream(bytes, mimeType);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName(filename)
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }

    private Form<Void> buildEnderecoImportForm() {
        Form<Void> importForm = new Form<>("importEnderecoForm");
        importForm.setMultiPart(true);
        importForm.setOutputMarkupId(true);

        FileUploadField fileUpload = new FileUploadField("enderecoFileUpload");
        importForm.add(fileUpload);

        importForm.add(new AjaxButton("importEnderecoBtn") {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FileUpload upload = fileUpload.getFileUpload();
                if (upload == null) {
                    ValidationFeedback.showToast(target, "error", "Selecione um arquivo XLSX.");
                    return;
                }
                try (java.io.InputStream is = upload.getInputStream()) {
                    int count = exportService.importEnderecos(clienteId, is);
                    ValidationFeedback.showToast(target, "success",
                            count + " endereço(s) importado(s) com sucesso!");
                    target.add(enderecosContainer);
                    target.add(importForm);
                    target.appendJavaScript("lucide.createIcons();");
                } catch (Exception e) {
                    ValidationFeedback.showToast(target, "error",
                            "Erro na importação: " + e.getMessage());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                ValidationFeedback.handleFormError(target, form);
            }
        });

        return importForm;
    }

    private Link<Void> buildEnderecoTemplateLink(String id) {
        return new Link<>(id) {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                byte[] bytes = exportService.templateEnderecosImport();
                IResourceStream stream = new ByteArrayResourceStream(bytes,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(stream)
                                .setFileName("template-enderecos.xlsx")
                                .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
    }
}
