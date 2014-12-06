package edu.usca.acsc492l.flightplanner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * A class to generate a flight plan with input information from the user.
 *
 * @author Dylon Edwards
 */
public class FlightPlan {
	
	/** Used to determine whether an input int is larger than the maximum size allowed */
	public static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	
	/** Used to determine whether an input float is larger than the maximum size allowed */
	public static final BigDecimal MAX_DECIMAL = BigDecimal.valueOf(Float.MAX_VALUE);
	
	/** Nicely formats decimal values */
	public static final DecimalFormat formatter = new DecimalFormat("#,###.##");
	
	/** Reads input from the user */
	public static final Scanner input = new Scanner(System.in);
	
	/** Holds the {@link Airplane} objects in the database */
	protected HashSet<Airplane> airplanes;
	
	/** Holds the {@link Airport} objects in the database */
	protected HashSet<Airport> airports;
	
	/** Holds the {@link NAVBeacon} objects in the database */
	protected HashSet<NAVBeacon> navbeacons;
	
	/** Holds the list of destinations to traverse during the current flight */
	protected ArrayList<Destination> flightDestinations;
	
	/** 
	 * Holds the names of each of the {@link Airport} and {@link NAVBeacon} 
	 * objects in the database 
	 */
	protected Hashtable<String, Vertex> names;
	
	/** 
	 * Holds the ICAO ID's of each of the {@link Airport} and {@link NAVBeacon} 
	 * objects in the database 
	 */
	protected Hashtable<String, Vertex> ICAOids;
	
	/** Holds the makes of all the {@link Airplane}s */
	protected Hashtable<String, Airplane> airplaneMakes;
	
	/** Holds the models of all the {@link Airplane}s */
	protected Hashtable<String, Airplane> airplaneModels;
	
	/** Holds each {@link Airport} with AVGAS fuel */
	protected HashSet<Airport> airportsWithAVGAS;
	
	/** Holds each {@link Airport} with JA_a fuel */
	protected HashSet<Airport> airportsWithJA_a;
	
	/** Holds the Coordinate objects for every Vertex node */
	protected Hashtable<String, Vertex> coordinates;
	
	/** Holds all the {@link Edge} nodes in the flight plan */
	protected ArrayList<Edge> edges;
	
	/** Finds the shortest path between two Vertex objects */
	protected Dijkstra dijkstra;
	
	/** Holds the current {@link Airplane} being flown in by the user */
	protected Airplane airplane;
	
	/** Holds the most recent timestamp of when the flight plan was determined */
	protected long timestamp;
	
	/**
	 * Constructs a default FlightPlan object
	 */
	public FlightPlan() {
		// Instantiate the list of Edge nodes for this FlightPlan
		edges = new ArrayList<Edge>();
		
		// Instantiate the list of destinations for this FlightPlan
		flightDestinations = new ArrayList<Destination>() {
			
			// Instantiates flightDestinations with an overridden add(E e) method that
			// adds a new Edge to this.edges whenever a new Destination is added, and
			// the size of flightDestinations is greater than one
			@Override
			public boolean add(Destination destination) {
				boolean success = super.add(destination);
				if (success) {
					
					// Get the number of elements in this ArrayList
					int size = size();
					
					// If there are more than one, create a new Edge between the current 
					// Destination object and the previous one
					if (size > 1) {
						Destination prevDest = get(size - 2);
						
						Edge edge = new Edge(
							prevDest.getDestination(), destination.getDestination()
						);
						
						edge.calculateHeading();
						edge.getTime(airplane);
						edges.add(edge);
					}
				}
				
				return success;
			}

			// Clears both this ArrayList and that of this.edges
			@Override
			public void clear() {
				super.clear();
				edges.clear();
			}
		};
		
		// Instantiate the Coordinate HashSet to track the existing Coordinates
		coordinates = new Hashtable<String, Vertex>();
		
		// Instantiate the Hastables that store Vertex names and ICAO ID's
		names = new Hashtable<String, Vertex>();
		ICAOids = new Hashtable<String, Vertex>();
		
		// Instantiate the Hashtables that store Airplane makes and models
		airplaneMakes = new Hashtable<String, Airplane>();
		airplaneModels = new Hashtable<String, Airplane>();
		
		// Instantiate the lists of Airports in the database with each type of fuel
		airportsWithAVGAS = new HashSet<Airport>();
		airportsWithJA_a = new HashSet<Airport>();
		
		// Instantiate the Dijkstra object of the database to determine the shortest
		// path between any two Vertex nodes in the database
		dijkstra = new Dijkstra();
		
		// Initialize the database
		airplanes = new HashSet<Airplane>();
		airports = new HashSet<Airport>();
		navbeacons = new HashSet<NAVBeacon>();
	}
	
