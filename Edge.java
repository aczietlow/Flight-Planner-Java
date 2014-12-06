package edu.usca.acsc492l.flightplanner;

import static java.lang.Math.*;

/**
 * Holds a weighted edge between two {@link Airport} or {@link NAVBeacon} objects on a 
 * 2-dimensional plane
 *
 * @author Dylon Edwards
 */
public class Edge implements Comparable<Edge> {
	
	/** Holds the directions in which the {@link Airplane} may travel */
	public static enum DirectionsEnum {
		
		/** Represents Due North */
		NORTH("Due North"),
		
		/** Represents North West */
		NORTH_WEST("North West"),
		
		/** Represents North East */
		NORTH_EAST("North East");
		
		/** Holds the String to return when {@link #toString()} is called */
		private final String toString;
		
		/**
		 * Constructs a DirectionsEnum Enumeration
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		DirectionsEnum(String toString) {
			this.toString = toString;
		}
		
		/**
		 * Overrides the toString() method of the Enumeration class to return
		 * the {@link #toString} attribute of this DirectionsEnum
		 *
		 * @return The String representation of this DirectionsEnum
		 */
		@Override
		public String toString() {
			return toString;
		}
	}
	
	/** Holds the {@link Airport} from which this Edge originates */
	protected final Vertex fromVertex;
	
	/** Holds the Airport with which the originating {@link Airport} is adjacent */
	protected final Vertex toVertex;
	
	/** Holds the weight of this Edge */
	protected final float weight;
	
	/** Holds the angle of the heading of this Edge */
	protected float theta = 0;
	
	/** Holds the direction in which the {@link Airplane} will travel */
	protected DirectionsEnum direction;
	
	/** 
	 * Holds the difference of the {@link Coordinate#latitude} of {@link #toVertex} and 
	 * the {@link Coordinate#latitude} of {@link #fromVertex} 
	 */
	protected final float v_x;
	
	/** 
	 * Holds the difference of the {@link Coordinate#longitude} of {@link #toVertex} and 
	 * the {@link Coordinate#longitude} of {@link #fromVertex} 
	 */
	protected final float v_y;
	
	/** Holds the magnitude of vector v, where v = (v_x, v_y) */
	protected final float magnitudeOfV;
	
	/** Holds the time previously returned by the {@link #getTime(Airplane)} method */
	protected float time = 0.0f;
	
	/**
	 * Constructs a new Edge object with the supplied originating {@link Airport} 
	 * or {@link NAVBeacon}, destination {@link Airport} or {@link NAVBeacon}, 
	 * and weight between them.
	 *
	 * @param fromVertex The {@link Airport} or {@link NAVBeacon} from which this 
	 *                   Edge originates
	 * @param toVertex   The {@link Airport} or {@link NAVBeacon} with which the 
	 *                   {@link #fromVertex} is adjacent
	 * @throws NullPointerException When fromVertex is null
	 * @throws NullPointerException When toVertex is null
	 */
	public Edge(Vertex fromVertex, Vertex toVertex) {
		if (fromVertex == null) {
			throw new NullPointerException("fromVertex may not be null");
		}
		
		if (toVertex == null) {
			throw new NullPointerException("toVertex may not be null");
		}
		
		// Initialize the fromVertex and toVertex using those passed as parameters
		this.fromVertex = fromVertex;
		this.toVertex   = toVertex;
		
		// Gets the latitude and longitude of the fromVertex
		Coordinate fromVertexCoordinate = fromVertex.getCoordinate();
		float fromVertexLatitude  = fromVertexCoordinate.getLatitude();
		float fromVertexLongitude = fromVertexCoordinate.getLongitude();
		
		// Gets the latitude and longitude of the toVertex
		Coordinate toVertexCoordinate = toVertex.getCoordinate();
		float toVertexLatitude  = toVertexCoordinate.getLatitude();
		float toVertexLongitude = toVertexCoordinate.getLongitude();
		
		// Determines the x and y coordinates of vector v, where 
		// v := (x, y) = (v_x, v_y)
		v_x = toVertexLatitude - fromVertexLatitude;
		v_y = toVertexLongitude - fromVertexLongitude;
		
		// Determines the magnitude of vector v
		magnitudeOfV = (float)(sqrt(pow(v_x, 2) + pow(v_y, 2)));
		
		// Determine the distance between the two Vertex nodes, where 
		// 1 degree latitude = 40,000 / 360 = ~111.1
		weight = magnitudeOfV * 111.1f;
	}
	
	/**
	 *
	 */
	public Edge(Vertex fromVertex, Vertex toVertex, Airplane airplane) throws VertexException {
		this(fromVertex, toVertex);
		
		if (airplane.getRange() < weight) {
			throw new VertexException();
		}
	}
	
	/**
	 * Returns the vertex from which this Edge originates
	 *
	 * @return The {@link #fromVertex} attribute
	 */
	public Vertex getFromVertex() {
		return fromVertex;
	}
	
	/**
	 * Returns the vertex with which the {@link #fromVertex} is adjacent
	 *
	 * @return The {@link #toVertex} attribute
	 */
	public Vertex getToVertex() {
		return toVertex;
	}
	
	/**
	 * Returns the weight of this edge
	 *
	 * @return The {@link #weight} attribute
	 */
	public float getWeight() {
		return weight;
	}
	
