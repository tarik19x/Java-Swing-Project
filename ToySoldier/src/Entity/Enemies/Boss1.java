package Entity.Enemies;

import Entity.*;
import TileMap.TileMap;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.text.StyledEditorKit.BoldAction;

public class Boss1 extends Enemy {
	
	private BufferedImage[] sprites;
	private Random rand;
	
	public Boss1(TileMap tm) {
		
		super(tm);
		rank = 2;
		moveSpeed = 3;
		maxSpeed = 4;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;
		
		width = 52;
		height = 36;
		cwidth = 40;
		cheight = 26;
		
		health = maxHealth = 70;
		damage = 1;
		bounce = true;
		
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Enemies/devil36.png"
				)
			);
			
			sprites = new BufferedImage[3];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(
					i * width,
					0,
					width,
					height
				);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		
		right = true;
		facingRight = true;
		
	}
	
	private void getNextPosition() {
		
		if(health >=1) maxSpeed = (maxHealth/(health));
		
		
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
				dx = maxSpeed;
			}
		}
		
		// falling
		if(falling) {
			dy += fallSpeed;
			dx = 0;
		}
		
	}
	
	public void update() {
		
		// update position
		getNextPosition();
		
		checkTileMapCollision();		
		setPosition(xtemp,ytemp);
		
		
		System.out.println("XTEMP: " + xtemp + " YTEMP: " + ytemp );
		
		
		
		// check flinching
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 400) {
				flinching = false;
			}
			
		}
		
		
		// if it hits a wall, go other direction
	if(right && dx == 0) {
			
			right = false;
		left = true;
			facingRight = false;
		}
		else if(left && dx == 0) {
			right = true;
			left = false;
			facingRight = true;
		}
		
		
		
		
		// update animation
		animation.update();
		
	}
	
	public void draw(Graphics2D g) {
		
		//if(notOnScreen()) return;
		
		setMapPosition();
		
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		
		super.draw(g);
		
	}
	
}





