package org.rebotted.directory.exceptions;

public class InvalidDirectoryNameException extends InvalidNameException {

    private static final long serialVersionUID = 6214764160978219616L;

    public InvalidDirectoryNameException(String name) {
        super(name + " is not a valid directory name.");
    }

}
