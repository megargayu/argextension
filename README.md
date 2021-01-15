# JDA Utilities Argument Extension
### A simple extension that adds argument capabilities to [JDA-Utilities](https://github.com/JDA-Applications/JDA-Utilities)

When you get an event and run `getArgs()` in a command, you will get a string. Now, this is great and all, but I thought - why don't we make a simple `ArgumentCommand` class which will automatically parse arguments for you? This is the result of that thought.

## How it works
To make a command in regular JDA-Utilities, you would extend the `Command` class:
```java
public class MyCommand extends Command {
    public MyCommand() {
        // ...
    }
}
```
To add argument parsing to this, replace `Command` with `ArgumentCommand` and set the `requiredArguments` and `optionalArguments` variables!
```java
public class MyArgumentCommand extends ArgumentCommand {
    public MyArgumentCommand() {
        this.requiredArguments = new Argument[] { 
                new Argument("name", "description")
        };
        this.optionalArguments = new Argument[] {
                new Argument("name", "description", "type", value -> {
                    // Validator
                })
        };
    }
}
```

## Arguments
An argument contains a `name` and a `description`. If you want a validator, put a `type` (for example, if your validator checks for numbers, then you would make `type` equal to `"number"`), and a `Validator` (or a lambda expression) which returns a `ValidatorOutput`. Here is an example for checking whether the inputted string can be parsed by `Integer.parseInt`:
```java
public class MyNumberCommand extends ArgumentCommand {
    public MyNumberCommand() {
        this.requiredArguments = new Argument[] {
                new Argument("number", "a number", "number", value -> {
                    try {
                        Integer.parseInt(value);
                        return new ValidatorOutput(true);
                    } catch (NumberFormatException e) {
                        return new ValidatorOutput(false, "Invalid integer provided!");
                    }
                })
        };
    }
}
```

## Custom help consumer
I have also made a custom help consumer which creates an embed instead of plain text and works well with `ArgumentCommand`s. To use this, just set it as the help consumer in the `CommandClientBuilder`:
```java
CommandClientBuilder client = new CommandClientBuilder()
        .setHelpConsumer(new HelpConsumer());
```

If you just want to use this help consumer, remove everything after the `// Remove these if statements if you don't want to use ArgumentCommands` comment in the `HelpConsumer` file.