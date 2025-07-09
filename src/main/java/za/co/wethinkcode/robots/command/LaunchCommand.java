package za.co.wethinkcode.robots.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import za.co.wethinkcode.robots.Position;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;
import java.util.Random;
import static za.co.wethinkcode.robots.config.Config.*;
import static za.co.wethinkcode.robots.client.Client.formatState;

/**
 * The Launch class represents a command to launch a robot in the world.
 * It extends the Command class and implements the execute method to perform the launch action.
 */
public class LaunchCommand extends Command {
    private static LaunchCommand instance;

    /**
     * Private constructor to create a Launch command with a name and arguments.
     *
     * @param argument the robot name
     * @param arguments the JsonArray containing robot type
     */
    private LaunchCommand(String argument, JsonArray arguments) {
        super("launch", argument, arguments);
    }

    /**
     * Gets the singleton instance with the given robot name and arguments.
     *
     * @param robotName the name of the robot to launch
     * @param arguments the JsonArray containing robot type
     * @return The singleton instance of LaunchCommand configured with the specified arguments
     */
    public static synchronized LaunchCommand getInstance(String robotName, JsonArray arguments) {
        if (instance == null) {
            instance = new LaunchCommand(robotName, arguments);
        } else {
            setArguments(instance, robotName, arguments);
        }
        return instance;
    }


    /**
     * Helper method to set the arguments for an existing LaunchCommand instance.
     * 
     * @param instance the LaunchCommand instance to update
     * @param robotName the new robot name
     * @param arguments the new JsonArray of arguments
     */
    private static void setArguments(LaunchCommand instance, String robotName, JsonArray arguments) {
        try {
            java.lang.reflect.Field argumentField = Command.class.getDeclaredField("argument");
            java.lang.reflect.Field argumentsField = Command.class.getDeclaredField("arguments");

            argumentField.setAccessible(true);
            argumentsField.setAccessible(true);

            argumentField.set(instance, robotName);
            argumentsField.set(instance, arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a Launch command with the specified name.
     * The name is set to "launch".
     */
    @Override
    public JsonObject execute(World world) {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        String robotType = getArguments().get(0).getAsString();
        if (world.getBots().stream().noneMatch(r -> r.getName().equals(getArgument()))) {
            Robot newRobot = new Robot(getArgument(), robotType);
            world.setCurrentRobot(newRobot);
            world.addRobot(newRobot);
            Position randPos;
            Random rand = new Random();
            while (true){
                randPos = new Position(rand.nextInt(HEIGHT), rand.nextInt(WIDTH));
                if (world.isLaunchAllowed(randPos)){
                    newRobot.setPosition(randPos);
                    break;
                }

            }
            System.out.println("Robot "+newRobot.getName() +  " launched into random Position: \n" + formatState(newRobot.state()));

            response.addProperty("result", "OK");
            data.addProperty("message", "Robot '" + getArgument() + "' of type '" + robotType + "' launched.");
        }
        response.add("data", data);
        return response;
    }
}
