package com.desafio.estagio.wicket.util;

import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

/**
 * Wraps a byte[] as a Wicket IResourceStream for file-download responses.
 */
public class ByteArrayResourceStream extends AbstractResourceStream {

    @Serial
    private static final long serialVersionUID = 1L;

    private final byte[] data;
    private final String contentType;

    public ByteArrayResourceStream(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void close() throws IOException {
    }
}