	/**
	 * Sets the airplanes HashSet of this FlightPlan
	 *
	 * @param airplanes The HashSet of {@link Airplane} objects with which to 
	 *                  initialize the database
	 * @throws NullPointerException When the airplanes HashSet is null
	 */
	public void setAirplanes(HashSet<Airplane> airplanes) {
		try {
			// (Re)Initialize the Airplanes DataBase
			for (Airplane airplane : this.airplanes) {
				removeAirplane(airplane);
			}
			
			for (Airplane airplane : airplanes) {
				addAirplane(airplane);
			}
		} catch (NullPointerException exception) {
			throw new NullPointerException("airplanes may not be null");
		} catch (FlightPlanException exception) {
			System.err.println(exception.getMessage());
		}
	}
	
	/**
	 * Returns an array containing all the {@link Airplane} objects within this database
	 * 
	 * @return The {@link #airplanes} attribute copied to an {@link Airplane} array
	 */
	public Airplane[] getAirplanes() {
		return airplanes.toArray(new Airplane[0]);
	}
	
	/**
	 * Adds an {@link Airplane} object to the database
	 *
	 * @param airplane The airplane object to add to the database
	 * @throws NullPointerException When airplane is null
	 */
	public void addAirplane(Airplane airplane) {
		try {
			airplanes.add(airplane);
			airplaneMakes.put(airplane.getMake(), airplane);
			airplaneModels.put(airplane.getModel(), airplane);
		} catch (NullPointerException exception) {
			throw new NullPointerException("airplane may not be null");
		}
	}
	
	/**
	 * Removes the specified {@link Airplane} object from the database
	 *
	 * @param airplane The {@link Airplane} object to remove from the database
	 * @throws FlightPlanException  When airplane is not in the database
	 * @throws NullPointerException When airplane is null
	 */
	public void removeAirplane(Airplane airplane) throws FlightPlanException {
		try {
			if (airplanes.contains(airplane)) {
				airplanes.remove(airplane);
				airplaneMakes.remove(airplane.getMake());
				airplaneModels.remove(airplane.getModel());
			} else {
				throw new FlightPlanException(
					airplane.getMake() + " " + airplane.getModel() + 
					" Airplane is not in the database"
				);
			}
		} catch (NullPointerException exception) {
			throw new NullPointerException("airplane may not be null");
		}
	}
	
	/**
	 * Adds a new Vertex object to the database
	 *
	 * @param vertex   The Vertex object to add to the database
	 * @param database The database into which to add vertex
	 * @throws NullPointerException When either parameter is null
	 */
	protected void addVertex(Vertex vertex, 
	                         HashSet<? extends Vertex> database) 
	                         throws FlightPlanException {
		
		// Holds the union of the Airport and NAVBeacon Vertex objects in the database
		HashSet<Vertex> vertices = new HashSet<Vertex>(airports);
		vertices.addAll(navbeacons);
		
		// Add vertex to database
		((HashSet<Vertex>) database).add(vertex);
		
		// Get the Coordinate of vertex
		Coordinate coordinate = vertex.getCoordinate();
		
		// Add vertex's Coordinate to the database
		coordinates.put(coordinate.toString(), vertex);
		
		// Add the vertex's ICAO ID to the database
		ICAOids.put(vertex.getICAOid(), vertex);
		
		// Add the vertex's name to the database
		names.put(vertex.getName(), vertex);
	}
	
