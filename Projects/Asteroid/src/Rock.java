
import java.awt.Polygon;

import java.util.ArrayList;

public class Rock {

	public double xPos, yPos, radius, dirPos;
	private double xVel, yVel, dirVel;
	private double[] points, terrainPoints;

	public Rock(double xP, double yP, double xV, double yV, double r, int numPts) {
		xPos = xP;
		yPos = yP;
		xVel = xV;
		yVel = yV;
		radius = r;
		dirPos =  Math.PI;
		dirVel = (Math.random() - 0.5) * 0.1;
		points = new double[numPts];
		terrainPoints = new double[numPts];

		generatePoints(numPts);
		generateTerrain();
	}

	public void update() {
		xPos += xVel;
		yPos += yVel;

		dirPos += dirVel;

		//Bounds
		if (xPos > Asteroid.width + radius) {
			xPos = -radius;
		} else if (xPos < -radius) {
			xPos = Asteroid.width + radius ;
		}
		if (yPos > Asteroid.height + radius) {
			yPos = -radius;
		} else if (yPos < -radius) {
			yPos = Asteroid.height + radius;
		}

		//Direction mapping between 0 and 2 pi
		if (dirPos > Math.PI * 2) {
			dirPos = dirPos - (Math.PI * 2);
		} else if (dirPos < 0) {
			dirPos = dirPos + (Math.PI * 2);
		}
	}

	private void generatePoints(int numPts) {
		//Generate random points evenly from 0 to 2pi
		for (int i = 0; i < numPts; i++) {
			double temp = (Math.PI * 2 / numPts * i) + ((Math.random() - 0.5) * Math.PI);

			if (temp < 0) {
				temp = Math.abs(temp);
			} else if (temp > Math.PI * 2) {
				temp = (Math.PI * 4) - temp;
			}
			points[i] = temp;
		}

		//Sort Points
		double temp;
		for (int i = 1; i < points.length; i++) {
			for (int j = i; j > 0; j--) {
				if (points[j] < points[j - 1]) {
					temp = points[j];
					points[j] = points[j - 1];
					points[j - 1] = temp;
				}
			}
		}
	}

	private void generateTerrain() {
		for (int i = 0; i < points.length; i++) {
			terrainPoints[i] = radius + ((Math.random() - 0.5) * 16);
		}
	}

	public Polygon getPoly() {
		int[] xPointsRel = new int[points.length];
		int[] yPointsRel = new int[points.length];

		for (int i = 0; i < points.length; i++) {
			xPointsRel[i] = (int)(xPos + (terrainPoints[i] * Math.cos(points[i] + dirPos)));
			yPointsRel[i] = (int)(yPos + (terrainPoints[i] * Math.sin(points[i] + dirPos)));
		}
		return new Polygon(xPointsRel, yPointsRel, points.length);
	}
}
