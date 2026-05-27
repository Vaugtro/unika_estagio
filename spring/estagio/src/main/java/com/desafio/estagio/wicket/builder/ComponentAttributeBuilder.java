package com.desafio.estagio.wicket.builder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import java.io.Serial;
import java.io.Serializable;

/**
 * Fluent builder for configuring Wicket {@link Component} attributes and behaviors.
 * <p>
 * Enables concise chains such as:
 * <pre>{@code
 *     ComponentAttributeBuilder.of(cpfField)
 *         .setOutputMarkupId(true)
 *         .setRequired(true)
 *         .add(AttributeModifierBuilder.create()
 *             .placeholder("000.000.000-00")
 *             .dataAttr("mask", "000.000.000-00")
 *             .build())
 *         .build();
 * }</pre>
 *
 * @param <T> the component type being configured
 */
public class ComponentAttributeBuilder<T extends Component> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final T component;

    private ComponentAttributeBuilder(T component) {
        this.component = component;
    }

    /**
     * Creates a new {@link ComponentAttributeBuilder} wrapping the given component.
     *
     * @param <T>       the component type
     * @param component the component to configure
     * @return a new builder wrapping {@code component}
     */
    public static <T extends Component> ComponentAttributeBuilder<T> of(T component) {
        return new ComponentAttributeBuilder<>(component);
    }

    /**
     * Adds one or more {@link AttributeModifier}s to the component.
     *
     * @see Component#add(AttributeModifier...)
     */
    @SafeVarargs
    public final ComponentAttributeBuilder<T> add(AttributeModifier... modifiers) {
        component.add(modifiers);
        return this;
    }

    /**
     * Adds a {@link Behavior} to the component.
     *
     * @see Component#add(Behavior...)
     */
    @SafeVarargs
    public final ComponentAttributeBuilder<T> addBehavior(Behavior... behaviors) {
        component.add(behaviors);
        return this;
    }

    /**
     * Sets the visibility of the component.
     *
     * @see Component#setVisible(boolean)
     */
    public ComponentAttributeBuilder<T> setVisible(boolean visible) {
        component.setVisible(visible);
        return this;
    }

    /**
     * Sets whether the component is enabled.
     *
     * @see Component#setEnabled(boolean)
     */
    public ComponentAttributeBuilder<T> setEnabled(boolean enabled) {
        component.setEnabled(enabled);
        return this;
    }

    /**
     * Sets whether the component renders its markup id.
     *
     * @see Component#setOutputMarkupId(boolean)
     */
    public ComponentAttributeBuilder<T> setOutputMarkupId(boolean outputMarkupId) {
        component.setOutputMarkupId(outputMarkupId);
        return this;
    }

    /**
     * Sets whether the component renders a placeholder tag.
     *
     * @see Component#setOutputMarkupPlaceholderTag(boolean)
     */
    public ComponentAttributeBuilder<T> setOutputMarkupPlaceholderTag(boolean outputPlaceholderTag) {
        component.setOutputMarkupPlaceholderTag(outputPlaceholderTag);
        return this;
    }

    /**
     * Marks the component as not visible and not enabled.
     */
    public ComponentAttributeBuilder<T> hide() {
        component.setVisible(false);
        component.setEnabled(false);
        return this;
    }

    /**
     * Returns the configured component.
     *
     * @return the configured component
     */
    public T build() {
        return component;
    }
}
