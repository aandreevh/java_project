package hykar.api.server.command.enums;

public enum CommandStatus {

    SUCCESS(0), INVALID_COMMAND(1), INVALID_ARGUMENTS(2), ERROR(3), DISCONNECT(-1);

    private long status = 0;

    CommandStatus(long status) {
        this.status = status;
    }

    public static CommandStatus fromStatusCode(long status) {
        for (CommandStatus s : CommandStatus.values())
            if (s.getStatus() == status) return s;

        return null;
    }

    public long getStatus() {
        return status;
    }


}
