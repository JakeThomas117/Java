
public class Shot {
	public int life;
	public double xPos, yPos;
	private double xVel, yVel;

	public Shot(double xP, double yP, double xV, double yV) {
		xPos = xP;
		yPos = yP;
		xVel = xV;
		yVel = yV;
		life = 0;
	}

	public void update() {
		xPos += xVel;
		yPos += yVel;

		life++;

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
	}
}
