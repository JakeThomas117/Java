
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Polygon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class Asteroid {

	// ENGINE VARIABLES
	private static JFrame Frame;
	private static Canvas Canv;
	private static Graphics2D GR;
	private static KeyBoardListener KL;
	private static MouseEventListener ML;

	private static BufferedImage collision;
	private static Graphics2D collisionGR;
	private static boolean hasUpdated;

	// FILE HANDLING VARIABLES
	private static File GameFile;
	private static String AssetsPath;
	private static Scanner Fsc;
	private static FileWriter fw;

	// GAME WORLD VARIABLES
	public static int width, height;
	private static int difficulty, score, highScore, endlessHighscore;
	private static boolean gameOver, win, gameplay, debug, debugFlip, endless;
	private static Color red, darkred, blue, darkblue, green, darkgreen;

	// GAME OBJECTS
	private static Ship Player;
	private static ArrayList<Shot> Shots;
	private static ArrayList<Rock> Rocks;

	// SOUND CLIPS
	public static AudioClip BackgroundMusic, MenuMusic, Death, Shoot, Shoot2, Shoot3, Thrust, RockBreak, RockBreak2, RockBreak3;
	public static int shootSound;
	public static boolean rockSound;

	private static void setup() {
		// File Handling Variable Initialization
		Json = new Gson();
		String OS = System.getProperty("os.name");
		boolean mac = false;
		if (OS.indexOf("Mac") > -1) {
			mac = true;
		} else if (OS.indexOf("Windows") > -1) {
			mac = false;
		}

		if (mac) {
			GameFile = new File(
								System.getProperty("user.home") + "/Library/Application Support/Asteroid/Highscore.txt");
			AssetsPath = System.getProperty("user.home") + "/Library/Application Support/Asteroid/Assets/";

			System.out.println("\n=Mac=");
			System.out.println("Highscore file saved to \"" + System.getProperty("user.home")
							   + "/Library/Application Support/Asteroid/Highscore.txt\"");
			System.out.println("Game assets saved to " + AssetsPath);
			System.out.println("\n==================\n");
		} else {
			GameFile = new File(System.getProperty("user.home") + "/Documents/Asteroid/Highscore.txt");
			AssetsPath = System.getProperty("user.home") + "/Documents/Asteroid/Assets/";

			System.out.println("\n=Windows=");
			System.out.println("Highscore file saved to \"" + System.getProperty("user.home")
							   + "/Documents/Asteroid/Highscore.txt\"");
			System.out.println("Game assets saved to " + AssetsPath);
			System.out.println("\n==================\n");
		}

		GameFile.getParentFile().mkdirs();
		initaializeFileScanner();
		System.out.println("File Scanner Initialized");

		width = 800;
		height = 800;
		score = 0;
		if (Fsc.hasNextInt()) {
			highScore = Fsc.nextInt();
		}
		if (Fsc.hasNextInt()) {
			endlessHighscore = Fsc.nextInt();
		}
		difficulty = 10;
		gameplay = true;
		debug = false;
		endless = false;
		gameOver = false;
		Rocks = new ArrayList<Rock>();
		Shots = new ArrayList<Shot>();
		Player = new Ship(Shots);

		red = new Color(230, 0, 0);
		darkred = new Color(200, 0, 0);
		green = new Color(0, 230, 0);
		darkgreen = new Color(0, 200, 0);
		blue = Color.blue;
		darkblue = new Color(0, 0, 200);

		shootSound = 0;
		rockSound = true;
		System.out.println("Game Variables Initialized");

		// Engine Variable Initialization and JFrame Setup
		hasUpdated = false;
		Frame = new JFrame("Asteroid");
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Frame.setResizable(false);

		Canv = new Canvas();
		Canv.setSize(width, height);
		Frame.add(Canv);
		Frame.pack();

		Canv.createBufferStrategy(3);

		ML = new MouseEventListener();
		Canv.addMouseListener(ML);
		Canv.addMouseMotionListener(ML);

		KL = new KeyBoardListener();
		Canv.addKeyListener(KL);

		Frame.setVisible(true);

		collision = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		collisionGR = collision.createGraphics();
		System.out.println("Engine Variables Initialized");

		soundClipSetup();

		System.out.println("Sound Clips Initialized\nSetup Complete\n==================\n");
	}

	private static void soundClipSetup() {
		draw("Setting Up Assets...");
		System.out.println("Sound Clips Initializing...");

		try {
			if (!new File(AssetsPath + "AsteroidSounds.zip").exists() || Files.readAttributes(Paths.get(AssetsPath + "AsteroidSounds.zip"), BasicFileAttributes.class).size() < 2800000) {
				runCommand("curl -LO jake-thomas.com/Downloads/Assets/Asteroid/AsteroidSounds.zip");
				System.out.println("  Audio Downloaded");
				UnzipUtility.unzip(AssetsPath + "AsteroidSounds.zip", AssetsPath);
			}

			Shoot = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Laser_Shoot5.wav"));
			Shoot2 = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Laser_Shoot5.wav"));
			Shoot3 = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Laser_Shoot5.wav"));
			System.out.println("   1/6 Shoot Initialized");

			///////// Thrust/////////
			Thrust = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Thrust.wav"));
			System.out.println("   2/6 Thrust Initialized");

			///////// RockBreak/////////
			RockBreak = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Explosion2.wav"));
			RockBreak2 = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Explosion2.wav"));
			System.out.println("   3/6 RockBreak Initialized");

			///////// Background/////////
			BackgroundMusic = Applet.newAudioClip(new URL("file:///" + AssetsPath + "alienbluesCompressed.wav"));
			System.out.println("   4/6 BackgroundMusic Initialized");

			///////// MenuMusic/////////
			MenuMusic = Applet.newAudioClip(new URL("file:///" + AssetsPath + "MenuMusicCompressed.wav"));
			System.out.println("   5/6 MenuMusic Initialized");

			///////// Death/////////
			Death = Applet.newAudioClip(new URL("file:///" + AssetsPath + "Explosion7.wav"));
			System.out.println("   6/6 DeathExplosion Initialized");
		} catch (Exception e) {}
	}

	private static void input() {
		Canv.requestFocus();

		if (KL.up()) {
			Player.applyForces(1, 0);
			if (!Player.thrusting && Thrust != null) {
				Thrust.loop();
			}
			Player.thrusting = true;
		} else {
			Player.thrusting = false;
			if (Thrust != null)
				Thrust.stop();
		}
		if (KL.right()) {
			Player.applyForces(0, -1);
		}
		if (KL.left()) {
			Player.applyForces(0, 1);
		}
		if (KL.space()) {
			Player.shooting = true;
		} else {
			Player.shooting = false;
		}
	}

	private static void logic() {
		if (Rocks.size() < 1) {
			win = true;
			gameOver = true;
			return;
		}
		Player.update();

		for (int i = 0; i < Shots.size(); i++) {
			Shots.get(i).update();
			if (Shots.get(i).life > 25) {
				Shots.remove(i);
				score -= 25;
			}
		}

		// Player-Rock collision
		collisionGR.setColor(Color.black);
		collisionGR.fillRect(0, 0, width, height);
		for (int i = 0; i < Rocks.size(); i++) {
			Rock currRock = Rocks.get(i);
			currRock.update();
			collisionGR.setColor(Color.white);
			collisionGR.fillPolygon(currRock.getPoly());
		}
		if (collision.getRGB((int)Player.xPos, (int)Player.yPos) == Color.white.getRGB()) {
			gameOver = true;
			return;
		}

		// Shot-Rock collision
		for (int i = 0; i < Rocks.size(); i++) {
			Rock currRock = Rocks.get(i);
			for (int j = 0; j < Shots.size(); j++) {
				Shot currShot = Shots.get(j);
				double dist = Math.sqrt(Math.pow(currShot.xPos - currRock.xPos, 2) + Math.pow(currShot.yPos - currRock.yPos, 2));
				if (dist < currRock.radius) {
					if (currRock.radius > 30) {
						spawnRocks((int)(currRock.radius / 22), currRock.xPos, currRock.yPos, currRock.radius / 2);
					}
					Rocks.remove(i);
					Shots.remove(j);
					score += 100;
					if (rockSound) {
						if (RockBreak != null)
							RockBreak.play();
					} else {
						if (RockBreak2 != null)
							RockBreak2.play();
					}
					rockSound = !rockSound;
				}
			}
		}
	}

	private static void draw(int swch) {
		GR = (Graphics2D)Canv.getBufferStrategy().getDrawGraphics();
		// setColor GR.setColor(new Color(30, 30, 30));
		// Circle GR.fillOval(xPos, yPos, width, height);
		// Rectangle GR.fillRect(0, 0, width, height);
		// Text GR.drawString("String", x, y); (x, y relative to bottom left corner of
		// string)
		// FontChange GR.setFont(Font.decode("TimesNewRoman-BOLD-16"));
		// ("FontName-MODIFIER-fontsize")

		switch (swch) {
			case 0:
				// Background
				GR.setColor(Color.black);
				GR.fillRect(0, 0, width, height);

				// Player
				GR.setColor(Color.white);
				GR.drawPolygon(Player.getPoly());

				GR.setFont(Font.decode("Arial-BOLD-32"));
				GR.drawString("" + score, 16, 48);

				if (Player.thrusting) {
					GR.drawPolygon(new int[] {(int)Player.xPos, (int)(Player.xPos + (10 * Math.cos(Player.dirPos + 2.4))), (int)(Player.xPos - (20 * Math.cos(Player.dirPos))), (int)(Player.xPos - (10 * Math.sin(Player.dirPos - 3.88))) },
								   new int[] {(int)Player.yPos, (int)(Player.yPos + (10 * Math.sin(Player.dirPos + 2.4))), (int)(Player.yPos - (20 * Math.sin(Player.dirPos))), (int)(Player.yPos + (10 * Math.cos(Player.dirPos - 3.88))) }, 4);
				}

				// Shots
				for (int i = 0; i < Shots.size(); i++) {
					GR.fillOval((int)Shots.get(i).xPos, (int)Shots.get(i).yPos, 2, 2);
				}

				// Rocks
				for (int i = 0; i < Rocks.size(); i++) {
					Rock currRock = Rocks.get(i);
					GR.setColor(Color.white);
					GR.drawPolygon(currRock.getPoly());
					if (debug) {
						GR.setColor(Color.cyan);
						GR.drawOval((int)(currRock.xPos - currRock.radius), (int)(currRock.yPos - currRock.radius), (int)(currRock.radius * 2), (int)(currRock.radius * 2));
						GR.drawLine((int)(currRock.xPos), (int)(currRock.yPos), (int)(currRock.xPos + (currRock.radius * Math.cos(currRock.dirPos))), (int)(currRock.yPos + (currRock.radius * Math.sin(currRock.dirPos))));
					}
				}
				break;

			case 1:
				GR.setColor(Color.black);
				GR.fillRect(0, 0, width, height);

				for (int i = 0; i < Rocks.size(); i++) {
					Rock currRock = Rocks.get(i);
					GR.setColor(Color.white);
					GR.drawPolygon(currRock.getPoly());
				}

				if (ML.withinRect(0, height / 5, width, height / 5)) {
					GR.setColor(green);
				} else {
					GR.setColor(darkgreen);
				}
				GR.fillRect(0, height / 5, width, height / 5);

				if (ML.withinRect(0, height * 2 / 5, width, height / 5)) {
					GR.setColor(blue);
				} else {
					GR.setColor(darkblue);
				}
				GR.fillRect(0, height * 2 / 5, width, height / 5);

				if (ML.withinRect(0, height * 3 / 5, width, height / 5)) {
					GR.setColor(red);
				} else {
					GR.setColor(darkred);
				}
				GR.fillRect(0, height * 3 / 5, width, height / 5);
				GR.setColor(Color.white);
				GR.setFont(Font.decode("Arial-BOLD-48"));
				GR.drawString("START", width / 6, height * 2 / 5);
				GR.drawString("SETTINGS", width / 6, height * 3 / 5);
				GR.drawString("QUIT", width / 6, height * 4 / 5);

				GR.setFont(Font.decode("Arial-normal-20"));
				GR.drawString("Use the w, a, s, and d keys to move.", width / 2, height * 3 / 10);
				GR.drawString("Use the spacebar to shoot.", width * 7 / 12, (height * 3 / 10) + 20);
				GR.drawString("Highscore: " + highScore, width * 15 / 24, (height * 5 / 10) + 20);
				GR.drawString("Endless Highscore: " + endlessHighscore, width * 7 / 12, (height * 5 / 10) + 45);
				break;

			case 2:
				GR.setColor(Color.black);
				GR.fillRect(0, 0, width, height);

				for (int i = 0; i < Rocks.size(); i++) {
					Rock currRock = Rocks.get(i);
					GR.setColor(Color.white);
					GR.drawPolygon(currRock.getPoly());
				}

				/////////////// GREEN///////////////
				if (ML.withinRect(0, height / 5, width, height / 5)) {
					GR.setColor(green);
				} else {
					GR.setColor(darkgreen);
				}
				GR.fillRect(0, height / 5, width / 2, height / 5);

				if (debug) {
					if (ML.withinRect(width / 2, height / 5, width / 4, height / 5)) {
						GR.setColor(new Color(150, 0, 0));
					} else {
						GR.setColor(new Color(100, 0, 0));
					}
					GR.fillRect(width / 2, height / 5, width / 4, height / 5);

					if (ML.withinRect(width * 3 / 4, height / 5, width / 4, height / 5)) {
						GR.setColor(darkgreen);
					} else {
						GR.setColor(new Color(0, 150, 0));
					}
					GR.fillRect(width * 3 / 4, height / 5, width / 4, height / 5);
				} else {
					if (ML.withinRect(width / 2, height / 5, width / 4, height / 5)) {
						GR.setColor(darkred);
					} else {
						GR.setColor(new Color(150, 0, 0));
					}
					GR.fillRect(width / 2, height / 5, width / 4, height / 5);

					if (ML.withinRect(width * 3 / 4, height / 5, width / 4, height / 5)) {
						GR.setColor(new Color(0, 150, 0));
					} else {
						GR.setColor(new Color(0, 100, 0));
					}
					GR.fillRect(width * 3 / 4, height / 5, width / 4, height / 5);
				}

				//////////// BLUE//////////////
				if (ML.withinRect(0, height * 2 / 5, width, height / 5)) {
					GR.setColor(blue);
				} else {
					GR.setColor(darkblue);
				}
				GR.fillRect(0, height * 2 / 5, width / 2, height / 5);

				GR.setColor(new Color(0, 0, 150));
				GR.fillRect(width / 3, height * 2 / 5, width * 2 / 3, height / 5);

				// Difficulty highlighting
				GR.setColor(new Color(0, 0, 180));
				if (ML.withinRect(width / 2, height * 2 / 5, width / 6, height / 5)) {
					GR.fillRect(width / 2, height * 2 / 5, width / 6, height / 5);
				} else if (ML.withinRect(width * 2 / 3, height * 2 / 5, width / 6, height / 5)) {
					GR.fillRect(width * 2 / 3, height * 2 / 5, width / 6, height / 5);
				} else if (ML.withinRect(width * 5 / 6, height * 2 / 5, width / 6, height / 5)) {
					GR.fillRect(width * 5 / 6, height * 2 / 5, width / 6, height / 5);
				}

				GR.setColor(new Color(0, 0, 220));
				if (difficulty == 5) {
					GR.fillRect(width / 2, height * 2 / 5, width / 6, height / 5);
				} else if (difficulty == 10) {
					GR.fillRect(width * 2 / 3, height * 2 / 5, width / 6, height / 5);
				} else if (difficulty == 15) {
					GR.fillRect(width * 5 / 6, height * 2 / 5, width / 6, height / 5);
				}

				// Endless highlighting
				GR.setColor(new Color(0, 0, 180));
				if (ML.withinRect(width / 3, height * 2 / 5, width / 6, height / 10)) {
					GR.fillRect(width / 3, height * 2 / 5, (width / 6) + 2, height / 10);
				} else if (ML.withinRect(width / 3, height / 2, width / 6, height / 10)) {
					GR.fillRect(width / 3, height / 2, (width / 6) + 2, height / 10);
				}

				GR.setColor(new Color(0, 0, 220));
				if (endless) {
					GR.fillRect(width / 3, height * 2 / 5, (width / 6) + 2, height / 10);
				} else {
					GR.fillRect(width / 3, height / 2, (width / 6) + 2, height / 10);
				}

				////////////// RED//////////////
				if (ML.withinRect(0, height * 3 / 5, width, height / 5)) {
					GR.setColor(red);
				} else {
					GR.setColor(darkred);
				}
				GR.fillRect(0, height * 3 / 5, width, height / 5);

				if (ML.withinRect(25, height - 100, 150, 50)) {
					GR.setColor(red);
				} else {
					GR.setColor(darkred);
				}
				GR.fillRect(25, height - 100, 150, 50);

				GR.setColor(Color.white);
				GR.setFont(Font.decode("Arial-BOLD-48"));
				GR.drawString("Debug", 30, height * 2 / 5);
				GR.drawString("Difficulty", 30, height * 3 / 5);
				GR.drawString("BACK", width / 3, (height * 4 / 5) - 2);

				GR.setFont(Font.decode("Arial-BOLD-32"));
				GR.drawString("On", (width * 7 / 8) - 20, (height * 3 / 10) + 10);
				GR.drawString("Off", (width * 5 / 8) - 20, (height * 3 / 10) + 10);

				GR.setFont(Font.decode("Arial-BOLD-20"));
				GR.drawString("Easy", (width * 7 / 12) - 20, (height / 2) + 7);
				GR.drawString("Normal", (width * 9 / 12) - 35, (height / 2) + 7);
				GR.drawString("Hard", (width * 11 / 12) - 20, (height / 2) + 7);

				GR.drawString("Endless", (width / 3) + 30, (height / 2) + 7);
				GR.drawString("On", (width / 3) + 50, (height / 2) - 33);
				GR.drawString("Off", (width / 3) + 50, (height / 2) + 52);

				GR.setFont(Font.decode("Arial-BOLD-16"));
				GR.drawString("Clear Highscore", 40, height - 70);

				break;

			case 3:
				if (!KL.r()) {
					GR.setColor(Color.red);
					GR.setFont(Font.decode("Arial-BOLD-56"));
					GR.drawString("Game Over", (width / 2) - 135, height / 2);
					GR.setFont(Font.decode("Arial-BOLD-25"));
					GR.drawString("Press q to quit", (width / 2) - 75, (height / 2) + 50);
					if (debug) {
						GR.drawString("Hold r to enable invincibility.", (width / 2) - 160, (height / 2) + 25);
					}
				}
				break;

			case 4:
				GR.setColor(Color.green);
				GR.setFont(Font.decode("Arial-BOLD-48"));
				GR.drawString("You Win!", (width / 2) - 100, height / 2);
				if (debug) {
					GR.setFont(Font.decode("Arial-BOLD-25"));
					GR.drawString("Your score will not be recorded beacuse debug is enabled.", (width / 2) - 350,
								  (height / 2) + 50);
				}
				GR.setFont(Font.decode("Arial-BOLD-25"));
				GR.drawString("Press the spacebar to continue", (width / 2) - 185, (height / 2) + 90);
				break;
		}

		GR.dispose();
		Canv.getBufferStrategy().show();
	}

	public static void draw(String s) {
		GR = (Graphics2D)Canv.getBufferStrategy().getDrawGraphics();

		GR.setColor(Color.black);
		GR.fillRect(0, 0, width, height);

		GR.setColor(new Color(150, 150, 150));
		GR.setFont(Font.decode("Arial-Bold-36"));
		GR.drawString(s, 200, 400);

		GR.dispose();
		Canv.getBufferStrategy().show();
		Canv.getBufferStrategy().show();
		Canv.getBufferStrategy().show();
	}

	public static void main(String[] args) {
		setup();
		sleep(100);
		while (true) {
			reset();
			mainMenu();

			if (BackgroundMusic != null)
				BackgroundMusic.loop();

			double then = System.currentTimeMillis();
			double now;
			double lag = 0;
			while (gameplay) {
				while (!gameOver) {
					now = System.currentTimeMillis();
					lag += now - then;
					then = now;

					input();

					while (lag >= 30.96) {
						logic();
						lag -= 30.96;
						hasUpdated = true;
					}
					if (hasUpdated) draw(0);
				}

				if ((!debug || !KL.r()) && Thrust != null) {
					Thrust.stop();
				}

				if (!win && (!debug || !KL.r()) && Death != null) {
					Death.play();
				}

				if (!win) {
					boolean deathPause = true;
					while (deathPause) {
						draw(3);
						sleep(10);
						Canv.requestFocus();
						if (KL.r() && debug) {
							deathPause = false;
							gameOver = false;
							then = System.currentTimeMillis();
						}
						if (KL.q()) {
							if (!debug && endless && score > endlessHighscore) {
								endlessHighscore = score;
							}
							deathPause = false;
							gameplay = false;
							score = 0;
							if (BackgroundMusic != null)
								BackgroundMusic.stop();
						}
					}
				} else if (endless) {
					spawnRocks(difficulty);
					gameOver = false;
					win = false;
				} else {
					if (!debug && score > highScore) {
						highScore = score;
					}
					while (!KL.space()) {
						draw(4);
						gameplay = false;
					}
					if (BackgroundMusic != null)
						BackgroundMusic.stop();
				}
			}
		}
	}

	private static void reset() {
		Rocks.clear();
		Shots.clear();
		Player = new Ship(Shots);
		gameplay = true;
		gameOver = false;
		win = false;
	}

	private static void mainMenu() {
		System.out.println("Main Menu");
		spawnRocks(difficulty);
		if (MenuMusic != null)
			MenuMusic.loop();
		int then = (int)System.currentTimeMillis();
		int now;
		boolean menu = true;
		while (menu) {
			now = (int)System.currentTimeMillis();
			if (now - then > 1000 / 15) {
				draw(1);
				if (ML.leftClick() && now - then > 1000 / 5) {
					if (ML.withinRect(0, height / 5, width, height / 5)) {
						menu = false;
						System.out.println("Game Start");
					} else if (ML.withinRect(0, height * 2 / 5, width, height / 5)) {
						// SETTINGS
						settings();
					} else if (ML.withinRect(0, height * 3 / 5, width, height / 5)) {
						// QUIT
						writeData();
						System.out.println("Program Terminating");
						System.exit(0);
					}
					then = (int)System.currentTimeMillis();
				}
			}
		}
		if (MenuMusic != null)
			MenuMusic.stop();
	}

	private static void settings() {
		System.out.println("  Settings");
		boolean sett = true;
		int now;
		int then = (int)System.currentTimeMillis();
		while (sett) {
			now = (int)System.currentTimeMillis();
			if (now - then > 1000 / 15) {
				draw(2);
				if (ML.leftClick() && now - then > 1000 / 5) {
					if (ML.withinRect(width / 2, height / 5, width / 4, height / 5)) {
						debug = false;
						System.out.println("  Debug: false");
					} else if (ML.withinRect(width * 3 / 4, height / 5, width / 4, height / 5)) {
						debug = true;
						System.out.println("  Debug: true");
					} else if (ML.withinRect(width / 2, height * 2 / 5, width / 6, height / 5)) {
						Rocks.clear();
						difficulty = 5;
						System.out.println("  Difficulty: 5");
						spawnRocks(difficulty);
					} else if (ML.withinRect(width * 2 / 3, height * 2 / 5, width / 6, height / 5)) {
						Rocks.clear();
						difficulty = 10;
						System.out.println("  Difficulty: 10");
						spawnRocks(difficulty);
					} else if (ML.withinRect(width * 5 / 6, height * 2 / 5, width / 6, height / 5)) {
						Rocks.clear();
						difficulty = 15;
						System.out.println("  Difficulty: 15");
						spawnRocks(difficulty);
					} else if (ML.withinRect(0, height * 3 / 5, width, height / 5)) {
						sett = false;
						System.out.println("  Exiting Settings");
					} else if (ML.withinRect(25, height - 100, 125, 50)) {
						highScore = 0;
						endlessHighscore = 0;
						System.out.println("  Highscores Reset");
					} else if (ML.withinRect(width / 3, height * 2 / 5, width / 6, height / 10)) {
						endless = true;
						System.out.println("  Endless: true");
					} else if (ML.withinRect(width / 3, height / 2, width / 6, height / 10)) {
						endless = false;
						System.out.println("  Endless: false");
					}
					then = (int)System.currentTimeMillis();
				}
			}
		}
	}

	private static void initaializeFileScanner() {
		try {
			Fsc = new Scanner(GameFile);
		} catch (Exception FNE) {
			try {
				System.out.println("new file created");
				GameFile.createNewFile();
				Fsc = new Scanner(GameFile);
			} catch (Exception ioe) {
				ioe.printStackTrace(System.out);
			}
		}
	}

	private static void writeData() {
		try {
			fw = new FileWriter(GameFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.print(highScore + " " + endlessHighscore);
			out.close();
		} catch (Exception e) {
			System.out.println("FILEWRITER FAILED TO WRITE DATA");
		}
	}

	private static void spawnRocks(int numRocks) {
		for (int i = 0; i < numRocks; i++) {
			double radius = (Math.random() * 70) + 30;
			Rock newR = new Rock(Player.xPos + ((Math.random() - 0.5) * 200) + (Math.random() - 0.5) * width,
								 Player.yPos + ((Math.random() - 0.5) * 200) + (Math.random() - 0.5) * height,
								 (Math.random() - 0.5) * 10.0, (Math.random() - 0.5) * 10.0, radius, (int)(radius / 3));

			double dist = Math.sqrt(Math.pow(Player.xPos - newR.xPos, 2) + Math.pow(Player.yPos - newR.yPos, 2));

			if (dist > radius + 200 && newR.xPos > 0 && newR.xPos < width && newR.yPos > 0 && newR.yPos < height) {
				Rocks.add(newR);
			} else {
				i--;
			}
		}
		System.out.println("\t" + numRocks + " Rocks Spawned");
	}

	private static void spawnRocks(int numRocks, double x, double y, double radius) {
		for (int i = 0; i < numRocks; i++) {
			Rocks.add(new Rock(x, y, (Math.random() - 0.5) * 10.0, (Math.random() - 0.5) * 10.0, radius,
							   (int)(radius / 3)));
		}
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {}
	}

	public static void runCommand(String command) {
		try {
			new ProcessBuilder(new String[] {"/bin/bash", "-c", command}).directory(new File(AssetsPath)).start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}