	/**
	 * Calculates the heading of the {@link Airplane} on this Edge.<br />
	 * <pre>
	 * Taken from: http://mathforum.org/library/drmath/view/55417.html
	 *
	 * dlat &eq; lat2 - lat1
	 * dlon &eq; lon2 - lon1
	 * y &eq; sin(lon2-lon1)*cos(lat2)
	 * x &eq; cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon2-lon1)
	 * if y &gt; 0 then
	 *     if x &gt; 0 then tc1 = arctan(y/x)
	 *     if x &lt; 0 then tc1 = 180 - arctan(-y/x)
	 *     if x &eq; 0 then tc1 = 90
	 * if y &lt; 0 then
	 *     if x &gt; 0 then tc1 = -arctan(-y/x)
	 *     if x &lt; 0 then tc1 = arctan(y/x)-180
	 *     if x &eq; 0 then tc1 = 270
	 * if y &eq; 0 then
	 *     if x &gt; 0 then tc1 = 0
	 *     if x &lt; 0 then tc1 = 180
	 *     if x &eq; 0 then [the 2 points are the same]
	 * </pre>
	 */
	public void calculateHeading() {
		// The direction is always calculated from the North East
		direction = DirectionsEnum.NORTH_EAST;
		
		// Get the Coordinate of the formVertex
		Coordinate fromVertexCoordinate = fromVertex.getCoordinate();
		float lat1 = fromVertexCoordinate.getLatitude();
		float lon1 = fromVertexCoordinate.getLongitude();
		
		// Get the Coordinate of the toVertex
		Coordinate toVertexCoordinate = toVertex.getCoordinate();
		float lat2 = toVertexCoordinate.getLatitude();
		float lon2 = toVertexCoordinate.getLongitude();
		
		// Get the difference between the Coordinates
		float dlat = lat2 - lat1;
		float dlon = lon2 - lon1;
		
		// Determine the x and y variables which will be used to calculate the angle of degree
		// of the toVertex from to the fromVertex relative to North East
		float y = (float)(sin(lon2 - lon1) * cos(lat2));
		float x = (float)(cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1));
		
		// Using the algorithm above, calucate theta, or tcl
		if (y > 0) {
			if (x > 0) {
				theta = (float)(atan(y / x));
			} else if (x < 0) {
				theta = (float)(180.0f - atan(-y / x));
			} else {
				theta = 90.0f;
			}
		} else if (y < 0) {
			if (x > 0) {
				theta = (float)(-atan(-y / x));
			} else if (x > 0) {
				theta = (float)(atan(y / x) - 180.0f);
			} else {
				theta = 270.0f;
			}
		} else {
			if (x > 0) {
				theta = 0.0f;
			} else if (x < 0) {
				theta = 180.0f;
			} else {
				theta = 0.0f;
			}
		}
	}
	
	/**
	 * Returns the angle the {@link Airplane} will take along this Edge
	 *
	 * @return The {@link #theta} attribute
	 * @see #calculateHeading()
	 */
	public float getTheta() {
		return theta;
	}
	
	/**
	 * Returns the direction in which the {@link Airplane} will be traveling
	 *
	 * @return The {@link #direction} attribute
	 * @see #calculateHeading()
	 */
	public DirectionsEnum getDirection() {
		return direction;
	}
	
	/**
	 * Determines and returns the time required to traverse this Edge with 
	 * the given {@link Airplane}
	 *
	 * @param airplane The {@link Airplane} being used to traverse this Edge
	 * @return The time required to travel along this Edge in the selected 
	 *         {@link Airplane}
	 * @throws NullPointerException When airplane is null, since the time 
	 *                              required to traverse this edge cannot 
	 *                              be determined until airplane has been 
	 *                              initialized
	 */
	public float getTime(Airplane airplane) {
		try {
			// Holds the time required to traverse this edge at the cruise 
			// speed of the specified Airplane
			float time = weight / airplane.getCruiseSpeed();
			
			// Set the time required to traverse this Edge in airplane
			this.time = time;
		} catch (NullPointerException exception) {
			throw new NullPointerException(
				"time cannot be calculated since airplane is null"
			);
		}
		
		// Return the time required to traverse this Edge in the given Airplane
		return time;
	}
	
	/**
	 * Returns the previously determined time from {@link #getTime(Airplane)}
	 *
	 * @return The previous {@link #time}
	 */
	public float getTime() {
		return time;
	}
	
	/**
	 * Compares this Edge with another, and returns which is larger
	 *
	 * @param edge The edge with which to compare this one
	 * @return 1, 0, or -1 depending on whether this Edge is greater than, 
	 *         equal to, or less than the other
	 */
	@Override
	public int compareTo(Edge edge) {
		return Float.compare(weight, edge.getWeight());
	}
	
	/**
	 * Overrides the toString() method of this Object class to return the 
	 * heading, length, and time required to traverse this Edge in the 
	 * specified {@link Airplane}
	 *
	 * @return The String representation of this Edge node
	 */
	@Override
	public String toString() {
		int longestLabelWidth = max(
			fromVertex.getLongestLabelWidth(), toVertex.getLongestLabelWidth()
		);
		
		return String.format(" => %-" + longestLabelWidth + "s %s Degrees %s\n" +
		                     " => %-" + longestLabelWidth + "s %s Kilometers\n" +
		                     " => %-" + longestLabelWidth + "s %s Hours",
		                     "Heading:", FlightPlan.formatter.format(theta), direction.toString(),
		                     "Distance:", FlightPlan.formatter.format(weight),
		                     "Time:", FlightPlan.formatter.format(time));
	}
}