	/**
	 * Removes the specified Vertex object from the database
	 *
	 * @param vertex   The Vertex node to remove from the database
	 * @param database The database from which to remove vertex
	 * @throws FlightPlanException  When vertex is not in the database
	 * @throws NullPointerException When vertex is null
	 */
	protected void removeVertex(Vertex vertex, 
	                            HashSet<? extends Vertex> database) 
	                            throws FlightPlanException {
		
		if (database.contains(vertex)) {
			
			// Remove vertex from the database
			database.remove(vertex);
			
			// Remove vertex's Coordinate from the database
			coordinates.remove(vertex.getCoordinate());
			
			// Remove the vertex's ICAO ID from the database
			ICAOids.remove(vertex.getICAOid());
			
			// Remove the vertex's name from the database
			names.remove(vertex.getName());
		
			// Holds the union of all Airport and NAVBeacon Vertex objects 
			// in the database
			HashSet<Vertex> vertices = new HashSet<Vertex>(airports);
			vertices.addAll(navbeacons);
		} else {
			throw new FlightPlanException(
				vertex.getName() + " is not in the database"
			);
		}
	}
	
	/**
	 * Sets the airports HashSet of the database
	 *
	 * @param airports The HashSet of {@link Airport} objects with 
	 *                 which to initialize the database
	 * @throws NullPointerException When airports is null
	 */
	public void setAirports(HashSet<Airport> airports) {
		try {
			// Resets the set of Airports in the database (this is important
			// because the Coordinate and ICAO ID of every Airport must be
			// reset too)
			for (Airport airport : getAirports()) {
				removeAirport(airport);
			}
			
			// (Re)Initializes the Airport database (this is important
			// because the Coordinate and ICAO ID of every Airport must
			// be tracked for uniqueness)
			for (Airport airport : airports.toArray(new Airport[0])) {
				addAirport(airport);
			}
		} catch (FlightPlanException exception) {
			System.err.println(exception.getMessage());
		} catch (NullPointerException exception) {
			throw new NullPointerException("airports may not be null");
		}
	}
	
	/**
	 * Returns an array containing all the {@link Airport} objects within 
	 * this database
	 *
	 * @return The {@link #airports} attribute copied to an {@link Airport} 
	 *         array
	 */
	public Airport[] getAirports() {
		return airports.toArray(new Airport[0]);
	}
	
	/**
	 * Adds an {@link Airport} object to the database
	 *
	 * @param airport The {@link Airport} object to add to the database
	 * @throws NullPointerException When airport is null
	 */
	public void addAirport(Airport airport) throws FlightPlanException {
		addVertex(airport, airports);
	}
	
	/**
	 * Removes an {@link Airport} object from the database
	 *
	 * @param airport The {@link Airport} object to remove from the database
	 * @throws FlightPlanException  When aiport is not in the database
	 * @throws NullPointerException When airport is null
	 */
	public void removeAirport(Airport airport) throws FlightPlanException {
		try {
			removeVertex(airport, airports);
		} catch (FlightPlanException exception) {
			throw new FlightPlanException(exception);
		}
	}
	
	/**
	 * Adds an {@link Airport} to the {@link #airportsWithAVGAS list} of those 
	 * with AVGAS fuel
	 *
	 * @param airport The {@link Airport} to add to those with AVGAS fuel
	 */
	public void addAirportWithAVGAS(Airport airport) {
		airportsWithAVGAS.add(airport);
	}
	
	/**
	 * Removes an {@link Airport} from the {@link #airportsWithAVGAS list} of 
	 * those with AVGAS fuel
	 *
	 * @param airport The {@link Airport} to remove from those with AVGAS fuel
	 */
	public void removeAirportWithAVGAS(Airport airport) {
		airportsWithAVGAS.remove(airport);
	}
	
	/**
	 * Adds an {@link Airport} to the {@link #airportsWithJA_a list} of those 
	 * with Jet-A fuel
	 *
	 * @param airport The {@link Airport} to add to those with Jet-A fuel
	 */
	public void addAirportWithJA_a(Airport airport) {
		airportsWithJA_a.add(airport);
	}
	
