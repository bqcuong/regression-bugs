package com.milaboratory.core.io.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public class IllegalFileFormatException extends RuntimeException {
    public IllegalFileFormatException() {
    }

    public IllegalFileFormatException(String message) {
        super(message);
    }
}
