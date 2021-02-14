/*
* Copyright (C) By the Author
* Author    Yura Krymlov
* Created   2019-12
*/

package org.swisseph.app;


/**
 * @author Yura Krymlov
 * @version 1.1, 2019-12
 */
public class SweRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 2861837653194301096L;

    /**
     * Constructs a new SWE runtime exception with the specified detail message.
     */
    public SweRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new SWE runtime exception with the specified detail message and cause.
     */
    public SweRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SWE runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).
     */
    public SweRuntimeException(Throwable cause) {
        super(cause);
    }
}
