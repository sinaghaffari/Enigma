package enemies;




import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import tools.ReversePathIterator;

import main.GameFrame;
import main.Game;




public class EnemyTrack{
	
	public static boolean scoredyet=false;
	public String zombType;
	private double SPEED;
	private double EnemyX;
	private double EnemyY;
	private double futureX, futureY;
	private Line2D travelPath;
	private double EnemyA;
	private boolean EnemyKilled = false;
	private double EnemyHealth = 10;
	public Rectangle2D collisionBox;
	private int goalblock;
	private PriorityQueue<Integer> open=new PriorityQueue<Integer>(100,new fscoreComparator());
	private LinkedList<Integer> closed=new LinkedList<Integer>();
	private int[] parent=new int[GameFrame.game.map.nblocks];
	private long[] fscore=new long[GameFrame.game.map.nblocks];
	private long[] hscore=new long[GameFrame.game.map.nblocks];
	private long[] gscore=new long[GameFrame.game.map.nblocks];
	private int gotoblock=GameFrame.game.map.toId(EnemyX,EnemyY);
	private boolean noPath=true;
	private boolean isFinding=false;
	private double getPX,getPY;
	private int blockOn;
	private int enemyImgID ;
	public GeneralPath enemyPath = new GeneralPath();
	public ReversePathIterator rpi;
	public PathIterator pi;
	private long begHit= -1;
	private double zombDamage;


	public EnemyTrack(double x, double y, String name, int wave) {
		setUpTypes(x,y,name,wave);
		EnemyX=futureX=x;
		EnemyY=futureY=y;
		blockOn=main.GameFrame.game.map.toId(EnemyX,EnemyY);
		main.GameFrame.game.map.block[blockOn]=2;
		MoveThatGuy();
	}
	
	private void setUpTypes(double x, double y,String name, int wave){
		zombType=name;
		if(zombType.equals("walker")){
			enemyImgID= (int)(Math.random()*7);
			EnemyHealth=1+wave*3.5;
			SPEED=1.5 ;
			collisionBox = new Rectangle2D.Double(x - 20, y - 20, 40, 40);
			zombDamage=7.5;
		} else if(zombType.equals("swarmer")){
			enemyImgID= (int)(Math.random()*2)+7;
			EnemyHealth=wave*1.75;
			SPEED=3.25;
			zombDamage=10;
			collisionBox = new Rectangle2D.Double(x - 20, y - 20, 40, 40);
		} else if (zombType.equals("brute")){
			enemyImgID= (int)(Math.random()*4)+9;
			EnemyHealth=80 + wave*4;
			SPEED=1;
			collisionBox = new Rectangle2D.Double(x - 20, y - 20, 40, 40);
			zombDamage=12;
		} else{
			enemyImgID= (int)(Math.random()*4)+13;
			EnemyHealth=1+wave*3.5;
			SPEED=1.5;
			collisionBox = new Rectangle2D.Double(x - 20, y - 20, 40, 40);
			zombDamage=7;
		}
	}

	private boolean onPlayer(double x, double y){

		return main.GameFrame.game.map.toId(GameFrame.game.getPlayerX(),GameFrame.game.getPlayerY())== main.GameFrame.game.map.toId(x,y);
	}

