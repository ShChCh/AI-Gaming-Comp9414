import java.util.*;


public class playerQ {

	public currentMap myMap;
	public position currentPos;
	
	public int fwdDirection = 0;
	
	public int hasAxe = 0;
	public int hasDynamite = 0;
	public int hasKey = 0;
	public int hasTreasure = 0;
	public int hasRaft = 0;
	
	// temp testing
	public int[] wallNo = {0,0,0,0,0,0,0,0,0};
	
	public int inTheSea = 0;

	public List<position> doorsUnopened = new ArrayList<position>();

	public List<position> axePositions = new ArrayList<position>();

	public List<position> keyPositions = new ArrayList<position>();

	public List<position> dynamitePositions = new ArrayList<position>();

	public List<position> treePositions = new ArrayList<position>();

	public List<position> toSeaPositions = new ArrayList<position>();
	
	public List<position> seasidePositions = new ArrayList<position>();

	public List<position> seaTreePositions = new ArrayList<position>();

	public position landLeavePosition = new position();
	// 宝藏就一个
	public position treasurePosition = null;
	
	// 这玩意用来存我的目标节点，炸的那种
	public List<position> targetPos = new ArrayList<position>();
	public int targetType = -1; // 0: d ; 1:t ; 2: 下海; 3：上岸

	// 拿个宝藏需要多少个d
	public int dForTreasure = 6500;
	
	// 我每个还有炸弹的区域，是需要多少个炸弹才能进去
	public List<Integer> pNo = new ArrayList<Integer>();
	public int axeNo = 0;
	
	public playerQ(){
		currentPos = new position();
		myMap = new currentMap();
		fwdDirection = Direction.DirectionNorth;
        NodeAt(0,0).parentNode = Direction.SearchEnd;
	}

	// --------------- Map Info Funcs Begin -------------------------

	
	public boolean toolsJudge(List<position> toolList, int x, int y){
		boolean ret = false;

		for(int i=0; i<toolList.size(); i++){
			if(toolList.get(i).posX == x && 
			   toolList.get(i).posY == y){
				ret = true;
				break;
			}
		}
		return ret;
	}

	public void toolsRemove(List<position> toolList, int x, int y){
		for(int i=0; i<toolList.size(); i++){
			if(toolList.get(i).posX == x && 
			   toolList.get(i).posY == y){
				toolList.remove(i);
			}
		}
	}
	// get out boom position
	// 从封闭区域往外搜
	// update walls boomNo and spanning tree
	// 搜索完以后，给每个目标标记一串点，如果从这串点开始炸，炸到目标所需炸药量最少
	public List<position> getBoomPosition(position targ, int setTreasureD){
		resetSearched();
		int minBoomNo = 1;
		List<position> ret = new ArrayList<position>();
		List<position> wallList = new ArrayList<position>();
		Queue<position> bfs = new LinkedList<position>();
		position cur;
		bfs.offer(targ);
		while(bfs.size()!=0){
			cur = bfs.poll();
			int x = cur.posX;
			int y = cur.posY;
			myMap.getMapNode(x, y).searched = true;
			if(!myMap.map[x-1][y].searched){
				if(myMap.map[x-1][y].NodeType==MapNode.WALL && !toolsJudge(wallList,x-1,y)) wallList.add(new position(x-1,y));
				if(myMap.map[x-1][y].NodeType==MapNode.PATH) bfs.offer(new position(x-1,y));
			}
			if(!myMap.map[x+1][y].searched){
				if(myMap.map[x+1][y].NodeType==MapNode.WALL && !toolsJudge(wallList,x+1,y)) wallList.add(new position(x+1,y));
				if(myMap.map[x+1][y].NodeType==MapNode.PATH) bfs.offer(new position(x+1,y));
			}
			if(!myMap.map[x][y-1].searched){
				if(myMap.map[x][y-1].NodeType==MapNode.WALL && !toolsJudge(wallList,x,y-1)) wallList.add(new position(x,y-1));
				if(myMap.map[x][y-1].NodeType==MapNode.PATH) bfs.offer(new position(x,y-1));
			}
			if(!myMap.map[x][y+1].searched){
				if(myMap.map[x][y+1].NodeType==MapNode.WALL && !toolsJudge(wallList,x,y+1)) wallList.add(new position(x,y+1));
				if(myMap.map[x][y+1].NodeType==MapNode.PATH) bfs.offer(new position(x,y+1));
			}
		}
		bfs.clear();
		int flag = 0;
		while(wallList.size()>0){
			
			for(int i=0; i<wallList.size(); i++){
				myMap.getMapNode(wallList.get(i).posX, wallList.get(i).posY).searched = true;
				myMap.getMapNode(wallList.get(i).posX, wallList.get(i).posY).boomNo = minBoomNo;
			}
			List<position> nextLevelWalls = new ArrayList<position>();
			for(int j=0;  j<wallList.size(); j++){
				int x = wallList.get(j).posX;
				int y = wallList.get(j).posY;
				
				// reached outside with a tree
				if(myMap.getMapNode(x-1, y).reached ){
					flag = 1;
					if(!toolsJudge(ret,x,y)) ret.add(new position(x,y)); 
				}
				if(myMap.getMapNode(x+1, y).reached ){
					flag = 1; 
					if(!toolsJudge(ret,x,y)) ret.add(new position(x,y));
				}
				if(myMap.getMapNode(x, y-1).reached ){
					flag = 1; 
					if(!toolsJudge(ret,x,y)) ret.add(new position(x,y));
				}
				if(myMap.getMapNode(x, y+1).reached ){
					flag = 1; 
					if(!toolsJudge(ret,x,y)) ret.add(new position(x,y));
				}
				
					
				// add new walls
				if(!myMap.getMapNode(x-1, y).searched && myMap.getMapNode(x-1, y).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x-1,y));
				if(!myMap.getMapNode(x+1, y).searched && myMap.getMapNode(x+1, y).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x+1,y));
				if(!myMap.getMapNode(x, y-1).searched && myMap.getMapNode(x, y-1).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x,y-1));
				if(!myMap.getMapNode(x, y+1).searched && myMap.getMapNode(x, y+1).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x,y+1));
			}
			if(flag == 1){
				System.out.println("fFFFUUUUCCCKKK");
				break;
			}
			wallList.clear();
			wallList = nextLevelWalls;
			minBoomNo ++;
		}
