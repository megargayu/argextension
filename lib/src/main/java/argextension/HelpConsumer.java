package jdautils_argextension;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Date;
import java.util.function.Consumer;

public class HelpConsumer implements Consumer<CommandEvent> {
    private final Color embedColor;

    /**
     * A better help consumer (that uses embeds), which works for both {@link ArgumentCommand}s and
     * {@link Command}s. If you don't want to use {@link ArgumentCommand}s and just use this
     * help consumer, remove the if statements after the embed builder in the if statement for help on a specific
     * command.
     */
    public HelpConsumer() {
        this(Color.getColor("#00FF00"));
    }

    /**
     * A better help consumer (that uses embeds), which works for both {@link ArgumentCommand}s and
     * {@link Command}s. If you don't want to use {@link ArgumentCommand}s and just use this
     * help consumer, remove the if statements after the embed builder in the if statement for help on a specific
     * command.
     *
     * @param embedColor The color of the embed
     */
    public HelpConsumer(Color embedColor) {
        this.embedColor = embedColor;
    }

    @Override
    public void accept(CommandEvent event) {
        if (event.getArgs().equals("")) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(embedColor)
                    .setTitle("Help")
                    .setDescription("""
                            A list of commands and functionalities of this bot.

                            **Format:** `<command> (<aliases>)` `{<required argument>}` `[<optional argument>]`""")
                    .addField(new MessageEmbed.Field(
                            "`" + event.getClient().getTextualPrefix() + "help [COMMAND]`",
                            "Displays help for this command", false))
                    .setAuthor(event.getSelfUser().getName(), event.getSelfUser().getAvatarUrl())
                    .setFooter(event.getSelfUser().getName(), event.getSelfUser().getAvatarUrl())
                    .setTimestamp(new Date().toInstant());

            for (Command command : event.getClient().getCommands()) {
                if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                    StringBuilder aliases = new StringBuilder();
                    for (int i = 0; i < command.getAliases().length; i++)
                        aliases.append(event.getClient().getTextualPrefix())
                                .append(command.getAliases()[i])
                                .append((i < command.getAliases().length - 1) ? ", " : "");

                    embedBuilder.addField(new MessageEmbed.Field(
                            String.format("`%s%s%s`%s%s",
                                    event.getClient().getTextualPrefix(),
                                    command.getName(),
                                    !aliases.toString().equals("") ? " (" + aliases.toString() + ")" : "",
                                    command.getArguments() != null ? " " + command.getArguments() : "",
                                    command.getCategory() != null ? " (`" + command.getCategory().getName() + "`)" : ""),
                            // Capitalize first letter (inbuilt commands have lowercase first character)
                            command.getHelp().substring(0, 1).toUpperCase() + command.getHelp().substring(1),
                            false));
                }
            }

            event.reply(embedBuilder.build());
        } else if (!event.getArgs().contains(" ")) {
            String commandStr = event.getArgs();
            for (Command cmd : event.getClient().getCommands()) {
                boolean isCommand = cmd.getName().equals(commandStr);
                if (!isCommand)
                    for (String alias : cmd.getAliases())
                        if (alias.equals(commandStr)) {
                            isCommand = true;
                            break;
                        }

                if (isCommand) {
                    StringBuilder description = new StringBuilder(cmd.getHelp());
                    if (cmd.getAliases().length > 0) {
                        description.append("\n**Aliases:** `");
                        for (int i = 0; i < cmd.getAliases().length; i++)
                            description.append(cmd.getAliases()[i]).
                                    append((i < cmd.getAliases().length - 1) ? " " : "");
                        description.append("`");
                    }
                    description.append("\n**Usage:** `")
                            .append(event.getClient().getTextualPrefix())
                            .append(cmd.getName())
                            .append("`")
                            .append((cmd.getArguments() == null) ? "" : " `" + cmd.getArguments() + "`");
                    description.append("\n**Cooldown:** ")
                            .append(cmd.getCooldown()).append(" second(s)");

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setColor(embedColor)
                            .setTitle(cmd.getName())
                            .setDescription(description.toString())
                            .setAuthor(event.getSelfUser().getName(), event.getSelfUser().getAvatarUrl())
                            .setFooter(event.getSelfUser().getName(), event.getSelfUser().getAvatarUrl())
                            .setTimestamp(new Date().toInstant());

                    // Remove these if statements if you don't want to use ArgumentCommands
                    if (cmd instanceof ArgumentCommand && ((ArgumentCommand) cmd).getRequiredArguments().length > 0) {
                        ArgumentCommand argumentCmd = (ArgumentCommand) cmd;
                        StringBuilder requiredArgs = new StringBuilder();
                        for (int i = 0; i < argumentCmd.getRequiredArguments().length; i++) {
                            Argument arg = argumentCmd.getRequiredArguments()[i];
                            requiredArgs.append("**`").append(arg.getName().toUpperCase()).append("`** ")
                                    .append((arg.getType() != null) ? "type: `" + arg.getType() + "`" :
                                            "(no type specified")
                                    .append("\n")
                                    .append((arg.getDescription() != null && arg.getDescription().length() > 0) ?
                                            arg.getDescription() : "No description found!")
                                    .append((i < argumentCmd.getRequiredArguments().length - 1) ? "\n\n" : "");
                        }

                        embedBuilder.addField(new MessageEmbed.Field("Required Arguments",
                                requiredArgs.toString(),
                                false));
                    }

                    if (cmd instanceof ArgumentCommand && ((ArgumentCommand) cmd).getOptionalArguments().length > 0) {
                        ArgumentCommand argumentCmd = (ArgumentCommand) cmd;
                        StringBuilder optionalArgs = new StringBuilder();
                        for (int i = 0; i < argumentCmd.getOptionalArguments().length; i++) {
                            Argument arg = argumentCmd.getOptionalArguments()[i];
                            optionalArgs.append("**`").append(arg.getName().toUpperCase()).append("`** ")
                                    .append((arg.getType() != null) ? "type: `" + arg.getType() + "`" :
                                            "(no type specified")
                                    .append("\n")
                                    .append((arg.getDescription() != null && arg.getDescription().length() > 0) ?
                                            arg.getDescription() : "No description found!")
                                    .append((i < argumentCmd.getOptionalArguments().length - 1) ? "\n\n" : "");
                        }

                        embedBuilder.addField(new MessageEmbed.Field("Required Arguments",
                                optionalArgs.toString(),
                                false));
                    }

                    event.reply(embedBuilder.build());
                    return;
                }
            }

            event.reply("Command '" + commandStr + "' not found!");
        } else {
            event.reply("Invalid usage of 'help'!");
        }
    }
}
