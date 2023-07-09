package swe.ka.dhbw.util;

import de.dhbwka.swe.utils.util.AppLogger;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ArgumentParser {
    private static final CommandLineArgument[] commandLineArguments = new CommandLineArgument[] {
            new CommandLineArgument(new String[] {"-d", "--data"}, "The path to where all the data is stored."),
            new CommandLineArgument(new String[] {"-p", "--properties"}, "The path to the properties file."),
    };

    private ArgumentParser() {
    }

    public static ArgumentsParseResult parse(final String[] args) throws ArgumentParseException {
        Optional<String> dataPath = Optional.empty();
        Optional<String> propertiesPath = Optional.empty();

        String currentModifier = "";
        for (var arg : args) {
            if (arg.equals("-d") || arg.equals("--data")) {
                currentModifier = "-d";
                continue;
            }
            if (arg.equals("-p") || arg.equals("--properties")) {
                currentModifier = "-p";
                continue;
            }

            if (currentModifier.equals("")) {
                throw new ArgumentParseException("'" + arg + "' is not a valid command line option.");
            }

            if (currentModifier.equals("-d")) {
                if (dataPath.isPresent()) {
                    throw new ArgumentParseException("You can only specify one data path.");
                }
                dataPath = Optional.of(arg);
            }
            if (currentModifier.equals("-p")) {
                if (propertiesPath.isPresent()) {
                    throw new ArgumentParseException("You can only specify one properties path.");
                }
                propertiesPath = Optional.of(arg);
            }

            currentModifier = "";
        }

        var arguments = new ArgumentsParseResult(dataPath.get(), propertiesPath.get());


        return arguments;
    }

    public static void printCommandLineArguments() {
        var message = Arrays.stream(commandLineArguments).map(argument -> {
            var modifiers = Arrays.stream(argument.modifiers)
                    .collect(Collectors.joining(", "));
            return modifiers + "\t" + argument.description;
        }).collect(Collectors.joining("\n"));

        AppLogger.getInstance().info("Usage: java -jar <jar-file> [options]\n" +
                "Options:\n" +
                message);
    }

    public record CommandLineArgument<T>(String[] modifiers, String description, Optional<T> value) {
        public CommandLineArgument(final String[] modifiers, final String description) {
            this(modifiers, description, Optional.empty());
        }
    }

    public record ArgumentsParseResult(String dataPath, String propertiesPath) {
    }
}