
public class Direction {
	public final static  int DirectionEast = 0;  // right
	public final static  int DirectionSouth = 1; //down
	public final static  int DirectionWest = 2;  // left
	public final static  int DirectionNorth = 3; // up
	public final static  int SearchEnd = 4; // searchEnd
	public final static  int DirectionParent = 5; // find your papa
	public final static  int parentNotSet = 6; // find your papa
	
	public final static  char redircetion[][] = 
			{{'F','R','R','L'},
			 {'L','F','R','R'},
			 {'R','L','F','R'},
			 {'R','R','L','F'}};
	/**                 EAST SOUTH WEST NORTH (Source)
	 *         EAST      F    R     R    L
	 *         SOUTH     L    F     R    R
	 *         WEST      R    L     F    R
	 *         NORTH     R    R     L    F
	 *         (Destiny)
	 * */
	public static int turnLeft(int currentDirection){
		return (currentDirection + 3) % 4;
	}
	public static int turnRight(int currentDirection){
		return (currentDirection + 1) % 4;
	}
	public static int turnBack(int currentDirection){
		return (currentDirection + 2) % 4;
	}
    public static char changeDirTo(int currentDirection, int desDirection){
        return Direction.redircetion[currentDirection][desDirection];
    }
}
