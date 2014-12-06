package edu.usca.acsc492l.flightplanner;

import java.util.HashSet;
import java.math.BigDecimal;

/**
 * A class to define and construct airport objects.
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class Airport extends Vertex {
	
	/** Holds an array of {@link Runway} objects that each airport contains */
	protected HashSet<Runway> runways = new HashSet<Runway>();
	
	/** Holds the array of communication frequency's used by the airport */
	protected HashSet<Comm> comms = new HashSet<Comm>();
	
	/** Holds the boolean to know whether the airport has AVGAS gas for planes */
	protected boolean hasAVGAS = false;
	
	/** Holds the boolean to know whether the airport has JA_a gas for planes */
	protected boolean hasJA_a = false;
	
	/** Holds the elevation of this Airport (above sea level) */
	protected float elevation = 0.0f;
	
	/**
	 * Constructs an airport object.
	 *
	 * @param flightPlan The current {@link FlightPlan} instance
	 * @param ICAOid     The ICAOid of the airport
	 * @param name       The name of the airport
	 * @param hasAVGAS   The boolean of whether the airport has AVGAS
	 * @param hasJA_a    The boolean of whether the airport has JA_a
	 * @param coordinate The coordinate set of the airport
	 * @param elevation  The elevation of this Airport
	 * @see #hasAVGAS(boolean)
	 * @see #hasJA_a(boolean)
	 * @see #setElevation(String)
	 * @throws NullPointerException When any of the parameters is null
	 * @throws VertexException When any of the parameters is invalid
	 */	
	public Airport(FlightPlan flightPlan, 
	               String ICAOid, 
	               String name, 
	               boolean hasAVGAS, 
	               boolean hasJA_a, 
	               Coordinate coordinate,
	               String elevation)
	               throws VertexException {
		
		// Call the superclass' constructor
		super(flightPlan, ICAOid, name, coordinate);
		
		// Initialize the local attributes
		hasAVGAS(hasAVGAS);
		hasJA_a(hasJA_a);
		setElevation(elevation);
		
		// Request the longest label width to be 15 characters
		setLongestLabelWidth(15);
	}
	
	/**
	 * Returns whether this Airport carries AVGAS fuel
	 *
	 * @return the {@link #hasAVGAS} attribute of the airport
	 */
	public boolean hasAVGAS() {
		return hasAVGAS;
	}
	
	/**
	 * Sets whether this Airport carries AVGAS fuel
	 *
	 * @param hasAVGAS Whether this Airport carries AVGAS fuel
	 */
	public void hasAVGAS(boolean hasAVGAS) {
		this.hasAVGAS = hasAVGAS;
		
		if (hasAVGAS) {
			flightPlan.addAirportWithAVGAS(this);
		} else {
			flightPlan.removeAirportWithAVGAS(this);
		}
	}
	
	/**
	 * Returns whether this Airport carries Jet-A fuel
	 *
	 * @return the {@link #hasJA_a} attribute of the airport
	 */
	public boolean hasJA_a() {
		return hasJA_a;
	}
	
	/**
	 * Sets whether this Airport carries Jet-A fuel
	 *
	 * @param hasJA_a Whether this Airport carries Jet-A fuel
	 */
	public void hasJA_a(boolean hasJA_a) {
		this.hasJA_a = hasJA_a;
		
		if (hasJA_a) {
			flightPlan.addAirportWithJA_a(this);
		} else {
			flightPlan.removeAirportWithJA_a(this);
		}
	}
	
	/**
	 * Adds a new {@link Runway} to this Airport
	 *
	 * @param runway The {@link Runway} to add to this Airport
	 * @throws NullPointerException When runway is null
	 */
	public boolean addRunway(Runway runway) {
		if (runway == null) {
			throw new NullPointerException("runway may not be null");
		}
		
		return runways.add(runway);
	}
	
	/**
	 * Removes a {@link Runway} from this Airport
	 *
	 * @param runway The {@link Runway} to remove from this Airport
	 * @throws NullPointerException When runway is null
	 */
	public boolean removeRunway(Runway runway) {
		if (runway == null) {
			throw new NullPointerException("runway may not be null");
		}
		
		return runways.remove(runway);
	}
	
	/**
	 * Returns an array containin all the {@link Runway}s in this Airport
	 *
	 * @return The {@link #runways} of this Airport
	 */
	public Runway[] getRunways() {
		return runways.toArray(new Runway[0]);
	}
	
	/**
	 * Returns the number of {@link Runway} objects in this Airport
	 *
	 * @return The size of {@link #runways}
	 */
	public int getNumberOfRunways() {
		return runways.size();
	}
	
	/**
	 * Adds a new {@link Comm} to this Airport
	 *
	 * @param comm The {@link Comm} to add to this Airport
	 * @throws NullPointerException When comm is null
	 */
	public void addComm(Comm comm) {
		if (comm == null) {
			throw new NullPointerException("comm may not be null");
		}
		
		comms.add(comm);
	}
	
	/**
	 * Removes a {@link Comm} from this Airport
	 *
	 * @param comm The {@link Comm} to remove from this Airport
	 * @throws NullPointerException When comm is null
	 */
	public void removeComm(Comm comm) {
		if (comm == null) {
			throw new NullPointerException("comm may not be null");
		}
		
		comms.remove(comm);
	}
	
	/**
	 * Returns an array containing all the {@link Comm} objects in
	 * this Airport
	 *
	 * @return the {@link #comms} of this Airport
	 */
	public Comm[] getComms() {
		return comms.toArray(new Comm[0]);
	}
	
	/**
	 * Sets the elevation of this Airport
	 *
	 * @param elevation The elevation of this Airport
	 * @throws AirportException When elevation is less than zero, or below sea level, or when casting 
	 *                          elevation to type float would result in a buffer overflow
	 * @throws NullPointerException When elevation is null
	 */
	public void setElevation(String elevation) throws AirportException {
		try {
			// Create a new BigDecimal object with the elevation
			BigDecimal tmpDecimal = new BigDecimal(elevation);
			
			// Make sure elevation is greater than zero (sea level) and less
			// than the maximum float value to prevent a buffer overflow
			if (tmpDecimal.compareTo(BigDecimal.ZERO) < 1 ||
			    tmpDecimal.compareTo(FlightPlan.MAX_DECIMAL) > 0) {
				
				// If it fails either proposition, throw a NumberFormatException
				// that will be caught in the next block
				throw new NumberFormatException();
			}
				
			// If the elevation is acceptable, assign it to this.elevation
			this.elevation = tmpDecimal.floatValue();
		} catch (NumberFormatException exception) {
				// Let the user know the elevation is invalid
				System.err.println("\nInvalid elevation: " + elevation);
				System.err.println("The elevation must be above sea level");
				System.out.print("Would you like to change it? [YES|no] ");
				
				// Get the user's response
				String reply = FlightPlan.input.nextLine().trim();
				
				// If the user explicitly stated, "no", throw an Exception and end the method here
				if (reply.matches("^(?i)n(?:o)?$")) {
					throw new AirportException("Invalid elevation: " + elevation, exception);
				}
				
				// Get the new elevation
				System.out.print("Enter the new elevation: ");
				elevation = FlightPlan.input.nextLine().trim();
				
				// Try to set the new elevation
				setElevation(elevation);
		} catch (NullPointerException exception) {
			throw new AirportException("elevation may not be null");
		}
	}
	
	/**
	 * Returns the elevation of this Airport
	 *
	 * @return The {@link #elevation} attribute
	 */
	public float getElevation() {
		return elevation;
	}
	
	/**
	 * Overrides the toString() method in the {@link Vertex} class to add the elevation of this 
	 * Airport and whether it provides either AVGAS or Jet-A fuel
	 *
	 * @return The String representation of this Airport
	 */
	@Override
	public String toString() {
		return super.toString() + 
		       String.format("\n => %-" + longestLabelWidth + "s %s Meters\n" +
		                     " => %-" + longestLabelWidth + "s %s\n" +
		                     " => %-" + longestLabelWidth + "s %s",
		                     "Elevation:", FlightPlan.formatter.format(elevation),
		                     "Has AVGAS Fuel:", hasAVGAS,
		                     "Has Jet-A Fuel:", hasJA_a);
	}
}
