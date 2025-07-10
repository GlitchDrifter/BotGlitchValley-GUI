package za.co.wethinkcode.robots;

/**
 * The Position class represents a position in a 2D space.
 * It contains x and y coordinates and provides methods to access them.
 */
public class Position {
  private final int x;
  private final int y;

  /**
   * Constructor to initialize the position with x and y coordinates.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public Position(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  /**
   * Checks if the current position is equal to another position.
   *
   * @param o the object to compare with
   * @return true if the positions are equal, false otherwise
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    final Position position = (Position) o;

    if (x != position.x)
      return false;
    return y == position.y;
  }

  /**
   * Returns the hash code of the position.
   *
   * @return the hash code of the position
   */
  @Override
  public String toString() {
    return "(x: " + x + ", y: " + y + ")";
  }

  /**
   * Checks if the current position is within a specified rectangular area.
   *
   * @param topLeft     the top-left corner of the rectangle
   * @param bottomRight the bottom-right corner of the rectangle
   * @return true if the position is within the rectangle, false otherwise
   */
  public boolean isIn(final Position topLeft, final Position bottomRight) {
    final boolean withinTop = y >= topLeft.getY();
    final boolean withinBottom = y <= bottomRight.getY();
    final boolean withinLeft = x >= topLeft.getX();
    final boolean withinRight = x <= bottomRight.getX();
    return withinTop && withinBottom && withinLeft && withinRight;
  }

  /**
   * Calculates the distance between this position and anotherPosition
   * 
   * @param anotherPosition A position to calculate distance from.
   * @param direction       direction of anotherPosition from this position.
   * @return The distance between this position and anotherPosition.
   */
  public int distanceFrom(final Position anotherPosition, final Direction direction) {
    return switch (direction) {
      case NORTH, SOUTH -> Math.abs(y - anotherPosition.getY());
      case EAST, WEST -> Math.abs((x - anotherPosition.getX()));

    };
  }

  /**
   * Calculates a new position based on the current robot's direction and the
   * number of steps.
   * 
   * @param nrSteps The number of steps to move.
   * @return The new position after moving.
   */
  public Position newPos(final Direction direction, final int nrSteps) {
    int newX = x;
    int newY = y;
    switch (direction) {
      case NORTH -> newY -= nrSteps;
      case SOUTH -> newY += nrSteps;
      case WEST -> newX -= nrSteps;
      case EAST -> newX += nrSteps;
    }

    return new Position(newX, newY);
  }
}
