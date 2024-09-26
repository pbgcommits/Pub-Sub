package Shared;

public class InputVerifier {
    public final String PORT_ERROR_MESSAGE = "Port number must be between 0 and 65535 (inclusive).";
    public int verifyPort(String[] args, int index, int expectedArgsLength, String usageMessage)
            throws IllegalArgumentException {
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

    public static int verifyTopicId(String[] input, int index, int numArguments, String usage) throws IllegalArgumentException {
        String prefixedUsage = "Usage: " + usage;
        if (input.length != numArguments) {
            throw new IllegalArgumentException(prefixedUsage);
        }
        try {
            return Integer.parseInt(input[index]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(prefixedUsage);
        }
    }
}
