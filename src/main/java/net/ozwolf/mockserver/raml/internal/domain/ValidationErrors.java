package net.ozwolf.mockserver.raml.internal.domain;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ValidationErrors {
    private final List<String> messages;

    public ValidationErrors() {
        this.messages = newArrayList();
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
