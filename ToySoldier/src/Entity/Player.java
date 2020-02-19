package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import Audio.AudioPlayer;
public class Player extends MapObject {
	
	// player stuff
	public int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;
	
	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;
	
	// punch
	private boolean punching;
	private int punchDamage;
	private int punchRange;
	
	// flying
	private boolean flying;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
		1, 6, 1, 1, 2, 1, 3
	};
	
	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int FLYING = 4;
	private static final int FIREBALL = 5;
	private static final int PUNCHING = 6;
	
	private HashMap<String, AudioPlayer> sfx;
	public Player(TileMap tm) {
		
		super(tm);
		
		width = 40;
		height = 52;
		cwidth = 30;
		cheight = 40;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 5;
		fire = maxFire = 2500;
		
		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();
		
		punchDamage = 8;
		punchRange = 40;
		
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Player/p.png"
				)
			);
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 7; i++) {
				
				BufferedImage[] bi =
					new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					if(i == 1) {
						bi[j] = spritesheet.getSubimage(
								(width+j * width),
								0 * height,
								width,
								height
						);
					}
					
					else	if(i == 2) {
						bi[j] = spritesheet.getSubimage(
								(2*width+j * width),
								0 * height,
								width,
								height
						);
					}
					
					else	if(i == 3) {
						bi[j] = spritesheet.getSubimage(
								(2*width+j * width),
								0 * height,
								width,
								height
						);
					}
					
					else	if(i == 4) {
						bi[j] = spritesheet.getSubimage(
								(5*width+j * width),
								0* height,
								width,
								height
						);
					}
					else	if(i == 5) {
						bi[j] = spritesheet.getSubimage(
								(7*width+j * width),
								0* height,
								width,
								height
						);
					}
					
					else	if(i == 6) {
						bi[j] = spritesheet.getSubimage(
								(6*width+j * width),
								0* height,
								width,
								height
						);
					}
					else {
						bi[j] = spritesheet.getSubimage(
								j * width,
								i * height,
								width,
								height
						);
					}
					
				}
				
				sprites.add(bi);
				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
		
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("jump", new AudioPlayer("/SFX/jump.mp3"));
		sfx.put("punch", new AudioPlayer("/SFX/punch.mp3"));
		sfx.put("fire",new AudioPlayer("/SFX/fire.mp3"));
		sfx.put("explosion",new AudioPlayer("/SFX/explosion.mp3"));
		
	}
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getFire() { return fire; }
	public int getMaxFire() { return maxFire; }
	
	public void setFiring() { 
		firing = true;
	}
	public void setScratching() {
		punching = true;
	}
	public void setGliding(boolean b) { 
		flying = b;
	}
	
 public void checkAttack(ArrayList<Enemy> enemies) {
		
		// loop through enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			
			// punch attack
			if(punching) {
				if(facingRight) {
					if(
						e.getx() > x &&
						e.getx() < x + punchRange && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(punchDamage);
					}
				}
				else {
					if(
						e.getx() < x &&
						e.getx() > x - punchRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(punchDamage);
					}
				}
			}
			
			// fireballs
			for(int j = 0; j < fireBalls.size(); j++) {
				if(fireBalls.get(j).intersects(e)) {
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
				}
			}
			
			// check enemy collision
			if(intersects(e)) {
				if(e.getBounce()&&falling&&dy>0)
				{
					e.hit(5);
					bounce();
				}
				else
				{
					hit(e.getDamage());
				}
			}
			
		}
		
	}
 
 public void checkObjects(ArrayList<Object> objects)
	{
	// loop through objects
	for(int i = 0; i < objects.size(); i++) {
		
		Object o = objects.get(i);
		
		// check object collision
		if(intersects(o)) {
				if(o.getBounce()&&falling&&dy>0&& y < o.gety()-3)
				{
					bounce();
				}
				else if(!o.getBounce())
				{
					o.kill();
				}
		}
		
	}
	
}
 
	
	public void hit(int damage) {
		if(flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0)
		dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	public void bounce()
	{
		setJumping(true); numJumps = 1;
	}

	private void getNextPosition() {
		
		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed+.4;
			}
		}
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		// cannot move while attacking, except in air
		if(
		(currentAction == PUNCHING || currentAction == FIREBALL) &&
		!(jumping || falling)) {
			dx = 0;
		}
		
		// jumping
		if(jumping && !falling) {
			sfx.get("jump").play();//hash map (string-->value)
			dy = jumpStart;
			falling = true;	
		}
		
		// falling
		if(falling) {
			
			if(dy > 0 && flying) dy += fallSpeed-.13 ;
			else dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
			
		}
		
	}
	
	public void update() {
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check attack has stopped
		if(currentAction == PUNCHING) {
			if(animation.hasPlayedOnce()) punching = false;
		}
		if(currentAction == FIREBALL) {
			if(animation.hasPlayedOnce()) firing = false;
		}
		
		// fireball attack
				fire += 1;
				if(fire > maxFire) fire = maxFire;
				if(firing && currentAction != FIREBALL) {
					if(fire > fireCost) {
						sfx.get("fire").play();
						fire-= fireCost;
						FireBall fb = new FireBall(tileMap, facingRight);
						fb.setPosition(x, y);
						fireBalls.add(fb);
					}
				}
		
				// update fireballs
				for(int i = 0; i < fireBalls.size(); i++) {
					fireBalls.get(i).update();
					if(fireBalls.get(i).shouldRemove()) {
						fireBalls.remove(i);
						i--;
					}
				}
				
				// check done flinching
				if(flinching) {
					long elapsed =
						(System.nanoTime() - flinchTimer) / 1000000;
					if(elapsed > 1000) {
						flinching = false;
					}
				}
		
		// set animation
		if(punching) {
			if(currentAction != PUNCHING) {
				sfx.get("punch").play();
				currentAction = PUNCHING;
				animation.setFrames(sprites.get(PUNCHING));
				animation.setDelay(50);
				width = 30;
			}
		}
		else if(firing) {
			if(currentAction != FIREBALL) {
				
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy > 0) {
			if(flying) {
				if(currentAction != FLYING) {
					currentAction = FLYING;
					animation.setFrames(sprites.get(FLYING));
					animation.setDelay(200);
					width = 30;
				}
			}
			else if(currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy < 0) {
			if(currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}
		else if(left || right) {
			if(currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}
		else {
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		animation.update();
		
		// set direction
		if(currentAction != PUNCHING && currentAction != FIREBALL) {
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		System.out.println("X: " + x + " Y: " + y);
		setMapPosition();
		
		// draw fireballs
				for(int i = 0; i < fireBalls.size(); i++) {
					fireBalls.get(i).draw(g);
				}
		
		// draw player
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		if(facingRight) {
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - width / 2),
				(int)(y + ymap - height / 2),
				null
			);
		}
		else {
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - width /2 + width),
				(int)(y + ymap - height / 2),
				-width-10,
				height,
				null
			);
			
		}
		
	}
	
}

















