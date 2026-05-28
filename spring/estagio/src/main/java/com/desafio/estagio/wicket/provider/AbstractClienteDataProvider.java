package com.desafio.estagio.wicket.provider;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.Iterator;

public abstract class AbstractClienteDataProvider<T> implements IDataProvider<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    private long pageSize;

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        pageSize = Math.max(pageSize, count);
        Pageable pageable = PageRequest.of(
                (int) (first / pageSize), (int) pageSize, Sort.by("id").ascending());
        return findAll(pageable).getContent().iterator();
    }

    @Override
    public long size() {
        return count();
    }

    @Override
    public IModel<T> model(T object) {
        final Long id = extractId(object);
        return new LoadableDetachableModel<T>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected T load() {
                return findByIdList(id);
            }
        };
    }

    @Override
    public void detach() {
    }

    protected abstract Page<T> findAll(Pageable pageable);

    protected abstract long count();

    protected abstract T findByIdList(Long id);

    protected abstract Long extractId(T object);
}
