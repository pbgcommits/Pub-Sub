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

    public enum PublisherCommand {
        CREATE {
            @Override
            public String toString() {
                return "create";
            }
            @Override
            public String getUsage() {
                return "create {topic_name}";
            }
            @Override
            public String getInfo() {
                return "create a new topic";
            }
        },
        PUBLISH {
            @Override
            public String toString() {
                return "publish";
            }
            @Override
            public String getUsage() {
                return "publish {topic_id} {message}";
            }
            @Override
            public String getInfo() {
                return "publish a message to the given topic";
            }
        },
        SHOW {
            @Override
            public String toString() {
                return "show";
            }
            @Override
            public String getUsage() {
                return "show {topic_id}";
            }
            @Override
            public String getInfo() {
                return "show the number of subscribers to the given topic";
            }
        },
        DELETE {
            @Override
            public String toString() {
                return "delete";
            }
            @Override
            public String getUsage() {
                return "delete {topic_id}";
            }
            @Override
            public String getInfo() {
                return "delete the given topic from the network";
            }
        };
        public abstract String toString();
        public abstract String getUsage();
        public abstract String getInfo();
        public static String getPublisherCommandUsage() {
            StringBuilder sb = new StringBuilder();
            for (PublisherCommand p : PublisherCommand.values()) {
                sb.append(p.getUsage() + ": " + p.getInfo() + "\n");
            }
            return sb.toString();
        }
    }
}
