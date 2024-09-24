package subscriber;

import publisher.PublisherCommand;

public enum SubscriberCommand {
    LIST {
        @Override
        public String toString() {
            return "list";
        }
        @Override
        public String getUsage() {
            return "list";
        }
        @Override
        public String getInfo() {
            return "show all available topics to be subscribed to";
        }
    },
    SUB {
        @Override
        public String toString() {
            return "sub";
        }
        @Override
        public String getUsage() {
            return "sub {topic_id}";
        }
        @Override
        public String getInfo() {
            return "subscribe to the topic with the given id";
        }
    },
    CURRENT {
        @Override
        public String toString() {
            return "current";
        }
        @Override
        public String getUsage() {
            return "current";
        }
        @Override
        public String getInfo() {
            return "show all topics to which you are currently subscribed";
        }
    },
    UNSUB {
        @Override
        public String toString() {
            return "unsub";
        }
        @Override
        public String getUsage() {
            return "unsub {topic_id}";
        }
        @Override
        public String getInfo() {
            return "unsubscribe from the given topic";
        }
    };
    public abstract String toString();
    public abstract String getUsage();
    public abstract String getInfo();
    public static String getSubscriberCommandUsage() {
        StringBuilder sb = new StringBuilder();
        for (SubscriberCommand s : SubscriberCommand.values()) {
            sb.append(s.getUsage() + ": " + s.getInfo() + "\n");
        }
        return sb.toString();
    }
}
