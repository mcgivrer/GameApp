package com.snapgames.apps.desktop.game.entity;

import com.snapgames.apps.desktop.game.entity.util.Align;

import java.awt.*;

/**
 * <p>The {@link TextObject} is an enhanced {@link Entity} used to display Text on screen.</p>
 *
 * <p>It adds a <code>text</code> and a <code>value</code> to the {@link Entity} Attributes to be displayed on screen.
 * A simple text can be used &nd it will be directly drawn on screen. If a value object is set, the text must
 * contain the {@link String#format(String, Object...)} conversion operation to be applied on to be displayed.</p>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class TextObject extends Entity {
    public String text;
    public Object value;
    public Font font;
    public Color textColor = Color.WHITE;

    public Align textAlign = Align.LEFT;

    public TextObject(String name) {
        super(name);
    }

    public TextObject setText(String t) {
        this.text = t;
        return this;
    }

    public TextObject setValue(Object t) {
        this.value = t;
        return this;
    }

    public TextObject setTextColor(Color tc) {
        this.textColor = tc;
        return this;
    }

    public TextObject setTextAlign(Align a) {
        this.textAlign = a;
        return this;
    }

    public String getText() {
        if (text != null && text.contains("%") && value != null) {
            return String.format(text, value);
        }
        return text;
    }

    public TextObject setFont(Font f) {
        this.font = f;
        return this;
    }

    public Font getFont() {
        return this.font;
    }

    public Object getValue() {
        return value;
    }
}
