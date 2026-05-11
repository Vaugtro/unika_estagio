package com.desafio.estagio.wicket.components.paginator;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.AttributeModifier;

public class LucidePaginator extends Panel {
    private final IPageable pageable;
    private final int maxPagesDisplayed = 5;

    public LucidePaginator(String id, IPageable pageable) {
        super(id);
        this.pageable = pageable;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Botão Primeira página - DIRETAMENTE no panel
        Link<Void> firstLink = new Link<Void>("firstLink") {
            @Override
            public void onClick() {
                pageable.setCurrentPage(0);
            }

            @Override
            public boolean isEnabled() {
                return pageable.getCurrentPage() > 0;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(pageable.getCurrentPage() > 0);
            }
        };
        add(firstLink);

        // Botão Anterior - DIRETAMENTE no panel
        Link<Void> prevLink = new Link<Void>("prevLink") {
            @Override
            public void onClick() {
                pageable.setCurrentPage(pageable.getCurrentPage() - 1);
            }

            @Override
            public boolean isEnabled() {
                return pageable.getCurrentPage() > 0;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(pageable.getCurrentPage() > 0);
            }
        };
        add(prevLink);

        // Links das páginas - DIRETAMENTE no panel
        long totalPages = Math.max(1, pageable.getPageCount());
        long currentPage = pageable.getCurrentPage();
        long startPage = Math.max(0, currentPage - maxPagesDisplayed / 2);
        long endPage = Math.min(totalPages - 1, startPage + maxPagesDisplayed - 1);

        if (endPage - startPage + 1 < maxPagesDisplayed && startPage > 0) {
            startPage = Math.max(0, endPage - maxPagesDisplayed + 1);
        }

        final long finalStartPage = startPage;
        final long finalEndPage = endPage;

        Loop pageLinks = new Loop("pageLinks", (int) (finalEndPage - finalStartPage + 1)) {
            @Override
            protected void populateItem(LoopItem item) {
                final long pageIndex = finalStartPage + item.getIndex();

                Link<Void> pageLink = new Link<Void>("pageLink") {
                    @Override
                    public void onClick() {
                        pageable.setCurrentPage(pageIndex);
                    }
                };

                Label pageNumber = new Label("pageNumber", String.valueOf(pageIndex + 1));
                pageLink.add(pageNumber);

                // Marca a página atual como ativa
                if (pageIndex == currentPage) {
                    pageLink.add(new AttributeModifier("class", "active"));
                }

                item.add(pageLink);
            }
        };
        add(pageLinks);

        // Botão Próxima - DIRETAMENTE no panel
        Link<Void> nextLink = new Link<Void>("nextLink") {
            @Override
            public void onClick() {
                pageable.setCurrentPage(pageable.getCurrentPage() + 1);
            }

            @Override
            public boolean isEnabled() {
                return pageable.getCurrentPage() < pageable.getPageCount() - 1;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(pageable.getCurrentPage() < pageable.getPageCount() - 1);
            }
        };
        add(nextLink);

        // Botão Última página - DIRETAMENTE no panel
        Link<Void> lastLink = new Link<Void>("lastLink") {
            @Override
            public void onClick() {
                if (pageable.getPageCount() > 0) {
                    pageable.setCurrentPage(pageable.getPageCount() - 1);
                }
            }

            @Override
            public boolean isEnabled() {
                return pageable.getPageCount() > 0 &&
                        pageable.getCurrentPage() < pageable.getPageCount() - 1;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(pageable.getPageCount() > 0 &&
                        pageable.getCurrentPage() < pageable.getPageCount() - 1);
            }
        };
        add(lastLink);
    }
}