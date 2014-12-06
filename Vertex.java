package edu.usca.acsc492l.flightplanner;

import java.util.Hashtable;

/**
 * Assists Dijkstra objects with determining the shortest path between 
 * any two Vertex nodes
 *
 * @author Dylon Edwards
 */
public abstract class Vertex implements Comparable<Vertex> {
	
	/** Holds the current {@link FlightPlan} instance */
	protected final FlightPlan flightPlan;
	
	/** Holds the name of the Vertex */
	protected String name = "unknown";
	
	/** Holds the ICAO ID of the Vertex */
	protected String ICAOid = "unknown";
	
	/** Holds the coordinate of this Vertex */
	protected Coordinate coordinate;
	
	/** 
	 * Holds the minimum weight between this Vertex and all its 
	 * adjacent Vertex objects 
	 */
	protected float minWeight;
	
	/** 
	 * Holds the previous vertex to this one in Dijkstra's shortest path 
	 */
	protected Vertex previousVertex;
	
	/** Holds all the Edges adjacent to this one */
	protected Hashtable<String, Edge> edges;
	
	/** Holds the timestamp of the last time this Vertex was used was */
	private long timestamp;
	
	/** 
	 * Holds the longest string width for the labels in the 
	 * {@link #toString()} method 
	 */
	protected int longestLabelWidth = 11;
	
	/**
	 * Constructs a Vertex object with the given name and ICAO ID
	 *
	 * @param flightPlan The current {@link FlightPlan} instance
	 * @param name The name of this Vertex node
	 * @param ICAOid The ICAO ID of this Vertex node
	 * @param coordinate The {@link Coordinate} of this Vertex node
	 * @throws NullPointerException When any of the parameters is null
	 * @throws VertexException When name contains characters which are neither 
	 *                         alpha-numeric, spaces, or periods
	 * @throws VertexException When ICAOid does not consist of exactly four letters
	 * @throws VertexException When coordinate already exists in flightPlan
	 */
	public Vertex(FlightPlan flightPlan, 
	              String ICAOid, 
	              String name, 
	              Coordinate coordinate) 
	              throws VertexException {
		
		if (flightPlan == null) {
			throw new VertexException("flightPlan may not be null");
		}
		
		// Initialize this.flightPlan to that in the parameters
		this.flightPlan = flightPlan;
		
		// Set the local ICAO ID, name, and Coordinate attributes
		setICAOid(ICAOid);
		setName(name);
		setCoordinate(coordinate);
		
		// Although this method is used primarily to reset this Vertex object's
		// attributes, it is also useful for initializing them
		resetVertex();
	}
	
	/**
	 * Sets the {@link #longestLabelWidth longest label width} of the 
	 * {@link #toString()} method by finding determining the maximum 
	 * of the current {@link #longestLabelWidth} and the one supplied 
	 * as a parameter
	 *
	 * @param longestLabelWidth The requested {@link #longestLabelWidth}
	 */
	protected void setLongestLabelWidth(int longestLabelWidth) {
		if (longestLabelWidth > this.longestLabelWidth) {
			this.longestLabelWidth = longestLabelWidth;
		}
	}
	
	/**
	 * Returns the {@link #setLongestLabelWidth longest label width} of the
	 * {@link #toString()} method
	 *
	 * @return The {@link #setLongestLabelWidth longest label width}
	 */
	public int getLongestLabelWidth() {
		return longestLabelWidth;
	}
	
