package edu.usca.acsc492l.flightplanner;

/**
 * A model class that represents a NAV Beacon object
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class NAVBeacon extends Vertex {

	/** Represents the various different types of NAV Beacons */
	public static enum NAVBeaconType{
		
		/** Represents the VOR family of NAV Beacons */
		VOR("VOR"),
		
		/** Represents the VORTAC family of NAV Beacons */
		VORTAC("VORTAC"),
		
		/** Represents the NDB family of NAV Beacons */
		NDB("NDB"),
		
		/** Represents the LORAN family of NAV Beacons */
		LORAN("LORAN");
		
		/** Holds the String to return when {@link #toString()} is called */
		private final String toString;
		
		/** 
		 * Constructs a NAVBeaconType Enumeration 
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		NAVBeaconType(String toString) {
			this.toString = toString;
		}
		
		/** 
		 * Overrides the Enumeration toString() method 
		 *
		 * @return The {@link #toString} representation of this AirplaneType
		 */
		@Override
		public String toString() {
			return toString;
		}
	};
	
	/** Holds the type of this NAVBeacon */
	protected NAVBeaconType type;

	/** Holds the Coordinate of this NAVBeacon */
	protected Coordinate coordinate;

	/**
	 * Constructs a NAV Beacon object with the given ICAO ID, name, type, 
	 * and coordinate
	 *
	 * @param flightPlan The current {@link FlightPlan} instance
	 * @param ICAOid The ICAO ID of this NAV Beacon
	 * @param name The name of this NAV Beacon
	 * @param type The type of this NAV Beacon, which is one of either VOR, 
	 *             VORTAC, NDB, or LORAN
	 * @param coordinate The coordinate pair of latitude and longitude of 
	 *                   this NAVBeacon
	 * @throws NAVBeaconException
	 * @throws NAVBeaconException
	 */	
	public NAVBeacon (FlightPlan flightPlan, 
	                  String ICAOid, 
	                  String name, 
	                  NAVBeaconType type, 
	                  Coordinate coordinate) 
	                  throws VertexException {
		
		super (flightPlan, ICAOid, name, coordinate);
		
		// Request the longest label width to be 5 characters
		setLongestLabelWidth(5);
		
		// Set the type of this NAV Beacon
		setType(type);
	}
	
	/**
	 * Sets the type of this NAV Beacon
	 *
	 * @param type The {@link NAVBeaconType} of this NAVBeacon
	 * @throws NullPointerException When type is null
	 */
	public void setType(NAVBeaconType type) {
		if (type == null) {
			throw new NullPointerException(
				"The NAV Beacon's type may not be null."
			);
		}
		
		this.type = type;
	}
	
	/**
	 * Returns the {@link NAVBeaconType} of this NAVBeacon
	 *
	 * @return The {@link #type} of this NAVBeacon
	 */
	public NAVBeaconType getType() {
		return type;
	}
	
	/**
	 * Overrides the toString() method in the {@link Vertex} class by 
	 * appending the {@link #type} of this NAVBeacon to the end of it.
	 *
	 * @return The String representation of this NAVBeacon
	 */
	@Override
	public String toString() {
		return super.toString() + 
		       String.format("\n => %-" + longestLabelWidth + "s %s",
		                     "Type:", type.toString());
	}
}