	/**
	 * Removes an {@link Airport} from the {@link #airportsWithJA_a list} of 
	 * those with Jet-A fuel
	 *
	 * @param airport The {@link Airport} to remove from those with Jet-A fuel
	 */
	public void removeAirportWithJA_a(Airport airport) {
		airportsWithJA_a.remove(airport);
	}
	
	/**
	 * Sets the {@link NAVBeacon} HashSet of the database
	 *
	 * @param navbeacons The HashSet of {@link NAVBeacon} objects with which 
	 *                   to initialize the database
	 * @throws NullPointerException When navbeacons is null
	 */
	public void setNAVBeacons(HashSet<NAVBeacon> navbeacons) {
		try {
			// Resets the set of NAVBeacons in the database (this is important
			// because the Coordinate and ICAO ID of every NAVBeacon must be
			// reset too)
			for (NAVBeacon navbeacon : getNAVBeacons()) {
				removeNAVBeacon(navbeacon);
			}
			
			// (Re)Initializes the NAVBeacon database (this is important
			// because the Coordinate and ICAO ID of every NAVBeacon must
			// be tracked for uniqueness)
			for (NAVBeacon navbeacon : navbeacons.toArray(new NAVBeacon[0])) {
				addNAVBeacon(navbeacon);
			}
		} catch (FlightPlanException exception) {
			System.err.println(exception.getMessage());
		} catch (NullPointerException exception) {
			throw new NullPointerException("navbeacons may not be null");
		}
	}
	
	/**
	 * Returns an array containing all the {@link NAVBeacon} objects within
	 * this database
	 *
	 * @return The {@link #navbeacons} attribute copied to a {@link NAVBeacon}
	 *         array
	 */
	public NAVBeacon[] getNAVBeacons() {
		return navbeacons.toArray(new NAVBeacon[0]);
	}
	
	/**
	 * Adds a {@link NAVBeacon} object to the database
	 *
	 * @param navbeacon The {@link NAVBeacon} object to add to the database
	 * @throws NullPointerException When navbeacon is null
	 */
	public void addNAVBeacon(NAVBeacon navbeacon) {
		try {
			addVertex(navbeacon, navbeacons);
		} catch (NullPointerException exception) {
			throw new NullPointerException("navbeacon may not be null");
		} catch (FlightPlanException exception) {
			System.err.println(exception.getMessage());
		}
	}
	
	/**
	 * Removes a {@link NAVBeacon} object from the database
	 *
	 * @param navbeacon The {@link NAVBeacon} object to remove from the database
	 * @throws FlightPlanException  When navbeacon is not in the database
	 * @throws NullPointerException When navbeacon is null
	 */
	public void removeNAVBeacon(NAVBeacon navbeacon) throws FlightPlanException {
		try {
			removeVertex(navbeacon, navbeacons);
		} catch (NullPointerException exception) {
			throw new NullPointerException("navbeacon may not be null");
		}
	}
	
