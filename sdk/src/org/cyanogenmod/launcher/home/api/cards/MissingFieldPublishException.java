package org.cyanogenmod.launcher.home.api.cards;

/**
 * An exception to denote the case where a CardData is attempted to be published when all of it's
 * required fields are not present.
 */
public class MissingFieldPublishException extends Exception {

    public MissingFieldPublishException(String message) {
        super(message);
    }
}
