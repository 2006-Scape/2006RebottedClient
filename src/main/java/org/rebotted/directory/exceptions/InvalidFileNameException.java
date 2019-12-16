package org.rebotted.directory.exceptions;

public class InvalidFileNameException extends InvalidNameException {

    private static final long serialVersionUID = -3076779825506160714L;

    public InvalidFileNameException(String name) {
        super(name + " is not a valid file name.");
    }

}