	/**
	 * Ensures the ICAO ID passed as a parameter is valid, and prompts the user 
	 * to correct it if not
	 *
	 * @param ICAOid     The new ICAO ID to assign vertex
	 * @param vertex     The {@link Vertex} node which owns ICAOid
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} 
	 *                   class
	 * @throws VertexException When ICAOid is invalid and the user chooses 
	 *                         not to correct it
	 */
	public static String getValidICAOid(String ICAOid, 
	                                    Vertex vertex, 
	                                    FlightPlan flightPlan) 
	                                    throws VertexException {
		
		// Trim and convert the ICAO ID to all-caps
		ICAOid = ICAOid.trim().toUpperCase();
		
		// Get the map of Vertex nodes by ICAO ID from flightPlan
		Hashtable<String, Vertex> ICAOids = flightPlan.getICAOids();
		
		// Check if there exists a conflicting Vertex with the same ICAOid
		Vertex conflict = ICAOids.get(ICAOid);
		
		if (conflict != null && (vertex != null ? conflict != vertex : true)) {
			// Let the user know a conflict exists, and prompt him to change ICAOid
			System.err.printf(
				"\nThe ICAO ID, %s, conflicts with that for %s\n", ICAOid, conflict.getName()
			);
			
			// Get the user's response
			System.out.print("Would you like to change it? [YES|no] ");
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user specified, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new VertexException(ICAOid + " already exists in the database.");
			}
			
			// Get the new ICAOid
			System.out.print("Enter the new ICAO ID: ");
			ICAOid = FlightPlan.input.nextLine();
			
			// Try to validate the new ICAOid
			return getValidICAOid(ICAOid, vertex, flightPlan);
		}
		
		// Make sure ICAOid consists of exactly four letters, A-Z
		if (!ICAOid.matches("^[A-Z]{4}$")) {
			// Let the user know ICAOid is invalid, and prompt him to change it
			System.err.println("\nInvalid ICAO ID: " + ICAOid);
			System.err.println(
				"The ICAO ID must be exactly four alphabetic characters in length."
			);
			
			// Get the user's response
			System.out.print("Would you like to change it? [YES|no] ");
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new VertexException("Invalid ICAO ID: " + ICAOid);
			}
			
			// Get the new ICAOid
			System.out.print("Enter the new ICAO ID: ");
			ICAOid = FlightPlan.input.nextLine();
			