//		System.out.println("===Inside Size: "+insideBoomPos.size()+" ===");
		//get out position
		// 看看当前位置，想去找某个目标，从哪开始炸
		if(flag==1){
			if(setTreasureD==1)
				dForTreasure = minBoomNo;
			if(setTreasureD==0)
				pNo.add(minBoomNo);
			if(setTreasureD==2)
				axeNo = minBoomNo;
		}
		return ret;
	}
	public void redirPt(position target){
		MapNode tmpNode = myMap.map[target.posX][target.posY];
		int availableType = MapNode.PATH;
		if(inTheSea==1)
			availableType = MapNode.SEA;
		if(myMap.map[target.posX-1][target.posY].reached && myMap.map[target.posX-1][target.posY].NodeType==availableType) tmpNode.parentNode = Direction.DirectionNorth;
		if(myMap.map[target.posX+1][target.posY].reached && myMap.map[target.posX+1][target.posY].NodeType==availableType) tmpNode.parentNode = Direction.DirectionSouth;
		if(myMap.map[target.posX][target.posY-1].reached && myMap.map[target.posX][target.posY-1].NodeType==availableType) tmpNode.parentNode = Direction.DirectionWest;
		if(myMap.map[target.posX][target.posY+1].reached && myMap.map[target.posX][target.posY+1].NodeType==availableType) tmpNode.parentNode = Direction.DirectionEast;
	}
	// 只针对s8，当宝藏那块和炸弹有交集的时候，炸一炸
	public void Intersection(List<position> treasurePos,List<List<position>> dPos){
		List<position> potentialTarget = new ArrayList<position>();
		List<position> intersectionList = new ArrayList<position>();
		List<Integer> boomNeeds = new ArrayList<Integer>();
		for(int m=0; m<treasurePos.size(); m++){
			if(!toolsJudge(potentialTarget,treasurePos.get(m).posX,treasurePos.get(m).posY))
				potentialTarget.add(treasurePos.get(m));
		}
		System.out.println(" Intersectin 1 ");
		// 找交点
		for(int i=0; i<dPos.size(); i++){
			List<position> childList = dPos.get(i);
			for(int m=0; m<childList.size(); m++){
				if(!toolsJudge(potentialTarget,childList.get(m).posX,childList.get(m).posY))
					potentialTarget.add(childList.get(m));
				else{
					intersectionList.add(childList.get(m));
					boomNeeds.add(pNo.get(i));
				}
			}
		}
		System.out.println(" Intersectin 2 ");
		// 调方向
//		for(int j=0; j<intersectionList.size(); j++){
//			position target = intersectionList.get(j);
//			redirPt(target);
//		}
		potentialTarget.clear();
		for(int k=0; k<intersectionList.size(); k++)
			if(boomNeeds.get(k) <= hasDynamite)
				potentialTarget.add(intersectionList.get(k));

		System.out.println(" Intersectin 3 ");

		for(int k=0; k<potentialTarget.size(); k++)
			System.out.println("potential : x="+potentialTarget.get(k).posX+
					" y="+potentialTarget.get(k).posY);
		System.out.println(" Intersectin 3 ");
		position target = reachList(potentialTarget,currentPos);
		System.out.println(" Intersectin 3-1 ");
		if(target!=null){
			System.out.println(" target D 1 ");
			targetPos.add(target);
			redirPt(target);
			return;
		}
		

		System.out.println(" Intersectin 4 ");
		if(targetPos.size()==0){
			for(int i=0; i<pNo.size(); i++){
				if(pNo.get(i) <= hasDynamite){
					List<position> childList = dPos.get(i);
					if(childList.size()>0){
//						System.out.println(" target D 2 ");
						System.out.println(pNo.get(i)+" target D 2 "+hasDynamite);
						target = childList.get(0);
						redirPt(target);
						targetPos.add(childList.get(0));
						return;
					}
				}
			}
		}
	}
	public void available(Queue<position> bfs, List<position> l){
		position nextPos = bfs.poll();
		int x = nextPos.posX;
		int y = nextPos.posY;
//		System.out.println("==Input=="+nextPos.posX+":"+nextPos.posY);
		myMap.getMapNode(x, y).searched = true;
		if(toolsJudge(doorsUnopened, x, y) && hasKey==0){
			System.out.println("heihei----------");
			return;
		}
		// 能通到tree 的 seaside pt
		l.add(new position(x,y));
//		System.out.println("==Add Links=="+x+":"+y);
		
		if(!myMap.getMapNode(x-1, y).searched && myMap.getMapNode(x-1, y).NodeType==MapNode.PATH) bfs.offer(new position(x-1,y));
		if(!myMap.getMapNode(x+1, y).searched && myMap.getMapNode(x+1, y).NodeType==MapNode.PATH) bfs.offer(new position(x+1,y));
		if(!myMap.getMapNode(x, y-1).searched && myMap.getMapNode(x, y-1).NodeType==MapNode.PATH) bfs.offer(new position(x,y-1));
		if(!myMap.getMapNode(x, y+1).searched && myMap.getMapNode(x, y+1).NodeType==MapNode.PATH) bfs.offer(new position(x,y+1));
	}
	
	public boolean wallOnLand(position targetP, List<position> treeList){
		Queue<position> bfs = new LinkedList<position>();
		List<position> wallList = new ArrayList<position>(); // 内层墙
		bfs.offer(new position(targetP.posX,targetP.posY));
		while(bfs.size()>0){
			position p = bfs.poll();	
			int x = p.posX;
			int y = p.posY;
			myMap.getMapNode(x, y).searched = true;
			if(!myMap.getMapNode(x-1, y).searched){
				if(myMap.getMapNode(x-1, y).NodeType==MapNode.WALL && !toolsJudge(wallList,x-1,y)) 
					wallList.add(new position(x-1,y));
				if(myMap.getMapNode(x-1, y).NodeType==MapNode.PATH) bfs.offer(new position(x-1,y));
			}
			if(!myMap.getMapNode(x+1, y).searched){
				if(myMap.getMapNode(x+1, y).NodeType==MapNode.WALL && !toolsJudge(wallList,x+1,y)) 
					wallList.add(new position(x+1,y));
				if(myMap.getMapNode(x+1, y).NodeType==MapNode.PATH) bfs.offer(new position(x+1,y));
			}
			if(!myMap.getMapNode(x, y-1).searched){
				if(myMap.getMapNode(x, y-1).NodeType==MapNode.WALL && !toolsJudge(wallList,x,y-1)) 
					wallList.add(new position(x,y-1));
				if(myMap.getMapNode(x, y-1).NodeType==MapNode.PATH) bfs.offer(new position(x,y-1));
			}
			if(!myMap.getMapNode(x, y+1).searched){
				if(myMap.getMapNode(x, y+1).NodeType==MapNode.WALL && !toolsJudge(wallList,x,y+1)) 
					wallList.add(new position(x,y+1));
				if(myMap.getMapNode(x, y+1).NodeType==MapNode.PATH) bfs.offer(new position(x,y+1));
			}
		}
		int currentBoomNo = 1;
//		System.out.println("===inside walls===");
//		for(int i=0; i<wallList.size(); i++)
//			System.out.println(" p x:"+wallList.get(i).posX+" y:"+wallList.get(i).posY);
//		System.out.println("===inside walls===");
		while(wallList.size()>0){
			for(int i=0; i<wallList.size(); i++){
				myMap.getMapNode(wallList.get(i).posX, wallList.get(i).posY).searched = true;
				myMap.getMapNode(wallList.get(i).posX, wallList.get(i).posY).boomNo = currentBoomNo;
			}
			List<position> nextLevelWalls = new ArrayList<position>();
			for(int j=0;  j<wallList.size(); j++){
				int x = wallList.get(j).posX;
				int y = wallList.get(j).posY;
				
				// reached outside with a tree
				if(myMap.getMapNode(x-1, y).searched && toolsJudge(treeList, x-1, y) && currentBoomNo<=hasDynamite) return true;
				if(myMap.getMapNode(x+1, y).searched && toolsJudge(treeList, x+1, y) && currentBoomNo<=hasDynamite) return true;
				if(myMap.getMapNode(x, y-1).searched && toolsJudge(treeList, x, y-1) && currentBoomNo<=hasDynamite) return true;
				if(myMap.getMapNode(x, y+1).searched && toolsJudge(treeList, x, y+1) && currentBoomNo<=hasDynamite) return true;
			
				// add new walls
				if(!myMap.getMapNode(x-1, y).searched && myMap.getMapNode(x-1, y).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x-1,y));
				if(!myMap.getMapNode(x+1, y).searched && myMap.getMapNode(x+1, y).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x+1,y));
				if(!myMap.getMapNode(x, y-1).searched && myMap.getMapNode(x, y-1).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x,y-1));
				if(!myMap.getMapNode(x, y+1).searched && myMap.getMapNode(x, y+1).NodeType==MapNode.WALL) nextLevelWalls.add(new position(x,y+1));
			}
			wallList.clear();
			wallList = nextLevelWalls;
			currentBoomNo ++;
		}