	/**
	 * Sets the flight plan according to the user defined list of destinations
	 *
	 * @param startDestination       The beginning destination of the flight plan
	 * @param endDestination         The ending destination of the flight plan
	 * @param additionalDestinations Additional destinations to add to the flight plan
	 * @param airplane               The {@link Airplane} in which the user wishes to fly
	 * @throws NullPointerException When any of the parameters is null
	 */
	public void setFlightPlan(Airport startDestination,
	                          Airport endDestination,
	                          ArrayList<Vertex> additionalDestinations,
	                          Airplane airplane)
	                          throws FlightPlanException {
	
		if (startDestination == null) {
			throw new NullPointerException("startDestination may not be null");
		}
		
		if (endDestination == null) {
			throw new NullPointerException("endDestination may not be null");
		}
		
		if (airplane == null) {
			throw new NullPointerException("airplane may not be null");
		}
		
		// Holds all the Airports with the required type of fuel
		Airport[] airports;
		
		// Determine whether the Airplane requires AVGAS or Jet-A fuel
		if (airplane.getType().equals(Airplane.AirplaneType.PROP)) {
			airports = airportsWithAVGAS.toArray(new Airport[0]);
		} else {
			airports = airportsWithJA_a.toArray(new Airport[0]);
		}
		
		this.airplane = airplane;
		
		int indices = airports.length;
		
		int destinations;
		if (additionalDestinations != null) {
			destinations = additionalDestinations.size();
		} else {
			destinations = 0;
		}
		
		// Holds the current time
		long timestamp = System.currentTimeMillis();
		
		// Two Vertex nodes to point to Destinations while the shortest paths are being
		// calculated
		Vertex destinationOne, destinationTwo;
		
		// Connect every Vertex node, whether Airport or NAVBeacon, together along the
		// Flight Plan
		for (int index = 0; index < indices; index ++) {
			destinationOne = airports[index];
			
			destinationOne.addEdge(startDestination, timestamp, airplane);
			startDestination.addEdge(destinationOne, timestamp, airplane);
			
			destinationOne.addEdge(endDestination, timestamp, airplane);
			endDestination.addEdge(destinationOne, timestamp, airplane);
			
			// Adds an edge between each Airport with the required fuel type and each 
			// additional destination (This is not executed if additionalDestinations 
			// is either null or empty)
			for (int innerIndex = 0; innerIndex < destinations; innerIndex ++) {
				destinationTwo = additionalDestinations.get(innerIndex);
				
				destinationOne.addEdge(destinationTwo, timestamp, airplane);
				destinationTwo.addEdge(destinationOne, timestamp, airplane);
			}
			
			// Adds an edge between each Airport with the required fuel type
			for (int innerIndex = index; innerIndex < indices; innerIndex ++) {
				destinationTwo = airports[innerIndex];
				
				destinationOne.addEdge(destinationTwo, timestamp, airplane);
				destinationTwo.addEdge(destinationOne, timestamp, airplane);
			}
		}
		
		// Compute the shortest paths between all Vertex nodes
		dijkstra.computePaths(startDestination);
		
		// Holds all the Edges of startDestination
		Hashtable<String, Edge> edges = startDestination.getEdges();
		
		// Heaps Edge nodes according to their weight
		PriorityQueue<Edge> edgesByPriority = new PriorityQueue<Edge>();
		
		// Add every Edge along the additional destinations to the priority
		// queue to be added to the Flight Plan
		if (additionalDestinations != null) {
			for (Vertex destination : additionalDestinations) {
				edgesByPriority.add(edges.get(destination.getICAOid()));
			}
		}
		
		// Points to the current Edge being used
		Edge edge;
		
		// Holds the shortest path between the two Vertex nodes being examined
		ArrayList<Destination> shortestPath;
		
		// Get the shortest path between each of the additional destinations
		while ((edge = edgesByPriority.poll()) != null) {
			Vertex destination = edge.getToVertex();
			
			// Get the shortest path between the additional destination and
			// the last destination
			shortestPath = 
				dijkstra.getShortestPath(destination, airplane, timestamp);
			
			// Append it to the Flight Plan
			appendToFlightDestinations(shortestPath, timestamp);
			
			// Compute the next shortest paths relative to the current destination
			dijkstra.computePaths(destination);
		}
		
		// Get the shortest path between the last destination and the ending
		// Airport
		shortestPath = 
			dijkstra.getShortestPath(endDestination, airplane, timestamp);
		
		// Append it to the Flight Plan
		appendToFlightDestinations(shortestPath, timestamp);
		
		// Add "beginning the flight" to the list of reasons for visiting 
		// the first Airport
		flightDestinations.get(0).addReason("beginning the flight");
		
		// Add "ending the flight" to the list of reasons for visiting 
		// the last Airport
		flightDestinations.get(flightDestinations.size() - 1)
		                  .addReason("ending the flight");
	}
	
