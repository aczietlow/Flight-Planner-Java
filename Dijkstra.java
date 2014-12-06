package edu.usca.acsc492l.flightplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * <p>Dijkstra's Shortest Path Algorithm for finding the shortest weighted path between 
 * two vertices (taken from <em><ins>Discrete Mathematics and Its Applications</ins></em>, 
 * <em>6<sup>th</sup> Edition</em>, by <em>Kenneth H. Rosen</em>):</p>
 * 
 * <strong>procedure</strong> <em>Dijkstra</em>(<em>G</em>: weighted connected simple graph, 
 * with all weights positive)<br />
 * {<em>G</em> has vertices <em>a = v<sub>0</sub>, v<sub>1</sub>, &hellip;, v<sub>n</sub> = 
 * z</em> and weights <em>w(v<sub>i</sub>, v<sub>j</sub>)</em>, where <em>w(v<sub>i</sub>, 
 * v<sub>j</sub>) = &infin;</em> if <em>{v<sub>i</sub>, v<sub>j</sub>}</em> is not an edge 
 * in <em>G</em>}<br />
 * <strong>for</strong> <em>i</em> := <em>1</em> <strong>to</strong> <em>n</em><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<em>L(v<sub>i</sub>)</em> := <em>&infin;</em>
 * <br />
 * <em>L(a)</em> := <em>0</em><br />
 * <em>S</em> := </em>&empty;</em><br />
 * {the weights are now initialized so that the weight of <em>a</em> is <em>0</em> and all other 
 * weights are <em>&infin;</em>, and <em>S</em> is the empty set}<br />
 * <strong>while</strong> <em>z &notin; S</em><br />
 * <strong>begin</strong><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<em>u</em> := a vertex not in <em>S</em> with 
 * <em>L(u)</em> minimal<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<em>S</em> := <em>S &cup; {u}</em><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong>for</strong> all vertices <em>v</em> 
 * not in <em>S</em><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <strong>if</strong> 
 * <em>L(u)</em> + <em>w(u, v)</em> &lt; <em>L(v)</em> <strong>then</strong> <em>L(v)</em> := 
 * <em>L(u)</em> + <em>w(u, v)</em><br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{this adds a vertex to <em>S</em> with minimal 
 * weight and updates the weights of vertices not in <em>S</em>}<br />
 * <strong>end</strong> {<em>L(z)</em> = length of a shortest path from <em>a</em> to <em>z</em>}
 *
 * @author Dylon Edwards
 */
public class Dijkstra {
	
	/** Holds the distance between each {@link Airport} with the required fuel type */
	protected float distance = Float.NaN;
	
	/** Holds the entire distance traveled during each complete flight plan */
	protected float totalDistance = Float.NaN;
	
	/** Holds the total time required to complete the entire flight plan */
	protected float totalTime = Float.NaN;
	
	/** Holds the current date in milliseconds */
	private long timestamp;
	
	/**
	 * Constructs a default Dijkstra object
	 */
	public Dijkstra() {
		// The following method call is useful for both initializing 
		// and resetting the attributes of this Dijkstra instance
		resetDijkstra();
	}
	
	/**
	 * Computes the shortest paths from the origin vertex to each destination 
	 * vertex using Dijkstra's Shortest Path algorithm
	 * 
	 * @param a the origin {@link Vertex} from which the shortest paths are calculated
	 * @throws NullPointerException When a is null
	 */
	public void computePaths(Vertex a) throws VertexException {
		if (a == null) {
			throw new NullPointerException("a may not be null");
		}
		
		// Set the minimum weight of `a' to 0
		a.setMinWeight(0);
		
		// Instantiate a new PriorityQueue to assist in determining the shortest 
		// paths between the vertices
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(a);
		
		while (!vertexQueue.isEmpty()) {
			
			// Assign vertexQueue's head vertex to `u'
			Vertex u = vertexQueue.poll();
			
			for (Edge e : u.getEdges().values()) {
				
				// ... assign `v' to the current adjacent vertex of `u'
				Vertex v = e.getToVertex();
				
				// ... get the weight of the current edge
				float weight = e.getWeight();
				
				// ... get the total weight of the current edge plus the minimum weight of u
				float weightThroughU = u.getMinWeight() + weight;
				
				if (weightThroughU < v.getMinWeight()) {
					
					// Remove Vertex v from the priority queue so it 
					// may be reheaped when it is added back in
					vertexQueue.remove(v);
					
					v.setMinWeight(weightThroughU);
					v.setPreviousVertex(u);
					vertexQueue.add(v);
				}
			}
		}
	}
	
