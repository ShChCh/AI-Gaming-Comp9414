
public class MapNode {
	public static char PATH = 0;
	public static char WALL = 1;
	public static char TREE = 2;
	public static char SEA = 3;
	public static char NONE = 4;
	
	public char NodeType = 0;

	public boolean reached = false; // the player has reached this Node before
	public boolean searched = false; // the player has reached this Node before

	public position position; // spanning tree
	public int parentNode = 0; // spanning tree
	public int wallParentNode = 0; // spanning tree
	public int boomNo = 0; // spanning tree
	
	public char SeaCode = 0;
	public char TreeCode = 0;
	public char WallCode = 0;
	public char PathCode = 0;
	
	// $ k d - a
//	public char TreasureFlag = 0;
//	public char KeyFlag = 0;
//	public char DynamiteFlag = 0;
//	public char DoorFlag = 0;
//	public char AxeFlag = 0;
}
