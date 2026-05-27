package com.desafio.estagio.wicket.builder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for chaining multiple {@link AttributeModifier}s on a {@link Component}.
 * <p>
 * Usage:
 * <pre>{@code
 * AttributeModifierBuilder.on(textField)
 *     .placeholder("Logradouro")
 *     .dataAttr("field", "logradouro")
 *     .cssClass("form-control")
 *     .build();
 * }</pre>
 */
public class AttributeModifierBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient List<AttributeModifier> modifiers = new ArrayList<>();
    private final Component component;

    private AttributeModifierBuilder(Component component) {
        this.component = component;
    }

    /**
     * Creates a new builder for the given component.
     */
    public static AttributeModifierBuilder on(Component component) {
        return new AttributeModifierBuilder(component);
    }

    /**
     * Appends a CSS class via {@link AttributeAppender}.
     */
    public AttributeModifierBuilder cssClass(String cssClass) {
        modifiers.add(new AttributeAppender("class", cssClass));
        return this;
    }

    /**
     * Sets the {@code placeholder} attribute.
     */
    public AttributeModifierBuilder placeholder(String placeholder) {
        modifiers.add(new AttributeModifier("placeholder", placeholder));
        return this;
    }

    /**
     * Sets a {@code data-*} attribute.
     */
    public AttributeModifierBuilder dataAttr(String name, String value) {
        modifiers.add(new AttributeModifier("data-" + name, value));
        return this;
    }

    /**
     * Sets the {@code title} attribute.
     */
    public AttributeModifierBuilder title(String title) {
        modifiers.add(new AttributeModifier("title", title));
        return this;
    }

    /**
     * Sets or removes the {@code disabled} attribute based on the flag.
     */
    public AttributeModifierBuilder disabled(boolean disabled) {
        if (disabled) {
            modifiers.add(new AttributeModifier("disabled", "disabled"));
        }
        return this;
    }

    /**
     * Sets any custom attribute to the given value.
     */
    public AttributeModifierBuilder custom(String attribute, String value) {
        modifiers.add(new AttributeModifier(attribute, value));
        return this;
    }

    /**
     * Applies all accumulated modifiers to the component and returns it.
     */
    public Component build() {
        for (AttributeModifier modifier : modifiers) {
            component.add(modifier);
        }
        return component;
    }
}