	/**
	 * Appends a list of Destination objects to the flight plan
	 *
	 * @param list      The list of Destination objects to append to the 
	 *                  flight plan
	 * @param timestamp The timestamp of the currently running job
	 * @throws NullPointerException When list is null
	 */
	protected void appendToFlightDestinations(ArrayList<Destination> list, 
	                                          long timestamp) {
		
		try {
			// If the timestamps differ, a new flight plan is being created
			// and the old one should be reset
			if (this.timestamp != timestamp) {
				this.timestamp = timestamp;
				flightDestinations.clear();
			}
			
			// Add each Destination to the current flight plan
			for (Destination destination : list.toArray(new Destination[0])) {
				flightDestinations.add(destination);
			}
		} catch (NullPointerException exception) {
			throw new NullPointerException("list may not be null");
		}
	}
	
	/**
	 * Returns a Hashtable containing all the {@link Airport} and NAV Beacon 
	 * ICAO IDs in the database, and their corresponding Vertex node
	 *
	 * @return The {@link #ICAOids} attribute
	 */
	public Hashtable<String, Vertex> getICAOids() {
		return ICAOids;
	}
	
	/**
	 * Returns a Hashtable containing all the {@link Airport} and {@link NAVBeacon} 
	 * names in the database, and their corresponding Vertex nodes
	 *
	 * @return The {@link #names} attribute
	 */
	public Hashtable<String, Vertex> getNames() {
		return names;
	}
	
	/**
	 * Returns a Hashtable containing all the {@link Vertex} nodes in the database, 
	 * referenced according to their Coordinate pair.
	 *
	 * @return The {@link #coordinates} attribute
	 */
	public Hashtable<String, Vertex> getCoordinates() {
		return coordinates;
	}
	
	/**
	 * Returns a Hashtable containing all the {@link Airplane}s in the database, 
	 * referenced according to their makes
	 *
	 * @return The {@link #airplaneMakes} attribute
	 */
	public Hashtable<String, Airplane> getAirplaneMakes() {
		return airplaneMakes;
	}
	
	/**
	 * Returns a Hashtable containing all the {@link Airplane}s in the database,
	 * referenced according to their models
	 *
	 * @return The {@link #airplaneModels} attribute
	 */
	public Hashtable<String, Airplane> getAirplaneModels() {
		return airplaneModels;
	}
	
	/**
	 * Returns the number of {@link Airplane}s in the database
	 *
	 * @return The size of {@link #airplanes}
	 */
	public int getAirplaneCount() {
		return airplanes.size();
	}
	
	/**
	 * Returns the number of {@link Airport}s in the database
	 *
	 * @return The size of {@link #airports}
	 */
	public int getAirportCount() {
		return airports.size();
	}
	
	/**
	 * Returns the number of {@link NAVBeacon}s in the database
	 *
	 * @return The size of {@link #navbeacons}
	 */
	public int getNAVBeaconCount() {
		return navbeacons.size();
	}
	
