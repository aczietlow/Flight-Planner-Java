package edu.usca.acsc492l.flightplanner;

import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Imports from Apache's Xerces2 XML library (3rd party)
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

/**
 * A class to serve as a model for transferring data to and from an XML database
 *
 * @author Dylon Edwards
 */
public class DatabaseModel {
	
	/** Holds the {@link Airplane} objects */
	protected HashSet<Airplane> airplanes;
	
	/** Holds the {@link Airport} objects */
	protected HashSet<Airport> airports;
	
	/** Holds the {@link NAVBeacon} objects */
	protected HashSet<NAVBeacon> navbeacons;
	
	/** Holds the database file name */
	protected final File databaseFile;
	
	/** Holds the {@link FlightPlan} object to use in creating {@link Airport}s */
	protected final FlightPlan flightPlan;
	
	/** Holds the username */
	protected String username;
	
	/** Holds the user's session */
	protected Element usernameElement;
	
	/** Maintains the XML database */
	protected Document document;
	
	/** Produces DocumentBuilder objects */
	DocumentBuilderFactory docFactory;
	
	/** Parses XML documents */
	DocumentBuilder docBuilder;
	
	/**
	 * Constructs a DatabaseModel object to read and write the flight plan database
	 *
	 * @param flightPlan The {@link FlightPlan} object to use in creating {@link Airport}s
	 * @throws NullPointerException When flightPlan is null
	 */
	public DatabaseModel(FlightPlan flightPlan) throws FlightPlanException, Exception {
		if (flightPlan == null) {
			throw new NullPointerException("flightPlan may not be null");
		}
		
		this.flightPlan = flightPlan;
		
		// Holds the location of the database file
		String databaseURI = "resources/database.xml";
		
		// Attempt to obtain the URL of the database URI
		URL databaseURL = getClass().getResource(databaseURI);
		
		// If the database file exists ...
		if (databaseURL != null) {
			
			// ... instantiate databaseFile with it
			databaseFile = new File(databaseURL.toURI());
		
		// ... otherwise ...
		} else {
			
			// ... try to create the file
			databaseFile = new File(databaseURI);
			
			// Get the parent folder of the database file
			File parentFile = databaseFile.getParentFile();
			
			// Make sure it exists
			if (!parentFile.exists()) {
				
				// If not, try to create it
				if (!parentFile.mkdirs()) {
					throw new FlightPlanException("\"" + databaseURI + "\" cannot be created");
				}
			
			// Make sure the application has permission to write the database file
			} else if (!parentFile.canWrite() || 
			           databaseFile.exists() && !databaseFile.canWrite()) {
				
				// Let the user know the database cannot be written
				throw new FlightPlanException(
					"\"" + databaseURI + "\" cannot be written due to lack of permissions"
				);
			}
		}
		
		// Make sure the file exists
		databaseFile.createNewFile();
		
		// Instantiate the classes that will create the XML Document
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();

		// Initialize the Object sets necessary to construct the FlightPlan database
		airplanes  = new HashSet<Airplane>();
		airports   = new HashSet<Airport>();
		navbeacons = new HashSet<NAVBeacon>();
	}
	
	/**
	 * Initializes the flight database of the {@link FlightPlan} class
	 */
	protected void initializeFlightPlanDatabase() {
		// Initialize the airplanes HashSet of flightPlan
		flightPlan.setAirplanes(airplanes);
		
		// Initialize the airports HashSet of flightPlan
		flightPlan.setAirports(airports);
		
		// Initialize the navbeacons HashSet of flightPlan
		flightPlan.setNAVBeacons(navbeacons);
	}
	
	/**
	 * Sets the username variable with which to save and load the current session
	 */
	protected void setUserName() {
		
		// Get the user's name
		System.out.print("username: ");
		username = FlightPlan.input.nextLine().trim();
		
		// Make sure the username is valid and alpha-numeric
		if (username.length() > 0 && username.matches("^(?i).*[^a-z0-9_].*$")) {
			System.err.println("Invalid username: " + username);
			System.out.println();
			
			setUserName();
		}
		
		// If the user did not enter a name ...
		if (username.length() == 0) {
			
			// ... make sure they do not want to continue without saving their session
			System.out.println("If you don't enter a valid username, your session will not be saved.");
			System.out.print("Are you sure you want to continue? [NO|yes] ");
			
			// Obtain the user's reply
			String reply = FlightPlan.input.nextLine().trim();
			
			// ... prompt them for their username again unless they explicitly
			// state they wish to continue using the default session
			if (!reply.matches("^(?i)y(?:es)?$")) {
				setUserName();
			}
		}
	}
	
