package src.univ;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.peng.Vector3d;

import java.lang.Math;

/**
 * Group 22
 * A class to hold the information of a CelestialBody
 * @author L.Debnath
 * @date 14 Mar 21
 */
public class CelestialBody 
{
	public double mass;
	public double radius;
	public String name = null;
	public String image = null;
	public String icon = null;
	public Vector3d velocity;
	public Vector3d location;
	public LocalDateTime time = null;

	public CelestialBody(Vector3d location, Vector3d velocity, double mass, double radius, String name, String image, String icon, LocalDateTime time)
	{
		this.location = location;
		this.velocity = velocity;
		this.mass = mass;
		this.radius = radius;
		this.name = name;
		this.image = image;
		this.icon = icon;
		this.time = time;
		time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
	/**
	 * Returns true if the coordinate is within the location of the Celestial Body
	 * @param coordinate
	 * @return
	 */
	public boolean collision(Vector3d object)
	{
		// Calculate distance from origin ( (a1-a2)^2 + (b1-b2)^2 + (c1-c2)^2)^1/2 
		double deltaA = Math.pow((location.getX() - object.getX()), 2);
		double deltaB = Math.pow((location.getY() - object.getY()), 2);
		double deltaC = Math.pow((location.getZ() - object.getZ()), 2);
		double dist = deltaA + deltaB + deltaC;
		dist = Math.sqrt(dist);
		if(dist < radius)
			return true;
		return false;
	}

	/**
	 * Calculate the Sphere of Influence of an orbiting planet.
	 * @param a The semi-major axis of the orbit/ the distance to the orbiting planet.
	 * @param mSmaller Mass of the smaller orbiting planet.
	 * @param mGreater Mass of the larger planet, of which is being orbited.
	 * @return A distance in meters, representing the sphere of influence from the planet's centre. 
	 * Ex. Venus SOI orbiting the sun is 0.616e6 km
	 * Note: Will throw RunTimeException if mGreater < mSmaller
	 */
	public double calculateSOI(double mSmaller, double mGreater, double a)
	{
		if(mSmaller>mGreater)																//Check to ensure correct variable placement with regards to mass. If error found throw runtime exception.
		{
			throw new RuntimeException("mSmaller > mGreater");
		}

		return a*(Math.pow((mSmaller/mGreater), (2.0/5.0)));
	}

	/**
	 * Orbital velocity calculation to determine velocity required to stay in orbit at determined height
	 * @param r2 The height of the orbit above the planets surface (In meters)
	 * @return the Velcoty required to remain in orbit at the specified height. (meters per second)
	 * Method should be called from the Celestial Body to be orbited, as it will access the planet's radius, to determine the height from the planets center.
	 */
	public double orbitalVelocity(double r2)
	{
		double G = 6.67408e-11;									//Universal gravitational constant
		double r = radius+r2; 							//Represents total height from the centre of the planet
		return Math.sqrt((G*mass)/r);
		
	}
	public double acceleration()
	{
		double G = 6.67408e-11;	
		return (G*mass)/(radius*radius);
	}
	public double orbitalPeriod(double r2)
	{
		double G = 6.67408e-11;
		double r = radius+r2;
		return Math.sqrt(((r*r*r)*(4*(Math.PI * Math.PI)))/(G*mass));
	}
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public CelestialBody updateCopy(Vector3d position, Vector3d velocity, LocalDateTime time)
	{
		return new CelestialBody(position, velocity, mass, radius, name, image, icon, time);
	}
	
	public double timeInMs()
	{
		double t = 0;
		t = t + time.getSecond() * 1000;
		t = t + time.getMinute() * 60000;
		t = t + time.getHour() * 3.6E+6;
		return t;
	}
}

