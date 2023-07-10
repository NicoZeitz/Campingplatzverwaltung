package swe.ka.dhbw.util;

import de.dhbwka.swe.utils.util.AppLogger;
import swe.ka.dhbw.control.Campingplatzverwaltung;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ArgumentParser {
    private static final CommandLineArgument[] commandLineArguments = new CommandLineArgument[] {
            new CommandLineArgument(new String[] {"-d", "--data"}, "The path to where all the data is stored."),
            new CommandLineArgument(new String[] {"-p", "--properties"}, "The path to the properties file."),
            new CommandLineArgument(new String[] {"-i", "--images"}, "The path to the images folder."),
            new CommandLineArgument(new String[] {"-h", "--help"}, "Prints this help message."),
            new CommandLineArgument(new String[] {"-v", "--version"}, "Prints the version of this program.")
    };

    private ArgumentParser() {
    }

    public static ArgumentsParseResult parse(final String[] args) throws ArgumentParseException {
        Optional<String> dataPath = Optional.empty();
        Optional<String> propertiesPath = Optional.empty();
        Optional<String> imagesPath = Optional.empty();

        var currentModifier = "";
        for (var arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                printCommandLineArguments();
                System.exit(0);
            }

            if (arg.equals("-v") || arg.equals("--version")) {
                AppLogger.getInstance().info("Version: " + Campingplatzverwaltung.VERSION);
                System.exit(0);
            }

            if (arg.equals("--data") || arg.equals("-d")) {
                currentModifier = "-d";
                continue;
            }

            if (arg.equals("--properties") || arg.equals("-p")) {
                currentModifier = "-p";
                continue;
            }

            if (arg.equals("--images") || arg.equals("-i")) {
                currentModifier = "-i";
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

            if (currentModifier.equals("-i")) {
                if (imagesPath.isPresent()) {
                    throw new ArgumentParseException("You can only specify one images path.");
                }
                imagesPath = Optional.of(arg);
            }

            currentModifier = "";
        }

        if (dataPath.isEmpty()) {
            throw new ArgumentParseException("You have to specify a data path.");
        }
        if (propertiesPath.isEmpty()) {
            throw new ArgumentParseException("You have to specify a properties path.");
        }
        if (imagesPath.isEmpty()) {
            throw new ArgumentParseException("You have to specify a images path.");
        }

        var arguments = new ArgumentsParseResult(dataPath.get(), propertiesPath.get(), imagesPath.get());
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

    public record ArgumentsParseResult(String dataPath, String propertiesPath, String imagesPath) {
    }
}