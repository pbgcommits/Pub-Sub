package Shared;

public enum GlobalCommand {
    HELP {
        public String[] getOptions() {
            String[] o = {"h", "help"};
            return o;
        }
        @Override
        public String toString() {
            return "help";
        }
        @Override
        public String getUsage() {
            return "h/help";
        }
        @Override
        public String getInfo() {
            return "show this list of commands";
        }
    },
    DISCONNECT {
        public String[] getOptions() {
            String[] o = {"d", "dc", "disconnect"};
            return o;
        }
        @Override
        public String toString() {
            return "d/dc/disconnect";
        }
        @Override
        public String getUsage() {
            return "d/dc/disconnect";
        }
        @Override
        public String getInfo() {
            return "disconnect from the network (warning: all saved info will be lost!)";
        }
    };
    public abstract String toString();
    public abstract String getUsage();
    public abstract String getInfo();
    public abstract String[] getOptions();
    public static String getGlobalCommandUsage() {
        StringBuilder sb = new StringBuilder();
        for (GlobalCommand g : GlobalCommand.values()) {
            sb.append(g.getUsage() + ": " + g.getInfo() + "\n");
        }
        return sb.toString();
    }
}
