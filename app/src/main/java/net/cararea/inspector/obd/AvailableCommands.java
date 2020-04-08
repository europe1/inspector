package net.cararea.inspector.obd;

import com.github.pires.obd.commands.ObdCommand;

import java.util.List;

public class AvailableCommands {
    private static List<ObdCommand> availableCommands;

    static List<ObdCommand> getCommands() {
        return  availableCommands;
    }

    public static void setCommands(List<ObdCommand> commands) {
        availableCommands = commands;
    }
}
