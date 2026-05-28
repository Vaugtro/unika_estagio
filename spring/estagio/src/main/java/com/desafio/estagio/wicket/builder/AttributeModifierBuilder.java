package com.desafio.estagio.wicket.builder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fluent builder for creating and applying {@link AttributeModifier} instances.
 * <p>
 * Enables concise chains such as:
 * <pre>{@code
 *     AttributeModifierBuilder.create()
 *         .placeholder("000.000.000-00")
 *         .dataAttr("mask", "000.000.000-00")
 *         .buildAndAdd(cpfField);
 * }</pre>
 */
public class AttributeModifierBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<AttributeModifier> modifiers;

    private AttributeModifierBuilder() {
        this.modifiers = new ArrayList<>();
    }

    /**
     * Creates a new {@link AttributeModifierBuilder}.
     */
    public static AttributeModifierBuilder create() {
        return new AttributeModifierBuilder();
    }

    /**
     * Adds a CSS class attribute modifier.
     */
    public AttributeModifierBuilder cssClass(String cssClass) {
        modifiers.add(new AttributeModifier("class", cssClass));
        return this;
    }

    /**
     * Adds a placeholder attribute modifier.
     */
    public AttributeModifierBuilder placeholder(String placeholder) {
        modifiers.add(new AttributeModifier("placeholder", placeholder));
        return this;
    }

    /**
     * Adds a data-* attribute modifier.
     *
     * @param attr  the data attribute name (e.g. "mask" → "data-mask")
     * @param value the attribute value
     */
    public AttributeModifierBuilder dataAttr(String attr, String value) {
        modifiers.add(new AttributeModifier("data-" + attr, value));
        return this;
    }

    /**
     * Adds a title attribute modifier.
     */
    public AttributeModifierBuilder title(String title) {
        modifiers.add(new AttributeModifier("title", title));
        return this;
    }

    /**
     * Conditionally adds a disabled attribute modifier.
     * <p>
     * When {@code disabled} is {@code true}, adds {@code disabled="disabled"}.
     * When {@code false}, no modifier is added.
     */
    public AttributeModifierBuilder disabled(boolean disabled) {
        if (disabled) {
            modifiers.add(new AttributeModifier("disabled", "disabled"));
        }
        return this;
    }

    /**
     * Adds a generic attribute modifier.
     */
    public AttributeModifierBuilder attribute(String name, String value) {
        modifiers.add(new AttributeModifier(name, value));
        return this;
    }

    /**
     * Returns the accumulated list of {@link AttributeModifier}s.
     * <p>
     * The returned list is unmodifiable. To apply modifiers to a component
     * use {@link #buildAndAdd(Component)} instead.
     */
    public List<AttributeModifier> build() {
        return Collections.unmodifiableList(new ArrayList<>(modifiers));
    }

    /**
     * Applies all accumulated {@link AttributeModifier}s to the given component
     * and returns it for further chaining.
     *
     * @param <T>       the component type
     * @param component the component to modify
     * @return the same component instance, with all modifiers applied
     */
    public <T extends Component> T buildAndAdd(T component) {
        for (AttributeModifier modifier : modifiers) {
            component.add(modifier);
        }
        return component;
    }
}
