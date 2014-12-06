package edu.usca.acsc492l.flightplanner;

import java.math.BigDecimal;

/**
 * A model class to represent a Communication node in an {@link Airport} class
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class Comm {
	
	/** Describes the various {@link Airport} frequencies */
	public static enum CommType {
		
		/** The {@link Airport}'s Automatic Terminal Information Service frequency */
		ATIS("ATIS"),
		
		/** The {@link Airport}'s Multicom frequency */
		MULTICOM("Multicom"),
		
		/** The {@link Airport}'s Unicom frequency */
		UNICOM("Unicom"),
		
		/** The {@link Airport}'s FAA Flight Service Station frequency */
		FAA_FLIGHT_SERVICE_STATION("FAA Flight Service Station"),
		
		/** The {@link Airport}'s Air Traffic Control frequency */
		AIRPORT_TRAFFIC_CONTROL("Airport Traffic Control"),
		
		/** The {@link Airport}'s Clearance Delivery Position frequency */
		CLEARANCE_DELIVERY_POSITION("Clearance Delivery Position"),
		
		/** The {@link Airport}'s Ground Control Position in Tower frequency */
		GROUND_CONTROL_POSITION_IN_TOWER("Ground Control Position in Tower"),
		
		/** The {@link Airport}'s Radar or Non-Radar Approach Control Position frequency */
		RADAR_OR_NONRADAR_APPROACH_CONTROL_POSITION("Radar or Non-Radar Approach Control Position"),
		
		/** The {@link Airport}'s Radar Departure Control Position frequency */
		RADAR_DEPARTURE_CONTROL_POSITION("Radar Departure Control Position"),
		
		/** The {@link Airport}'s FAA Air Route Traffic Control Center frequency */
		FAA_AIR_ROUTE_TRAFFIC_CONTROL_CENTER("FAA Air Route Traffic Control Center"),
		
		/** The {@link Airport}'s Class C frequency */
		CLASS_C("Class C"),
		
		/** The {@link Airport}'s Emergency frequency */
		EMERGENCY_CALL("Emergency Call");
		
		/** Holds the String to return when {@link #toString()} is called */
		private String toString;
		
		/** 
		 * Constructs a CommType Enumeration 
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		CommType(String toString) {
			this.toString = toString;
		}
		
		/** 
		 * Overrides the Enumeration toString() method 
		 *
		 * @return The {@link #toString} representation of this CommType
		 */
		@Override
		public String toString() {
			return toString;
		}
	};

	/** Holds the minimum allowed frequency for this Comm node */
	protected static final BigDecimal minFreq = new BigDecimal("118");
	
	/** Holds the maximum allowed frequency for this Comm node */
	protected static final BigDecimal maxFreq = new BigDecimal("136.975");
	
	/** Holds the {@link CommType} of this Comm */
	protected CommType type;
	
	/** Holds the frequency of this Comm */
	protected float freq;
	
	/**
	 * Constructs a Comm node
	 *
	 * @param type The {@link CommType} of this Comm node
	 * @param freq The frequency of this Comm node
	 * @throws CommException When either parameter is invalid
	 * @throws NullPointerException When either parameter is null
	 * @see #setType(CommType)
	 * @see #setFreq(String)
	 */
	public Comm(CommType type, String freq) throws CommException {
		setType(type);
		setFreq(freq);
	}
	
	/**
	 * Sets the frequency of this Comm node
	 *
	 * @param freq The frequency to assign this Comm node
	 * @throws CommException When freq is invalid
	 */
	public void setFreq(String freq) throws CommException {
		try {
			// Try to instantiate a new BigDecimal with the user's response to
			// validate it against correct values
			BigDecimal tmpDecimal = new BigDecimal(freq);
			
			// Make sure freq is greater than the minimum frequency and less 
			// than the maximum one
			if (tmpDecimal.compareTo(minFreq) < 0 ||
				tmpDecimal.compareTo(maxFreq) > 0) {
				
				// If either proposition succeeds, throw a NumberFormatException that
				// will be caught in the next block
				throw new NumberFormatException();
			}
				
			// If the frequency is acceptable, assign it to this.freq
			this.freq = tmpDecimal.floatValue();
		} catch (NumberFormatException exception) {
			// Let the user know freq is invalid, and prompt him to change it
			System.err.println("\nInvalid frequency: " + freq);
			System.err.println("Frequency must be between 118 and 136.975 MHz.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String reply = FlightPlan.input.nextLine().trim();
			
			// If the user explicitly stated, "no", throw an Exception and end the method here
			if (reply.matches("^(?i)n(?:o)?$")) {
				throw new CommException("Invalid frequency: " + freq, exception);
			}
				
			// Get the new freq
			System.out.print("Enter the new frequency: ");
			freq = FlightPlan.input.nextLine();
			
			// Try to set the new freq
			setFreq(freq);
		} catch (NullPointerException exception) {
			throw new CommException("freq may not be null");
		}
	}
	
	/**
	 * Returns the frequency of this Comm node
	 *
	 * @return The {@link #freq} of this Comm node
	 */
	public float getFreq() {
		return freq;
	}
	
	/**
	 * Sets the {@link CommType} of this Comm node
	 *
	 * @param type The {@link #type} of this Comm node
	 * @throws NullPointerException When type is null
	 */
	public void setType(CommType type) {
		if (type == null) {
			throw new NullPointerException("type may not be null");
		}
		
		this.type = type;
	}
	
	/**
	 * Returns the {@link CommType} of this Comm node
	 *
	 * @return The {@link #type} of this Comm node
	 */
	public CommType getType() {
		return type;
	}
	
	/**
	 * Overrides the toString() method in the Object class to display this Comm node's
	 * {@link #type} and {@link #freq}
	 *
	 * @return The String representation of this Comm node
	 */
	@Override
	public String toString() {
		return String.format("%s\n" +
		                     " => %s %s MHz",
		                     type.toString(), 
							 "Frequency:", FlightPlan.formatter.format(freq));
	}
}
