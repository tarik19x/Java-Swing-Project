package GameState;

import Main.GamePanel;
import TileMap.*;
import Entity.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import Entity.Enemies.*;
import Audio.AudioPlayer;
public class Level1State extends GameState {
	
	private TileMap tileMap;
	private Background bg;
	Random rand = new Random();
	private Player player;
	private ArrayList<Enemy>enemies;
	private ArrayList<Explosion>explosions;
	
	private HUD hud;
	
	private AudioPlayer bgMusic;
	
	public Level1State(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}
	
	public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/test678.png", 0.1);
		
		player = new Player(tileMap);
		player.setPosition(50, 100);
		populateEnemies();
		
		
		
		explosions = new ArrayList<Explosion>();
		
		hud =new HUD(player);
		
		bgMusic = new AudioPlayer("/Music/Game of Thrones.mp3");
		bgMusic.play();
		
	}
	
private void populateEnemies() {
		
		enemies = new ArrayList<Enemy>();
		
		RedBug s;
		Point[] points = new Point[] {
				
			new Point(100, 0),
		       new Point(280, 100),
			new Point(780, 200),
			new Point(890, 200),
			new Point(960, 200),
			new Point(1525, 200),
			new Point(1605, 200),
			new Point(1680, 200),
			new Point(1810,200),
			new Point(2620, 200),
			new Point (2930, 200),
			new Point(2760, 200),
			new Point(2900, 200),
			//new Point(3100, 200)
			//new Point(3330, 200)
			
		};
		for(int i = 0; i < points.length; i++) {
			s = new RedBug(tileMap);
			s.setPosition(points[i].x, points[i].y);
			
			enemies.add(s);
		}
		Spider spider;
		Point[] spiderpoints = new Point[] {
			new Point(310, rand.nextInt(80)),
			new Point(430, rand.nextInt(75)),
			new Point(615, rand.nextInt(75)),
			new Point(890, rand.nextInt(75)),
			new Point(1000, rand.nextInt(75)),
			new Point(1037, rand.nextInt(75)),
			new Point(1190, rand.nextInt(75)),
			new Point(1250, rand.nextInt(75)),
			//new Point(1840, rand.nextInt(75)),
			new Point(2310+120, rand.nextInt(100)),
			new Point(2530, rand.nextInt(100)),
			new Point(2565, rand.nextInt(100)),
			new Point(2600, rand.nextInt(100))
		
		};
		for(int i = 0; i < spiderpoints.length; i++) {
			spider = new Spider(tileMap, spiderpoints[i].x, 00);
			spider.setPosition(spiderpoints[i].x, spiderpoints[i].y);
			enemies.add(spider);
		}
		Boss1 boss = new Boss1(tileMap);
		//2650, 200 boss
		boss.setPosition(3100, 150);
		enemies.add(boss);
		
	}
	
	public void update() {
		
		// update player
		player.update();
		tileMap.setPosition(
			GamePanel.WIDTH / 2 - player.getx(),
			GamePanel.HEIGHT / 2 - player.gety()
		);
		
		// set background
				bg.setPosition(tileMap.getx(), tileMap.gety());
				
				// attack enemies
				player.checkAttack(enemies);
				
				// update all enemies
				for(int i = 0; i < enemies.size(); i++) {
					Enemy e = enemies.get(i);
					e.update();
					if(e.isDead()) {
						enemies.remove(i);
						i--;
						explosions.add(
							new Explosion(e.getx(), e.gety()));
					}
				}
				// update explosions
				for(int i = 0; i < explosions.size(); i++) {
					explosions.get(i).update();
					if(explosions.get(i).shouldRemove()) {
						explosions.remove(i);
						i--;
					}
				}
	}
	
	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);
		
		// draw tilemap
		tileMap.draw(g);
		
		// draw player
		player.draw(g);
		
		//draw enemies
		for(int i=0;i<enemies.size();i++)
		{
			enemies.get(i).draw(g);
		}
		
		// draw explosions
				for(int i = 0; i < explosions.size(); i++) {
					explosions.get(i).setMapPosition(
						(int)tileMap.getx(), (int)tileMap.gety());
					explosions.get(i).draw(g);
				}
		
		//draw hud
		hud.draw(g);
		
		
		if( player.getx()>3100)
		{
			g.setColor(Color.BLACK);
			g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);
			Font font = new Font("Arial", Font.BOLD, 16);
			g.setFont(font);
			g.setColor(Color.CYAN);
			
			 
				
					g.drawString("You Have Done It !!! ", 130, 140);
					bgMusic.stop();
					MenuState ms=new MenuState(null);


			}
		
		
		if( player.gety()>200)
		{
			g.setColor(Color.BLACK);
			g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);
			Font font = new Font("Arial", Font.BOLD, 16);
			g.setFont(font);
			g.setColor(Color.RED);
			
			 
				
					g.drawString("Game Over ", 260, 150);
					bgMusic.stop();
					MenuState ms=new MenuState(null);
                    
			}
		
		if( player.health<1)
		{
			g.setColor(Color.BLACK);
			g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);
			Font font = new Font("Arial", Font.BOLD, 16);
			g.setFont(font);
			g.setColor(Color.YELLOW);
			
			 
				
					g.drawString("Better Luck Next Time     ", 230, 150);
					bgMusic.stop();
					MenuState ms=new MenuState(null);

			}
		
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(true);
		if(k == KeyEvent.VK_RIGHT) player.setRight(true);
		//if(k == KeyEvent.VK_UP) player.setUp(true);
		if(k == KeyEvent.VK_DOWN) player.setDown(true);
		if(k == KeyEvent.VK_UP) player.setJumping(true);
		if(k == KeyEvent.VK_C) player.setGliding(true);
		if(k == KeyEvent.VK_X) player.setScratching();
		if(k == KeyEvent.VK_SPACE) player.setFiring();
	}
	
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(false);
		if(k == KeyEvent.VK_RIGHT) player.setRight(false);
		//if(k == KeyEvent.VK_UP) player.setUp(false);
		if(k == KeyEvent.VK_DOWN) player.setDown(false);
		if(k == KeyEvent.VK_UP) player.setJumping(false);
		if(k == KeyEvent.VK_C) player.setGliding(false);
	}
	
}












