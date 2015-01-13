package net.ozwolf.mockserver.raml.internal.domain;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {
    private final List<String> messages;

    public ValidationErrors() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String error, Object... formattedParameters) {
        this.messages.add(String.format(error, formattedParameters));
    }

    public void combineWith(ValidationErrors errors) {
        this.messages.addAll(errors.getMessages());
    }

    public boolean isInError() {
        return !this.messages.isEmpty();
    }

    public List<String> getMessages() {
        return messages;
    }
}
