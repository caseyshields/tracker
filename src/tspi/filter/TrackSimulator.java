package tspi.filter;

import java.util.Random;

import org.apache.commons.math3.linear.RealVector;

import rotation.Vector3;
import tspi.model.Pedestal;
import tspi.model.Polar;

/** Used to generate a series of test measurements of an idealized motion. */
class TrackSimulator {
	
	/** A parametric model of the simulated trajectory. */
	interface Trajectory {
		public RealVector getState( double time );
	}
	// TODO implement a linear model, then a second order kinematic model
	// then possibly some circle with a constant acceleration...
	
	Trajectory model;
	Pedestal pedestals[];
	Random random;
	
	/** Create a trajectory simulator.
	 * @param model the ideal path
	 * @param pedestal the pedestals who will measure the trajectory
	 * @param random The random number generator for the simulation */
	public TrackSimulator( Trajectory model, Pedestal pedestals[], Random random ) {
		this.model = model;
		this.pedestals = pedestals;
		this.random = random;
	} 
	
	/** Obtain a ideal point from the trajectory model then generate a measurement vector 
	 * by pointing each pedestal at the ideal point then perturbing it by the
	 * pedestals' error model. */
	Polar[] generate( double time, Polar measurements[] ) {
		
		// find the value of the parametric model at the given time
		RealVector p = model.getState(time);
		Vector3 efg = new Vector3( p.getEntry(0), p.getEntry(1), p.getEntry(2) );
		
		// have each pedestal take a measurement, including error
		for (int n=0; n<pedestals.length; n++) {
			Pedestal pedestal = pedestals[n];
			pedestal.pointToLocation( efg );
			measurements[n] = pedestal.getPerturbedLocal(random);
		}
		
		return measurements;
	}
}