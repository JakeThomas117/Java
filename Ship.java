
import java.awt.Polygon;

import java.util.ArrayList;

public class Ship {

	public double xPos, yPos, dirPos;
	private double xVel, yVel, dirVel, xAcc, yAcc, dirAcc;
	private double accCoeff, maxVel, maxDirVel;
	private double vDampeningBounds, vDampeningSpeed, vKillBound;
	private double dirDampeningBounds, dirDampeningSpeed, dirKillBound;

	public boolean shooting, thrusting;
	private boolean canShoot;
	private int shootTick;

	private int[] xPoints, yPoints;

	private ArrayList<Shot> Shots;

	private static final double PI2 = 2 * Math.PI;

	public Ship(ArrayList<Shot> shots) {
		xPos = Asteroid.width / 2;
		yPos = Asteroid.height / 2;
		dirPos = 0;
		xVel = 0;
		yVel = 0;
		dirVel = 0;
		xAcc = 0;
		yAcc = 0;
		dirAcc = 0;

		accCoeff = 0.75;
		maxVel = 30;
		maxDirVel = 0.16;
		vDampeningBounds = 0.8;
		vDampeningSpeed = 0.075;
		dirDampeningBounds = 0.01;
		dirDampeningSpeed = 0.005;
		dirKillBound = 0.075;
		shooting = false;
		canShoot = true;
		shootTick = 0;

		thrusting = false;

		Shots = shots;
	}

	public void update() {
		xVel += xAcc;
		yVel += yAcc;
		xAcc = 0;
		yAcc = 0;
		xPos += xVel;
		yPos += yVel;

		dirVel += dirAcc;
		dirAcc = 0;
		dirPos += dirVel;

		//Direction mapping between 0 and 2 pi
		if (dirPos > PI2) {
			dirPos = dirPos - PI2;
		} else if (dirPos < 0) {
			dirPos = dirPos + PI2;
		}

		//Velocity Capping
		if (xVel > maxVel) {
			xVel = maxVel;
		} else if (xVel < -maxVel) {
			xVel = -maxVel;
		}
		if (yVel > maxVel) {
			yVel = maxVel;
		} else if (yVel < -maxVel) {
			yVel = -maxVel;
		}
		if (dirVel > maxDirVel) {
			dirVel = maxDirVel;
		} else if (dirVel < -maxDirVel) {
			dirVel = -maxDirVel;
		}

		//Dampening
		if (xVel > vDampeningBounds) {
			xVel -= vDampeningSpeed;
		} else if (xVel < -vDampeningBounds) {
			xVel += vDampeningSpeed;
		} else if (xVel > -vKillBound && xVel < vKillBound) {
			xVel = 0;
		}
		if (yVel > vDampeningBounds) {
			yVel -= vDampeningSpeed;
		} else if (yVel < -vDampeningBounds) {
			yVel += vDampeningSpeed;
		} else if (yVel > -vKillBound && yVel < vKillBound) {
			yVel = 0;
		}
		if (dirVel > dirDampeningBounds) {
			dirVel -= dirDampeningSpeed;
		} else if (dirVel < -dirDampeningBounds) {
			dirVel += dirDampeningSpeed;
		} else if (dirVel > -dirKillBound && dirVel < dirKillBound) {
			dirVel = 0;
		}


		//Bounds
		if (xPos > Asteroid.width) {
			xPos = xPos - Asteroid.width;
		} else if (xPos < 0) {
			xPos = Asteroid.width + xPos;
		}
		if (yPos > Asteroid.height) {
			yPos = yPos - Asteroid.height;
		} else if (yPos < 0) {
			yPos = Asteroid.height + yPos;
		}

		shootTick++;
		if (shootTick >= 7) {
			canShoot = true;
			shootTick -= 7;
		}

		if (canShoot && shooting) {
			shoot();
			canShoot = false;
			shootTick = 0;
		}
	}

	public void applyForces(int thrust, int dirForce) {
		if (thrust != 0) {
			xAcc = accCoeff * Math.cos(dirPos) * thrust;
			yAcc = accCoeff * Math.sin(dirPos) * thrust;
		}
		if (dirForce != 0) {
			dirAcc = 0.023 * -dirForce;
		}
	}

	public void shoot() {
		if (Asteroid.shootSound == 0) {
			if (Asteroid.Shoot != null) Asteroid.Shoot.play();
		} else if (Asteroid.shootSound == 1) {
			if (Asteroid.Shoot2 != null) Asteroid.Shoot2.play();
		} else {
			if (Asteroid.Shoot3 != null) Asteroid.Shoot3.play();
		}
		Asteroid.shootSound++;
		if (Asteroid.shootSound > 2) {
			Asteroid.shootSound = 0;
		}
		Shots.add(new Shot(xPos + (5 * Math.cos(dirPos)), yPos + (5 * Math.sin(dirPos)), xVel + (15 * Math.cos(dirPos)), yVel + (15 * Math.sin(dirPos))));
	}

	public Polygon getPoly() {
		xPoints = new int[] {(int)xPos, (int)(xPos + (20 * Math.cos(dirPos + 2.4))), (int)(xPos + (20 * Math.cos(dirPos))), (int)(xPos - (20 * Math.sin(dirPos - 3.88)))};
		yPoints = new int[] {(int)yPos, (int)(yPos + (20 * Math.sin(dirPos + 2.4))), (int)(yPos + (20 * Math.sin(dirPos))), (int)(yPos + (20 * Math.cos(dirPos - 3.88)))};
		return new Polygon(xPoints, yPoints, 4);
	}
}