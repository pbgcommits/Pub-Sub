package shared.util;

/**
 * Verifies user input.
 * @author Patrick Barton Grace 1557198
 */
public class InputVerifier {
    /**
     * Read in a list of arguments from a user and verify that a valid port number is in the correct position.
     * @param args The list of arguments.
     * @param index The index at which a valid port number should be found.
     * @param expectedArgsLength The number of arguments which should be passed in
     * @param usage The usage message to display if the arguments are invalid.
     * @return The valid port number, parsed to an integer.
     * @throws IllegalArgumentException If the given arguments are used incorrectly.
     */
    public int verifyPort(String[] args, int index, int expectedArgsLength, String usage)
            throws IllegalArgumentException {
        final String PORT_ERROR_MESSAGE = "Port number must be between 0 and 65535 (inclusive).";
        String usageMessage = "Usage: " + usage;
        if (args.length != expectedArgsLength) {
            throw new IllegalArgumentException(usageMessage);
        }
        try {
            int port = Integer.parseInt(args[index]);
            if (0 > port || port > 65535) {
                throw new IllegalArgumentException(PORT_ERROR_MESSAGE);
            }
            return port;
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(usageMessage);
        }
    }

}
