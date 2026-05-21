package com.desafio.estagio.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

/**
 * Base class for service-layer unit tests.
 * Subclasses should use @InjectMocks on the service under test
 * and @Mock on all dependencies.
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {
    // Mockito's Strictness.STRICT_STUBS is the default since Mockito 3.x
    // Unused stubs trigger an exception, keeping tests clean.
}
