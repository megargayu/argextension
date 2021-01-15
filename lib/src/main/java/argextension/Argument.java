package argextension;

public class Argument {
    /**
     * The name of the argument
     */
    private final String name;

    /**
     * The description of the argument
     */
    private final String description;

    /**
     * The type of the argument
     */
    private final String type;

    /**
     * The validator of the argument - {@link Validator#isValid(String)} is run when arguments are passed
     */
    private final Validator validator;

    /**
     * Create an argument for an {@link ArgumentCommand} with the specified name and description
     *
     * @param name        The name of the argument - {@link #getName()}
     * @param description The description of the argument (can be empty/null) - {@link #getDescription()}
     */
    public Argument(String name, String description) {
        this(name, description, null, null);
    }

    /**
     * Create an argument for an {@link ArgumentCommand} with the specified name, description, type, and validator
     *
     * @param name        The name of the argument - {@link #getName()}
     * @param description The description of the argument (can be empty/null) - {@link #getDescription()}
     * @param type        The type of the argument - {@link #getType()}
     * @param validator   The validator of the argument - {@link #isValid(String)}
     */
    public Argument(String name, String description, String type, Validator validator) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Argument name cannot be null or empty!");
        if (type != null && type.length() == 0)
            throw new IllegalArgumentException("Type cannot be empty!");
        this.name = name;
        this.description = description;
        this.type = type;
        this.validator = validator;
    }

    /**
     * Get the name of the argument
     *
     * @return The name of the argument
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the argument
     *
     * @return The description of the argument
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the type of the argument
     *
     * @return The type of the argument
     */
    public String getType() {
        return type;
    }

    /**
     * Check if value is valid
     *
     * @param value The value to check
     * @return If the output is valid and the error string (null if none provided)
     */
    public ValidatorOutput isValid(String value) {
        return validator != null ? validator.isValid(value) : new ValidatorOutput(true);
    }
}
