package src.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import src.peng.Vector3d;
import src.univ.CelestialBody;

/**
 * Group 22
 * Test class for CelestialBody
 * @author L.Debnath
 * @date 14 Mar 21
 */
class TestCelestialBody 
{	
	LocalTime t1 = LocalTime.parse("00:01");
	LocalDate d1 = LocalDate.parse("2021-04-01");
	double radius = 100;											// Radius of CelestialBody
	Vector3d centre = new Vector3d();								// Centre of 0,0,0
	double epsilon = 10e3;											//Assign error range

	@Test
	void testCollision() 
	{
		LocalTime t1 = LocalTime.parse("00:01");
		LocalDate d1 = LocalDate.parse("2021-04-01");
		
		// Arrange (set up everything for your test)
		CelestialBody cb = new CelestialBody(centre, centre, 0, radius, null, null, null, LocalDateTime.of(d1, t1));
		
		Vector3d[] input = new Vector3d[10];
		for(int i = 0; i < input.length; i++)
		{
			input[i] = new Vector3d(10*i,10*i,10*i);					
		}
		boolean[] exp = {true, true, true, true, true, 
						true, false, false, false, false};				
		boolean[] act = new boolean[exp.length];						
		
		// 	Act (Do the test)
		for(int i = 0; i < input.length; i++)
		{
			act[i] = cb.collision(input[i]);
		}
		
		// Assert (compare the expected and actual results)
		for(int i = 0; i < exp.length; i++)
		{
			assertEquals(exp[i], act[i]);
		}
	}
	
	@Test
	void testTimeInMs_Second()
	{
		LocalTime t1 = LocalTime.parse("00:00:01");
		LocalDate d1 = LocalDate.parse("0001-01-01");
		CelestialBody cb = new CelestialBody(null, null, 0, 0, null, null, null, LocalDateTime.of(d1, t1));
		assertEquals(1000, cb.timeInMs());
	}
	
	@Test
	void testTimeInMs_Min()
	{
		LocalTime t1 = LocalTime.parse("00:50:00");
		LocalDate d1 = LocalDate.parse("0001-01-01");
		CelestialBody cb = new CelestialBody(null, null, 0, 0, null, null, null, LocalDateTime.of(d1, t1));
		assertEquals(3e+6, cb.timeInMs());
	}
	
	@Test
	void testTimeInMs_Hour()
	{
		LocalTime t1 = LocalTime.parse("03:00:00");
		LocalDate d1 = LocalDate.parse("0001-01-01");
		CelestialBody cb = new CelestialBody(null, null, 0, 0, null, null, null, LocalDateTime.of(d1, t1));
		assertEquals(1.08e+7, cb.timeInMs());
	}

	//TODO (Travis) Check if accuracy is important for these tests (Confirm epsilon with group member)
	@Test
	void testSOI()
	{
		/*Venus*/
		double venusSOIExact = 616275769.13; 				//Radius in meters
		double venusMass = 4.8685e24;						//Mass of venus
		double venusToSun = 108.2e9;						//Average orbiting radius of venus in m

		/*Uranus*/
		double uranusSOIExact = 51794507161.31; 			//Radius in meters
		double uranusMass = 8.6813e25;						//Mass of uranus
		double uranusToSun = 2872.5e9;						//Average orbiting radius of uranus

		/*Sun*/
		double sunMass = 1.988500e30;

		/*Celestial Body*/
		CelestialBody cb = new CelestialBody(centre, centre, 0, radius, null, null, null, LocalDateTime.of(d1, t1));

		/*Tests*/
		assertTrue(inRange(venusSOIExact, cb.calculateSOI(venusMass, sunMass, venusToSun),epsilon)); 	//SOI test for Venus
		assertTrue(inRange(uranusSOIExact, cb.calculateSOI(uranusMass, sunMass, uranusToSun),epsilon)); //SoI test for uranus
		try																							
		{
			cb.calculateSOI(sunMass, venusMass, venusToSun);
		}
		catch(RuntimeException e)																		//Testing the catch and test of a runtimeException caused by incorrect masses
		{
			assertEquals("mSmaller > mGreater", e.getMessage());
		}
	}

	@Test
	/*Test aims to compute the orbital velcoty of the Earth orbiting the sun, and compare to exact orbital velocity*/
	void testOrbitalVelocity()
	{
		/*Sun*/
		CelestialBody sun = new CelestialBody(new Vector3d(), new Vector3d(), 1.988500e30, 696340e3, "Sun", "", "",  LocalDateTime.of(d1, t1));
		double sunRadius =  696340e3;
		double distSunToEarth = 149.6e9;
		double r2 = distSunToEarth - sunRadius;

		/*Earth*/
		double exactOrbitalVelocity = 29800;

		/*Test*/
		double velocityForOrbit = sun.orbitalVelocity(r2);
		System.out.println(velocityForOrbit);
		assertTrue(inRange(exactOrbitalVelocity,velocityForOrbit, epsilon));
	}

	/**
	 * Boolean method to determine if a double is within range
	 * @param exact The accurate answer to the equations
	 * @param approx The calculated answer to the equation.
	 * @param epsilon Error range
	 * @return Boolean if it is within range epsilon of the exact answer.
	 */
	public boolean inRange(double exact, double approx, double epsilon)
	{
		if(Math.abs(exact-approx) < epsilon)
		{
			return true;
		}
		return false;
	}
}
