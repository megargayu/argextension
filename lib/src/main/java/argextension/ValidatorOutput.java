package jdautils_argextension;

public class ValidatorOutput {
    private final boolean isValid;
    private final String errorMessage;

    /**
     * The output of {@link Validator#isValid(String)}
     *
     * @param isValid If the value is valid
     */
    public ValidatorOutput(boolean isValid) {
        this(isValid, null);
    }

    /**
     * The output of {@link Validator#isValid(String)}
     *
     * @param isValid      If the value is valid
     * @param errorMessage The custom error message
     */
    public ValidatorOutput(boolean isValid, String errorMessage) {
        if (errorMessage != null && errorMessage.length() == 0)
            throw new IllegalArgumentException("Error message cannot be empty!");
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    /**
     * Get if the value is valid
     *
     * @return Whether the value is valid
     */
    public boolean getIsValid() {
        return isValid;
    }

    /**
     * Get the custom error message (if there is one)
     *
     * @return The custom error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
