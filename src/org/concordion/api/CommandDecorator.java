package org.concordion.api;

public interface CommandDecorator {
    Command decorate(Command toBeDecorated);
}
