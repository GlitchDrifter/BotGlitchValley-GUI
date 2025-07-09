package za.co.wethinkcode.robots;

/**
 * The UpdateResponse enum represents the possible responses to an update request.
 * It is used to indicate the result of an update operation in the robot's world.
 */
public enum UpdateResponse {
    SUCCESS,
    FAILURE_OUT_OF_BOUNDS,
    FAILURE_OBSTRUCTED,
    DIED_FELL_IN_PIT
}