	/**
	 * Dynamically creates and determines the reasons for visiting a {@link Destination}
	 *
	 * @param vertex The {@link Vertex} being visited
	 * @param type   The {@link Airplane.AirplaneType} of the {@link Airplane} being used
	 * @return The {@link Destination} created using vertex and type
	 */
	protected Destination getDestination(Vertex vertex, Airplane.AirplaneType type) {
		// Create a new Destination with vertex
		Destination destination = new Destination(vertex);
		
		// Dynamically determine why vertex is being visited
		if (vertex instanceof Airport) {
			// Land at the Airport
			destination.addReason(
				"landing at " + vertex.getName().replaceAll("(?i)\\sairport$", "") + " Airport"
			);
			
			// If the Airport carries the required fuel type for airplane, refuel there
			if (type.equals(Airplane.AirplaneType.PROP)) {
				if (((Airport) vertex).hasAVGAS()) {
					
					// The Airplane is refueling
					destination.addReason("refueling");
					distance = 0;
				}
			} else {
				if (((Airport) vertex).hasJA_a()) {
					
					// The Airplane is refueling
					destination.addReason("refueling");
					distance = 0;
				}
			}
		} else {
			// The Airplane is flying over a NAV Beacon
			destination.addReason(
				"flying over " + vertex.getName().replaceAll("(?i)\\snav\\s*beacon$", "") + " NAV Beacon"
			);
		}
		
		// Return the new Destination
		return destination;
	}
	
	/**
	 * Returns the shortest path from the source vertex to the target vertex
	 * 
	 * @param z         The target vertex to which the source vertex is attempting to 
	 *                  establish a path.
	 * @param airplane  The {@link Airplane} being used to traverse this path
	 * @param timestamp The time at which this method was invoked; in the event it differs 
	 *                  from the timestamp attribute of this Dijkstra object, the local 
	 *                  attributes of this Dijkstra object will be reset to accomodate for 
	 *                  the new path
	 * @return the shortest path of vertices from the source vertex to the target vertex.
	 * @throws DijkstraException When the distance flown is greater than the possible 
	 *                           range of the selected {@link Airplane}
	 * @throws NullPointerException When any of the parameters is null
	 */
	public ArrayList<Destination> getShortestPath(Vertex z,
	                                              Airplane airplane, 
	                                              long timestamp) 
	                                              throws DijkstraException {

		if (z == null) {
			throw new NullPointerException("z may not be null");
		}
		
		if (airplane == null) {
			throw new NullPointerException("airplane may not be null");
		}
		
		// If the timestamp differs from the last time this Dijkstra was queried,
		// a new flight plan is being constructed and all the values of this
		// Dijkstra should be reset
		if (this.timestamp != timestamp) {
			this.timestamp = timestamp;
			resetDijkstra();
		}
		
		// Holds the type of this Airplane, which dictates what kind of fuel it needs
		Airplane.AirplaneType type = airplane.getType();
		
		// Holds the range of this Airplane, which determines whether the flight plan is feasible
		float range = airplane.getRange();
		
		// Instantiate a new ArrayList to hold the shortest path from vertex a to vertex z
		ArrayList<Destination> path = new ArrayList<Destination>();
		
		// Retrieve the shortest path from vertex a to vertex z
		Vertex vertex = z;
		
		// Holds the previous vertex in the list
		Vertex previousVertex;
	
		// Holds the Edge object between vertex and prevVertex
		Edge edge;
		
		// Holds the weight of the Edge
		float weight;
		
		// Get the Destination object of the current Vertex
		Destination destination = getDestination(vertex, type);
		
		// If the previous vertex is not null, get the Edge between vertex and it, otherwise,
		// get the Edge between vertex and itself
		edge = vertex.getEdge(vertex.previousVertex != null ? vertex.previousVertex : vertex);
		
		// Add destination to the path
		path.add(destination);
		
		// Work backwards from the current Vertex
		while (vertex.previousVertex != null) {
			previousVertex = vertex;         // Let previousVertex point to the current Vertex
			vertex = vertex.previousVertex;  // Let vertex point to its previous Vertex
			
			// Fetches the Edge object between vertex and prevVertex
			edge = vertex.getEdge(previousVertex);
			weight = edge.getWeight();
			
			// Increment the distance traveled by the weight of the Edge
			distance += weight;
			
			// Ensure the distance can be covered by the Airplane
			if (distance > range) {
				throw new DijkstraException(
					"Due to lack of refueling facilities, this trip is impossible."
				);
			}
			
			// Construct a new Destination with vertex
			destination = getDestination(vertex, type);
			
			// Add the destination to the path of destinations
			path.add(destination);
			
			// Increment the total distance and time required to traverse this path
			totalDistance += weight;
			totalTime += edge.getTime(airplane);
		}
		
		// Reverse the order of the shortest path List so it describes the path 
		// from vertex a to vertex z, rather than from vertex z to vertex a
		Collections.reverse(path);
		
		// Return the shortest path List
		return path;
	}
	
	/**
	 * Returns the total distance in Kilometers traveled this entire flight plan
	 *
	 * @return The {@link #totalDistance} attribute
	 */
	public float getTotalDistance() {
		return totalDistance;
	}
	
	/**
	 * Returns the total time required to traverse the entire flight plan
	 *
	 * @return The {@link #totalTime} attribute
	 */
	public float getTotalTime() {
		return totalTime;
	}
	
	/**
	 * Resets the attributes of this Dijkstra object
	 */
	public void resetDijkstra() {
		distance = 0;
		totalDistance = 0;
		totalTime = 0;
	}
}
