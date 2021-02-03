package org.megargayu.commands;

import org.megargayu.argextension.Argument;
import org.megargayu.argextension.ArgumentCommand;
import org.megargayu.argextension.ValidatorOutput;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;

public class TestCommand extends ArgumentCommand {
    public TestCommand() {
        this.name = "test";
        this.help = "a testing command";
        this.guildOnly = false;
        this.aliases = new String[]{"t"};
        this.requiredArguments = new Argument[]{
                new Argument("number", "the test number", "number", value -> {
                    try {
                        Integer.parseInt(value);
                        return new ValidatorOutput(true);
                    } catch (NumberFormatException e) {
                        return new ValidatorOutput(false, "Invalid integer provided!");
                    }
                })
        };
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        int number = Integer.parseInt(args.get(0));
        event.reply(String.valueOf(number));
    }
}
