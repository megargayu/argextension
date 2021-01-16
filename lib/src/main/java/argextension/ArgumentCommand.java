package jdautils_argextension;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Command} with automatic argument parsing (see docs
 * of {@link Command} for more info)
 */
public abstract class ArgumentCommand extends Command {
    /**
     * The array of required {@link Argument}s
     */
    protected Argument[] requiredArguments;

    /**
     * The array of optional {@link Argument}s (these are added to the end;
     * see {@link #getArguments()} for more info)
     */
    protected Argument[] optionalArguments;

    /**
     * Split a string into it's arguments - split on spaces and quotes
     * <br>Thanks to <a href="https://stackoverflow.com/a/366532">this StackOverflow answer</a> and
     * <a href="https://stackoverflow.com/posts/comments/40428033">this StackOverflow comment</a> for this code
     * (which was cleaned up a bit)
     *
     * @param arguments The un-parsed argument string
     * @return The resulting list of arguments
     */
    public static List<String> splitArguments(String arguments) {
        List<String> split = new ArrayList<>();
        Pattern regex = Pattern.compile("\"([^\"]*)\"|'([^']*)'|[^\\s]+");
        Matcher regexMatcher = regex.matcher(arguments);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) split.add(regexMatcher.group(1)); // Double quotes
            else if (regexMatcher.group(2) != null) split.add(regexMatcher.group(2)); // Single quotes
            else split.add(regexMatcher.group()); // No quotes
        }
        return split;
    }

    /**
     * Get required arguments
     *
     * @return An array of all required arguments
     */
    public Argument[] getRequiredArguments() {
        return requiredArguments == null ? new Argument[0] : requiredArguments;
    }

    /**
     * Get optional arguments
     *
     * @return An array of all optional arguments
     */
    public Argument[] getOptionalArguments() {
        return optionalArguments == null ? new Argument[0] : optionalArguments;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (getRequiredArguments().length == 0 && getOptionalArguments().length == 0) {
            execute(event, new ArrayList<>(0));
            return;
        }

        List<String> split = splitArguments(event.getArgs());

        // Proper usage string
        String properUsage = "The proper usage would be: `" + event.getClient().getTextualPrefix() + 
                getName() + " " + getArguments() + "`";

        // No arguments provided but there are required arguments
        if (split.size() == 0 && getRequiredArguments().length > 0) {
            event.reply("You didn't provide any arguments, " + event.getAuthor().getName() + "!\n" + properUsage);
            return;
        }

        // There are too less arguments provided to satisfy all required arguments
        if (split.size() < getRequiredArguments().length) {
            StringBuilder requiredArgs = new StringBuilder();
            for (int i = split.size(); i < getRequiredArguments().length; i++) {
                requiredArgs.append(getRequiredArguments()[i].getName())
                        .append(i < getRequiredArguments().length - 1 ? ", " : "");
            }

            event.reply("You didn't provide the required arg(s) \"" + requiredArgs.toString() + "\"!\n" + properUsage);
            return;
        }

        // There are more provided arguments than all arguments that could be passed in
        if (split.size() > getRequiredArguments().length + getOptionalArguments().length) {
            event.reply("You provided too many arguments!\n" + properUsage);
            return;
        }

        // Check all arguments using the provided validator
        String defaultErrorString = "You provided an invalid argument for argument `" + name + "`!\n" +
                getArguments();
        int i = 0;
        for (; i < getRequiredArguments().length; i++) {
            Argument argument = getRequiredArguments()[i];

            ValidatorOutput validatorOutput;
            if (!((validatorOutput = argument.isValid(split.get(i))).getIsValid())) {
                event.reply((validatorOutput.getErrorMessage() == null ?
                        defaultErrorString : validatorOutput.getErrorMessage()) + "\n" + properUsage);
                return;
            }
        }

        for (; i < split.size(); i++) {
            Argument argument = getOptionalArguments()[i - getRequiredArguments().length];

            ValidatorOutput validatorOutput;
            if (!((validatorOutput = argument.isValid(split.get(i))).getIsValid())) {
                event.reply((validatorOutput.getErrorMessage() == null ?
                        defaultErrorString : validatorOutput.getErrorMessage()) + "\n" + properUsage);
                return;
            }
        }

        // Execute!
        execute(event, split);
    }

    /**
     * The main body method of a {@link ArgumentCommand}.
     * <br>This is the "response" for a successful
     * {@link Command#run(CommandEvent)}.
     * <br><br>Docs copied from {@link Command#execute(CommandEvent)}
     *
     * @param event The {@link CommandEvent} that
     *              triggered this Command
     * @param args  A {@link java.util.HashMap} of indexes as keys and
     *              {@link String}s where the string is the value of the corresponding
     *              index of a argument
     */
    protected abstract void execute(CommandEvent event, List<String> args);

    /**
     * Override for {@link Command#getArguments()}<br><br>
     * Generate a usage string in the following format (no prefix or command name included):
     * <br><pre>{required argument: {@link Argument#getType() type}} ... [optional argument: {@link Argument#getType() type}] ...</pre>
     * Note that optional arguments <i>always</i> come after required arguments (in the same order they are specified).
     *
     * @return The generated string in the format shown above
     */
    @Override
    public String getArguments() {
        StringBuilder usageString = new StringBuilder();
        for (int i = 0; i < getRequiredArguments().length; i++) {
            Argument argument = getRequiredArguments()[i];
            usageString.append("{")
                    .append(argument.getName().toUpperCase())
                    .append(argument.getType() != null ? ": " + argument.getType() : "")
                    .append("}");
            if (i < getRequiredArguments().length - 1 || getOptionalArguments().length > 0) usageString.append(" ");
        }

        for (int i = 0; i < getOptionalArguments().length; i++) {
            Argument argument = getOptionalArguments()[i];
            usageString.append("[")
                    .append(argument.getName().toUpperCase())
                    .append(argument.getType() != null ? ": " + argument.getType() : "")
                    .append("]");
            if (i < getOptionalArguments().length - 1) usageString.append(" ");
        }
        return usageString.toString();    }
}