	/**
	 * Finds all the {@link Airplane} objects that have the given substring in 
	 * either their make or model
	 *
	 * @param substring The substring to search for within the {@link Airplane} 
	 *                  objects
	 */
	public Airplane[] getAirplanesByMakeOrModel(final String substring) {
		// Initializes a Thread-safe HashSet to store the Airplane 
		// objects with the given substring
		final Set<Airplane> airplaneSet = 
			Collections.synchronizedSet(new HashSet<Airplane>());
		
		// Create a new Thread pool to find all the Airplane objects 
		// with the given substring in either their make or model
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		// Execute a new Thread to find all the Airplane objects that 
		// contain the substring in their make
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String make : airplaneMakes.keySet().toArray(new String[0])) {
					if (make.matches("^(?i).*" + substring + ".*$")) {
						airplaneSet.add(airplaneMakes.get(make));
					}
				}
			}
		});
		
		// Execute a new Thread to find all the Airplane objects that contain the
		// substring in their model
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String model : airplaneModels.keySet().toArray(new String[0])) {
					if (model.matches("^(?i).*" + substring + ".*$")) {
						airplaneSet.add(airplaneModels.get(model));
					}
				}
			}
		});
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			// Wait for the Thread pool to finish
		}
		
		// Return the results
		return airplaneSet.toArray(new Airplane[0]);
	}
	
	/**
	 * Finds all the {@link Airport} objects with the given substring in either 
	 * their name or ICAO ID
	 *
	 * @param substring The substring to search for within the {@link Airport} 
	 *                  objects
	 */
	public Airport[] getAirportsByNameOrICAOid(final String substring) {
		// Initializes a Thread-safe HashSet to store the Airport 
		// objects with the given substring
		final Set<Airport> airportSet = 
			Collections.synchronizedSet(new HashSet<Airport>());
		
		// Create a new Thread pool to find all the Airport objects with the 
		// given substring in either their name or ICAO ID
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		// Find all the Airport objects with the given substring in their name
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String name : names.keySet().toArray(new String[0])) {
					if (name.matches("^(?i).*" + substring + ".*$")) {
						Vertex tmpVertex = names.get(name);
						
						// If the Vertex is an Airport, add it to the set
						if (tmpVertex instanceof Airport) {
							airportSet.add((Airport) tmpVertex);
						}
					}
				}
			}
		});
		
		// Find all the Airport objects with the given substring in their ICAO ID
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String ICAOid : ICAOids.keySet().toArray(new String[0])) {
					if (ICAOid.matches("^(?i).*" + substring + ".*$")) {
						Vertex tmpVertex = ICAOids.get(ICAOid);
						
						// If the Vertex is an Airport, add it to the set
						if (tmpVertex instanceof Airport) {
							airportSet.add((Airport) tmpVertex);
						}
					}
				}
			}
		});
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			// Wait for the Thread pool to finish
		}
		
		// Return the results
		return airportSet.toArray(new Airport[0]);
	}
	
	/**
	 * Finds all the {@link NAVBeacon} objects with the given substring in 
	 * either their name or ICAO ID
	 *
	 * @param substring The substring to search for within the {@link NAVBeacon} 
	 *                  objects
	 */
	public NAVBeacon[] getNAVBeaconsByNameOrICAOid(final String substring) {
		// Initializes a Thread-safe HashSet to store the NAVBeacon 
		// objects with the given substring
		final Set<NAVBeacon> navbeaconSet = 
			Collections.synchronizedSet(new HashSet<NAVBeacon>());
		
		// Create a new Thread pool to find all the NAVBeacon objects with 
		// the given substring in either their name or ICAO ID
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		// Find all the NAVBeacon objects with the given substring in their name
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String name : names.keySet().toArray(new String[0])) {
					if (name.matches("^(?i).*" + substring + ".*$")) {
						Vertex tmpVertex = names.get(name);
						
						// If the Vertex is an NAVBeacon, add it to the set
						if (tmpVertex instanceof NAVBeacon) {
							navbeaconSet.add((NAVBeacon) tmpVertex);
						}
					}
				}
			}
		});
		
		// Find all the NAVBeacon objects with the given substring in their ICAO ID
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				// Find all the NAVBeacons with the substring in their ICAO ID
				for (String ICAOid : ICAOids.keySet().toArray(new String[0])) {
					if (ICAOid.matches("^(?i).*" + substring + ".*$")) {
						Vertex tmpVertex = ICAOids.get(ICAOid);
						
						// If the Vertex is an NAVBeacon, add it to the set
						if (tmpVertex instanceof NAVBeacon) {
							navbeaconSet.add((NAVBeacon) tmpVertex);
						}
					}
				}
			}
		});
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			// Wait for the Thread pool to finish
		}
		
		// Return the results
		return navbeaconSet.toArray(new NAVBeacon[0]);
	}
	
	/**
	 * Finds all the Vertex objects with the given substring in either their 
	 * name or ICAO ID
	 *
	 * @param substring The substring to search for within the {@link Airport} 
	 *                  objects
	 */
	public Vertex[] getVerticesByNameOrICAOid(final String substring) {
		// Initializes a Thread-safe HashSet to store the Airport 
		// objects with the given substring
		final Set<Vertex> vertexSet = 
			Collections.synchronizedSet(new HashSet<Vertex>());
		
		// Create a new Thread pool to find all the Airport objects with the given
		// substring in either their name or ICAO ID
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		// Find all the Airport objects with the given substring in their name
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String name : names.keySet().toArray(new String[0])) {
					if (name.matches("^(?i).*" + substring + ".*$")) {
						vertexSet.add(names.get(name));
					}
				}
			}
		});
		
		// Find all the Airport objects with the given substring in their ICAO ID
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String ICAOid : ICAOids.keySet().toArray(new String[0])) {
					if (ICAOid.matches("^(?i).*" + substring + ".*$")) {
						vertexSet.add(ICAOids.get(ICAOid));
					}
				}
			}
		});
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			// Wait for the Thread pool to finish
		}
		
		// Return the results
		return vertexSet.toArray(new Vertex[0]);
	}
	
	/**
	 * Overrides the toString() method of Object to return the entire flight plan
	 *
	 * @return The String representation of this FlightPlan
	 * @see #setFlightPlan(Airport, Airport, ArrayList, Airplane)
	 */
	@Override
	public String toString() {
		String toString = "";
		
		int size = flightDestinations.size();
		
		// Add every Destination in the current flight plan to the String to return
		for (int index = 0; index < size; index ++) {
			
			// Holds the current Destination in the flight plan at index
			Destination dest = flightDestinations.get(index);
			
			// Holds the Vertex node of dest
			Vertex vertex = dest.getDestination();
			
			// Holds the size of the longestLabelWidth attribute of vertex,
			// which will be used to format the output
			int longestLabelWidth = vertex.getLongestLabelWidth();
			
			// Append the current index, name, ICAO ID, and Coordinate of vertex to the
			// String to return
			toString += String.format("%d) %s\n" +
			                          " => %-" + longestLabelWidth + "s %s\n" +
			                          " => %-" + longestLabelWidth + "s %s\n",
			                          (index +1), vertex.getName(),
			                          "ICAO ID:", vertex.getICAOid(),
			                          "Coordinate:", vertex.getCoordinate().toString());
			
			// If the vertex is an instance of Airport, append its elevation to the String 
			// to return
			if (vertex instanceof Airport) {
				toString += String.format(" => %-" + longestLabelWidth + "s %s Meters\n",
			                              "Elevation:", ((Airport) vertex).getElevation());
			}
			
			// Make sure the index is greater than zero, because there can exist no edge 
			// between a single vertex and itself (unless it is looped, but the index in 
			// that case would still have to be greater than zero)
			if (index > 0) {
				Edge edge = edges.get(index - 1);
				toString += String.format("%s\n", edge.toString());
			}
			
			// Append the Destination to the String to return
			toString += String.format("%s\n", dest.toString());
				
			if (vertex instanceof Airport) {
				toString += "\n => Runways:\n\n";
				toString += String.format("    %-20s    %-20s    %s\n", "Number:", "Type:", "Length:");
				for (Runway runway : ((Airport) vertex).getRunways()) {
					toString += String.format("    %-20s    %-20s    %s Meters\n", 
											  runway.getNumber(), 
											  runway.getType(), 
											  FlightPlan.formatter.format(runway.getLength()));
				}
				
				toString += "\n => Communication Frequencies:\n\n";
				toString += String.format("    %-44s    %s\n", "Type:", "Frequency:");
				
				for (Comm comm : ((Airport) vertex).getComms()) {
					toString += String.format("    %-44s    %s MHz\n", comm.getType(), 
					                          FlightPlan.formatter.format(comm.getFreq()));
				}
				
				toString += "\n";
			}

			toString += "\n";
		}
		
		// Finally, append the total distance and time required to traverse the entire 
		// flight plan to the String to return
		toString += String.format("Final Statistics\n" +
		                          " => %-15s %s Kilometers\n" +
		                          " => %-15s %s Hours",
		                          "Total Distance:", formatter.format(dijkstra.getTotalDistance()),
		                          "Total Time:", formatter.format(dijkstra.getTotalTime()));
		
		// Return the String representation of this FlightPlan
		return toString;
	}
}
