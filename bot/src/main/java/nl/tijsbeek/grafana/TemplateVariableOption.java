package nl.tijsbeek.grafana;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TemplateVariableOption {
    private boolean selected;
    private String text;
    private String value;

    public static TemplateVariableOption byName(String name) {
        return new TemplateVariableOption()
                .setSelected(false)
                .setText(name)
                .setValue(name);
    }

    public boolean isSelected() {
        return selected;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    @NotNull
    public TemplateVariableOption setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    @NotNull
    public TemplateVariableOption setText(String text) {
        this.text = text;
        return this;
    }

    @NotNull
    public TemplateVariableOption setValue(String value) {
        this.value = value;
        return this;
    }

    @NonNls
    @Override
    public String toString() {
        return "TemplateVariableOption{" +
                "selected=" + selected +
                ", text='" + text + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
