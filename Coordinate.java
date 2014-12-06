package edu.usca.acsc492l.flightplanner;

import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * A model class to represent the coordinate of either an {@link Airport} or {@link NAVBeacon} 
 * object
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class Coordinate {
	
	/** Holds the latitude of the coordinate */
	private float latitude;
	
	/** Holds the longitude of the coordinate */
	private float longitude;
	
	/** Holds the maximum value of either {@link #latitude} or {@link #longitude} */
	protected static final BigDecimal MAX_COORDINATE = new BigDecimal("180");
	
	/** Holds the minimum value of either {@link #latitude} or {@link #longitude} */
	protected static final BigDecimal MIN_COORDINATE = new BigDecimal("-180");
	
	/**
	 * Constructs a coordinate object
	 * 
	 * @param latitude  The latitude to assign this Coordinate
	 * @param longitude The longitude to assign this Coordinate
	 * @throws CoordinateException When either parameter is invalid
	 * @see #setLatitude(float)
	 * @see #setLongitude(float)
	 */
	private Coordinate(float latitude, float longitude) throws CoordinateException {
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	/**
	 *
	 */
	public static String getValidLatitude(String latitude) throws CoordinateException {
		try {
			// Try to create a new BigDecimal with latitude
			BigDecimal tmpDecimal = new BigDecimal(latitude);
			
			// Make sure latitude is between -180 and 180
			if (tmpDecimal.compareTo(MAX_COORDINATE) > 0 ||
				tmpDecimal.compareTo(MIN_COORDINATE) < 0) {
				
				// If it is not, throw a new NumberFormatException that will be caught
				// in the next block
				throw new NumberFormatException();
			}
			
			// If latitude is acceptable, assign it to this.latitude
			return tmpDecimal.toString();
		} catch (NumberFormatException exception) {
			// Let the user know latitude is invalid, and prompt him to change it
			System.err.println("\nInvalid latitude: " + latitude);
			System.err.println("Latitude should be greater than -180 and less than 180");
			System.out.print("Would you like to change it? [YES|no] ");
		
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new CoordinateException("Invalid latitude: " + latitude, exception);
			}
				
			// Get the new latitude
			System.out.print("Enter the new latitude: ");
			latitude = FlightPlan.input.nextLine();
			
			// Try to set the new latitude
			return getValidLatitude(latitude);
		}
	}
	
	/**
	 *
	 */
	public static String getValidLongitude(String longitude) throws CoordinateException {
		try {
			// Try to create a new BigDecimal with longitude
			BigDecimal tmpDecimal = new BigDecimal(longitude);
			
			// Make sure longitude is between -180 and 180
			if (tmpDecimal.compareTo(MAX_COORDINATE) > 0 ||
				tmpDecimal.compareTo(MIN_COORDINATE) < 0) {
				
				// If it is not, throw a NumberFormatException that will be caught
				// in the next block
				throw new NumberFormatException();
			}
			
			// If longitude is acceptable, assign it to this.longitude
			return tmpDecimal.toString();
		} catch (NumberFormatException exception) {
			// Let the user know longitude is invalid, and prompt him to change it
			System.err.println("\nInvalid longitude: " + longitude);
			System.err.println("Longitude should be greater than -180 and less than 180");
			System.out.print("Would you like to change it? [YES|no] ");
		
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new CoordinateException("Invalid longitude: " + longitude, exception);
			}
				
			// Get the new longitude
			System.out.print("Enter the new longitude: ");
			longitude = FlightPlan.input.nextLine();
			
			// Try to set the new longitude
			return getValidLongitude(longitude);
		}
	}
	
	/**
	 * Creates a new Coordinate with the given latitude and longitude, which have already 
	 * been validated by the {@link #getValidLatitude(String)} and 
	 * {@link #getValidLongitude(String)} methods from the {@link Main} class, determines 
	 * its validity, and if it is unique, returns it as a valid Coordinate.
	 *
	 * @param latitude   The {@link #latitude} of the new Coordinate
	 * @param longitude  The {@link #longitude} of the new Coordinate
	 * @param vertex     The {@link Vertex} node from which this Coordinate came
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} class
	 * @throws CoordinateException When the supplied latitude and longitude are invalid for 
	 *                             the given Vertex, and the user chooses not to fix them
	 * @throws NumberFormatException When either latitude or longitude are invalid Floats
	 * @see #getValidLatitude(String)
	 * @see #getValidLongitude(String)
	 */
	public static Coordinate getValidCoordinate(String latitude, 
	                                            String longitude, 
	                                            Vertex vertex, 
	                                            FlightPlan flightPlan) 
	                                            throws CoordinateException, 
	                                                   NumberFormatException {

		// Instantiate a new Coordinate with the given latitude and longitude
		Coordinate coordinate = new Coordinate(new Float(latitude), new Float(longitude));
		
		// Get all the existing Coordinates in the database
		Hashtable<String, Vertex> coordinates = flightPlan.getCoordinates();
		
		// Check if the Coordinate exists in the database, and if the existins Coordinate is 
		// owned by the same Vertex that was passed as a parameter
		for (Vertex conflict : coordinates.values().toArray(new Vertex[0])) {
			
			// If the current coordinate exists in the database, and vertex is either null or 
			// not the same Vertex that owns the Coordinate in the database
			if (conflict.getCoordinate().equals(coordinate) && 
			   (vertex != null ? vertex != conflict : true)) {
			
				// There exists a conflict, so let the user know and prompt him to fix it
				System.err.println("\nCoordinate, " + coordinate + ", exists.");
				System.out.print("Would you like to change it? [YES|no] ");
				
				// Get the user's response
				String reply = FlightPlan.input.nextLine().trim();
				
				// If the user replied, "no", throw an Exception and end the method here
				if (reply.matches("^(?i)n(?:o)?$")) {
					throw new CoordinateException("Coordinate, " + coordinate + ", exists.");
				}
				
				// Get the new latitude
				System.out.print("Enter the new latitude: ");
				latitude = FlightPlan.input.nextLine();
				
				// Get the new longitude
				System.out.print("Enter the new longitude: ");
				longitude = FlightPlan.input.nextLine();
				
				// Try to create a valid Coordinate with the new latitude and longitude
				return getValidCoordinate(latitude, longitude, vertex, flightPlan);
			}
		}
		
		// If Coordinate is valid, return it
		return coordinate;
	}
	
	/**
	 * Sets the latitude of this Coordinate
	 *
	 * @param latitude The latitude to assign this Coordinate
	 */
	protected void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Sets the {@link #latitude} of the {@link Vertex} passed as a parameter
	 *
	 * @param latitude   The latitude of vertex
	 * @param vertex     The {@link Vertex} node which owns the Coordinate being 
	 *                   changed
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} class
	 * @throws CoordinateException When latitude is less than -180 or greater than 
	 *                             180 degrees
	 */
	public static void setLatitude(String latitude, 
	                               Vertex vertex, 
	                               FlightPlan flightPlan) 
	                               throws CoordinateException {
		
		try {
			// Make sure latitude is valid
			latitude = getValidLatitude(latitude);
			
			// Create a new Coordinate with the new latitude and existing longitude
			Coordinate coordinate = getValidCoordinate(
				latitude, new Float(vertex.getCoordinate().getLongitude()).toString(),
				vertex, flightPlan
			);
			
			// Update vertex with the new Coordinate
			vertex.setCoordinate(coordinate);
		} catch (NumberFormatException exception) {
			// Let the user know latitude is invalid, and prompt him to fix it
			System.err.println("\nInvalid latitude: " + latitude);
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's reply
			String reply = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an exception and end the method here
			if (reply.matches("^(?i)n(?:o)?$")) {
				throw new CoordinateException("Invalid latitude: " + latitude, exception);
			}
			
			// Get the new latitude
			System.out.print("Enter the new latitude: ");
			latitude = FlightPlan.input.nextLine();
			
			// Try to set the new latitude
			setLatitude(latitude, vertex, flightPlan);
		}
	}
	
	/**
	 * Returns the latitude of this Coordinate
	 *
	 * @return The {@link #latitude} of this Coordinate
	 */
	public float getLatitude() {
		return latitude;
	}
	
	/**
	 * Sets the longitude of this Coordinate
	 *
	 * @param longitude The longitude of this Coordinate
	 */
	protected void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Sets the {@link #longitude} of the {@link Vertex} passed as a parameter
	 *
	 * @param longitude  The longitude of vertex
	 * @param vertex     The {@link Vertex} node which owns the Coordinate being
	 *                   changed
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} class
	 * @throws CoordinateException When longitude is less than -180 or greater than 
	 *                             180 degrees
	 */
	public static void setLongitude(String longitude, 
	                                Vertex vertex, 
	                                FlightPlan flightPlan) 
	                                throws CoordinateException {
		
		try {
			// Make sure longitude is valid
			longitude = getValidLongitude(longitude);
			
			// Create a new Coordinate with the existing latitude and new longitude
			Coordinate coordinate = getValidCoordinate(
				new Float(vertex.getCoordinate().getLatitude()).toString(), longitude, 
				vertex, flightPlan
			);
			
			// Update vertex with the new Coordinate
			vertex.setCoordinate(coordinate);
		} catch (NumberFormatException exception) {
			// Let the user know longitude is invlalid, and prompt him to change it
			System.err.println("\nInvalid longitude: " + longitude);
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String reply = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an exception and end the method here
			if (reply.matches("^(?i)n(?:o)?$")) {
				throw new CoordinateException("Invalid longitude: " + longitude, exception);
			}
			
			// Get the new longitude
			System.out.print("Enter the new longitude: ");
			longitude = FlightPlan.input.nextLine();
			
			// Try to set the new longitude
			setLongitude(longitude, vertex, flightPlan);
		}
	}
	
	/**
	 * Returns the longitude of this Coordinate
	 *
	 * @return The {@link #longitude} of this Coordinate
	 */
	public float getLongitude() {
		return longitude;
	}
	
	/**
	 * Overrides the Object.equals(Object obj) method by determines if
	 * two coordinates sets are equal using their latitude and longitude
	 * values
	 *
	 * @return Whether this Coordinate and another are equal
	 */
	@Override
	public boolean equals(Object coordinate) {
		
		// Only perform the following check of the Object is another Coordinate
		if (coordinate instanceof Coordinate) {
			
			// Get the latitude and longitude of this Coordinate for
			// comparison
			float x1 = latitude;
			float y1 = longitude;
			
			// Get the latitude and longitude of the Coordinate against 
			// which to compare this one
			float x2 = ((Coordinate) coordinate).getLatitude();
			float y2 = ((Coordinate) coordinate).getLongitude();
			
			// If the latitude and longitude values are equal, return true
			if (x1 == x2 && y1 ==y2) {
				return true;
			}
		}
		
		// If the Object is either not a Coordinate or its latitude and longitude
		// don't match this one, return false
		return false;
	}
	
	/**
	 * Overrides the toString() method on the Object class to return an ordered pair
	 * consisting of the latitude and longitude of this Coordinate
	 *
	 * @return The String representation of this Coordinate
	 */
	@Override
	public String toString() {
		return String.format("(%s, %s)",
		                     FlightPlan.formatter.format(latitude),
		                     FlightPlan.formatter.format(longitude));
	}
}
