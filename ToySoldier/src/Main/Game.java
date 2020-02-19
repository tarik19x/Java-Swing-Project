package Main;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Container;


public class Game {
	
	public static void main(String[] args) {
		
		
		JFrame window = new JFrame("Toy Soldier");
		
		
		window.setLocation(300,175);
		
		
		window.setContentPane(new GamePanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setBackground(Color.blue);
		window.pack();
		window.setVisible(true);
		
	}
	
} 
