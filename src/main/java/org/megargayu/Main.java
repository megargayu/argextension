package org.megargayu;

import org.megargayu.argextension.HelpConsumer;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import org.megargayu.commands.TestCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static final Properties properties = new Properties();

    public static void main(String[] args) throws LoginException, IOException {
        InputStream propertyInputStream = Main.class.getClassLoader().getResourceAsStream("config.properties");
        properties.load(propertyInputStream);

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder()
                .useDefaultGame()
                .setOwnerId(properties.getProperty("ownerID"))
                .setEmojis("\u2714\uFE0F", "\u26A0\uFE0F", "\u274C\uFE0F")
                .setPrefix(properties.getProperty("prefix"))
                .setHelpConsumer(new HelpConsumer())
                .addCommands(
                        new TestCommand(),
                        new PingCommand(),
                        new ShutdownCommand());

        JDA jda = JDABuilder.createDefault(properties.getProperty("token"))
                .addEventListeners(waiter, client.build())
                .build();
    }
}
