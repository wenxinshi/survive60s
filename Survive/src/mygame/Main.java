package mygame;

import java.util.Random;
import java.util.logging.Level;

import jgame.JGColor;
import jgame.JGFont;
import jgame.JGObject;
import jgame.platform.StdGame;

public class Main extends StdGame {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();

	}

	public Main() {
		initEngine(400, 300);
	}

	public final static int PLAYERCID = 1;
	public final static int STARCID = 2;
	public final static int BOMBCID = 3;
	public final static int OUTSCREEN = 4;
	public final static int PRESERVECID = 5;
	public final static int FIRECID = 6;

	public final static int CDINVISIBLE = 100;
	public final static int INVISIBLE = 50;
	public final static int CDBOMB = 400;

	public static enum LEVEL {
		EASY, HARD, DIFFICULT, SURVIVE
	};

	LEVEL level;
	long timing = -1;
	long timeLapse = 0;
	boolean inGame = true;
	Random r = new Random();
	
	int CDBomb = 0;
	int CDInvisible = 0;
	int invisible = 0;
	int timeInvisible = 10;
	boolean invisibleEnable = false;

	public void defineLevel() {
		removeObjects(null, 0);
		timeLapse = 0;
		timing = -1;
		inGame = true;
		
		CDBomb = 0;
		CDInvisible = 0;
		invisible = 0;
		timeInvisible = 10;
		invisibleEnable = false;
		

		newStar();
		new Player(this.pfWidth() / 2, this.pfHeight() / 2, 7);

	}

	public int randomX() {
		int _width = pfWidth();
		int[] xpos = new int[2];
		xpos[0] = -r.nextInt(20);
		xpos[1] = _width + r.nextInt(20);

		return xpos[r.nextInt(2)];
	}

	public int randomY() {
		int _height = pfHeight();
		int[] ypos = new int[2];
		ypos[0] = -r.nextInt(20);
		ypos[1] = r.nextInt(20) + _height;

		return ypos[r.nextInt(2)];

	}

	public int randomDir(int _dir) {
		int[] rDir = new int[2];
		rDir[0] = -1;
		rDir[1] = 1;
		return rDir[_dir];
	}

	public void newStar() {
		switch (level) {
		case EASY:
			for (int i = 0; i < 10; i++) {
				new Star(randomX(), randomY(), randomDir(r.nextInt(2)),
						randomDir(r.nextInt(2)), r.nextInt(2) + 1,
						r.nextInt(2) + 1);
			}

			break;
		case HARD:
			for (int i = 0; i < 15; i++) {
				new Star(randomX(), randomY(), randomDir(r.nextInt(2)),
						randomDir(r.nextInt(2)), r.nextInt(2) + 1,
						r.nextInt(2) + 1);
			}

			break;

		case DIFFICULT:
			for (int i = 0; i < 20; i++) {
				new Star(randomX(), randomY(), randomDir(r.nextInt(2)),
						randomDir(r.nextInt(2)), r.nextInt(2) + 1,
						r.nextInt(2) + 1);
			}
			break;
		}
	}

	@Override
	public void initCanvas() {
		setCanvasSettings(20, 15, 16, 16, JGColor.black, JGColor.black, null);
	}

	public void initGame() {

		defineMedia("MediaSetting.tbl");
		if (isMidlet()) {
			setFrameRate(18, 1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(25, 1);
		}
		initial_lives = 3;
		lives_img = "ship";
		startgame_ingame = true;
		leveldone_ingame = true;

		level = LEVEL.EASY;
		setTileSettings("#", OUTSCREEN, 0);
	}

	public void doFrameTitle() {
		level = LEVEL.EASY;
	}

	public void paintFrameTitle() {
		setFont(new JGFont("Sans", 0, 23));
		drawString("Survive In 60\"", pfWidth() / 2, pfHeight() / 6, 0);
		setFont(new JGFont("Sans", 0, 15));
		drawString("Level: " + level, pfWidth() / 2, pfHeight() / 3, 0);
		drawString("Press Space to Start or Enter for Help", pfWidth() / 2,
				pfHeight() / 2, 0);
	}

	public void paintFrameStartLevel() {
		if (level == LEVEL.EASY)
			drawString(" Enter Easy Level", 160, 50, 0,
					getZoomingFont(title_font, seqtimer + 40, 1, 1 / 40.0),
					title_color);
		else if (level == LEVEL.HARD) {
			drawString("Enter Hard Level", 160, 50, 0,
					getZoomingFont(title_font, seqtimer + 40, 1, 1 / 40.0),
					title_color);
		} else if (level == LEVEL.DIFFICULT)
			drawString("Enter Difficult Level", 160, 50, 0,
					getZoomingFont(title_font, seqtimer + 40, 1, 1 / 40.0),
					title_color);
	}

	public void paintFrameStartGame() {
	}

	public void doFrameInGame() {
		if (timing < 0) {
			timing = System.currentTimeMillis();
			return;
		}
		// ////Collision
		if (inGame) {
			moveObjects();
			checkCollision(STARCID, PLAYERCID);
			checkCollision(STARCID, STARCID);
			checkBGCollision(OUTSCREEN, STARCID);

			if (timing > 0) {
				long nowTiming = System.currentTimeMillis();
				timeLapse += nowTiming - timing;
				timing = nowTiming;
			}

		}
		// //////

		if (timeLapse >= 60 * 1000) {
			inGame = false;
			if (level == LEVEL.EASY) {
				level = LEVEL.HARD;
				timeLapse = 0;
				timing = -1;
			} else if (level == LEVEL.HARD) {
				level = LEVEL.DIFFICULT;
				timeLapse = 0;
				timing = -1;
			} else if (level == LEVEL.DIFFICULT) {
				level = LEVEL.SURVIVE;

			} else if (level == LEVEL.SURVIVE) {
				gameOver();
			}

			levelDone();
		}
	}

	public void paintFrameInGame() {
		// super.paintFrameInGame();
		setFont(new JGFont("Sans", 0, 12));
		drawString("Time:" + timeLapse / 1000, pfWidth() / 2, pfHeight() / 30,
				0);
		if(invisibleEnable){
			drawString("Hiding:"+((int) 100*invisible/INVISIBLE)+"%", pfWidth() / 10*4, pfHeight() / 20*19, 0);
		}
		else
			drawString("Charging:"+(100-(int) 100*CDInvisible/CDINVISIBLE)+"%", pfWidth() / 10*4, pfHeight() / 20*19, 0);
		
		this.drawRect(5, pfHeight()/20*19-5, 30, 20, true, false);
		this.setColor(JGColor.red);
		this.drawRect(5, pfHeight()/20*19-5, 30-30*CDBomb/CDBOMB, 20, true, false);
		this.setColor(JGColor.black);
		drawString((100-(int) 100*CDBomb/CDBOMB)+"%", 20, pfHeight() / 20*19, 0);
	
	}

	public void startGameOver() {
		removeObjects(null, 0);
	}

	public void paintFrameGameOver() {
		if (level == LEVEL.SURVIVE) {
			drawString(" You Are Survived", 160, 50, 0,
					getZoomingFont(title_font, seqtimer + 80, 1, 1 / 40.0),
					title_color);
			return;
		}
		super.paintFrameGameOver();

	}

	public void doFrameLifeLost() {
		removeObjects(null, 0);
		defineLevel();
	}

	public class Player extends JGObject {
		Player(double x, double y, double speed) {
			super("Player", true, x, y, PLAYERCID, "ship", speed, speed);
		}


		int speed;
		int timeFire = 5;	

		boolean fire_newStar_Enable = false;


		boolean CDBombControl() {
			if (CDBomb > 0) {
				CDBomb--;
				return false;
			}
			return true;
		}

		boolean CDInvisibleControl() {
			if (CDInvisible > 0) {
				CDInvisible--;
				invisible--;
				if (invisible > 0) {
					invisibleEnable = true;
				} else {
					invisibleEnable = false;
				}
				return false;
			}
			return true;
		}

		public void move() {
			setDir(0, 0);
			boolean CDBombIndicator = CDBombControl();
			boolean CDInvisibleIndicator = CDInvisibleControl();

			if (fire_newStar_Enable) {
				new JGObject("fire", true, this.x, this.y, FIRECID, "fire",
						timeFire);
				newStar();
				fire_newStar_Enable = false;
			}
			if (timeInvisible > 0) {
				timeInvisible--;
			}
			if (getKey(key_left) && x > 0)
				xdir = -1;
			if (getKey(key_right) && x < pfWidth() - 32)
				xdir = 1;
			if (getKey(key_up) && y > 0)
				ydir = -1;
			if (getKey(key_down) && y < pfHeight() - 16)
				ydir = 1;
			if (CDBombIndicator && getKey(key_fire)) {
				// clearKey(key_fire);
				removeObjects(null, STARCID);
				fire_newStar_Enable = true;
				CDBomb = CDBOMB;

			}
			if (CDInvisibleIndicator && getKey(key_action)) {
				// clearKey(key_action);
				CDInvisible = CDINVISIBLE;
				invisible = INVISIBLE;

			}
		}

		public void hit(JGObject obj) {

			if (invisibleEnable) {
				obj.remove();
				new Star(randomX(), randomY(), randomDir(r.nextInt(2)),
						randomDir(r.nextInt(2)), r.nextInt(3) + 1,
						r.nextInt(3) + 1);

			} else if (obj.colid == STARCID)
				lifeLost();

		}

	}

	public class Star extends JGObject {
		boolean inScreen = false;
		boolean trace = false;

		Star(int x, int y, int xDir, int yDir, int xSpeed, int ySpeed) {
			super("Star", true, x, y, STARCID, "star", xSpeed, ySpeed);
			xdir = xDir;
			ydir = yDir;
		}

		public void move() {
			if (inScreen) {
				if (x < 2 || x > pfWidth() - 10) {
					xdir = -xdir;
				}
				if (y < 2 || y > pfHeight() - 10)
					ydir = -ydir;
				return;
			}

			if (!inScreen) {
				if (x < -20 || x > pfWidth() + 20) {
					xdir = -xdir;
				}
				if (y < -20 || y > pfHeight() + 20)
					ydir = -ydir;
			}

			if (x > 2 && y > 2 && x < (pfWidth() - 10) && y < (pfHeight() - 10)) {
				inScreen = true;
			}
		}

		public void hit(JGObject obj) {

			if (checkCollision(STARCID, -xspeed, -yspeed) == 0) {
				xdir = -xdir;
				ydir = -xdir;
			}

		}

		public void hit_bg(int tilecid) {
			if (level == LEVEL.EASY) {
				xspeed = r.nextInt(2) + 1;
				yspeed = r.nextInt(2) + 1;
			}
			if (level == LEVEL.HARD) {
				xspeed = r.nextInt(3) + 1;
				yspeed = r.nextInt(3) + 1;

			}

			if (level == LEVEL.DIFFICULT) {
				xspeed = r.nextInt(4) + 1;
				yspeed = r.nextInt(4) + 1;
			}

		}

	}

}
