package src.traj;

import java.util.ArrayList;

import src.conf.SimulationSettings;
import src.peng.NewtonGravityFunction;
import src.peng.ODEFunctionInterface;
import src.peng.State;
import src.peng.Vector3d;
import src.peng.Vector3dInterface;
import src.solv.ODESolver;
import src.solv.RungeKutta4th;
import src.univ.Universe;
import src.visu.Visualiser;

public class RouteController extends GuidanceController
{
	private double mutationRate;
	private double initialSpeed;
	private final int GENERATION_KILL = 50;
	private final double MAXIMUM_SPEED = 2000; 
	private ODESolver solver = new RungeKutta4th();
	
	public RouteController(Universe universe, int source, int target, SimulationSettings settings) 
	{
		super(universe, target);
		mutationRate = 1000;
		initialSpeed = settings.probeStartVelocity.norm();
		Vector3d targetVector = universe.universe[target][settings.getEndStep()].location;
		trajectory = hillClimbAlogrithm(targetVector, settings);
	}
	
	/**
	 * An implementation of the Hill Climbing algorithm that tests the current settings and every
	 * combination of steps that can be generated by mutating the initial velocity vector.
	 * Picking the closest vector at each iterative step.
	 */
	private Vector3d[] hillClimbAlogrithm(Vector3d target, SimulationSettings settings)
	{
		boolean keepGoing = true;
		int generations = 0;
		
		while(keepGoing && (generations < GENERATION_KILL)) 
		{
			SimulationSettings newSettings = takeStep(target, settings);
			
			if(newSettings.probeStartVelocity.equals(settings.probeStartVelocity))
			{
				if(mutationRate > 0.001)
					mutationRate = mutationRate/10;
				else
					keepGoing = false;
			}
			else
				settings = newSettings;
			generations++;
			System.out.println(generations + " " + settings.probeStartVelocity.toString() 
				+ " Speed: " + (settings.probeStartVelocity.norm()-initialSpeed));
			Visualiser.getInstance().clearTempTrajectories();
		}
		return planRoute(settings);
	}
	
	private SimulationSettings takeStep(Vector3d target, SimulationSettings settings)
	{
		ArrayList<SimulationSettings> settingsGrid = generateSettingsGrid(settings);
				
		SimulationSettings bestSettings = settings;
		Vector3d closestPoint = testRoute(settings);
		
		for(SimulationSettings each: settingsGrid)
		{
			Vector3d currentPoint = testRoute(each);
			if(closerToTarget(currentPoint, closestPoint, target))
			{
				bestSettings = each;
				closestPoint = currentPoint;
			}
		}
		return bestSettings;
	}

	/*
	 * Plan the final route chosen and plot all of the points on the trajectory
	 */
	private Vector3d[] planRoute(SimulationSettings settings)
	{
		double[] masses = addMassToEnd(universe.masses, 700);
		ODEFunctionInterface funct = new NewtonGravityFunction(masses);
		
		Vector3d[] trajectory = new Vector3d[settings.noOfSteps];
		trajectory[0] = (Vector3d) settings.probeStartPosition;
		
		int currentStep = 0;
		
		Vector3d currentPosition = (Vector3d) settings.probeStartPosition;
		Vector3d currentVelocity = (Vector3d) settings.probeStartVelocity;
		while(currentStep < settings.noOfSteps)
		{		
			double currentTime = currentStep * settings.stepSize;
			State currentState = addProbe(universe.getStateAt(currentStep), currentPosition, currentVelocity);
			State nextState = solver.step(funct, currentTime, currentState, settings.stepSize);
			
			currentPosition = getProbePosition(nextState);
			currentVelocity = getProbeVelocity(nextState);
			trajectory[currentStep++] = currentPosition;
		}
		Visualiser.getInstance().clearTempTrajectories();
		Visualiser.getInstance().addPermTrajectory(trajectory);
		
		finalSettings = settings.copy();
		finalSettings.probeStartPosition = currentPosition;
		finalSettings.probeStartVelocity = currentVelocity;
		finalSettings.stepOffset = currentStep;
		return trajectory;
	}
	
	/*
	 * Test routes to compare different parameters return only the final location for comparison
	 */
	private Vector3d testRoute(SimulationSettings settings)
	{
		double[] masses = addMassToEnd(universe.masses, 700);
		ODEFunctionInterface funct = new NewtonGravityFunction(masses);
		
		Vector3d[] trajectory = new Vector3d[settings.noOfSteps];
		trajectory[0] = (Vector3d) settings.probeStartPosition;
		
		int currentStep = 0;
		Vector3d currentPosition = (Vector3d) settings.probeStartPosition;
		Vector3d currentVelocity = (Vector3d) settings.probeStartVelocity;
		while(currentStep < settings.noOfSteps)
		{		
			double currentTime = currentStep * settings.stepSize;
			State currentState = addProbe(universe.getStateAt(currentStep), currentPosition, currentVelocity);
			State nextState = solver.step(funct, currentTime, currentState, settings.stepSize);
			
			currentPosition = getProbePosition(nextState);
			currentVelocity = getProbeVelocity(nextState);
			trajectory[currentStep++] = currentPosition;
		}
		Visualiser.getInstance().addTempTrajectory(trajectory);
		return trajectory[trajectory.length-1];
	}
	
	private ArrayList<SimulationSettings> generateSettingsGrid(SimulationSettings initialSettings)
	{
		ArrayList<SimulationSettings> outputSettings = new ArrayList<SimulationSettings>();
		
		for(int x = -1; x <= 1; x++)
		{
			for(int y = -1; y <= 1; y++)
			{
				for(int z = -1; z <= 1; z++)
				{
					Vector3d changeAmount = new Vector3d(mutationRate * x, mutationRate * y, mutationRate * z);
					SimulationSettings modifiedSettings = initialSettings.copy();
					modifiedSettings.probeStartVelocity = modifiedSettings.probeStartVelocity.add(changeAmount);
					if(!overMaxSpeed(modifiedSettings.probeStartVelocity))
						outputSettings.add(modifiedSettings);
				}
			}
		}
		return outputSettings;
	}
			
	/*
	 * returns {@code true} if a is closer to the target than b
	 */
	private boolean closerToTarget(Vector3d a, Vector3d b, Vector3d target)
	{
		double dA = a.dist(target);
		double dB = b.dist(target);
		if( dA < dB )
			return true;
		return false;
	}
	
	private boolean overMaxSpeed(Vector3dInterface vector)
	{
		double relativeSpeed = vector.norm() - initialSpeed;
		double absSpeed = Math.abs(relativeSpeed);
		if(absSpeed > MAXIMUM_SPEED)
			return true;
		return false;
	}
}