	/**
	 * Constructs a database from the specified XML file.
	 *
	 * @throws IOException When database does not exist
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void readDatabase() throws IOException,
	                                  ParserConfigurationException,
	                                  SAXException {
		
		// Obtain the user's name
		setUserName();
		
		try {
			// Holds all the nodes from the file
			document = docBuilder.parse(databaseFile);
		} catch (SAXParseException exception) {
			// The database does not yet exist, so end the method here
			return;
		}
		
		// Fetch the node corresponding to the username (quick-and-dirty, but effective)
		NodeList usernameElements = document.getElementsByTagName(username);
		
		// If there is no previous session recorded for the username ...
		if (usernameElements.getLength() == 0) {
			
			// ... stop here
			return;
		}
		
		// Get the user's previous session
		usernameElement = (Element)usernameElements.item(0);
			
		// Holds all the airplanes elements in the document
		NodeList airplanesElements = usernameElement.getElementsByTagName("airplanes");
		
		// Holds the airlpanes element from the document
		Element airplanesElement = (Element)airplanesElements.item(0);
		
		// Holds all the Airplane objects in the document
		NodeList airplaneElements = airplanesElement.getElementsByTagName("airplane");
		
		// Store all the Airplane objects in the database
		if (airplaneElements.getLength() > 0) {
			
			// Declare some variables to hold the information for each Airplane object
			Text airplaneMake;           // Holds the make attribute of the current Airplane
			Text airplaneModel;          // Holds the model attribute of the current Airplane
			Text airplaneType;           // Holds the type attribute of the current Airplane
			String airplaneTypeVal;      // Holds the String representation of airplaneType
			Text airplaneTankSize;       // Holds the tankSize attribute of the current Airplane
			Text airplaneLitersPerHour;  // Holds the litersPerHour attribute of the current Airplane
			Text airplaneCruiseSpeed;    // Holds the cruiseSpeed attribute of the current Airplane
			
			// Points to the current Airplane object
			Airplane airplane;
			
			// Points to the current airplane element
			Element airplaneElement;
			
			// Add each Airplane object to the database
			for (int index = 0; index < airplaneElements.getLength(); index ++) {
				
				// Get the reference to the current airplane element
				airplaneElement = (Element)airplaneElements.item(index);
				
				// Get the make of this Airplane
				airplaneMake = (Text)(
					(Element)airplaneElement.getElementsByTagName("make").item(0)
				).getFirstChild();
				
				// Get the model of this Airplane
				airplaneModel = (Text)(
					(Element)airplaneElement.getElementsByTagName("model").item(0)
				).getFirstChild();
				
				// Get the type of this Airplane
				airplaneType = (Text)(
					(Element)airplaneElement.getElementsByTagName("type").item(0)
				).getFirstChild();
				
				// Get the tank size of this Airplane
				airplaneTankSize = (Text)(
					(Element)airplaneElement.getElementsByTagName("tank_size").item(0)
				).getFirstChild();
				
				// Get the ratio of liter of fuel burned per hour of this Airplane
				airplaneLitersPerHour = (Text)(
					(Element)airplaneElement.getElementsByTagName("liters_per_hour").item(0)
				).getFirstChild();
				
				// Get the cruise speed of this Airplane
				airplaneCruiseSpeed = (Text)(
					(Element)airplaneElement.getElementsByTagName("cruise_speed").item(0)
				).getFirstChild();
				
				// Get the String representation of airplaneType
				airplaneTypeVal = airplaneType.getWholeText();

				try {
					// Instantiates a new Airplane object with the collected information
					airplane = new Airplane(
						airplaneMake.getWholeText(),
						airplaneModel.getWholeText(),
						airplaneTypeVal.equals("JET") ? 
						Airplane.AirplaneType.JET :
						airplaneTypeVal.equals("PROP") ? 
						Airplane.AirplaneType.PROP :
						Airplane.AirplaneType.TURBO_PROP,
						airplaneTankSize.getWholeText(),
						airplaneLitersPerHour.getWholeText(),
						airplaneCruiseSpeed.getWholeText(),
						flightPlan
					);
					
					// Add the Airplane object to the database
					airplanes.add(airplane);
				} catch (FlightPlanException exception) {
					System.err.println(exception.getMessage());
				}
			}
		}
		
		// Holds all the airports elements in the document
		NodeList airportsElements = usernameElement.getElementsByTagName("airports");
		
		// Holds the airports element from the document
		Element airportsElement = (Element)airportsElements.item(0);
		
		// Holds all the Airport objects in the document
		NodeList airportElements = airportsElement.getElementsByTagName("airport");
		
		// Store all the Airport objects in the database
		if (airportElements.getLength() > 0) {
		
			// Declare some variables to hold the information for each Airport object
			Text airportICAOid;     // Holds the ICAOid attribute of the current Airport object
			Text airportName;       // Holds the name attribute of the current Airport object
			Text airportHasAVGAS;   // Holds the hasAVGAS attribute of the current Airport object
			Text airportHasJA_a;    // Holds the hasJA_a attribute of the current Airport object
			Text airportElevation;  // Holds the elevation attribute of the current Airport object
			
			// Holds the Airport object's coordinate
			Element airportCoordinateElement;  // Holds the coordinate of the current Airport object
			Text airportLatitude;              // Holds the latitude attribute of the current Airport
			Text airportLongitude;             // Holds the longitude attribute of the current Airport
			
			// Points to the current Airport Coordinate object
			Coordinate airportCoordinate;
			
			// Hold the attributes of each Runway object for the Airport
			NodeList airportRunwaysElements;     // Holds all the runways elements in the document
			Element airportRunwaysElement;       // Points to the first runways element in the document
			NodeList airportRunwayElements;      // Holds all the runway elements in the runways element
			Element airportRunwayElement;        // Points to each runway element
			Element airportRunwayNumberElement;  // Holds the number of the runway
			Text airportRunwayType;              // Holds the type attribute of each runway element
			String airportRunwayTypeVal;         // Holds the String representation of airportRunwayType
			Text airportRunwayLength;            // Holds the length attribute of each runway element
			Text airportRunwayNumber;            // Holds the value of the runway number
			
			// Points to each Airport Runway object
			Runway airportRunway;
			
			// Hold the attributes of each Comm object for the Airport
			NodeList airportCommsElements;  // Holds all the comms element in each airport element
			Element airportCommsElement;    // Points to the first comms element in the document
			NodeList airportCommElements;   // Holds all the comm elements in the comms element
			Element airportCommElement;     // Points to each comm element
			Text airportCommType;           // Holds the type attribute of each comm element
			String airportCommTypeVal;      // Holds the String representation of airportCommType
			Text airportCommFreq;           // Holds the freq attribute of each comm element
			
			// Points to each Airport Comm object
			Comm airportComm;
			
			// Points to the current Airport object
			Airport airport;
			
			// Points to the current airplane element
			Element airportElement;
			
			// Add each Airport object to the database
			for (int index = 0; index < airportElements.getLength(); index ++) {
				
				// Get the reference to the current airport element
				airportElement = (Element)airportElements.item(index);
				
				// Acquire the ICAO ID of this Airport
				airportICAOid = (Text)(
					(Element)airportElement.getElementsByTagName("icao_id").item(0)
				).getFirstChild();
				
				// Acquire the name of this Airport
				airportName = (Text)(
					(Element)airportElement.getElementsByTagName("name").item(0)
				).getFirstChild();
				
				// Acquire whether this Airport carries AVGAS fuel
				airportHasAVGAS = (Text)(
					(Element)airportElement.getElementsByTagName("has_avgas").item(0)
				).getFirstChild();
				
				// Acquire whether this Airport carries Jet-A fuel
				airportHasJA_a = (Text)(
					(Element)airportElement.getElementsByTagName("has_ja_a").item(0)
				).getFirstChild();
				
				// Acquire the elevation of this Airport
				airportElevation = (Text)(
					(Element)airportElement.getElementsByTagName("elevation").item(0)
				).getFirstChild();
				
				// Get the coordinate of this Airport
				airportCoordinateElement = 
					(Element)airportElement.getElementsByTagName("coordinate").item(0);
				
				// Get the latitude of this Airport
				airportLatitude = (Text)(
					(Element)airportCoordinateElement.getElementsByTagName("latitude").item(0)
				).getFirstChild();
				
				// Get the longitude of this Airport
				airportLongitude = (Text)(
					(Element)airportCoordinateElement.getElementsByTagName("longitude").item(0)
				).getFirstChild();

				try {
					// Instantiate a new Coordinate object for this Airport
					airportCoordinate = Coordinate.getValidCoordinate(
						airportLatitude.getWholeText(), airportLongitude.getWholeText(), null, flightPlan
					);
					
					// Instantiate an Airport object with the obtained attributes to add to the database
					airport = new Airport(flightPlan,
										airportICAOid.getWholeText(),
										airportName.getWholeText(),
										new Boolean(airportHasAVGAS.getWholeText()).booleanValue(),
										new Boolean(airportHasJA_a.getWholeText()).booleanValue(),
										airportCoordinate,
										airportElevation.getWholeText());
					
					// Get all the runways elements for this airport
					airportRunwaysElements = airportElement.getElementsByTagName("runways");
					airportRunwaysElement  = (Element)airportRunwaysElements.item(0);
					airportRunwayElements  = airportRunwaysElement.getElementsByTagName("runway");
					
					// Obtain the information of each runway element for the current Airport
					for (int innerIndex = 0; innerIndex < airportRunwayElements.getLength(); innerIndex ++) {
						
						// Get the current runway element
						airportRunwayElement = (Element)airportRunwayElements.item(innerIndex);
						
						// Get the type of this Runway
						airportRunwayType   = (Text)(
							(Element)airportRunwayElement.getElementsByTagName("type").item(0)
						).getFirstChild();
						
						// Get the length of this Runway
						airportRunwayLength = (Text)(
							(Element)airportRunwayElement.getElementsByTagName("length").item(0)
						).getFirstChild();
						
						// Get the String representation of airportRunwayType
						airportRunwayTypeVal = airportRunwayType.getWholeText();
						
						// Get the number of this Runway
						airportRunwayNumber = (Text)(
							(Element)airportRunwayElement.getElementsByTagName("number").item(0)
						).getFirstChild();
						
						try {
							// Instantiate a new Runway object with the attributes from this runway element
							airportRunway = new Runway(
								airportRunwayNumber.getWholeText(),
								airportRunwayLength.getWholeText(),
								airportRunwayTypeVal.equals("VISUAL_RUNWAY") ? 
								Runway.RunwayType.VISUAL_RUNWAY :
								airportRunwayTypeVal.equals("NONPRECISION_RUNWAY") ? 
								Runway.RunwayType.NONPRECISION_RUNWAY :
								Runway.RunwayType.PRECISION_RUNWAY
							);

							// Add this Runway to the current Airport
							airport.addRunway(airportRunway);
						} catch (FlightPlanException _exception) {
							System.err.println(_exception.getMessage());
						}
					}
					
					// Get all the comm elements for the current Airport
					airportCommsElements = airportElement.getElementsByTagName("comms");
					airportCommsElement  = (Element)airportCommsElements.item(0);
					airportCommElements  = airportCommsElement.getElementsByTagName("comm");
					
					// Obtain the information of each comm element for the current Airport
					for (int innerIndex = 0; innerIndex < airportCommElements.getLength(); innerIndex ++) {
						
						// Get the current comm element
						airportCommElement = (Element)airportCommElements.item(innerIndex);
						
						// Get the type of this Comm element
						airportCommType = (Text)(
							(Element)airportCommElement.getElementsByTagName("type").item(0)
						).getFirstChild();
						
						// Get the frequency of this Comm element
						airportCommFreq = (Text)(
							(Element)airportCommElement.getElementsByTagName("freq").item(0)
						).getFirstChild();
						
						// Get the String representation of airportCommType
						airportCommTypeVal = airportCommType.getWholeText();
						
						try {
							// Instantiate a new Comm object with the acquired attributes
							airportComm = new Comm(
								airportCommTypeVal.equals("ATIS") ? 
								Comm.CommType.ATIS :
								airportCommTypeVal.equals("MULTICOM") ? 
								Comm.CommType.MULTICOM :
								airportCommTypeVal.equals("UNICOM") ? 
								Comm.CommType.UNICOM :
								airportCommTypeVal.equals("FAA_FLIGHT_SERVICE_STATION") ? 
								Comm.CommType.FAA_FLIGHT_SERVICE_STATION :
								airportCommTypeVal.equals("AIRPORT_TRAFFIC_CONTROL") ? 
								Comm.CommType.AIRPORT_TRAFFIC_CONTROL :
								airportCommTypeVal.equals("CLEARANCE_DELIVERY_POSITION") ? 
								Comm.CommType.CLEARANCE_DELIVERY_POSITION :
								airportCommTypeVal.equals("GROUND_CONTROL_POSITION_IN_TOWER") ? 
								Comm.CommType.GROUND_CONTROL_POSITION_IN_TOWER :
								airportCommTypeVal.equals("RADAR_OR_NONRADAR_APPROACH_CONTROL_POSITION") ? 
								Comm.CommType.RADAR_OR_NONRADAR_APPROACH_CONTROL_POSITION :
								airportCommTypeVal.equals("RADAR_DEPARTURE_CONTROL_POSITION") ? 
								Comm.CommType.RADAR_DEPARTURE_CONTROL_POSITION :
								airportCommTypeVal.equals("FAA_AIR_ROUTE_TRAFFIC_CONTROL_CENTER") ? 
								Comm.CommType.FAA_AIR_ROUTE_TRAFFIC_CONTROL_CENTER :
								airportCommTypeVal.equals("CLASS_C") ? 
								Comm.CommType.CLASS_C :
								Comm.CommType.EMERGENCY_CALL,
								airportCommFreq.getWholeText()
							);

							// Add the current Comm object to the current Airport
							airport.addComm(airportComm);
						} catch (FlightPlanException _exception) {
							System.err.println(_exception.getMessage());
						}
					}
					
					// Add the current Airport to the database
					airports.add(airport);
				} catch (FlightPlanException exception) {
					System.err.println(exception.getMessage());
				}
			}
		}
		
		// Holds all the navbeacons elements in the document
		NodeList navbeaconsElements = usernameElement.getElementsByTagName("navbeacons");
		
		// Points to the first navbeacons element in navbeaconsElements
		Element navbeaconsElement   = (Element)navbeaconsElements.item(0);
		
		// Holds all the navbeacon elements in navbeaconsElement
		NodeList navbeaconElements  = navbeaconsElement.getElementsByTagName("navbeacon");
		
		// Store all the NAVBeacon objects in the database
		if (navbeaconElements.getLength() > 0) {
			
			// Declare some variables to hold the information for each NAVBeacon object
			Text navbeaconICAOid;     // Holds the ICAO ID of the current NAVBeacon object
			Text navbeaconName;       // Holds the name of the current NAVBeacon object
			Text navbeaconType;       // Holds the type of the current NAVBeacon object
			String navbeaconTypeVal;  // Holds the String representation of navbeaconType
			
			// Hold information regarding each NAVBeacon object's coordinate
			Element navbeaconCoordinateElement;  // Points to the current coordinate element
			Text navbeaconLatitude;              // Holds the latitude of each coordinate
			Text navbeaconLongitude;             // Holds the longitude of each coordinate
			
			// Points to the current Coordinate object
			Coordinate navbeaconCoordinate;
			
			// Points to the current NAVBeacon object
			NAVBeacon navbeacon;
			
			// Points to the current navbeacon element
			Element navbeaconElement;
			
			// Add each NAVBeacon to the database
			for (int index = 0; index < navbeaconElements.getLength(); index ++) {
				
				// Get the current navbeacon element
				navbeaconElement = (Element)navbeaconElements.item(index);
				
				// Get the ICAO ID of this NAV Beacon
				navbeaconICAOid = (Text)(
					(Element)navbeaconElement.getElementsByTagName("icao_id").item(0)
				).getFirstChild();
				
				// Get the name of this NAV Beacon
				navbeaconName = (Text)(
					(Element)navbeaconElement.getElementsByTagName("name").item(0)
				).getFirstChild();
				
				// Get the type of this NAV Beacon
				navbeaconType = (Text)(
					(Element)navbeaconElement.getElementsByTagName("type").item(0)
				).getFirstChild();
				
				// Get the String representation of navbeaconType
				navbeaconTypeVal = navbeaconType.getWholeText();
				
				// Get the coordinate of the current navbeacon element
				navbeaconCoordinateElement = 
					(Element)navbeaconElement.getElementsByTagName("coordinate").item(0);
				
				// Get the latitude of this NAV Beacon
				navbeaconLatitude = (Text)(
					(Element)navbeaconCoordinateElement.getElementsByTagName("latitude").item(0)
				).getFirstChild();
				
				// Get the longitude of this NAV Beacon
				navbeaconLongitude = (Text)(
					(Element)navbeaconCoordinateElement.getElementsByTagName("longitude").item(0)
				).getFirstChild();
				
				try {
					// Instantiate a new Coordinate object with the acquired Coordinate attributes
					navbeaconCoordinate = Coordinate.getValidCoordinate(
						navbeaconLatitude.getWholeText(), navbeaconLongitude.getWholeText(), null, flightPlan
					);
					
					// Instantiate a new NAVBeacon object with the acquired NAVBeacon attributes
					navbeacon = new NAVBeacon(
						flightPlan,
						navbeaconICAOid.getWholeText(),
						navbeaconName.getWholeText(),
						navbeaconTypeVal.equals("VOR") ? 
						NAVBeacon.NAVBeaconType.VOR :
						navbeaconTypeVal.equals("VORTAC") ? 
						NAVBeacon.NAVBeaconType.VORTAC :
						navbeaconTypeVal.equals("NDB") ? 
						NAVBeacon.NAVBeaconType.NDB :
						NAVBeacon.NAVBeaconType.LORAN,
						navbeaconCoordinate
					);

					// Add this NAVBeacon to the database
					navbeacons.add(navbeacon);
				} catch (FlightPlanException exception) {
					System.err.println(exception.getMessage());
				}
			}
		}
		
		// Initialize the FlightPlan database with the data obtained from the XML Document
		initializeFlightPlanDatabase();
	}
	
	/**
	 * Writes the current database to an XML file
	 */
	public void writeDatabase() throws FileNotFoundException,
	                                   IOException,
	                                   ParserConfigurationException {
		
		// Get all the Airplane, Airport, and NAVBeacon databases from flightPlan
		Airplane[] airplanes   = flightPlan.getAirplanes();
		Airport[] airports     = flightPlan.getAirports();
		NAVBeacon[] navbeacons = flightPlan.getNAVBeacons();
		
		// If the username element is null, the user chose to use the
		// default session, and the database will not be saved
		if (usernameElement == null) {
			
			// Double check that the default session is being used, and
			// that this method is not being called before readDatabase()
			if (username == null || username.length() == 0) {
				return;
			}
		}
		
		// If the document is null, the database does not yet exist ...
		if (document == null) {
			
			// ... so, create it
			document = docBuilder.newDocument();
		}
		
		// Holds the root node of the Document tree
		Element rootElement;
		
		// Determine whether the tree exists
		NodeList rootElements = document.getElementsByTagName("database");
		if (rootElements.getLength() == 0) {
			
			// Holds the root element of the document
			rootElement = document.createElement("database");
		
			// Record the root node
			document.appendChild(rootElement);
		}
		
		// Obtain the root element of the XML DOM
		rootElement = (Element)rootElements.item(0);
		
		try {
			// Remove the user's previous session so it may be replaced with the current one
			// Since the document has not been written yet, if something happens here, the 
			// user will still be able to restore their previous session
			rootElement.removeChild(usernameElement);
		} catch (DOMException exception) {
			// More than likely, this was thrown because the username does not currently exist
			// in the XML file -- so it is Ok to proceed
		} catch (NullPointerException exception) {
			// It is Ok to proceed
		}
		
		// Reset the username element
		usernameElement = document.createElement(username);
		
		// Holds all the Airplane elements in the document
		Element airplanesElement = document.createElement("airplanes");
		
		// Record each of the Airplane objects
		if (airplanes.length > 0) {
			
			// Declare some variables to hold the information for each Airplane object
			Element airplaneMakeElement;           // Records the Airplane's make
			Element airplaneModelElement;          // Records the Airplane's model
			Element airplaneTypeElement;           // Records the Airplane's type
			Element airplaneTankSizeElement;       // Records the Airplane's tankSize
			Element airplaneLitersPerHourElement;  // Records the Airplane's litersPerHour
			Element airplaneCruiseSpeedElement;    // Records the Airplane's cruiseSpeed
			
			Text airplaneMakeText;           // Holds the value of the Airplane's make attribute
			Text airplaneModelText;          // Holds the value of the Airplane's model attribute
			Text airplaneTypeText;           // Holds the value of the Airplane's type attribute
			Text airplaneTankSizeText;       // Holds the value of the Airplane's tankSize attribute
			Text airplaneLitersPerHourText;  // Holds the value of the Airplane's litersPerHour attribute
			Text airplaneCruiseSpeedText;    // Holds the value of the Airplane's cruiseSpeed attribute
			
			// Holds each airplane element
			Element airplaneElement;
			
			// Add each Airplane object to the database
			for (Airplane airplane : airplanes) {
				
				// Acquire the make of this Airplane
				airplaneMakeText = document.createTextNode(airplane.getMake());
				
				// Acquire the model of this Airplane
				airplaneModelText = document.createTextNode(airplane.getModel());
				
				// Acquire the type of this Airplane
				airplaneTypeText = document.createTextNode(
					airplane.getType().toString()
				);
				
				// Acquire the tank size of this Airplane
				airplaneTankSizeText = document.createTextNode(
					new Float(airplane.getTankSize()).toString()
				);
				
				// Get the ratio of liter of fuel burned per hour of this Airplane
				airplaneLitersPerHourText = document.createTextNode(
					new Float(airplane.getLitersPerHour()).toString()
				);
				
				// Acquire the cruise speed of this Airplane
				airplaneCruiseSpeedText = document.createTextNode(
					new Float(airplane.getCruiseSpeed()).toString()
				);
				
				// Record the Airplane's make attribute
				airplaneMakeElement = document.createElement("make");
				airplaneMakeElement.appendChild(airplaneMakeText);
				
				// Record the Airplane's model attribute
				airplaneModelElement = document.createElement("model");
				airplaneModelElement.appendChild(airplaneModelText);
				
				// Recorde the Airplane's type attribute
				airplaneTypeElement = document.createElement("type");
				airplaneTypeElement.appendChild(airplaneTypeText);
				
				// Record the Airplane's tankSize attribute
				airplaneTankSizeElement = document.createElement("tank_size");
				airplaneTankSizeElement.appendChild(airplaneTankSizeText);
				
				// Record the Airplane's litersPerHour attribute
				airplaneLitersPerHourElement = document.createElement("liters_per_hour");
				airplaneLitersPerHourElement.appendChild(airplaneLitersPerHourText);
				
				// Record the Airplane's cruiseSpeed attribute
				airplaneCruiseSpeedElement = document.createElement("cruise_speed");
				airplaneCruiseSpeedElement.appendChild(airplaneCruiseSpeedText);
				
				// Set each attribute as a child node of the current airplane element
				airplaneElement = document.createElement("airplane");
				airplaneElement.appendChild(airplaneMakeElement);
				airplaneElement.appendChild(airplaneModelElement);
				airplaneElement.appendChild(airplaneTypeElement);
				airplaneElement.appendChild(airplaneTankSizeElement);
				airplaneElement.appendChild(airplaneLitersPerHourElement);
				airplaneElement.appendChild(airplaneCruiseSpeedElement);
				
				// Record the current airplane element
				airplanesElement.appendChild(airplaneElement);
			}
		}
		
		// Holds all the Airport elements in the document
		Element airportsElement = document.createElement("airports");
		
		// Record the information of each Airport
		if (airports.length > 0) {
			
			// Declare some variables to hold the information for each Airport object
			Element airportICAOidElement;     // Records the ICAOid attribute of the current Airport
			Element airportNameElement;       // Records the name attribute of the current Airport
			Element airportHasAVGASElement;   // Records the hasAVGAS attribute of the current Airport
			Element airportHasJA_aElement;    // Records the hasJA_a attribute of the current Airport
			Element airportElevationElement;  // Records the elevation attribute of the current Airport
			
			Text airportICAOidText;     // Holds the value of the Airport's ICAOid attribute
			Text airportNameText;       // Holds the value of the Airport's name attribute
			Text airportHasAVGASText;   // Holds the value of the Airport's hasAVGAS attribute
			Text airportHasJA_aText;    // Holds the value of the Airport's hasJA_a attribute
			Text airportElevationText;  // Holds the value of the Airport's elevation attribute
			
			// Holds the Airport object's coordinate information
			Coordinate airportCoordinate;      // Points to each Airport's Coordinate
			Element airportCoordinateElement;  // Records each Airport's Coordinate information
			Element airportLatitudeElement;    // Records each Airport's latitude
			Text airportLatitudeText;          // Holds the value of each Airport's latitude
			Element airportLongitudeElement;   // Records each Airport's longitude
			Text airportLongitudeText;         // Holds the value of each Airport's longitude
			
			// Holds each Runway object for the Airport
			Element airportRunwayTypeElement;    // Records the type attribute of each Runway
			Element airportRunwayLengthElement;  // Records the length attribute of each Runway
			Element airportRunwayNumberElement;  // Records the number attribute of each Runway
			
			Text airportRunwayTypeText;    // Holds the value of each Runway's type attribute
			Text airportRunwayLengthText;  // Holds the value of each Runway's length attribute
			Text airportRunwayNumberText;  // Holds the value of each Runway's number attribute
			
			Element airportRunwaysElement; // Records all the runway elements in the database
			Element airportRunwayElement;  // Records each runway element in the database
			
			// Holds each Comm object for the Airport
			Element airportCommTypeElement;  // Records the type attribute of each Airport Comm
			Element airportCommFreqElement;  // Records the freq attribute of each Airport Comm
			
			Text airportCommTypeText;  // Holds the value of the type attribute of each Airport Comm
			Text airportCommFreqText;  // Holds the value of the freq attribute of each Airport Comm
			
			Element airportCommsElement;  // Records all the comm elements in the database
			Element airportCommElement;   // Records each comm element in the database
			
			// Records each Airport in the database
			Element airportElement;
			
			// Add each Airport object to the database
			for (Airport airport : airports) {
				
				// Acquire the ICAO ID this Airport
				airportICAOidText = document.createTextNode(airport.getICAOid());
				
				// Acquire the name of this Airport
				airportNameText = document.createTextNode(airport.getName());
				
				// Acquire whether this Airport carries AVGAS fuel
				airportHasAVGASText = document.createTextNode(
					new Boolean(airport.hasAVGAS()).toString()
				);
				
				// Acquire whether this Airport carries Jet-A fuel
				airportHasJA_aText = document.createTextNode(
					new Boolean(airport.hasJA_a()).toString()
				);
				
				// Acquire the elevation of this Airport
				airportElevationText = document.createTextNode(
					new Float(airport.getElevation()).toString()
				);
				
				// Record the Airport's ICAOid attribute
				airportICAOidElement = document.createElement("icao_id");
				airportICAOidElement.appendChild(airportICAOidText);
				
				// Record the Airport's name attribute
				airportNameElement = document.createElement("name");
				airportNameElement.appendChild(airportNameText);
				
				// Record the Airport's hasAVGAS attribute
				airportHasAVGASElement = document.createElement("has_avgas");
				airportHasAVGASElement.appendChild(airportHasAVGASText);
				
				// Record the Airport's hasJA_a attribute
				airportHasJA_aElement = document.createElement("has_ja_a");
				airportHasJA_aElement.appendChild(airportHasJA_aText);
				
				// Get the Coordinate of this Airport
				airportCoordinate = airport.getCoordinate();
				
				// Acquire the latitude of this Airport
				airportLatitudeText = document.createTextNode(
					new Float(airportCoordinate.getLatitude()).toString()
				);
				
				// Acquire the longitude of this Airport
				airportLongitudeText = document.createTextNode(
					new Float(airportCoordinate.getLongitude()).toString()
				);
				
				// Record the Airport's latitude
				airportLatitudeElement = document.createElement("latitude");
				airportLatitudeElement.appendChild(airportLatitudeText);
				
				// Record the Airport's longitude
				airportLongitudeElement = document.createElement("longitude");
				airportLongitudeElement.appendChild(airportLongitudeText);
				
				// Record the Airport's Coordinate
				airportCoordinateElement = document.createElement("coordinate");
				airportCoordinateElement.appendChild(airportLatitudeElement);
				airportCoordinateElement.appendChild(airportLongitudeElement);
				
				// Record the Airport's elevation attribute
				airportElevationElement = document.createElement("elevation");
				airportElevationElement.appendChild(airportElevationText);
				
				// Record each runway element in the database
				airportRunwaysElement = document.createElement("runways");
				
				// Add each Runway object to the Airport
				for (Runway runway : airport.getRunways()) {
					
					// Obtain the type of this Runway
					airportRunwayTypeText = document.createTextNode(
						runway.getType().toString()
					);
					
					// Obtain the length of this Runway
					airportRunwayLengthText = document.createTextNode(
						new Float(runway.getLength()).toString()
					);
					
					// Obtain the number of this Runway
					airportRunwayNumberText = document.createTextNode(
						new Integer(runway.getNumber()).toString()
					);
					
					// Record the Runway's type attribute
					airportRunwayTypeElement = document.createElement("type");
					airportRunwayTypeElement.appendChild(airportRunwayTypeText);
					
					// Record the Runway's length attribute
					airportRunwayLengthElement = document.createElement("length");
					airportRunwayLengthElement.appendChild(airportRunwayLengthText);
					
					// Record the Runway's number attribute
					airportRunwayNumberElement = document.createElement("number");
					airportRunwayNumberElement.appendChild(airportRunwayNumberText);
					
					// Set the runway element's child nodes to the acquired Runway attributes
					airportRunwayElement = document.createElement("runway");
					airportRunwayElement.appendChild(airportRunwayTypeElement);
					airportRunwayElement.appendChild(airportRunwayLengthElement);
					airportRunwayElement.appendChild(airportRunwayNumberElement);
					
					// Record the runway element
					airportRunwaysElement.appendChild(airportRunwayElement);
				}
				
				// Records all of the comm elements in the Airport
				airportCommsElement = document.createElement("comms");
				
				// Add each Comm object to the Airport
				for (Comm comm : airport.getComms()) {
					
					// Acquire the required Comm attributes
					airportCommTypeText = document.createTextNode(comm.getType().toString());
					airportCommFreqText = document.createTextNode(
						new Float(comm.getFreq()).toString()
					);
					
					// Record the Comm's type attribute
					airportCommTypeElement = document.createElement("type");
					airportCommTypeElement.appendChild(airportCommTypeText);
					
					// Record the Comm's freq attribute
					airportCommFreqElement = document.createElement("freq");
					airportCommFreqElement.appendChild(airportCommFreqText);
					
					// Set the comm element's child nodes to the acquired Comm attributes
					airportCommElement = document.createElement("comm");
					airportCommElement.appendChild(airportCommTypeElement);
					airportCommElement.appendChild(airportCommFreqElement);
					
					// Record the comm element
					airportCommsElement.appendChild(airportCommElement);
				}
				
				// Records the current airport element
				airportElement = document.createElement("airport");
				
				// Append each acquired Airport attribute as child nodes
				// to the current airport element
				airportElement.appendChild(airportICAOidElement);
				airportElement.appendChild(airportNameElement);
				airportElement.appendChild(airportHasAVGASElement);
				airportElement.appendChild(airportHasJA_aElement);
				airportElement.appendChild(airportCoordinateElement);
				airportElement.appendChild(airportElevationElement);
				airportElement.appendChild(airportRunwaysElement);
				airportElement.appendChild(airportCommsElement);
				
				// Record the current airport element
				airportsElement.appendChild(airportElement);
			}
		}
		
		// Records all the NAVBeacon elements in the document
		Element navbeaconsElement = document.createElement("navbeacons");
		
		// Record each NAVBeacon in the database
		if (navbeacons.length > 0) {
			
			// Declare some variables to hold the information for each NAVBeacon object
			Element navbeaconICAOidElement;  // Records each NAVBeacon's ICAOid attribute
			Element navbeaconNameElement;    // Records each NAVBeacon's name attribute
			Element navbeaconTypeElement;    // Records each NAVBeacon's type attribute
			
			Text navbeaconICAOidText;  // Holds the value of each NAVBeacon's ICAOid attribute
			Text navbeaconNameText;    // Holds the value of each NAVBeacon's name attribute
			Text navbeaconTypeText;    // Holds the value of each NAVBeacon's type attribute
			
			// Records each NAVBeacon's Coordinate
			Element navbeaconCoordinateElement;
			
			Element navbeaconLatitudeElement;   // Records each NAVBeacon's latitude
			Element navbeaconLongitudeElement;  // Records each NAVBeacon's longitude
			
			Text navbeaconLatitudeText;   // Holds the value of each NAVBeacon's latitude
			Text navbeaconLongitudeText;  // Holds the value of each NAVBeacon's longitude
			
			// Points to the Coordinate of each NAVBeacon
			Coordinate navbeaconCoordinate;
			
			// Records each NAVBeacon
			Element navbeaconElement;
			
			// Add each NAVBeacon object to the database
			for (NAVBeacon navbeacon : navbeacons) {
				
				// Acquire the required NAVBeacon attributes
				navbeaconICAOidText = document.createTextNode(navbeacon.getICAOid());
				navbeaconNameText = document.createTextNode(navbeacon.getName());
				navbeaconTypeText = document.createTextNode(navbeacon.getType().toString());
				
				// Record the ICAOid attribute of the current NAVBeacon
				navbeaconICAOidElement = document.createElement("icao_id");
				navbeaconICAOidElement.appendChild(navbeaconICAOidText);
				
				// Record the name attribute of the current NAVBeacon
				navbeaconNameElement = document.createElement("name");
				navbeaconNameElement.appendChild(navbeaconNameText);
				
				// Record the type attribute of the current NAVBeacon
				navbeaconTypeElement = document.createElement("type");
				navbeaconTypeElement.appendChild(navbeaconTypeText);
				
				// Get the reference to the Coordinate of the current NAVBeacon
				navbeaconCoordinate = navbeacon.getCoordinate();
				
				// Acquire the latitude of the current NAVBeacon
				navbeaconLatitudeText = document.createTextNode(
					new Float(navbeaconCoordinate.getLatitude()).toString()
				);
				
				// Acquire the longitude of the current NAVBeacon
				navbeaconLongitudeText = document.createTextNode(
					new Float(navbeaconCoordinate.getLongitude()).toString()
				);
				
				// Record the latitude attribute of the current NAVBeacon
				navbeaconLatitudeElement = document.createElement("latitude");
				navbeaconLatitudeElement.appendChild(navbeaconLatitudeText);
				
				// Record the longitude attribute of the current NAVBeacon
				navbeaconLongitudeElement = document.createElement("longitude");
				navbeaconLongitudeElement.appendChild(navbeaconLongitudeText);
				
				// Append the acquired Coordinate attributes as children to the 
				// coordinate element
				navbeaconCoordinateElement = document.createElement("coordinate");
				navbeaconCoordinateElement.appendChild(navbeaconLatitudeElement);
				navbeaconCoordinateElement.appendChild(navbeaconLongitudeElement);
				
				// Append the acquired NAVBeacon attributes as children to the 
				// navbeacon element
				navbeaconElement = document.createElement("navbeacon");
				navbeaconElement.appendChild(navbeaconICAOidElement);
				navbeaconElement.appendChild(navbeaconNameElement);
				navbeaconElement.appendChild(navbeaconTypeElement);
				navbeaconElement.appendChild(navbeaconCoordinateElement);
				
				// Record the navbeacon element
				navbeaconsElement.appendChild(navbeaconElement);
			}
		}
		
		// Append the airplanes, airports, and navbeacons 
		// elements as children to the root node
		usernameElement.appendChild(airplanesElement);
		usernameElement.appendChild(airportsElement);
		usernameElement.appendChild(navbeaconsElement);
		
		rootElement.appendChild(usernameElement);
		
		// Formats the output of the XML document
		OutputFormat format = new OutputFormat(document);
		format.setEncoding("UTF-8");
		format.setIndenting(false);
		
		// Serializes and records the database as an XML document
		XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(databaseFile), format
		);
		
		// Serialize the document into XML
		serializer.serialize(document);
	}
	
	/**
	 * Returns the HashSet of {@link Airplane} objects parsed from the file
	 *
	 * @return The {@link #airplanes} attribute
	 */
	public HashSet<Airplane> getAirplanes() {
		return airplanes;
	}
	
	/**
	 * Returns the HashSet of {@link Airport} objects parsed from the file
	 *
	 * @return The {@link #airports} attribute
	 */
	public HashSet<Airport> getAirports() {
		return airports;
	}
	
	/**
	 * Returns the HashSet of {@link NAVBeacon} objects parsed from the file
	 *
	 * @return The {@link #navbeacons} attribute
	 */
	public HashSet<NAVBeacon> getNAVBeacons() {
		return navbeacons;
	}
}