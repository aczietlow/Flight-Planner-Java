package edu.usca.acsc492l.flightplanner;

import java.util.ArrayList;

/**
 * A helper class to hold information regarding each destination in the flight plan.
 *
 * @author Dylon Edwards
 */
public class Destination {
	
	/** Holds the Vertex to be used as this destination */
	protected final Vertex destination;
	
	/** Holds the reasons for visiting this destination */
	protected ArrayList<String> reasons;
	
	/**
	 * Constructs a Desintation object with the given Vertex node
	 *
	 * @param destination The destination, either an Airport or NAVBeacon
	 * @throws NullPointerException When destination is null
	 */
	public Destination(Vertex destination) {
		if (destination == null) {
			throw new NullPointerException("destination may not be null");
		}
		
		this.destination = destination;
		reasons = new ArrayList<String>();
	}
	
	/**
	 * Add a reason for stopping at the destination
	 *
	 * @param reason The reason for stopping at the destination
	 * @throws NullPointerException When reason is null
	 */
	public void addReason(String reason) {
		if (reason == null) {
			throw new NullPointerException("reason may not be null");
		}
		
		reasons.add(reason);
	}
	
	/**
	 * Returns the reasons for visiting this Destination
	 *
	 * @return The {@link #reasons} attribute
	 */
	public ArrayList<String> getReasons() {
		return reasons;
	}
	
	/**
	 * Returns the Vertex serving as this Destination
	 *
	 * @return The {@link #destination} attribute
	 */
	public Vertex getDestination() {
		return destination;
	}
	
	/**
	 * Overrides the toString() method of the Object class to return all the 
	 * {@link #reasons} for visiting this Destination
	 *
	 * @return The String representation of this Destination
	 */
	@Override
	public String toString() {
		
		int counter = 1;
		int max = reasons.size();
		int longestLabelWidth = destination.getLongestLabelWidth();
		
		String toString = "";
		for (String reason : reasons.toArray(new String[0])) {
			toString += String.format(" => %-" + longestLabelWidth + "s %s",
			                          "Reason:", reason);
			if (counter < max) {
				toString += '\n';
			}
			counter ++;
		}
		
		return toString;
	}
}