			// Try to validate the new ICAOid
			return getValidICAOid(ICAOid, vertex, flightPlan);
		}
		
		// If ICAOid is valid, return it
		return ICAOid;
	}
	
	/**
	 * Ensures the name passed as a parameter is valid, and prompts the user to 
	 * correct it if not
	 *
	 * @param name       The new name to assign vertex
	 * @param vertex     The {@link Vertex} node which owns name
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} 
	 *                   class
	 * @throws VertexException When name is invalid and the user chooses not to 
	 *                         correct it
	 */
	public static String getValidName(String name, 
	                                  Vertex vertex, 
	                                  FlightPlan flightPlan) 
	                                  throws VertexException {
		
		// Clean the name a little
		name = name.trim().replaceAll("\\s+", " ");
		
		// Get the map of Vertex nodes by name form flightPlan
		Hashtable<String, Vertex> names = flightPlan.getNames();
		
		// Check if there exists a conflicting Vertex with the same name
		Vertex conflict = names.get(name);
		if (conflict != null && (vertex != null ? conflict != vertex : true)) {
			// If there is, let the user know and prompt him to change name
			System.err.printf(
				"\nThe name, %s, conflicts with that for %s\n", name, conflict.getName()
			);
			
			// Get the user's response
			System.out.print("Would you like to change it? [YES|no] ");
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user specified, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new VertexException(name + " already exists in the database.");
			}
				
			// Get the new name
			System.out.print("Enter the new name: ");
			name = FlightPlan.input.nextLine();
			
			// Try to validate the new name
			return getValidName(name, vertex, flightPlan);
		}
		
		// Make sure the name's length is greater than zero, and that it is alpha-numeric
		if (name.length() == 0 || name.matches("^.*[^\\w\\d\\s\\.].*$")) {
			// Let the user know name is invalid, and prompt him to change it
			System.err.println("\nInvalid name: \"" + name + "\"");
			System.err.println(
				"The name may only contain characters A-Z, 0-9, periods, and spaces."
			);
			
			// Get the user's response
			System.out.print("Would you like to change it? [YES|no] ");
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user specified, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new VertexException("Invalid name: " + name);
			}
				
			// Get the new name
			System.out.print("Enter the new name: ");
			name = FlightPlan.input.nextLine();
			
			// Try to validate the new name
			return getValidName(name, vertex, flightPlan);
		}
		
		// If name is valid, return it
		return name;
	}
	
	/**
	 * Sets the ICAO ID of this Vertex
	 *
	 * @param ICAOid The ICAO ID of this Vertex
	 * @throws NullPointerException When ICAOid is null
	 * @throws VertexException When ICAOid is not exactly four letters long
	 */
	public void setICAOid(String ICAOid) {
		try {
			// Trim and convert the ICAO ID to all-caps
			ICAOid = ICAOid.trim().toUpperCase();
			
			// Get the map of Vertex nodes by ICAO ID from flightPlan
			Hashtable<String, Vertex> ICAOids = flightPlan.getICAOids();
			
			// Remove the reference to the old ICAOid from ICAOids
			ICAOids.remove(this.ICAOid);
			
			// Update this.ICAOid with the new ICAOid
			this.ICAOid = ICAOid;
			
			// Add the reference to the new ICAOid to ICAOids
			ICAOids.put(this.ICAOid, this);
		} catch (NullPointerException exception) {
			throw new NullPointerException("ICAOid may not be null");
		}
	}
	
	/**
	 * Returns the ICAO ID of this Vertex
	 *
	 * @return The {@link #ICAOid} attribute
	 */
	public String getICAOid() {
		return ICAOid;
	}
	
	/**
	 * Sets the name of this Vertex
	 *
	 * @param name The name of this Vertex
	 * @throws NullPointerException When name is null
	 * @throws VertexException When name is not alpha-numeric
	 */
	public void setName(String name) {
		try {
			// Clean the name a little
			name = name.trim().replaceAll("\\s+", " ");
			
			// Get the map of Vertex nodes by name form flightPlan
			Hashtable<String, Vertex> names = flightPlan.getNames();
			
			// Remove the reference to the old name from names
			names.remove(this.name);
			
			// Update this.name with the new name
			this.name = name;
			
			// Add a reference to the new name in names
			names.put(this.name, this);
		} catch (NullPointerException exception) {
			throw new NullPointerException("name may not be null");
		}
	}
	
	/**
	 * Returns the name of this Vertex
	 *
	 * @return The {@link #name} attribute
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the {@link Coordinate} of this Vertex
	 *
	 * @param coordinate The {@link Coordinate} of this Vertex
	 * @throws VertexException When coordinate is invalid and the user does change it
	 * @throws NullPointerException When coordinate is null
	 */
	public void setCoordinate(Coordinate coordinate) {
		try {
			// Get the map of Vertex nodes by Coordinate from flightPlan
			Hashtable<String, Vertex> coordinates = flightPlan.getCoordinates();
			
			// Check if there exists a conflicting Vertex with the same Coordinate
			Vertex conflict = coordinates.get(coordinate.toString());
			
			try {
				// Remove the reference to the old coordinate of this Vertex from coordinates
				coordinates.remove(this.coordinate.toString());
			} catch (NullPointerException notInTheDatabaseYetException) {
				// Continue
			}
			
			// Update the coordinate of the Vertex with the new one
			this.coordinate = coordinate;
			
			// Add a reference to the new coordinate of this Vertex to coordinates
			coordinates.put(this.coordinate.toString(), this);
		} catch (NullPointerException exception) {
			throw new NullPointerException("coordinate may not be null");
		}
	}
	
	/**
	 * Returns the {@link Coordinate} of this Vertex
	 *
	 * @return The {@link #coordinate} attribute
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	/**
	 * Sets the minimum weight as determined by a {@link Dijkstra} object.
	 * <br /><br />
	 * <em>NOTE:</em> This should only be called by a {@link Dijkstra} object.
	 *
	 * @param minWeight The minimum set by a Dijkstra object
	 */
	public void setMinWeight(float minWeight) {
		this.minWeight = minWeight;
	}
	
	/**
	 * Returns this Vertex object's minWeight
	 *
	 * @return The {@link #minWeight} attribute
	 */
	public float getMinWeight() {
		return minWeight;
	}
	
	/**
	 * Adds an Edge between this Vertex and another
	 *
	 * @param vertex    The Vertex object with which to connect this one
	 * @param timestamp The current time in milliseconds
	 */
	public void addEdge(Vertex vertex, long timestamp, Airplane airplane) {
		
		// Reset all the Edge objects in this Vertex if this.timestamp 
		// differs from the timestamp passed as a parameter
		if (this.timestamp != timestamp) {
			this.timestamp = timestamp;
			resetVertex();
		}
		
		try {
			Edge edge = new Edge(this, vertex, airplane);
			edges.put(vertex.getICAOid(), edge);
		} catch (VertexException exception) {
			// The selected Airplane cannot traverse this Edge,
			// so don't add it to the list of Edges
		}
	}
	
	/**
	 * Returns the Edge object corresponding to the given Vertex, or null if it 
	 * doesn't exist
	 *
	 * @param vertex The Vertex with which to retrieve an Edge from {@link #edges}
	 * @return The Edge object contained at {@link #edges}.get(vertex.getICAOid())
	 */
	public Edge getEdge(Vertex vertex) {
		return edges.get(vertex.getICAOid());
	}
	
	/**
	 * Removes an Edge object from this.edges
	 *
	 * @param vertex The Vertex object to disconnect from this one
	 */
	public void removeEdge(Vertex vertex) {
		edges.remove(vertex.getICAOid());
	}
	
	/**
	 * Returns all the Edges adjacent to this Vertex
	 *
	 * @return The {@link #edges} attribute
	 */
	public Hashtable<String, Edge> getEdges() {
		return edges;
	}
	
	/**
	 * Sets the previous Vertex, as determined by a {@link Dijkstra} object
	 * <br /><br />
	 * <em>NOTE:</em> This should only be called by a {@link Dijkstra} object.
	 *
	 * @param previousVertex The previous Vertex set by a Dijkstra object
	 */
	public void setPreviousVertex(Vertex previousVertex) {
		this.previousVertex = previousVertex;
	}
	
	/**
	 * Returns the previous Vertex of this one
	 *
	 * @return The {@link #previousVertex} attribute
	 */
	public Vertex getPreviousVertex() {
		return previousVertex;
	}
	
	/**
	 * Resets the attributes of this Vertex relating to Dijkstra objects
	 */
	protected void resetVertex() {
		minWeight = Float.POSITIVE_INFINITY;
		previousVertex = null;
		edges = new Hashtable<String, Edge>();
	}
	
	/**
	 * Compares this Vertex with another
	 *
	 * @return Either -1, 0, or 1 depending on whether the {@link #minWeight} of 
	 *         this Vertex is less than, equal to, or greater than that of the 
	 *         one being compared
	 */
	@Override
	public int compareTo(Vertex vertex) {
		return Float.compare(minWeight, vertex.getMinWeight());
	}
	
	/**
	 * Overrides the default Object.equals(Object obj) method
	 *
	 * @return Whether this Vertex equals the Object it's being compared to
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			if (((Vertex) obj).getICAOid().equals(ICAOid) &&
			    ((Vertex) obj).getCoordinate().equals(coordinate)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Overrides the toString() method in the Object class to display the ICAO ID and
	 * name of this Vertex
	 *
	 * @return The String representation of this Vertex node
	 */
	@Override
	public String toString() {
		return String.format("%s\n" + 
		                     " => %-" + longestLabelWidth + "s %s\n" +
		                     " => %-" + longestLabelWidth + "s %s",
		                     name,
		                     "ICAO ID:", ICAOid,
		                     "Coordinate:", coordinate != null ? coordinate.toString() : 
		                     "unknown");
	}
}