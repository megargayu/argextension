package org.megargayu.argextension;

/**
 * A validator for an {@link Argument}
 */
public interface Validator {
    /**
     * Check if value is valid
     *
     * @param value The value to check
     * @return If the output is valid and the error string (null if none provided)
     */
    ValidatorOutput isValid(String value);
}
