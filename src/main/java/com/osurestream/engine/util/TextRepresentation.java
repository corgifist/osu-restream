package com.osurestream.engine.util;

import java.util.Objects;

public class TextRepresentation {

    private String font;
    private String text;
    private int size;

    public TextRepresentation(String font, String text, int size) {
        this.font = font;
        this.text = text;
        this.size = size;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextRepresentation that = (TextRepresentation) o;
        return size == that.size && font.equals(that.font) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, text, size);
    }
}