//		System.out.println("===get out===");
//		for(int i=0; i<treeList.size(); i++)
//			System.out.println(" x: "+treeList.get(i).posX+" y:"+treeList.get(i).posY+" SEARCH: "
//					+myMap.getMapNode(treeList.get(i).posX, treeList.get(i).posY).searched);
//		System.out.println("===get out===");
//		System.out.println("HasD"+hasDynamite);
//		System.out.println("===get out===");
		return false;
	}
	public boolean mergeTreeBySea(List<position> tree1, List<position> tree2){

		Queue<position> bfs = new LinkedList<position>();
		resetSearched();
		for(int i=0; i<tree1.size(); i++)
			bfs.offer(tree1.get(i));
		while(bfs.size()>0){
			position p = bfs.poll();
			int x = p.posX;
			int y = p.posY;
			
			myMap.getMapNode(x, y).searched = true;

			// add new walls
			if(!myMap.getMapNode(x-1, y).searched &&!myMap.getMapNode(x-1, y).reached && myMap.getMapNode(x-1, y).NodeType==MapNode.SEA) bfs.add(new position(x-1,y));
			if(!myMap.getMapNode(x+1, y).searched &&!myMap.getMapNode(x+1, y).reached && myMap.getMapNode(x+1, y).NodeType==MapNode.SEA) bfs.add(new position(x+1,y));
			if(!myMap.getMapNode(x, y-1).searched &&!myMap.getMapNode(x, y-1).reached && myMap.getMapNode(x, y-1).NodeType==MapNode.SEA) bfs.add(new position(x,y-1));
			if(!myMap.getMapNode(x, y+1).searched &&!myMap.getMapNode(x, y+1).reached && myMap.getMapNode(x, y+1).NodeType==MapNode.SEA) bfs.add(new position(x,y+1));
		
		}
		bfs.clear();

		for(int i=0; i<tree2.size(); i++)
			bfs.offer(tree2.get(i));

		while(bfs.size()>0){
			position p = bfs.poll();
			int x = p.posX;
			int y = p.posY;
			
			myMap.getMapNode(x, y).searched = true;

			// add new walls
			if(myMap.getMapNode(x-1, y).searched &&!myMap.getMapNode(x-1, y).reached && myMap.getMapNode(x-1, y).NodeType==MapNode.SEA) return true;
			if(myMap.getMapNode(x+1, y).searched &&!myMap.getMapNode(x+1, y).reached && myMap.getMapNode(x+1, y).NodeType==MapNode.SEA) return true;
			if(myMap.getMapNode(x, y-1).searched &&!myMap.getMapNode(x, y-1).reached && myMap.getMapNode(x, y-1).NodeType==MapNode.SEA) return true;
			if(myMap.getMapNode(x, y+1).searched &&!myMap.getMapNode(x, y+1).reached && myMap.getMapNode(x, y+1).NodeType==MapNode.SEA) return true;
		
		}
		return false;
	}
	public void findSeasidePt(){
		Queue<position> bfs = new LinkedList<position>();
		List<position> treeList = new ArrayList<position>(); // 各TREE所达的点
		List<List<position>> mergedTreeList = new ArrayList<List<position>>();

		for(int i=0; i<seaTreePositions.size(); i++){
			resetSearched();
			treeList.clear();
			bfs.offer(new position(seaTreePositions.get(i).posX, seaTreePositions.get(i).posY));
			while(bfs.size()>0)
				available(bfs,treeList);
			List<position> tmpToAdd = new ArrayList<position>();
			tmpToAdd.addAll(treeList);
			mergedTreeList.add(tmpToAdd);
		}// 全部当前T的联通集
		
		//合并通过路联通的
		for(int i=0; i<mergedTreeList.size(); i++){
			List<position> currTreeList = mergedTreeList.get(i);
			for(int j=0; j<i; j++){
				List<position> prevList = mergedTreeList.get(j);
				for(int m=0; m<prevList.size(); m++){
					if(toolsJudge(currTreeList, prevList.get(m).posX,  prevList.get(m).posY)){
						for(int k=0; k<currTreeList.size(); k++)
							if(!toolsJudge(prevList, currTreeList.get(k).posX,  currTreeList.get(k).posY))
								prevList.add(new position(currTreeList.get(k).posX,  currTreeList.get(k).posY));
						mergedTreeList.remove(i);
						i=0;
						break;
					}	
				}
				if(i==0) break;			
			}
		}
		
		// 合并通过海联通的
		for(int i=0; i<mergedTreeList.size(); i++){
			List<position> currTreeList = mergedTreeList.get(i);
			for(int j=0; j<i; j++){
				List<position> prevList = mergedTreeList.get(j);
				for(int m=0; m<prevList.size(); m++){
					if(mergeTreeBySea(currTreeList, prevList)){
						for(int k=0; k<currTreeList.size(); k++)
							if(!toolsJudge(prevList, currTreeList.get(k).posX,  currTreeList.get(k).posY))
								prevList.add(new position(currTreeList.get(k).posX,  currTreeList.get(k).posY));
						mergedTreeList.remove(i);
						i=0;
						break;
					}	
				}
				if(i==0) break;			
			}
		}
		
		// 直接拿宝藏
		if(treasurePosition!=null) {
			for(int i=0; i<mergedTreeList.size(); i++){
				if(toolsJudge(mergedTreeList.get(i),treasurePosition.posX, treasurePosition.posY)){
					for(int j=0; j<seasidePositions.size();j++){
						if(toolsJudge(mergedTreeList.get(i),seasidePositions.get(j).posX,seasidePositions.get(j).posY)){
							targetPos.add(seasidePositions.get(j));
							return;
						}
					}
				}
			}
		}

		// 岛上可以直接拿到KEY
		if(keyPositions.size()>0) {
			for(int k=0;k<keyPositions.size();k++){
				for(int i=0; i<mergedTreeList.size(); i++){
					if(toolsJudge(mergedTreeList.get(i),keyPositions.get(k).posX, keyPositions.get(k).posY)){
						for(int j=0; j<seasidePositions.size();j++){
							if(toolsJudge(mergedTreeList.get(i),seasidePositions.get(j).posX,seasidePositions.get(j).posY)){
								targetPos.add(seasidePositions.get(j));
								return;
							}
						}
					}
				}
			}
		}
		// 岛上可以直接拿到炸弹
		if(dynamitePositions.size()>0) {
			for(int m=0; m<dynamitePositions.size(); m++){
				for(int i=0; i<mergedTreeList.size(); i++){
					if(toolsJudge(mergedTreeList.get(i),dynamitePositions.get(m).posX, dynamitePositions.get(m).posY)){
						for(int j=0; j<seasidePositions.size();j++){
							if(toolsJudge(mergedTreeList.get(i),seasidePositions.get(j).posX,seasidePositions.get(j).posY)){
								targetPos.add(seasidePositions.get(j));
								return;
							}
						}
					}
				}
			}
		}

		System.out.println("== Seaside Trees ==");
		System.out.println("== D pos ==");
		for(int i=0; i<dynamitePositions.size();i++)
			System.out.println(" x: "+dynamitePositions.get(i).posX+" y:"+dynamitePositions.get(i).posY);
		System.out.println("== D pos ==");
//		System.out.println("=="+mergedTreeList.size()+"==");
//		for(int i=0; i<mergedTreeList.get(0).size(); i++)
//			System.out.println(" x: "+mergedTreeList.get(0).get(i).posX+" y:"+mergedTreeList.get(0).get(i).posY);
//		System.out.println("== mergedTreeList pos ==");
		System.out.println("== Seaside Trees ==");
		// 要开墙的treasure
		if(treasurePosition!=null) {
			for(int i=0; i<mergedTreeList.size(); i++){
				resetSearched();
				System.out.println("tag4");
				for(int ite=0; ite<mergedTreeList.get(i).size(); ite++)
					myMap.getMapNode(mergedTreeList.get(i).get(ite).posX, mergedTreeList.get(i).get(ite).posY).searched = true;
				if(wallOnLand(treasurePosition, mergedTreeList.get(i))){
					System.out.println("tag1");
					for(int j=0; j<seasidePositions.size();j++){
						if(toolsJudge(mergedTreeList.get(i),seasidePositions.get(j).posX,seasidePositions.get(j).posY)){
							targetPos.add(seasidePositions.get(j));
							return;
						}
					}
				}
			}
		}
		// 要开墙的D
		if(dynamitePositions.size()>0) {
			for(int k=0; k<dynamitePositions.size(); k++){
				for(int i=0; i<mergedTreeList.size(); i++){
					resetSearched();
					System.out.println("tag3");
					for(int ite=0; ite<mergedTreeList.get(i).size(); ite++)
						myMap.getMapNode(mergedTreeList.get(i).get(ite).posX, mergedTreeList.get(i).get(ite).posY).searched = true;
					if(wallOnLand(dynamitePositions.get(k), mergedTreeList.get(i))){
						System.out.println("tag2");
						for(int j=0; j<seasidePositions.size();j++){
							if(toolsJudge(mergedTreeList.get(i),seasidePositions.get(j).posX,seasidePositions.get(j).posY)){
								targetPos.add(seasidePositions.get(j));
								return;
							}
						}
					}
				}
			}
		}
	}
	public position reachList(List<position> list,position p){
		Queue<position> bfs = new LinkedList<position>();
		int x = 0;
		int y = 0;
		position nextPos = null;
		if(list==null || list.size()==0)
			return null;
		for(int i=0; i<list.size(); i++){
			resetSearched();
			bfs.offer(new position(list.get(i).posX, list.get(i).posY));
			while(bfs.size()>0){
				nextPos = bfs.poll();
				x = nextPos.posX;
				y = nextPos.posY;
//				System.out.println("==Input=="+nextPos.posX+":"+nextPos.posY);
				myMap.getMapNode(x, y).searched = true;
				if(toolsJudge(doorsUnopened, x, y) && hasKey==0){
					System.out.println("heihei----------");
					break;
				}
				// 能通到tree 的 seaside pt
				if(x==p.posX && y==p.posY)
					return list.get(i);
				
				if(!myMap.getMapNode(x-1, y).searched && myMap.getMapNode(x-1, y).NodeType==MapNode.PATH) bfs.offer(new position(x-1,y));
				if(!myMap.getMapNode(x+1, y).searched && myMap.getMapNode(x+1, y).NodeType==MapNode.PATH) bfs.offer(new position(x+1,y));
				if(!myMap.getMapNode(x, y-1).searched && myMap.getMapNode(x, y-1).NodeType==MapNode.PATH) bfs.offer(new position(x,y-1));
				if(!myMap.getMapNode(x, y+1).searched && myMap.getMapNode(x, y+1).NodeType==MapNode.PATH) bfs.offer(new position(x,y+1));
			
			}
		}
		return null;
	}
	// 得到树
	public List<position> getTreePath(position p){
		List<position> ret = new ArrayList<position>();
		Queue<position> bfs = new LinkedList<position>();
		position tmpP = new position();
		int x=0;
		int y=0;
		boolean flag = false;
		bfs.add(p);
		resetSearched();
		while(bfs.size()>0){
			tmpP = bfs.poll();
			x = tmpP.posX;
			y = tmpP.posY;
			myMap.getMapNode(x, y).searched = true;
			flag = false;
			if(myMap.getMapNode(x-1, y).reached && myMap.getMapNode(x, y).NodeType==MapNode.TREE){
				if(!toolsJudge(ret,x,y))
					ret.add(new position(x,y));
				flag = true;
			}
			if(myMap.getMapNode(x+1, y).reached && myMap.getMapNode(x, y).NodeType==MapNode.TREE){
				if(!toolsJudge(ret,x,y))
					ret.add(new position(x,y));
				flag = true;
			}
			if(myMap.getMapNode(x, y-1).reached && myMap.getMapNode(x, y).NodeType==MapNode.TREE){
				if(!toolsJudge(ret,x,y))
					ret.add(new position(x,y));
				flag = true;
			}
			if(myMap.getMapNode(x, y+1).reached && myMap.getMapNode(x, y).NodeType==MapNode.TREE){
				if(!toolsJudge(ret,x,y))
					ret.add(new position(x,y));
				flag = true;
			}
			if(flag) continue;
			

			if(!myMap.getMapNode(x-1, y).searched && (myMap.getMapNode(x-1, y).NodeType==MapNode.PATH || myMap.getMapNode(x-1, y).NodeType==MapNode.TREE)) bfs.offer(new position(x-1,y));
			if(!myMap.getMapNode(x+1, y).searched && (myMap.getMapNode(x+1, y).NodeType==MapNode.PATH || myMap.getMapNode(x+1, y).NodeType==MapNode.TREE)) bfs.offer(new position(x+1,y));
			if(!myMap.getMapNode(x, y-1).searched && (myMap.getMapNode(x, y-1).NodeType==MapNode.PATH || myMap.getMapNode(x, y-1).NodeType==MapNode.TREE)) bfs.offer(new position(x,y-1));
			if(!myMap.getMapNode(x, y+1).searched && (myMap.getMapNode(x, y+1).NodeType==MapNode.PATH || myMap.getMapNode(x, y+1).NodeType==MapNode.TREE)) bfs.offer(new position(x,y+1));
		
		
		}
		return ret;
	}
	// 砍树为了更多信息
	public List<position> curForInfor(){
		List<position> ret = new ArrayList<position>();
		for(int i=0; i<treePositions.size(); i++){
			position nextT = treePositions.get(i);
			int x = nextT.posX;
			int y = nextT.posY;
			boolean reach = false;
			boolean unreach = false;
			if(myMap.getMapNode(x-1, y).NodeType==MapNode.PATH) {
				if(!myMap.getMapNode(x-1, y).reached) unreach = true;
				else reach = true;
			}
			if(myMap.getMapNode(x+1, y).NodeType==MapNode.PATH) {
				if(!myMap.getMapNode(x+1, y).reached) unreach = true;
				else reach = true;
			}
			if(myMap.getMapNode(x, y-1).NodeType==MapNode.PATH) {
				if(!myMap.getMapNode(x, y-1).reached) unreach = true;
				else reach = true;
			}
			if(myMap.getMapNode(x, y+1).NodeType==MapNode.PATH) {
				if(!myMap.getMapNode(x, y+1).reached) unreach = true;
				else reach = true;
			}
			if(reach && unreach) ret.add(new position(x,y));
		}
		return ret;
	}
	// 目前就写了s8那种，宝藏和炸弹相交的，s9那种炸弹之间相交的还没写
	public void getSol(){
		List<position> treasurePos = new ArrayList<position>();
		List<List<position>> dPos = new ArrayList<List<position>>();
		System.out.println("-----------get SOL------------");
		pNo.clear();
		if(inTheSea==0){
//			System.out.println("come on u mother fucker!-3");
			// 开树拿东西
			if(hasAxe!=0){
				List<position> tmp = new ArrayList<position>();
				if(treasurePosition!=null) {
					tmp = getTreePath(treasurePosition);
					position target = reachList(tmp,currentPos);
					if(target!=null){
						targetPos.add(target);
						redirPt(target);
						targetType = 1;
						return;
					}
				}
				System.out.println("t 1");
				for(int i=0; i<dynamitePositions.size();i++){
					tmp = getTreePath(dynamitePositions.get(i));
					System.out.println("t 1-1");
					System.out.println("t 1-1 true:"+tmp.size());
					System.out.println("t 1-1 d size:"+dynamitePositions.size());
					position target = reachList(tmp,currentPos);
					if(target!=null){
						System.out.println("t 1-2");
						targetPos.add(target);
						redirPt(target);
						targetType = 1;
						return;
					}
					System.out.println("t 1-3");
				}
				System.out.println("t 2");
				if(hasKey==0)
					for(int i=0; i<keyPositions.size();i++){
						tmp = getTreePath(keyPositions.get(i));
						position target = reachList(tmp,currentPos);
						if(target!=null){
							targetPos.add(target);
							redirPt(target);
							targetType = 1;
							return;
						}
					}
				System.out.println("t 3");
			}
			if(treasurePosition!=null) {
				treasurePos=getBoomPosition(treasurePosition, 1);
			}
//			System.out.println("come on u mother fucker!-2");
			for(int i=0; i<dynamitePositions.size();i++){
				dPos.add(getBoomPosition(dynamitePositions.get(i),0));
			}
//			System.out.println("come on u mother fucker!-1");
			// 如果现在可以直接炸墙取宝藏了就随便找个最薄的点炸进去
			if(dForTreasure<=hasDynamite){
				// whether available
				position target = reachList(treasurePos,currentPos);
				if(target!=null){
					targetPos.add(target);
					redirPt(target);
				}
			}
			// 如果不行，就考虑下是不是有交点可以炸
			else
				Intersection(treasurePos,dPos);
			if(reachList(targetPos,currentPos)==null)
				targetPos.clear();
			if(targetPos.size()!=0){targetType = 0; return;}
			// 如果没斧子，搞一把去
//			System.out.println("come on u mother fucker!0");
			if(hasAxe==0 && axePositions.size()>0){
				List<position> axePos = new ArrayList<position>();
				for(int i=0;i<axePositions.size();i++){
					axeNo = 0;
					axePos = getBoomPosition(axePositions.get(i), 2);
					// 上方得到找axe所标定的外部炸墙点
					if(axeNo<=hasDynamite){
						targetPos.add(new position(axePos.get(0).posX,axePos.get(0).posY));
						position target = axePos.get(0);
						redirPt(target);
					}
					// 如果不行，就考虑下是不是有交点可以炸
					else{
						Intersection(axePos,dPos);
					}
					if(reachList(targetPos,currentPos)==null)
						targetPos.clear();
	//				System.out.println("AXE: x:"+targetPos.get(0).posX+" y:"+targetPos.get(0).posY);
					if(targetPos.size()!=0){targetType = 0; return;}
				}
				Intersection(axePos,dPos);
			}
			if(reachList(targetPos,currentPos)==null)
				targetPos.clear();
			if(targetPos.size()!=0){targetType = 0; return;}
//			System.out.println("come on u mother fucker!1");
			
			// 砍树得到更多信息
			if(treePositions.size()>0 && hasAxe==1){
				List<position> nextTreeToCut = curForInfor();
				for(int tf = 0 ; tf<nextTreeToCut.size(); tf++){
					position target = reachList(nextTreeToCut,currentPos);
					if(target!=null){
						targetPos.add(target);
						redirPt(target);
						targetType = 1;
						return;
					}
				}
			}
			// 没地炸墙，砍个树
			if(hasAxe!=0 && hasRaft==0 && treePositions.size()>0){
				position target = reachList(treePositions,currentPos);
				if(target!=null){
					targetPos.add(target);
					redirPt(target);
				}
			}
			if(targetPos.size()!=0){
				targetType = 1; 
				return;
			}
//			System.out.println("come on u mother fucker!2");
		}
		// 没事干了，下个海
		if(toSeaPositions.size()>0 && hasRaft==1){ 	// 下海 
			targetPos.add(toSeaPositions.get(0));
			position target = toSeaPositions.get(0);
			redirPt(target);
		}
		if(targetPos.size()!=0){targetType = 2; return;}
		// 下过海了，找登陆点
		if(inTheSea==1 && seaTreePositions.size()>0){ 	// 下海 , 海里才会寻找下一个登陆点
			findSeasidePt();
			if(targetPos.size()>0){
				position target = targetPos.get(0);
				redirPt(target);
			}
		}
		if(targetPos.size()!=0){targetType = 3; return;}
		// 上岛找完了，回去
		// 目前是上岛点，回去
		// 目前不是上岛点：不可能啊
		// 所以这个登陆点判断是无敌的
		targetType = -1;
		int backType = MapNode.PATH;
		if(inTheSea == 0) backType = MapNode.SEA;
		if(NodeAt(1,0).NodeType == backType ){
			NodeAt(0,0).parentNode = Direction.DirectionSouth;
			return;
		}
		if(NodeAt(-1,0).NodeType == backType ){
			NodeAt(0,0).parentNode = Direction.DirectionNorth;
			return;
		}
		if(NodeAt(0,1).NodeType == backType ){
			NodeAt(0,0).parentNode = Direction.DirectionEast;
			return;
		}
		if(NodeAt(0,-1).NodeType == backType ){
			NodeAt(0,0).parentNode = Direction.DirectionWest;
			return;
		}
	
		
	}
	
	// reset Searched
	// 如果不行，就考虑下是不是有交点可以炸
	public void resetSearched(){
		for(int i=0;i<currentMap.maxLen; i++)
			for(int j=0;j<currentMap.maxLen; j++)
				myMap.map[i][j].searched = false;
	}
	// update walls boomNo and spanning tree
	public void addWall(int x,int y,int boomNo,List<position> tmp){
		if(myMap.map[x][y].NodeType == MapNode.WALL && 
		   ((myMap.map[x][y].boomNo==0 ) || 
		   myMap.map[x][y].boomNo>boomNo+1)&&
		   !toolsJudge(tmp,x,y)){
				tmp.add(new position(x,y));
		}
	}
	// update stored map info using view array
	public void updateMap(char[][] view){ // 5 * 5
		for(int i=0; i<5; i++)
			for(int j=0; j<5; j++){ // out: .

				if(i==2 && j==2) continue;
				//redirection by rotating the view array
				int nodeX = currentPos.posX-2+i;
				int nodeY = currentPos.posY-2+j;
				if(fwdDirection == Direction.DirectionWest){
					nodeX = currentPos.posX+2-j;
					nodeY = currentPos.posY-2+i;
				}
				if(fwdDirection == Direction.DirectionSouth){
					nodeX = currentPos.posX+2-i;
					nodeY = currentPos.posY+2-j;
				}
				if(fwdDirection == Direction.DirectionEast){
					nodeX = currentPos.posX-2+j;
					nodeY = currentPos.posY+2-i;
				}
				
				//update
				
				if(view[i][j] == '.')
					continue;
				
				myMap.map[nodeX][nodeY].NodeType = MapNode.PATH; // default points
				
				if(view[i][j] == 'T'){
					myMap.map[nodeX][nodeY].NodeType = MapNode.TREE;
					if(inTheSea==1 && !toolsJudge(treePositions,nodeX,nodeY) && !toolsJudge(seaTreePositions,nodeX,nodeY))
						seaTreePositions.add(new position(nodeX,nodeY));
				}
				if(view[i][j] == '-'){
					if(!toolsJudge(doorsUnopened,nodeX,nodeY))
						doorsUnopened.add(new position(nodeX,nodeY));
				}
				if(view[i][j] == '*'){
					myMap.map[nodeX][nodeY].NodeType = MapNode.WALL;
					myMap.map[nodeX][nodeY].boomNo = 0;
				}
				if(view[i][j] == '~'){
					myMap.map[nodeX][nodeY].NodeType = MapNode.SEA;
				}
				if(view[i][j] == 'a' || view[i][j] == 'A' ){
					if(!toolsJudge(axePositions,nodeX,nodeY))
						axePositions.add(new position(nodeX,nodeY));
				}
				if(view[i][j] == 'k' || view[i][j] == 'K' ){
					if(!toolsJudge(keyPositions,nodeX,nodeY))
						keyPositions.add(new position(nodeX,nodeY));
				}
				if(view[i][j] == 'd' || view[i][j] == 'D' ){
					if(!toolsJudge(dynamitePositions,nodeX,nodeY))
						dynamitePositions.add(new position(nodeX,nodeY));
				}
				if(view[i][j] == '$'){
					if(treasurePosition == null)
						treasurePosition = new position(nodeX,nodeY);
				}
			}
	}
	// --------------- Map Info Funcs End ---------------------------
    // --------------- Move Functions Begin -------------------------
	
	// if next node is a tool (d,k,a,$)
	public char getTools(){
		char ret = 'F';
		if(treasurePosition!=null && treasurePosition.posX == currentPos.posX &&
				treasurePosition.posY == currentPos.posY){
			treasurePosition = null;
			hasTreasure = 1;
		}
		if(toolsJudge(axePositions,currentPos.posX,currentPos.posY)){
				hasAxe = 1;
				toolsRemove(axePositions,currentPos.posX,currentPos.posY);
		}

		if(toolsJudge(keyPositions,currentPos.posX,currentPos.posY)){
			hasKey = 1;
				toolsRemove(keyPositions,currentPos.posX,currentPos.posY);
		}

		if(toolsJudge(dynamitePositions,currentPos.posX,currentPos.posY)){
			hasDynamite += 1;
			toolsRemove(dynamitePositions,currentPos.posX,currentPos.posY);
		}

		if(toolsJudge(doorsUnopened,currentPos.posX,currentPos.posY)){
			toolsRemove(doorsUnopened,currentPos.posX,currentPos.posY);
			ret = 'U';
		}
		return ret;
	}
	
	// move forward in current direction
	public char moveForward(){
		int remainX = currentPos.posX;
		int remainY = currentPos.posY;
		int remainSeaStatus = myMap.getMapNode(currentPos.posX,currentPos.posY).NodeType;
		int remainSea = inTheSea;
		if(fwdDirection == Direction.DirectionWest){
			currentPos.posY--;
		}
		if(fwdDirection == Direction.DirectionSouth){
			currentPos.posX++;
		}
		if(fwdDirection == Direction.DirectionEast){
			currentPos.posY++;
		}
		if(fwdDirection == Direction.DirectionNorth){
			currentPos.posX--;
		}						
		if(toolsJudge(seasidePositions,currentPos.posX,currentPos.posY))
			toolsRemove(seasidePositions,currentPos.posX,currentPos.posY);
		//inTheSea
		if(myMap.getMapNode(currentPos.posX,currentPos.posY).NodeType == MapNode.PATH)
			inTheSea = 0;
		if(myMap.getMapNode(currentPos.posX,currentPos.posY).NodeType == MapNode.SEA)
			inTheSea = 1;
		if(remainSeaStatus==MapNode.PATH && myMap.getMapNode(currentPos.posX,currentPos.posY).NodeType == MapNode.SEA)
			hasRaft = 0;
		if(remainSea == 1 && inTheSea==0)
			toSeaPositions.clear();
		// if next node has a tool, take it.
		char ret = getTools();
		if(ret=='U'){
			currentPos.posX = remainX;
			currentPos.posY = remainY;
		}
		return ret;
	}

	// turn current direction
	public void turnFwdDirection(char cmd){
		if(cmd == 'R')
			fwdDirection = Direction.turnRight(fwdDirection);
		if(cmd == 'L')
			fwdDirection = Direction.turnLeft(fwdDirection);
	}
	
	// move to left node in current map
	public char moveToLeft(){
		char retCmd = 0;
		retCmd = Direction.changeDirTo(fwdDirection,Direction.DirectionWest);
		if(retCmd == 'F')
			return moveForward();
		turnFwdDirection(retCmd);
		return retCmd;
	}

	// move to right node in current map
	public char moveToRight(){
		char retCmd = 0;
		retCmd = Direction.changeDirTo(fwdDirection,Direction.DirectionEast);
		if(retCmd == 'F')
			return moveForward();
		turnFwdDirection(retCmd);
		return retCmd;
	}

	// move to down node in current map
	public char moveToDown(){
		char retCmd = 0;
		retCmd = Direction.changeDirTo(fwdDirection,Direction.DirectionSouth);
		if(retCmd == 'F')
			return moveForward();
		turnFwdDirection(retCmd);
		return retCmd;
	}
	
	// move to up node in current map
	public char moveToUp(){
		char retCmd = 0;
		retCmd = Direction.changeDirTo(fwdDirection,Direction.DirectionNorth);
		if(retCmd == 'F')
			return moveForward();
		turnFwdDirection(retCmd);
		return retCmd;
	}
	
    // --------------- Move Functions End -------------------------
    
	
	public int max(int a,int b,int c, int d){
		int ret = a;
		int maxNo = 1;
		if(a==0)
			maxNo = 0;
		if(b>ret){
			ret = b;
			maxNo = 2;
		}
		if(c>ret){
			ret = c;
			maxNo = 3;
		}
		if(d>ret){
			ret = d;
			maxNo = 4;
		}
		return maxNo;
	}
	
	// node based on current position
	public MapNode NodeAt(int disX, int disY){
		return myMap.getMapNode(currentPos.posX+disX, currentPos.posY+disY);
	}
	
	// judge whether has a key when face a door
	public boolean keyAndDoor(int disX, int disY){
		boolean ret = false;
		
		int nodeX = currentPos.posX + disX;
		int nodeY = currentPos.posY + disY;
		
		boolean isDoor = false;
		
		for(int i=0; i<doorsUnopened.size(); i++){
			if(doorsUnopened.get(i).posX == nodeX && 
			   doorsUnopened.get(i).posY == nodeY){
				isDoor = true;
				break;
			}
		}
		
		if(hasKey==0 && isDoor)
			ret = true;
		return ret;
	}
	
	// Heuristic Judge Function
	/*
	 *  U U U U U
	 *  L U U U R
	 *  L L ^ R R
	 *  L L D R R
	 *  L D D D R
	 *  
	 *  L: 	left area
	 *  R: 	right area
	 *  U: 	up area
	 *  D: 	down area
	 *  
	 *  weight is always 1 for each position
	 *  proportion for each direction in this matrix is not same
	 *  Current Weight Strategy: enhance the weight ratio in 'UP', decrease it in 'DOWN'
	 * */
	public int chooseDirection(){
		boolean leftAvailable = true;
		boolean rightAvailable = true;
		boolean upAvailable = true;
		boolean downAvailable = true;
		
		char availableType = MapNode.PATH;
		if(inTheSea == 1)
			availableType = MapNode.SEA;

		// if one direction is not available or has been reached
		if(NodeAt(-1,0).NodeType!=availableType || NodeAt(-1,0).reached || keyAndDoor(-1,0))
			upAvailable = false;
		if(NodeAt(1,0).NodeType!=availableType || NodeAt(1,0).reached || keyAndDoor(1,0))
			downAvailable = false;
		if(NodeAt(0,-1).NodeType!=availableType || NodeAt(0,-1).reached || keyAndDoor(0,-1))
			leftAvailable = false;
		if(NodeAt(0,1).NodeType!=availableType || NodeAt(0,1).reached || keyAndDoor(0,1))
			rightAvailable = false;

		// next move direction
		if(upAvailable) return Direction.DirectionNorth;
		if(leftAvailable) return Direction.DirectionWest;
		if(rightAvailable) return Direction.DirectionEast;
		if(downAvailable) return Direction.DirectionSouth;

		return Direction.DirectionParent;
	}

	// go to target
	// 如果我当前标定了target，也就是我要去炸的位置，则开始往那走。
	public int gotoTarget(){
		position parentNode = new position(targetPos.get(0).posX,targetPos.get(0).posY);
		position nextTarget = new position(targetPos.get(0).posX,targetPos.get(0).posY);
		if(targetPos==null || targetPos.size()==0)
			return 0;
		else{
//			System.out.println(" Target index: "+nextTarget.posX+" : "+nextTarget.posY);
//			System.out.println("Parent Go Target Boom: "+targetBoom.get(0));
			while(nextTarget.posX!=currentPos.posX || 
				  nextTarget.posY!=currentPos.posY){
				parentNode.posX = nextTarget.posX;
				parentNode.posY = nextTarget.posY;
				if(myMap.map[nextTarget.posX][nextTarget.posY].parentNode==Direction.DirectionEast)
					nextTarget.posY++;
				else if(myMap.map[nextTarget.posX][nextTarget.posY].parentNode==Direction.DirectionNorth)
					nextTarget.posX--;
				else if(myMap.map[nextTarget.posX][nextTarget.posY].parentNode==Direction.DirectionSouth)
					nextTarget.posX++;
				else if(myMap.map[nextTarget.posX][nextTarget.posY].parentNode==Direction.DirectionWest)
					nextTarget.posY--;
			}
		}
		
//		System.out.println("Parent Go Target: "+parentNode.posX+":"+parentNode.posY);
		return Direction.turnBack(myMap.map[parentNode.posX][parentNode.posY].parentNode);
	}
	
	// move
	public char move(int inputDir){
		char retCh = 0;
		int mvdir = inputDir;
		// 如果我当前标定了target，也就是我要去炸的位置，则开始往那走。
		if(targetPos.size()!=0){
			MapNode tmpN = myMap.map[targetPos.get(0).posX][targetPos.get(0).posY];
			if(fwdDirection == Direction.turnBack(tmpN.parentNode)){//reached boom position
//				System.out.print("go target ");
				if((fwdDirection == Direction.DirectionEast && 
				   currentPos.posX==tmpN.position.posX &&
				   currentPos.posY+1==tmpN.position.posY )||
				   (fwdDirection == Direction.DirectionWest && 
				   currentPos.posX==tmpN.position.posX &&
				   currentPos.posY-1==tmpN.position.posY )||
				   (fwdDirection == Direction.DirectionNorth && 
				   currentPos.posX-1==tmpN.position.posX &&
				   currentPos.posY==tmpN.position.posY )||
				   (fwdDirection == Direction.DirectionSouth && 
				   currentPos.posX+1==tmpN.position.posX &&
				   currentPos.posY==tmpN.position.posY )){
					targetPos.clear();
					if(targetType==0){
						hasDynamite--;
						myMap.map[tmpN.position.posX][tmpN.position.posY].NodeType = MapNode.PATH;
						targetType = -1;
						return Command.boom;
					}
					if(targetType==1){
						toolsRemove(treePositions, tmpN.position.posX,  tmpN.position.posY);
						targetType = -1;
						if(toolsJudge(seaTreePositions, tmpN.position.posX,  tmpN.position.posY))
							toolsRemove(seaTreePositions, tmpN.position.posX,  tmpN.position.posY);
//						System.out.print("cut ");
						hasRaft = 1;
						myMap.getMapNode(tmpN.position.posX, tmpN.position.posY).NodeType = MapNode.PATH;
						return Command.cutTree;
					}
					if(targetType==2){
//						toolsRemove(toSeaPositions, tmpN.position.posX,  tmpN.position.posY);
						targetType = -1;
						myMap.getMapNode(tmpN.position.posX, tmpN.position.posY).parentNode = Direction.SearchEnd;
//						System.out.println("==go to sea==");
						return moveForward();
					}
					if(targetType==3){
						targetType = -1;
						myMap.getMapNode(tmpN.position.posX, tmpN.position.posY).parentNode = Direction.SearchEnd;
						return moveForward();
					}
				}
			}
			mvdir = gotoTarget();
//			System.out.println("Check go target: "+mvdir);
		}
//		System.out.println("Result : "+axePositions.size()+" "
//				+keyPositions.size()+" "
//				+dynamitePositions.size()+" "
//				+doorsUnopened.size()+" "
//				+hasKey+" "
//				+hasDynamite+" ");
		switch(mvdir){
		case Direction.DirectionNorth: 
			if(NodeAt(-1,0).parentNode == Direction.parentNotSet)
				NodeAt(-1,0).parentNode = Direction.DirectionSouth; //spanning tree
			retCh = moveToUp();
			break;
		case Direction.DirectionWest:  
			if(NodeAt(0,-1).parentNode == Direction.parentNotSet)
				NodeAt(0,-1).parentNode = Direction.DirectionEast; //spanning tree
			retCh = moveToLeft();
			break;
		case Direction.DirectionEast:
			if(NodeAt(0,1).parentNode == Direction.parentNotSet)
				NodeAt(0,1).parentNode = Direction.DirectionWest;  //spanning tree
			retCh = moveToRight();
			break;
		case Direction.DirectionSouth:  
			if(NodeAt(1,0).parentNode == Direction.parentNotSet)
				NodeAt(1,0).parentNode = Direction.DirectionNorth;  //spanning tree
			retCh = moveToDown();
			break;
		case Direction.DirectionParent: 
			int parentDir = NodeAt(0,0).parentNode;
//			System.out.println("parent = "+parentDir);
			if(parentDir==Direction.SearchEnd){
//				if(NodeAt(0,0).NodeType==MapNode.SEA){
//					System.out.println("got sea X: "+currentPos.posX+" - "+currentPos.posY);
//					System.exit(0);
//				}
				// 看看还有没有目标要搞
				if(targetPos.size()==0){
					getSol();
				}
				if(targetPos.size()!=0){
					System.out.println("Next Target: x="+targetPos.get(0).posX+" y="+targetPos.get(0).posY);
					System.out.println("Current: x="+currentPos.posX+" y="+currentPos.posY);
					System.out.println("Status: hasKey:"+hasKey+" hasRaft:"+hasRaft);
				}
				return move(Direction.DirectionParent);
			}
			
			// current direction == direction to return parent node
			if(fwdDirection == parentDir){
				retCh = moveForward();
			}
			else{
//				System.out.println("fwd: "+fwdDirection+" PD: "+parentDir);
				// turn current direction; No position change, no different strategy
				retCh = Direction.changeDirTo(fwdDirection, parentDir);
				turnFwdDirection(retCh);
			}
		}
		return retCh;
	}

	// tree, wall, sea
	public void updateRelativePts(int Type, List<position> l){
		int nx = currentPos.posX;
		int ny = currentPos.posY;
		if(NodeAt(0,-1).NodeType==Type && !NodeAt(0,-1).reached){ 
			if(!toolsJudge(l,nx,ny-1))
				l.add(new position(nx,ny-1));
		}
		if(NodeAt(0,1).NodeType==Type && !NodeAt(0,1).reached){ 
			if(!toolsJudge(l,nx,ny+1))
				l.add(new position(nx,ny+1));
		}
		if(NodeAt(1,0).NodeType==Type && !NodeAt(1,0).reached){ 
			if(!toolsJudge(l,nx+1,ny))
				l.add(new position(nx+1,ny));
		}
		if(NodeAt(-1,0).NodeType==Type && !NodeAt(-1,0).reached){ 
			if(!toolsJudge(l,nx-1,ny))
				l.add(new position(nx-1,ny));
		}
	}
	// 每次探索寻路的时候，标记距离为1的靠路节点
	public void updateRelative(){
		if(inTheSea!=1){
			updateRelativePts(MapNode.TREE,treePositions);
			for(int i=0; i<treePositions.size(); i++)
				if(toolsJudge(seaTreePositions,treePositions.get(i).posX,treePositions.get(i).posY))
					toolsRemove(seaTreePositions,treePositions.get(i).posX,treePositions.get(i).posY);
			updateRelativePts(MapNode.SEA,toSeaPositions);
		}
		if(inTheSea==1){
			updateRelativePts(MapNode.PATH,seasidePositions);
		}
	}
	public char getAction(){
		NodeAt(0,0).reached = true;
		updateRelative();
		int ret = chooseDirection();
//		System.out.println(ret);
		char mvResult = move(ret);
//		System.out.println(mvResult+" "+targetPos.size()+" "+hasRaft+" "+treePositions.size());
		return mvResult;
	}
	
	// Heuristic search
	public char getActionByAI(char[][] view){
		char currentMV = 0;
		
		updateMap(view);
		
		currentMV = getAction();
		
		return currentMV;
	}
	
	
}