	public Thread enemyThread = new Thread(new Runnable() {
		public void run() {

			int timer=0;
			while (!EnemyKilled && main.GameFrame.isGaming ) {
				travelPath= new Line2D.Double(EnemyX,EnemyY,futureX,futureY);
				if(timer==0 && !isFinding && blockOn!=main.GameFrame.game.map.toId(GameFrame.game.getPlayerX(),GameFrame.game.getPlayerY())){	
					isFinding=true;
					open.clear();
					closed.clear();
					Arrays.fill(hscore,0);
					Arrays.fill(gscore,999999);
					Arrays.fill(fscore,0);
					Arrays.fill(parent,0);
					getPX=main.GameFrame.game.getPlayerX();
					getPY=main.GameFrame.game.getPlayerY();
					findPath();
					timer++;
				}
				else if(timer==20)
					timer=0;
				else
					timer++;

				if(!noPath && !pi.isDone()){
					double[] coords = new double[2];
					pi.currentSegment(coords);
					EnemyA = angleBetween(coords[0], coords[1], EnemyX, EnemyY);
					if(!onPlayer(futureX,futureY) && !zombType.equals("notinanymore") || !onPlayer(futureX+20,futureY) && !onPlayer(futureX,futureY) && !onPlayer(futureX,futureY+20) && !onPlayer(futureX+20,futureY+20)){
						EnemyX += SPEED * Math.cos( Math.toRadians(EnemyA) );
						EnemyY += SPEED * Math.sin( Math.toRadians(EnemyA) );
						if(!zombType.equals("notinanymore"))
							collisionBox = new Rectangle2D.Double(EnemyX - 20, EnemyY - 20, 40, 40);
						else
							collisionBox = new Rectangle2D.Double(EnemyX - 20, EnemyY - 20, 40, 40);
					}
					if (zombType.contains("brute")) {

						if ((1.0/EnemyHealth) * 20 * 3.0 < 1) {
							SPEED = 1.0;
						} else if ((1.0/EnemyHealth) * 20 * 3.0 > 3.0) {
							SPEED = 2.5;
						} else {
							SPEED = (1.0/EnemyHealth) * 20 * 3.0;
						}
					}
					futureX = EnemyX +  SPEED * Math.cos( Math.toRadians(EnemyA) );
					futureY = EnemyY +  SPEED * Math.sin( Math.toRadians(EnemyA) );
					if(travelPath.intersects(coords[0] - 10, coords[1] - 10, 20, 20)){          
						pi.next();
					}
				}				
				if(main.GameFrame.game.map.toId(EnemyX,EnemyY)!=blockOn){
					if( !zombType.equals("notinanymore")){
						main.GameFrame.game.map.block[blockOn]=0;
						blockOn=main.GameFrame.game.map.toId(EnemyX,EnemyY);
						main.GameFrame.game.map.block[blockOn]=2;
					}
					else{
						main.GameFrame.game.map.block[blockOn]=main.GameFrame.game.map.block[blockOn+1]=main.GameFrame.game.map.block[blockOn+main.GameFrame.game.map.xblocks]=main.GameFrame.game.map.block[blockOn+main.GameFrame.game.map.xblocks+1]=0;
						blockOn=main.GameFrame.game.map.toId(EnemyX,EnemyY);
						main.GameFrame.game.map.block[blockOn]=main.GameFrame.game.map.block[blockOn+1]=main.GameFrame.game.map.block[blockOn+main.GameFrame.game.map.xblocks]=main.GameFrame.game.map.block[blockOn+main.GameFrame.game.map.xblocks+1]=2;
					}
				}




				if(collisionBox.intersects(main.GameFrame.game.getPlayerX()-30, main.GameFrame.game.getPlayerY()-30, 60, 60))
					doDamage();
				else
					begHit=-1;
				try {
					Thread.sleep(17);
				} catch (InterruptedException e) {
				}
				CheckThatGuy();
				while (main.GameFrame.game != null && main.GameFrame.game.pause) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
				}
			}
		}
	});


	private void doDamage(){
			if(begHit==-1)
				begHit=System.currentTimeMillis();

			if(System.currentTimeMillis()-begHit>=250){		
				if (GameFrame.game.pHealth - zombDamage <= 0 && !scoredyet) {
					scoredyet=true;
					GameFrame.game.hs.addScore((int)GameFrame.game.score, "->");
					GameFrame.game.pHealth = 0;
					int tempC = (int)Math.round(Math.random() * 8);
					if (tempC == 0) {
						GameFrame.game.killPlayer("Your brains were eaten out by a " + zombType);
					} else if (tempC == 1) {
						GameFrame.game.killPlayer("You were slaughtered by a " + zombType);
					}  else if (tempC == 2) {
						GameFrame.game.killPlayer("You know the goal of the game isn't to get eaten. ");
					} else if (tempC == 3) {
						GameFrame.game.killPlayer("Unfortunately you did not die a happy death... You were eaten by a zombie.");
					} else if (tempC == 4) {
						GameFrame.game.killPlayer("If this was real life, who would pay for your funeral expenses?");
					}  else if (tempC == 5) {
						GameFrame.game.killPlayer("you < " + zombType);
					} else if (tempC == 6) {
						GameFrame.game.killPlayer("Hah! My great grandmother did better than you!");
					} else if (tempC == 7) {
						GameFrame.game.killPlayer("Did it hurt? I bet it did.");
					} else if (tempC == 8) {
						GameFrame.game.killPlayer("You want to hear a joke? You were brutally mangled by a " + zombType);
					}
				}
				else {
					GameFrame.game.pHealth -= zombDamage;
				}
				begHit=-1;
			}
		
	}

	private void findPath(){
		Thread t = new Thread(new Runnable() {
			public void run() {	

				GeneralPath tempPath = new GeneralPath();
				long beg=System.currentTimeMillis();
				int start= GameFrame.game.map.toId(EnemyX,EnemyY);
				goalblock= GameFrame.game.map.toId(getPX,getPY);
				boolean done=false;
				int current=start;
				while(current!=goalblock){
					closed.push(current);
					addAdjacentSquares(current);
					if(open.size()==0){
						noPath=true;
						return;
					}
					if(System.currentTimeMillis()-beg>15){
						break;
					}
					current=open.poll();
				}

				tempPath.moveTo(GameFrame.game.map.idToX(current),GameFrame.game.map.idToY(current));
				while(parent[current]!=start){
					tempPath.lineTo(GameFrame.game.map.idToX(parent[current]),GameFrame.game.map.idToY(parent[current]));
					current=parent[current];
				}
				enemyPath = (GeneralPath) tempPath.clone();
				pi = rpi.getReversePathIterator(enemyPath);
				noPath=false;
				isFinding=false;
			}
		});
		t.start();
	}


	private boolean canTravel(int index,int current){

		if(GameFrame.game.map.block[index]>=1)
			return false;


		if(index==current-GameFrame.game.map.xblocks-1 && (GameFrame.game.map.block[current-GameFrame.game.map.xblocks]==1 || GameFrame.game.map.block[current-1]==1))
			return false;

		if(index==current-GameFrame.game.map.xblocks+1 && (GameFrame.game.map.block[current-GameFrame.game.map.xblocks]==1 || GameFrame.game.map.block[current+1]==1))
			return false;

		if(index==current+GameFrame.game.map.xblocks-1 && (GameFrame.game.map.block[current+GameFrame.game.map.xblocks]==1 || GameFrame.game.map.block[current-1]==1))
			return false;

		if(index==current+GameFrame.game.map.xblocks+1 && (GameFrame.game.map.block[current+GameFrame.game.map.xblocks]==1 || GameFrame.game.map.block[current+1]==1))
			return false;

		if(!zombType.equals("notinanymore"))
			return true;


		if(GameFrame.game.map.block[index]==2 && index!=blockOn+1 && index!=blockOn+GameFrame.game.map.xblocks && index!=blockOn+GameFrame.game.map.xblocks+1)
			return false;


		if(GameFrame.game.map.block[index+GameFrame.game.map.xblocks]==1)
			return false;
		if(GameFrame.game.map.block[index+1]==1)
			return false;
		if(GameFrame.game.map.block[index+GameFrame.game.map.xblocks+1]==1)
			return false;

		return true;


	}

	private boolean isDiagonal(int index,int current){
		return index==current-GameFrame.game.map.xblocks-1 ||index==current-GameFrame.game.map.xblocks+1 ||index==current+GameFrame.game.map.xblocks-1||index==current+GameFrame.game.map.xblocks+1;

	}


	private void updateScores(int index,int current){
		if(isDiagonal(index,current))
			gscore[index]=gscore[current]+14;
		else
			gscore[index]=gscore[current]+10;

		if(hscore[index]==0)
			hscore[index]=10*(Math.abs(goalblock % GameFrame.game.map.xblocks-index % GameFrame.game.map.xblocks)+Math.abs(goalblock/GameFrame.game.map.xblocks-index/GameFrame.game.map.xblocks));

		fscore[index]=gscore[index]+hscore[index];
	}


	private void addToList(int current, int index) {
		//if(index>=0 && index<map1.xblocks*map1.yblocks){
		if(canTravel(index,current) && !closed.contains(index)){
			if(!open.contains(index) ){
				gscore[index]=0;			
				updateScores(index,current);
				open.add(index);
				parent[index]=current;	
			}
			else if(isDiagonal(index, current) && gscore[current]+14<gscore[index] || gscore[current]+10<gscore[index]){
				updateScores(index,current);
				open.remove(index);
				open.add(index);
				parent[index]=current;	
			}

		}
		//}
	}


	private void addAdjacentSquares(int current){
		//here add all the possible adjacent squares to open list, if possible to travel on them
		addToList(current, current-GameFrame.game.map.xblocks-1);
		addToList(current, current-GameFrame.game.map.xblocks);
		addToList(current,current-GameFrame.game.map.xblocks+1);
		addToList(current,current-1);
		addToList(current,current+1);
		addToList(current,current+GameFrame.game.map.xblocks-1);
		addToList(current,current+GameFrame.game.map.xblocks);
		addToList(current,current+GameFrame.game.map.xblocks+1);
	}


	private boolean canSee(){
		return true;
	}

	public boolean heard(){
		return false;
	}


	public boolean hasReachedGoal(){
		Rectangle2D check = new Rectangle2D.Double(GameFrame.game.map.idToX(gotoblock)-10,GameFrame.game.map.idToY(gotoblock)-10,20,20);
		if(check.contains(EnemyX, EnemyY))
			return true;
		else
			return false;	
	}

	public double angleBetween( double x1, double y1, double x2, double y2 ) {  
		double t = Math.toDegrees(Math.atan2( (y1 - y2) , (x1 - x2)));
		if (t < 0)
			return t + 360;
		else
			return t;
	}
	public void MoveThatGuy() {
		enemyThread.start();
	}
	public void CheckThatGuy() {
		if (Math.sqrt((Math.pow((GameFrame.game.getPlayerX() - EnemyX), 2)) +
				(Math.pow((GameFrame.game.getPlayerY() - EnemyY), 2))) <= 100) {

		}
		if (EnemyHealth <= 0) {
			if (zombType.contains("brute")) {
				GameFrame.game.money += 40 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
				GameFrame.game.score += 40 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
			} else if (zombType.contains("walker")) {
				GameFrame.game.money += 10 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
				GameFrame.game.score += 10 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
			} else if (zombType.contains("swarmer")) {
				GameFrame.game.money += 20 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
				GameFrame.game.score += 20 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
			} else if (zombType.contains("striker")) {
				GameFrame.game.money += 30 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
				GameFrame.game.score += 30 + (GameFrame.game.spawnEnemies.waveNumber - 1) * 2;
				GameFrame.game.gunManager.explosionStart((int)EnemyX, (int)EnemyY);
			}
			EnemyKilled = true;
		}
	}
	public double getEnemyX() {
		return EnemyX;
	}
	public double getEnemyY() {
		return EnemyY;
	}
	public boolean getKilled() {
		return EnemyKilled;
	}
	public void setKilled( boolean b ) {
		EnemyKilled = b;
	}
	public int getDistance() {
		return Math.round((int) Math.sqrt((Math.pow((GameFrame.game.getPlayerX() -
				EnemyX), 2)) + (Math.pow((GameFrame.game.getPlayerY() - EnemyY), 2))));
	}
	public Rectangle2D getCollisionBox() {
		return collisionBox;
	}
	public void setEnemyHealth( double h ) {
		EnemyHealth = h;
	}
	public double getEnemyHealth() {
		return EnemyHealth;
	}
	public double getEnemyAngle() {
		return EnemyA;
	}
	public int getEnemyID() {
		return enemyImgID;
	}
	public String getType() {
		return zombType;
	}
	class fscoreComparator implements Comparator<Integer>
	{

		@Override
		public int compare(Integer o1, Integer o2) {
			if(fscore[o1]<fscore[o2])
				return -1;
			else
				return 1;
		}
	}
}