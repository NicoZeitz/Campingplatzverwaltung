package swe.ka.dhbw.util;

import de.dhbwka.swe.utils.util.AppLogger;
import swe.ka.dhbw.control.Campingplatzverwaltung;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ArgumentParser {
    private static final CommandLineArgument<?>[] commandLineArguments = new CommandLineArgument<?>[] {
            new CommandLineArgument<Void>(new String[] {"-d", "--data"}, "The path to where all the data is stored."),
            new CommandLineArgument<Void>(new String[] {"-p", "--properties"}, "The path to the properties file."),
            new CommandLineArgument<Void>(new String[] {"-i", "--images"}, "The path to the images folder."),
            new CommandLineArgument<Void>(new String[] {"-h", "--help"}, "Prints this help message."),
            new CommandLineArgument<Void>(new String[] {"-v", "--version"}, "Prints the version of this program."),
            new CommandLineArgument<Void>(new String[] {"--skip-configuration"}, "Skips the configuration window.")
    };

    private ArgumentParser() {
    }

    public static ArgumentsParseResult parse(final String[] args) throws ArgumentParseException {
        var dataPath = Optional.<String>empty();
        var propertiesPath = Optional.<String>empty();
        var imagesPath = Optional.<String>empty();
        var skipConfiguration = Optional.<Boolean>empty();

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

            if (arg.equals("--skip-configuration")) {
                skipConfiguration = Optional.of(true);
                continue;
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

        return new ArgumentsParseResult(dataPath.get(), propertiesPath.get(), imagesPath.get(), skipConfiguration.orElse(false));
    }

    public static void printCommandLineArguments() {
        var message = Arrays
                .stream(commandLineArguments)
                .map(argument -> {
                    final var modifiers = String.join(", ", argument.modifiers);
                    return modifiers + "\t" + argument.description;
                })
                .collect(Collectors.joining("\n"));

        AppLogger.getInstance().info("""
                Usage: java -jar <jar-file> [options]
                Options:
                """ + message
        );

    }

    public record CommandLineArgument<T>(String[] modifiers, String description, Optional<T> value) {
        public CommandLineArgument(final String[] modifiers, final String description) {
            this(modifiers, description, Optional.empty());
        }
    }

    public record ArgumentsParseResult(String dataPath, String propertiesPath, String imagesPath, boolean skipConfiguration) {
    }
}