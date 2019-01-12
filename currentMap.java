
public class currentMap {
	
	public MapNode[][] map;
	
	public static int maxLen = 162;
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	public currentMap(){
		map = new MapNode[maxLen][maxLen];
		for(int i=0; i<maxLen; i++)
			for(int j=0; j<maxLen; j++){
				map[i][j] = new MapNode();
				map[i][j].position = new position();
				map[i][j].NodeType = MapNode.NONE;
				map[i][j].parentNode = Direction.parentNotSet;
				map[i][j].position.posX = i;
				map[i][j].position.posY = j;
			}
	}
	public MapNode getMapNode(int x, int y){
		return map[x][y];
	}
}