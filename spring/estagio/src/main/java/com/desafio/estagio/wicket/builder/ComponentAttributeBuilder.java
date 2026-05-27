package com.desafio.estagio.wicket.builder;

import org.apache.wicket.Component;

import java.io.Serial;
import java.io.Serializable;

/**
 * Fluent builder for configuring common {@link Component} properties.
 * <p>
 * Usage:
 * <pre>{@code
 * ComponentAttributeBuilder.of(textField)
 *     .setOutputMarkupId(true)
 *     .setVisible(true)
 *     .setEnabled(false)
 *     .build();
 * }</pre>
 */
public class ComponentAttributeBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Component component;

    private ComponentAttributeBuilder(Component component) {
        this.component = component;
    }

    /**
     * Creates a new builder for the given component.
     */
    public static ComponentAttributeBuilder of(Component component) {
        return new ComponentAttributeBuilder(component);
    }

    /**
     * Sets whether this component and its children are visible.
     */
    public ComponentAttributeBuilder setVisible(boolean visible) {
        component.setVisible(visible);
        return this;
    }

    /**
     * Sets whether this component is enabled.
     */
    public ComponentAttributeBuilder setEnabled(boolean enabled) {
        component.setEnabled(enabled);
        return this;
    }

    /**
     * Sets whether a markup id attribute will be rendered.
     */
    public ComponentAttributeBuilder setOutputMarkupId(boolean outputMarkupId) {
        component.setOutputMarkupId(outputMarkupId);
        return this;
    }

    /**
     * Applies all accumulated settings and returns the component.
     */
    public Component build() {
        return component;
    }
}
