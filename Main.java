package edu.usca.acsc492l.flightplanner;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
 
/**
 * Serves as the main class of the application by providing a user interface through which
 * the user may interact with the program.
 *
 * @author Johnathan Morgan
 * @author Dylon Edwards
 */
public class Main {
	
	/** Declare a scanner object that will grab user input */
	protected static final Scanner input = new Scanner(System.in);
	
	/** Points to the current {@link Airport} object */
	protected Airport airport;
	
	/** Holds the {@link Airport#ICAOid} attribute of each {@link Airport} */
	protected String airportICAOid;
	
	/** Holds the {@link Airport#name} attribute of each {@link Airport} */
	protected String airportName;
	
	/** Holds the {@link Airport#hasAVGAS} attribute of each {@link Airport} */
	protected boolean airportHasAVGAS;
	
	/** Holds the {@link Airport#hasJA_a} attribute of each {@link Airport} */
	protected boolean airportHasJAa;
	
	/** Holds the {@link Airport#elevation} attribute of each {@link Airport} */
	protected String airportElevation;
	
	/** Holds the {@link Runway} of the current {@link Airport} object */
	protected Runway runway;
	
	/** Holds the {@link Runway#number} attribute of each {@link Runway} */
	protected String runwayNumber;
	
	/** Holds the {@link Runway#length} attribute of each {@link Runway} */
	protected String runwayLength;
	
	/** Holds the {@link Runway#type} attribute of each {@link Runway} */
	protected Runway.RunwayType runwayType;
	
	/** Holds the {@link Comm} of the current {@link Airport} object */
	protected Comm comm;
	
	/** Holds the {@link Comm#type} attribute of each {@link Comm} */
	protected Comm.CommType commType;
	
	/** Holds the {@link Comm#freq} attribute of each {@link Comm} */
	protected String commFreq;
	
	/** Points to the current {@link Airplane} object */
	protected Airplane airplane;
	
	/** Holds the {@link Airplane#make} attribute of each {@link Airplane} */
	protected String airplaneMake;
	
	/** Holds the {@link Airplane#model} attribute of each {@link Airplane} */
	protected String airplaneModel;
	
	/** Holds the {@link Airplane#type} attribute of each {@link Airplane} */
	protected Airplane.AirplaneType airplaneType;
	
	/** Holds the {@link Airplane#tankSize} attribute of each {@link Airplane} */
	protected String airplaneTankSize;
	
	/** Holds the {@link Airplane#litersPerHour} attribute of each {@link Airplane} */
	protected String airplaneLitersPerHour;
	
	/** Holds the {@link Airplane#cruiseSpeed} attribute of each {@link Airplane} */
	protected String airplaneCruiseSpeed;
	
	/** Points to the current {@link NAVBeacon} object */
	protected NAVBeacon navbeacon;
	
	/** Holds the {@link NAVBeacon#ICAOid} attribute of each {@link NAVBeacon} */
	protected String navbeaconICAOid;
	
	/** Holds hte {@link NAVBeacon#name} attribute of each {@link NAVBeacon} */
	protected String navbeaconName;
	
	/** Holds the {@link NAVBeacon#type} attribute of each {@link NAVBeacon} */
	protected NAVBeacon.NAVBeaconType navbeaconType;
	
	/** Points to the current {@link Coordinate} object */
	protected Coordinate coordinate;
	
	/** Holds the {@link Coordinate#latitude} attribute of each {@link Coordinate} */
	protected String latitude;
	
	/** Holds the {@link Coordinate#longitude} attribute of each {@link Coordinate} */
	protected String longitude;
	
	/** Holds the {@link Airport} from which the user begins his flight */
	protected Airport startDestination;
	
	/** Holds the {@link Airport} at which the user ends his flight */
	protected Airport endDestination;
	
	/** Holds the list of additional {@link Vertex destinations} the user wishes to visit */
	protected ArrayList<Vertex> additionalDestinations;
	
	/** declare FlightPlan instance, calculates the flight plan */
	protected FlightPlan flightPlan;
	
	/** Declare database model used for handling database storage */
	protected DatabaseModel database;
	
	/**
	 * Constructs a default Main object
	 */
	public Main() throws Exception {
		//instantiate addDests arraylist for holding extra destinations
		additionalDestinations = new ArrayList<Vertex>();
			
		//instantiate the FlightPlan class with database data
		flightPlan = new FlightPlan();
		
		//instantiate DatabaseModel for database info
		database = new DatabaseModel(flightPlan);
	
		//read dbFile to grab information from it
		database.readDatabase();
	}
	
	/**
	 * The main method. Instantiates the Main class and begins its execution.
	 *
	 * @param args Command-line arguments supplied to the application upon initiation
	 */
	public static void main(String[] args)  {
		try {
			//call to splash screen for warning (Not to be used for real flight plans...)
			displaySplash();

			//declare and instantiate an instance of the flightplan application main method
			Main app = new Main();
			
			//call to home screen of the application
			app.displayMainMenu();
		} catch (Exception exception) {
			// If an Exception gets this far, it should crash the application
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/** 
	 * Displays a Splash Screen to the user
	 */
	public static void displaySplash() {
		System.out.println();
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println(" DISCLAIMER: THIS SOFTWARE IS NOT TO BE USED FOR FLIGHT PLANNING OR NAVIGATIONAL PURPOSES ... ");
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println();
	}
	
 	/** 
	 * Displays the Home screen to the user
	 */
	public void displayMainMenu() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Welcome to the Flight Planner *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.println("Menu:");
		System.out.println("1) Manage Flight Plan Database");
		System.out.println("2) Create a New Flight Plan");
		System.out.println("3) Exit the Flight Plan Application");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			switch (uI) {
				case 1:
					/*
					 * This is a trick I picked up working with JavaScript over the summer:
					 *
					 * Instantiating a new Thread will allow the current Thread to run its
					 * course and its memory to be reclaimed, so even though the functions
					 * are called recursively, they do not add unnecessarily to the runtime
					 * heap, and do not create a memory leak.
					 */
					new Thread() {
						public void run() {
							displayManageDatabase();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							flightPlannerSelectPlane();
						}
					}.start();
					break;
				case 3:
					try {
						
						//writes all current information to the database
						database.writeDatabase();
					} catch (Exception exception) {
						
						//if an error occurs, print its stack trace before exiting the application
						exception.printStackTrace();
					} finally {
						System.exit(0);
					}
					break;
				default:
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid display error message and allow user to input again
			System.err.println("\nYou must select a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm the error
			input.nextLine();

			//redisplay home screen
			new Thread() {
				public void run() {
					displayMainMenu();
				}
			}.start();
		}
	}

	/** 
	 * Manages the {@link FlightPlan} database
	 */
	public void displayManageDatabase() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Manage Flight Plan Database *");
		System.out.println("*******************************");
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Manage Airports");
		System.out.println("2) Manage Airplanes");
		System.out.println("3) Manage NAV Beacons");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());

			// Check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							manageAirports();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							manageAirplanes();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							manageNAVBeacons();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is not valid display an error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm the error
			input.nextLine();
			
			//display ManageDatabase screen
			new Thread() {
				public void run() {
					displayManageDatabase();
				}
			}.start();
		}
	}
	
	/**
	 * Manages the {@link Airport}s in the database
	 */
	public void manageAirports() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Manage Airports Menu *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Query Existing Airports");
		System.out.println("2) Add New Airport");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							queryAirports();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							addNewAirportName();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							displayManageDatabase();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid display an error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm the error
			input.nextLine();
			
			//redisplay manage airports menu
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
		}
	}
	
	/**
	 * Queries existing {@link Airport}s
	 */
	public void queryAirports() {
		System.out.println();
		System.out.println("***************************");
		System.out.println("* Query Existing Airports *");
		System.out.println("***************************");
		System.out.println();
		
		// Make sure there is an Airport to query
		if (flightPlan.getAirportCount() == 0) {
			System.err.println("There are no Airports in the database.");
			System.out.print("Press <ENTER> to continue ...");
			
			// Wait for the user to acknowledge the message
			input.nextLine();
			
			// Return to the manageAirports() method
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
			
			// End the method here
			return;
		}
		
		System.out.print("Enter the [partial] ICAO ID or Name: ");
		String uI = input.nextLine().trim();
		
		//create an array of airports that match user input
		Airport[] airports = flightPlan.getAirportsByNameOrICAOid(uI);
		
		switch (airports.length) {
			case 0:
				
				//if airport list is empty display error
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				
				//allow user to confirm error
				input.nextLine();
				
				//redisplay query airports by creating new thread
				new Thread() {
					public void run() {
						queryAirports();
					}
				}.start();
				
				return;
			case 1:
				
				//if airport array has only one airport assign it as the current airport
				airport = airports[0];
				break;
			default:
				
				//displays header information for airport list
				System.out.println("Airports:");
				System.out.printf("%s    %s    %s\n", "Number:", "ICAO ID:", "Name:");
				
				int counter = 1;
				
				//displays all airports in the database
				for (Airport airport : airports) {
					System.out.printf("%-7s    %-8s    %s\n", counter + ")", airport.getICAOid(), airport.getName());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Airport: ");
					int index = Integer.parseInt(input.nextLine());
					
					airport = airports[index - 1];
				} catch (RuntimeException exception) {
					
					//if user input is invalid display an error message and allow user to input again
					System.err.println("\nYou must select a number corresponding to one of the Airports.");
					System.out.print("Press <ENTER> to continue ...");
					
					//allow user to confirm the error
					input.nextLine();
					
					//redisplay query airports screen by creating new thread
					new Thread() {
						public void run() {
							queryAirports();
						}
					}.start();
					
					return;
				}
		}
		
		//redisplay airport information by creating new thread
		new Thread() {
			public void run() {
				displayAirportInfo();
			}
		}.start();
	}
	
	/** 
	 * Displays information about the selected {@link Airport}
	 */
	public void displayAirportInfo() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Airport Information: *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		//create an array to hold Communication frequencies of the current airport
		Comm[] comms = airport.getComms();
		System.out.println("Communication Nodes:");
		System.out.printf("%-44s    %s\n", "Type:", "Frequency:");
		
		//loop that displays all Comm Frequencies of the current airport
		for (Comm comm : comms) {
			System.out.printf("%-44s    %s MHz\n", comm.getType(), FlightPlan.formatter.format(comm.getFreq()));
		}
		System.out.println();
		
		//array that holds all runways of the current airport
		Runway[] runways = airport.getRunways();
		System.out.println("Airport Runways:");
		System.out.printf("%-20s    %-20s    %s\n", "Number:", "Type:", "Length:");
		
		//loop that displays all runways of the current airport
		for (Runway runway : runways) {
			System.out.printf("%-20s    %-20s    %s Meters\n", runway.getNumber(), runway.getType(), FlightPlan.formatter.format(runway.getLength()));
		}
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Delete this Airport");
		System.out.println("2) Modify this Airport");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and calls appropriate method
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							deleteAirport();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyAirport();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							manageAirports();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm problem
			input.nextLine();
			
			//redisplay display airport info screen
			new Thread() {
				public void run() {
					displayAirportInfo();
				}
			}.start();
		}
	}
	
	/**
	 * Deletes the selected {@link Airport}
	 */
	public void deleteAirport() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Airport Information: *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		System.out.print("Are you sure you want to delete this Airport? [NO|yes] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity
		if (uI.matches("^(?i)y(?:es)?$")) {
			try {
				
				//delete selected airport from flight plan airport list
				flightPlan.removeAirport(airport);
			} catch (FlightPlanException exception) {
				
				//if error occurs display exception message
				System.err.println(exception.getMessage());
			}
		}
		
		//redisplay manage airports screen by creating new thread
		new Thread() {
			public void run() {
				manageAirports();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link Airport}
	 */
	public void modifyAirport() {
		System.out.println();
		System.out.println("***********************");
		System.out.println("* Airport Information *");
		System.out.println("***********************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		//creates array of communication frequencies
		Comm[] comms = airport.getComms();
		System.out.println("Communication Nodes:");
		System.out.printf("%-44s    %s\n", "Type:", "Frequency:");
		
		//loop that displays all communication frequencies for current airport
		for (Comm comm : comms) {
			System.out.printf("%-44s    %s MHz\n", comm.getType(), FlightPlan.formatter.format(comm.getFreq()));
		}
		System.out.println();
		
		//creates array of current airports runways
		Runway[] runways = airport.getRunways();
		System.out.println("Airport Runways:");
		System.out.printf("%-20s    %-20s    %s\n", "Number:", "Type:", "Length:");
		
		//loop to display runways of current airport
		for (Runway runway : runways) {
			System.out.printf("%-20s    %-20s    %s Meters\n", runway.getNumber(), runway.getType(), FlightPlan.formatter.format(runway.getLength()));
		}
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1)  Modify Name");
		System.out.println("2)  Modify ICAO ID");
		System.out.println("3)  Modify Latitude");
		System.out.println("4)  Modify Longitude");
		System.out.println("5)  Modify Elevation");
		System.out.println("6)  Modify AVGAS Fuel Information");
		System.out.println("7)  Modify JA_a Fuel Information");
		System.out.println("8)  Manage Runways");
		System.out.println("9)  Manage Communication Frequencies");
		System.out.println("10) Return to the Previous Menu");
		System.out.println("11) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and calls appropriate method
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							modifyAirportName();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyAirportICAOID();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							modifyAirportLat();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							modifyAirportLong();
						}
					}.start();
					break;
				case 5:
					new Thread() {
						public void run() {
							modifyAirportElev();
						}
					}.start();
					break;
				case 6:
					new Thread() {
						public void run() {
							updateHasAVGAS();
						}
					}.start();
					break;
				case 7:
					new Thread() {
						public void run() {
							updateHasJA_a();
						}
					}.start();
					break;
				case 8:
					new Thread() {
						public void run() {
							manageRunways();
						}
					}.start();
					break;
				case 9:
					new Thread() {
						public void run() {
							manageComms();
						}
					}.start();
					break;
				case 10:
					new Thread() {
						public void run() {
							displayAirportInfo();
						}
					}.start();
					break;
				case 11:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm problem
			input.nextLine();
			
			//redisplay add airport screen
			new Thread() {
				public void run() {
					modifyAirport();
				}
			}.start();
		}
	}
	
	/**
	 * Modifies the selected {@link Airport}'s {@link Airport#name}
	 */
	public void modifyAirportName() {
		System.out.println();
		System.out.println("******************************");
		System.out.println("* Modify Airport Information *");
		System.out.println("******************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		try {
			System.out.print("Enter the new name: ");
			String uI = Vertex.getValidName(input.nextLine(), airport, flightPlan);
			
			//sets airport name to user input
			airport.setName(uI);
		} catch (FlightPlanException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println(exception.getMessage());
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link Airport}'s {@link Airport#ICAOid}
	 */
	public void modifyAirportICAOID() {
		System.out.println();
		System.out.println("******************************");
		System.out.println("* Modify Airport Information *");
		System.out.println("******************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		try {
			System.out.print("Enter the new ICAO ID: ");
			String uI = Vertex.getValidICAOid(input.nextLine(), airport, flightPlan);
		
			airport.setICAOid(uI);
		} catch (FlightPlanException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println(exception.getMessage());
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link Airport}'s {@link Airport#ICAOid}
	 */
	public void modifyAirportLat() {
		System.out.println();
		System.out.println("******************************");
		System.out.println("* Modify Airport Information *");
		System.out.println("******************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		try {
			System.out.print("Enter the new Latitude: ");
			String uI = input.nextLine();
			
			Coordinate.setLatitude(uI, airport, flightPlan);
		} catch (FlightPlanException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println(exception.getMessage());
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link Airport}'s {@link Coordinate#longitude}
	 */
	public void modifyAirportLong() {
		System.out.println();
		System.out.println("******************************");
		System.out.println("* Modify Airport Information *");
		System.out.println("******************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		System.out.print("Enter the new longitude: ");
		String uI = input.nextLine();
		
		try {
			Coordinate.setLongitude(uI, airport, flightPlan);
		} catch (FlightPlanException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println(exception.getMessage());
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link Airport}'s {@link Airport#elevation}
	 */
	public void modifyAirportElev() {
		System.out.println();
		System.out.println("******************************");
		System.out.println("* Modify Airport Information *");
		System.out.println("******************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		System.out.print("Enter the new elevation: ");
		String uI = input.nextLine();
		
		try {
			
			//sets airport elevation to user input
			airport.setElevation(uI);
		} catch (FlightPlanException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println(exception.getMessage());
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies whether the selected {@link Airport} carries AVGAS fuel
	 */
	public void updateHasAVGAS() {
		System.out.println();
		System.out.println("***************************************");
		System.out.println("* View/Modify AVGAS Fuel Information: *");
		System.out.println("***************************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		System.out.print("Does this Airport have AVGAS fuel? [yes|no] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity
		if (uI.matches("^(?i)y(?:es)?$")) {
			airport.hasAVGAS(true);
		} else if (uI.matches("^(?i)n(?:o)?$")) {
			airport.hasAVGAS(false);
		} else {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter either yes or no.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay update Has AVGAS screen by creating new thread
			new Thread() {
				public void run() {
					updateHasAVGAS();
				}
			}.start();
			
			return;
		}
		
		//redisplay modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Modifies whether the selected {@link Airport} carries Jet-A fuel
	 */
	public void updateHasJA_a() {
		System.out.println();
		System.out.println("**************************************");
		System.out.println("* View/Modify JA_a Fuel Information: *");
		System.out.println("**************************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		System.out.print("Does this Aiport have JA_a fuel? [yes|no] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity
		if (uI.matches("^(?i)y(?:es)?$")) {
			airport.hasJA_a(true);
		} else if (uI.matches("^(?i)n(?:o)?$")) {
			airport.hasJA_a(false);
		} else {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter either yes or no.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplays update Has JA_a screen by creating new thread
			new Thread() {
				public void run() {
					updateHasJA_a();
				}
			}.start();
			
			return;
		}
		
		//redisplays modify airport screen by creating new thread
		new Thread() {
			public void run() {
				modifyAirport();
			}
		}.start();
	}
	
	/**
	 * Manages the selected {@link Airport}s {@link Airport#runways}
	 */
	public void manageRunways() {
		System.out.println();
		System.out.println("**************************");
		System.out.println("* Manage Airport Runways *");
		System.out.println("**************************");
		System.out.println();
		
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		//creates array of current airport's runways
		Runway[] runways = airport.getRunways();
		System.out.println("Airport Runways:");
		System.out.printf("%-20s    %-20s    %s\n", "Number:", "Type:", "Length:");
		
		//loop to dipslay each runway of the current airport
		for (Runway runway : runways) {
			System.out.printf("%-20s    %-20s    %s Meters\n", runway.getNumber(), runway.getType(), FlightPlan.formatter.format(runway.getLength()));
		}
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Add a Runway");
		System.out.println("2) Delete a Runway");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and calls appropriate method
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							addRunwayNumber();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							deleteRunway();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							addNewAirportName();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm message
			input.nextLine();
			
			//redisplay manage Runways screen
			new Thread() {
				public void run() {
					manageRunways();
				}
			}.start();
		}
	}
	
	/**
	 * Sets the {@link Runway#number} of the {@link Runway} being added to the selected {@link Airport}
	 */
	public void addRunwayNumber() {
		System.out.println();
		System.out.println("****************");
		System.out.println("* Add a Runway *");
		System.out.println("****************");
		System.out.println();
		
		System.out.println("Number: ");
		System.out.println("Length: ");
		System.out.println("Type:   ");
		System.out.println();
		
		System.out.println("Question 1 of 3:");
		System.out.print("Enter the Runway number: ");
		
		//sets runwayNumber to user input
		runwayNumber = input.nextLine();
		
		//call to add runway length method by creating new thread
		new Thread() {
			public void run() {
				addRunwayLength();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Runway#length} of the {@link Runway} being added to the selected {@link Airport}
	 */
	public void addRunwayLength() {
		System.out.println();
		System.out.println("****************");
		System.out.println("* Add a Runway *");
		System.out.println("****************");
		System.out.println();
		
		System.out.println("Number: " + runwayNumber);
		System.out.println("Length: ");
		System.out.println("Type:   ");
		System.out.println();
		
		System.out.println("Question 2 of 3");
		System.out.print("Enter the Runway length: ");
		
		//sets runway length to user input
		runwayLength = input.nextLine();
		
		//calls add runway type method by creating new thread
		new Thread() {
			public void run() {
				addRunwayType();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Runway#type} of the {@link Runway} being added to the currently selected {@link Airport}
	 */
	public void addRunwayType() {
		System.out.println();
		System.out.println("****************");
		System.out.println("* Add a Runway *");
		System.out.println("****************");
		System.out.println();
		
		System.out.println("Number: " + runwayNumber);
		System.out.println("Length: " + runwayLength);
		System.out.println("Type:   ");
		System.out.println();
		
		System.out.println("Available Runway Types:");
		System.out.println("1) Visual Runway");
		System.out.println("2) Non-Precision Runway");
		System.out.println("3) Precision Runway");
		System.out.println();
		
		try {
			System.out.println("Question 3 of 3");
			System.out.print("Enter the number corresponding the the Runway type: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and assigns runwayType based on user input
			switch (uI) {
				case 1:
					runwayType = Runway.RunwayType.VISUAL_RUNWAY;
					break;
				case 2:
					runwayType = Runway.RunwayType.NONPRECISION_RUNWAY;
					break;
				case 3:
					runwayType = Runway.RunwayType.PRECISION_RUNWAY;
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
			
			//call to add runway confirm method
			new Thread() {
				public void run() {
					addRunwayConfirm();
				}
			}.start();
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add Runway Type screen
			new Thread() {
				public void run() {
					addRunwayType();
				}
			}.start();
		}
	}
	
	/**
	 * Confirms the creation and addition of a {@link Runway} to the currently selected {@link Airport}
	 */
	public void addRunwayConfirm() {
		System.out.println();
		System.out.println("****************");
		System.out.println("* Add a Runway *");
		System.out.println("****************");
		System.out.println();
		
		System.out.println("Are these details correct?");
		System.out.println("Number: " + runwayNumber);
		System.out.println("Length: " + runwayLength);
		System.out.println("Type:   " + runwayType);
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Accept and Save this Runway");
		System.out.println("2) Discard this Runway");
		System.out.println();
			
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and either add runway or call appropriate method
			switch (uI) {
				case 1:
					try {
						runway = new Runway(runwayNumber, runwayLength, runwayType);
						airport.addRunway(runway);
					} catch (FlightPlanException exception) {
						System.out.println(exception.getMessage());
					}
					// Fall Through
				case 2:
					
					//displays manage Airport screen by creating new thread
					new Thread() {
						public void run() {
							manageAirports();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add Runway Confirm screen
			new Thread() {
				public void run() {
					addRunwayConfirm();
				}
			}.start();
		}
	}
	
	/**
	 * Deletes a {@link Runway} from the selected {@link Airport}
	 */
	public void deleteRunway() {
		System.out.println();
		System.out.println("*******************");
		System.out.println("* Delete a Runway *");
		System.out.println("*******************");
		System.out.println();
		
		//creates array of current airport's runways
		Runway[] runways = airport.getRunways();
		System.out.println("Airport Runways:");
		System.out.printf("%-20s    %-20s    %s\n", "Number:", "Type:", "Length:");
		
		//loop to display each runway of the current airport
		for (Runway runway : runways) {
			System.out.printf("%-20s    %-20s    %s Meters\n", runway.getNumber(), runway.getType(), FlightPlan.formatter.format(runway.getLength()));
		}
		System.out.println();
		
		try {
			System.out.print("Enter the Runway number you wish to remove: ");
			int uI = Integer.parseInt(input.nextLine());
			
			runway = null;
			
			//loop through runways array to find runway to delete
			for (Runway runway : runways) {
				if (runway.getNumber() == uI) {
					this.runway = runway;
					break;
				}
			}
			
			if (runway == null) {
				
				//throws number format exception if allowed input is not entered
				throw new NumberFormatException();
			}
			//call to delete runway confirm method by creating new thread
			new Thread() {
				public void run() {
					deleteRunwayConfirm();
				}
			}.start();
		} catch (NumberFormatException exception) {
			//displays an error to the user if input is invalid
			System.err.println("\nYou must select a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay delete runway screen
			new Thread() {
				public void run() {
					deleteRunway();
				}
			}.start();
		}
	}
	
	/**
	 * Confirms the user wishes to delete the {@link Runway} from the currently selected {@link Airport}
	 */
	public void deleteRunwayConfirm() {
		System.out.println();
		System.out.println("*******************");
		System.out.println("* Delete a Runway *");
		System.out.println("*******************");
		System.out.println();
		
		System.out.println("Runway: " + runway);
		System.out.println();
		
		System.out.print("Are you sure you want to delete this Runway? [NO|yes] ");
		String uI = input.nextLine().trim();
		//checks user input for validity 
		
		if (uI.matches("^(?i)y(?:es)?$")) {
			
			//removes selected runway from current airport
			airport.removeRunway(runway);
		}
		
		//call to display airport info by creating new thread
		new Thread() {
			public void run() {
				displayAirportInfo();
			}
		}.start();
	}
	
	/**
	 * Manages the {@link Comm}unication {@link Comm#freq}uencies of the selected {@link Airport}
	 */
	public void manageComms() {
		System.out.println();
		System.out.println("********************************************");
		System.out.println("* Manage Airport Communication Frequencies *");
		System.out.println("********************************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		//creates array of current airport's communication frequencies
		Comm[] comms = airport.getComms();
		System.out.println("Communication Nodes:");
		System.out.printf("%-44s    %s\n", "Type:", "Frequency:");
		
		//loop to display each communication frequency of the current airport
		for (Comm comm : comms) {
			System.out.printf("%-44s    %s MHz\n", comm.getType(), FlightPlan.formatter.format(comm.getFreq()));
		}
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Add a New Communication Node");
		System.out.println("2) Remove a Communication Node");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and calls appropriate method
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							addCommType();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							deleteComm();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							manageAirports();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay manage Comm Freqs screen
			new Thread() {
				public void run() {
					manageComms();
				}
			}.start();
		}
	}
	
	/**
	 * Sets the {@link Comm#type} of the {@link Comm}unication node being added to the selected {@link Airport}
	 */
	public void addCommType() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Add a Communication Frequency *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.println("Type:      ");
		System.out.println("Frequency: ");
		System.out.println();
		
		System.out.println("Available Communication Node Types:");
		System.out.println("1)  ATIS");
		System.out.println("2)  Multicom");
		System.out.println("3)  Unicom");
		System.out.println("4)  FAA Flight Service Station");
		System.out.println("5)  Airport Traffic Control");
		System.out.println("6)  Clearance Delivery Position");
		System.out.println("7)  Ground Control Position Tower");
		System.out.println("8)  Radar or Non-Radar Approach Control Position");
		System.out.println("9)  Radar Departure Control Position");
		System.out.println("10) FAA Air Route Traffic Control Center");
		System.out.println("11) Class C");
		System.out.println("12) Emergency Call");
		System.out.println();
		
		try {
			System.out.println("Question 1 of 2");
			System.out.print("Enter the number corresponding to the Communication node type: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity and assigns commType based on user input
			switch (uI) {
				case 1:
					commType = Comm.CommType.ATIS;
					break;
				case 2:
					commType = Comm.CommType.MULTICOM;
					break;
				case 3:
					commType = Comm.CommType.UNICOM;
					break;
				case 4:
					commType = Comm.CommType.FAA_FLIGHT_SERVICE_STATION;
					break;
				case 5:
					commType = Comm.CommType.AIRPORT_TRAFFIC_CONTROL;
					break;
				case 6:
					commType = Comm.CommType.CLEARANCE_DELIVERY_POSITION;
					break;
				case 7:
					commType = Comm.CommType.GROUND_CONTROL_POSITION_IN_TOWER;
					break;
				case 8:
					commType = Comm.CommType.RADAR_OR_NONRADAR_APPROACH_CONTROL_POSITION;
					break;
				case 9:
					commType = Comm.CommType.RADAR_DEPARTURE_CONTROL_POSITION;
					break;
				case 10:
					commType = Comm.CommType.FAA_AIR_ROUTE_TRAFFIC_CONTROL_CENTER;
					break;
				case 11:
					commType = Comm.CommType.CLASS_C;
					break;
				case 12:
					commType = Comm.CommType.EMERGENCY_CALL;
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay Add Comm Freq Type
			new Thread() {
				public void run() {
					manageComms();
				}
			}.start();
			
			return;
		}
		
		//redisplays add communication frequency screen by creating new thread
		new Thread() {
			public void run() {
				addCommFreq();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Comm#freq}uency of the {@link Comm}unication node being added to the selected {@link Airport}
	 */
	public void addCommFreq() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Add a Communication Frequency *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.println("Type:      " + commType);
		System.out.println("Frequency: ");
		System.out.println();
		
		System.out.println("Question 2 of 2");
		System.out.print("Enter the Communication Frequency (between 118 and 136.975 MHz): ");
		
		//assigns user input to commFreq
		commFreq = input.nextLine();
		
		//displays add communication freq confirm screen by creating new thread
		new Thread() {
			public void run() {
				addCommConfirm();
			}
		}.start();
	}
	
	/**
	 * Confirms that the user wishes to add the {@link Comm}unication node to the selected {@link Airport}
	 */
	public void addCommConfirm() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Add a Communication Frequency *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.println("Are these details correct?");
		System.out.println("Type:      " + commType);
		System.out.println("Frequency: " + commFreq);
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Accept and Save this Communication Node");
		System.out.println("2) Discard this Communication Node");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity and either creates new Comm Freq or calls apporpriate method
			switch (uI) {
				case 1:
					try {
						
						//creates new communication frequency from user supplied input
						comm = new Comm(commType, commFreq);
						airport.addComm(comm);
					} catch (FlightPlanException _exception) {
						
						//if error occurs print exception error message
						System.err.println(_exception.getMessage());
					}
					// Fall Through
				case 2:
					
					//displays manage communication frequencies screen by creating new thread
					new Thread() {
						public void run() {
							manageComms();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must select an available option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplays add Communication Frequency screen by creating new thread
			new Thread() {
				public void run() {
					addCommConfirm();
				}
			}.start();
		}
	}
	
	/**
	 * Deletes a {@link Comm}unication node from the selected {@link Airport}
	 */
	public void deleteComm() {
		System.out.println();
		System.out.println("************************************");
		System.out.println("* Delete a Communication Frequency *");
		System.out.println("************************************");
		System.out.println();
		
		System.out.println("Airport: " + airport);
		System.out.println();
		
		//creates an array of the current airport's comm frequencies
		Comm[] comms = airport.getComms();
		System.out.println("Communication Nodes:");
		System.out.printf("%-7s    %-44s    %s\n", "Number:", "Type:", "Frequency:");
		
		int counter = 1;
		//loop that displays each comm frequency of the current airport
		for (Comm comm : comms) {
			System.out.printf("%-7s    %-44s    %s MHz\n", counter + ")", comm.getType(), FlightPlan.formatter.format(comm.getFreq()));
			counter ++;
		}
		System.out.println();
		
		try {
			System.out.print("Enter the number corresponding to the Communication Node to remove: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//grabs the comm freq that corresponds to user input
			comm = comms[uI];
			
			//displays delete comm freq confirm screen by creating new thread
			new Thread() {
				public void run() {
					deleteCommConfirm();
				}
			}.start();
		} catch (RuntimeException exception) {
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay delete Comm Freq screen
			new Thread() {
				public void run() {
					deleteComm();
				}
			}.start();
		}
	}
	
	/**
	 * Confirms the user wishes to delete the {@link Comm}unication node from the selected {@link Airport}
	 */
	public void deleteCommConfirm() {
		System.out.println();
		System.out.println("************************************");
		System.out.println("* Delete a Communication Frequency *");
		System.out.println("************************************");
		System.out.println();
		
		System.out.println("Communication Node: " + comm);
		System.out.println();
		
		System.out.print("Are you sure you want to delete this Communication node? [NO|yes] ");
		String uI = input.nextLine().trim();
		
		//check user input for validity
		if (uI.matches("^(?i)y(?:es)?$")) {
			//removes selected comm frequency
			airport.removeComm(comm);
		}
		
		//display airport info screen by creating new thread
		new Thread() {
			public void run() {
				displayAirportInfo();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airport#name} of the {@link Airport} being added to the database
	 */
	public void addNewAirportName() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Name:      ");
		System.out.println("ICAO ID:   ");
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println("Elevation: ");
		System.out.println();
		
		try {
			System.out.println("Question 1 of 5");
			System.out.print("Enter the Name: ");

			airportName = Vertex.getValidName(input.nextLine(), null, flightPlan);
		} catch (VertexException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
			
			return;
		}
			
		new Thread() {
			public void run() {
				addNewAirportICAOID();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airport#ICAOid} of the {@link Airport} being added to the database
	 */
	public void addNewAirportICAOID() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Name:      " + airportName);
		System.out.println("ICAO ID:   ");
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println("Elevation: ");
		System.out.println();
		
		try {
			System.out.println("Question 2 of 5");
			System.out.print("Enter the ICAO ID: ");
			
			//sets airport ICAO ID to user input
			airportICAOid = Vertex.getValidICAOid(input.nextLine(), null, flightPlan);
		} catch (VertexException exception) {
			// Give the user the error message
			System.err.println(exception.getMessage());
			
			// Navigate back to the "Manage Airports" menu
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
			
			// End the method here
			return;
		}
		
		//dipslays add new airport latitude screen by creating new thread
		new Thread() {
			public void run() {
				addNewAirportLat();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Coordinate#latitude} of the {@link Airport} being added to the database
	 */
	public void addNewAirportLat() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Name:      " + airportName);
		System.out.println("ICAO ID:   " + airportICAOid);
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println("Elevation: ");
		System.out.println();
		
		try {
			System.out.println("Question 3 of 5");
			System.out.print("Enter the Latitude: ");
			
			latitude = Coordinate.getValidLatitude(input.nextLine());
		} catch (CoordinateException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
			
			return;
		}
		
		//displays add new airport longitude screen by creating new thread
		new Thread() {
			public void run() {
				addNewAirportLong();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Coordinate#longitude} of the {@link Airport} being added to the database
	 */
	public void addNewAirportLong() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Name:      " + airportName);
		System.out.println("ICAO ID:   " + airportICAOid);
		System.out.println("Latitude:  " + latitude);
		System.out.println("Longitude: ");
		System.out.println("Elevation: ");
		System.out.println();
		
		try {
			System.out.println("Question 4 of 5");
			System.out.print("Enter the Longitude: ");
			
			longitude = Coordinate.getValidLongitude(input.nextLine());
			
			coordinate = Coordinate.getValidCoordinate(latitude, longitude, null, flightPlan);
			
			latitude = new Float(coordinate.getLatitude()).toString();
			longitude = new Float(coordinate.getLongitude()).toString();
		} catch (CoordinateException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageAirports();
				}
			}.start();
			
			return;
		}
		
		//displays add new airport elevation screen by creating new thread
		new Thread() {
			public void run() {
				addNewAirportElev();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airport#elevation} of the {@link Airport} being added to the database
	 */
	public void addNewAirportElev() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Name:      " + airportName);
		System.out.println("ICAO ID:   " + airportICAOid);
		System.out.println("Latitude:  " + latitude);
		System.out.println("Longitude: " + longitude);
		System.out.println("Elevation: ");
		System.out.println();
		
		System.out.println("Question 5 of 5");
		System.out.print("Enter the Elevation: ");
		
		//sets airport elevation to user input
		airportElevation = input.nextLine();
		
		//displays add new airport confirm screen by creating new thread
		new Thread() {
			public void run() {
				addNewAirportConfirm();
			}
		}.start();
	}
	
	/**
	 * Confirms the user wishes to add the currently selected {@link Airport} to the database
	 */
	public void addNewAirportConfirm() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Add a New Airport *");
		System.out.println("*********************");
		System.out.println();
		
		System.out.println("Are these details correct?");
		System.out.println("Name:      " + airportName);
		System.out.println("ICAO ID:   " + airportICAOid);
		System.out.println("Latitude:  " + latitude);
		System.out.println("Longitude: " + longitude);
		System.out.println("Elevation: " + airportElevation);
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Accept and Save this Airport");
		System.out.println("2) Discard this Airport");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			//checks user input for validity and either creates new airport or calls appropriate method
			switch (uI) {
				case 1:
					try {
						coordinate = Coordinate.getValidCoordinate(latitude, longitude, null, flightPlan);
						
						airport = new Airport(flightPlan, 
											  airportICAOid, 
											  airportName, 
											  airportHasAVGAS, 
											  airportHasJAa, 
											  coordinate, 
											  airportElevation);

						//adds new airport to flightplan airports list
						flightPlan.addAirport(airport);
					} catch (FlightPlanException _exception) {
						//if error occurs display exception error message
						System.err.println(_exception.getMessage());
					}
					
					// Fall Through
				case 2:
					
					//displays manage airports screen by creating new thread
					new Thread() {
						public void run() {
							manageAirports();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			
			//displays an error to the user if input is invalid
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplays add new airport confirm screen by creating new thread
			new Thread() {
				public void run() {
					addNewAirportConfirm();
				}
			}.start();
		}
	}
	
	/**
	 * Manages the {@link Airplane}s in the database
	 */
	public void manageAirplanes() {
		System.out.println();
		System.out.println("*************************");
		System.out.println("* Manage Airplanes Menu *");
		System.out.println("*************************");
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Query Existing Airplanes");
		System.out.println("2) Add a New Airplane");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							queryAirplanes();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							addAirplaneMake();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							displayManageDatabase();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user in invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay manage Airplanes screen
			new Thread() {
				public void run() {
					manageAirplanes();
				}
			}.start();
		}
	}
	
	/**
	 * Queries any of the existing {@link Airplane}s in the database
	 */
	public void queryAirplanes() {
		System.out.println();
		System.out.println("****************************");
		System.out.println("* Query Existing Airplanes *");
		System.out.println("****************************");
		System.out.println();
		
		// Make sure there is an Airplane to query
		if (flightPlan.getAirplaneCount() == 0) {
			System.err.println("There are no Airplanes in the database.");
			System.out.print("Press <ENTER> to continue ...");
			
			// Wait for the user to acknowledge the message
			input.nextLine();
			
			// Return to the manageAirplanes() method
			new Thread() {
				public void run() {
					manageAirplanes();
				}
			}.start();
			
			// End the method here
			return;
		}
		
		System.out.print("Enter the [partial] Make or Model: ");
		String uI = input.nextLine().trim();
		
		//creates an array of airplanes based on user input
		Airplane[] airplanes = flightPlan.getAirplanesByMakeOrModel(uI);
		
		//checks airplane array size for validity and either sets current airplane or calls appropriate method
		switch (airplanes.length) {
			case 0:
				
				//displays an error to the user if input is invalid
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				
				//allow user to confirm error
				input.nextLine();
				
				//redisplays query airplane screen by creating new thread
				new Thread() {
					public void run() {
						queryAirplanes();
					}
				}.start();
				
				return;
			case 1:
				
				//sets current airplane to user input
				airplane = airplanes[0];
				break;
			default:
				System.out.println("Airplanes:");
				System.out.printf("%-7s    %-20s    %s\n", "Number:", "Make:", "Model:");
				
				int counter = 1;
				
				//loop that displays current airplane's information
				for (Airplane airplane : airplanes) {
					System.out.printf("%-7s    %-20s    %s\n", counter + ")", airplane.getMake(), airplane.getModel());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Airplane: ");
					int index = Integer.parseInt(input.nextLine());
					
					//sets current airplane to user input
					airplane = airplanes[index - 1];
				} catch (RuntimeException exception) {
					
					//displays an error to the user if input is invalid
					System.err.println("\nYou must select a number corresponding to one of the Airplanes.");
					System.out.print("Press <ENTER> to continue ...");
					
					//allow user to confirm error
					input.nextLine();
					
					//redisplays query airplanes screen by creating new thread
					new Thread() {
						public void run() {
							queryAirplanes();
						}
					}.start();
					
					return;
				}
		}
		//displays airplane info by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Displays the information of the selected {@link Airplane}
	 */
	public void displayAirplaneInfo() {
		System.out.println();
		System.out.println("********************");
		System.out.println("* Manage Airplanes *");
		System.out.println("********************");
		System.out.println();
		
		System.out.println("Airplane: " + airplane);
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Delete this Airplane");
		System.out.println("2) Modify this Airplane");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							deleteAirplane();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyAirplane();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							manageAirplanes();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay airplane Info screen
			new Thread() {
				public void run() {
					displayAirplaneInfo();
				}
			}.start();
		}
	}
	
	/**
	 * Modifies the selected {@link Airplane}
	 */
	public void modifyAirplane() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		//displays current airplane's name
		System.out.println("Airplane: " + airplane);
		System.out.println();
		
		System.out.println("Available Options:");
		System.out.println("1) Modify Make");
		System.out.println("2) Modify Model");
		System.out.println("3) Modify Type");
		System.out.println("4) Modify Tank Size");
		System.out.println("5) Modify Liters per Hour");
		System.out.println("6) Modify Cruise Speed");
		System.out.println("7) Return to the Previous Menu");
		System.out.println("8) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//checks user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							modifyAirplaneMake();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyAirplaneModel();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							modifyAirplaneType();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							modifyAirplaneTankSize();
						}
					}.start();
					break;
				case 5:
					new Thread() {
						public void run() {
							modifyAirplaneLPH();
						}
					}.start();
					break;
				case 6:
					new Thread() {
						public void run() {
							modifyAirplaneCruiseSpeed();
						}
					}.start();
					break;
				case 7:
					new Thread() {
						public void run() {
							displayAirplaneInfo();
						}
					}.start();
					break;
				case 8:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay deleteAirplane screen
			new Thread() {
				public void run() {
					modifyAirplane();
				}
			}.start();
		}
	}
	
	/**
	 * Modifies the {@link Airplane#make} of the selected {@link Airplane}
	 */
	public void modifyAirplaneMake() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		try {
			System.out.print("Enter the new make: ");
			String uI = Airplane.getValidMake(input.nextLine(), airplane.getModel(), airplane, flightPlan);
		
			airplane.setMake(uI);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//redisplays airplane info screen with update by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Airplane#model} of the selected {@link Airplane}
	 */
	public void modifyAirplaneModel() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		try {
			System.out.print("Enter the new model: ");
			String uI = Airplane.getValidModel(airplane.getMake(), input.nextLine(), airplane, flightPlan);
			
			//sets current airplanes model to user input
			airplane.setModel(uI);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//redisplays airplane info screen with update by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Airplane#type} of the selected {@link Airplane}
	 */
	public void modifyAirplaneType() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		System.out.println("Available Airplane Types");
		System.out.println("1) Jet Plane");
		System.out.println("2) Prop Plane");
		System.out.println("3) Turbo Prop Plane");
		System.out.println();
		
		try {
			System.out.print("Enter the number of the new type: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					airplaneType = Airplane.AirplaneType.JET;
					break;
				case 2:
					airplaneType = Airplane.AirplaneType.PROP;
					break;
				case 3:
					airplaneType = Airplane.AirplaneType.TURBO_PROP;
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
			//sets current airplane's type to airplaneType
			airplane.setType(airplaneType);
			
			//redisplays airplain info screen with update by creating new thread
			new Thread() {
				public void run() {
					displayAirplaneInfo();
				}
			}.start();
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must select a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay deleteAirplane screen
			new Thread() {
				public void run() {
					modifyAirplaneType();
				}
			}.start();
		}
	}
	
	/**
	 * Modifies the {@link Airplane#tankSize fuel tank size} of the selected {@link Airplane}
	 */
	public void modifyAirplaneTankSize() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		System.out.print("Enter the new tank size: ");
		String uI = input.nextLine();
		
		try {
			
			//sets airplane tanksize to user input
			airplane.setTankSize(uI);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//redisplays airplane info screen with update by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Airplane#litersPerHour liters of fuel used per hour} ratio of the selected {@link Airplane}
	 */
	public void modifyAirplaneLPH() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		System.out.print("Enter the new Liters per Hour ratio: ");
		String uI = input.nextLine();
		
		try {
			
			//sets current airplane LitersPerHour to user input
			airplane.setLitersPerHour(uI);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//redisplays airplane info screen with update by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Airplane#cruiseSpeed cruise speed} of the selected {@link Airplane}
	 */
	public void modifyAirplaneCruiseSpeed() {
		System.out.println();
		System.out.println("*******************************");
		System.out.println("* Modify Airplane Information *");
		System.out.println("*******************************");
		System.out.println();
		
		System.out.print("Enter the new cruise speed: ");
		String uI = input.nextLine();
		
		try {
			
			//sets current airplane Cruise Speed to user input
			airplane.setCruiseSpeed(uI);
		} catch (FlightPlanException exception) {
			
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		//redisplays airplane info screen with update by creating new thread
		new Thread() {
			public void run() {
				displayAirplaneInfo();
			}
		}.start();
	}
	
	/**
	 * Deletes the selected {@link Airplane} from the database
	 */
	public void deleteAirplane() {
		System.out.println();
		System.out.println("*******************");
		System.out.println("* Delete Airplane *");
		System.out.println("*******************");
		System.out.println();
		
		System.out.println("Airplane: " + airplane);
		System.out.println();
		
		System.out.print("Do you want to delete this Airplane? [NO|yes] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity and either removes airplane or calls appropriate method
		if (uI.matches("^(?i)y(?:es)?$")) {
			try {
				
				//removes current airplane from flightplan airplane list
				flightPlan.removeAirplane(airplane);
			} catch (FlightPlanException exception) {
				
				//if error occurs display exception error message
				System.err.println(exception.getMessage());
			}
		}
		
		//redisplays manage airplanes screen by creating new thread
		new Thread() {
			public void run() {
				manageAirplanes();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airplane#make} of the {@link Airplane} being added to the database
	 */
	public void addAirplaneMake() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            ");
		System.out.println("Model:           ");
		System.out.println("Type:            ");
		System.out.println("Tank Size:       ");
		System.out.println("Liters per Hour: ");
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Question 1 of 6");
		System.out.print("Enter the make: ");
		
		try {
			airplaneMake = Airplane.getValidMake(input.nextLine(), "", null, flightPlan);
		} catch (AirplaneException exception) {
			System.err.println(exception);
			
			new Thread() {
				public void run() {
					manageAirplanes();
				}
			}.start();
			
			return;
		}
		
		//displays add airplane model screen by creating new thread
		new Thread() {
			public void run() {
				addAirplaneModel();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airplane#model} of the {@link Airplane} being added to the database
	 */
	public void addAirplaneModel() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           ");
		System.out.println("Type:            ");
		System.out.println("Tank Size:       ");
		System.out.println("Liters per Hour: ");
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Question 2 of 6");
		System.out.print("Enter the model: ");
		
		try {
			airplaneModel = Airplane.getValidModel(airplaneMake, input.nextLine(), null, flightPlan);
		} catch (AirplaneException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageAirplanes();
				}
			}.start();
			
			return;
		}
		
		//displays add airplane type screen by creating new thread
		new Thread() {
			public void run() {
				addAirplaneType();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airplane#type} of the {@link Airplane} being added to the database
	 */
	public void addAirplaneType() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           " + airplaneModel);
		System.out.println("Type:            ");
		System.out.println("Tank Size:       ");
		System.out.println("Liters per Hour: ");
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Available Airplane Types");
		System.out.println("1) Jet Plane");
		System.out.println("2) Prop Plane");
		System.out.println("3) Turbo Prop Plane");
		System.out.println();
		
		try {
			System.out.println("Question 3 of 6");
			System.out.print("Enter the number corresponding to the type: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					airplaneType = Airplane.AirplaneType.JET;
					break;
				case 2:
					airplaneType = Airplane.AirplaneType.PROP;
					break;
				case 3:
					airplaneType = Airplane.AirplaneType.TURBO_PROP;
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
			
			//display add airplane tank size screen by creating new thread
			new Thread() {
				public void run() {
					addAirplaneTankSize();
				}
			}.start();
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add Airplane Type screen
			new Thread() {
				public void run() {
					addAirplaneType();
				}
			}.start();
		}
	}
	
	/**
	 * Sets the {@link Airplane#tankSize fuel tank size} of the {@link Airplane} being added to the database
	 */
	public void addAirplaneTankSize() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           " + airplaneModel);
		System.out.println("Type:            " + airplaneType);
		System.out.println("Tank Size:       ");
		System.out.println("Liters per Hour: ");
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Question 4 of 6");
		System.out.print("Enter the tank size: ");
		
		//sets airplane tank size to user input
		airplaneTankSize = input.nextLine();
		
		//display add airplane liters per hour screen by creating new thread
		new Thread() {
			public void run() {
				addAirplaneLitersPerHour();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airplane#litersPerHour liters of fuel used per hour} ratio of the {@link Airplane} being added to the database
	 */
	public void addAirplaneLitersPerHour() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           " + airplaneModel);
		System.out.println("Type:            " + airplaneType);
		System.out.println("Tank Size:       " + airplaneTankSize);
		System.out.println("Liters per Hour: ");
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Question 5 of 6");
		System.out.print("Enter the liters of fuel used per hour: ");
		
		//sets airplane Liters Per Hour to user input
		airplaneLitersPerHour = input.nextLine();
		
		//display add airplane Cruise Speed screen by creating new thread
		new Thread() {
			public void run() {
				addAirplaneCruiseSpeed();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Airplane#cruiseSpeed cruise speed} of the {@link Airplane} being added to the database
	 */
	public void addAirplaneCruiseSpeed() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           " + airplaneModel);
		System.out.println("Type:            " + airplaneType);
		System.out.println("Tank Size:       " + airplaneTankSize);
		System.out.println("Liters per Hour: " + airplaneLitersPerHour);
		System.out.println("Cruise Speed:    ");
		System.out.println();
		
		System.out.println("Question 6 of 6");
		System.out.print("Enter the cruise speed: ");
		
		//sets airplane Cruise Speed to user input
		airplaneCruiseSpeed = input.nextLine();
		
		//display add airplane confirm screen by creating new thread
		new Thread() {
			public void run() {
				addAirplaneConfirm();
			}
		}.start();
	}
	
	/**
	 * Confirms the user wants to add the current {@link Airplane} to the database
	 */
	public void addAirplaneConfirm() {
		System.out.println();
		System.out.println("**********************");
		System.out.println("* Add a New Airplane *");
		System.out.println("**********************");
		System.out.println();
		
		System.out.println("Are these details correct?");
		System.out.println("Make:            " + airplaneMake);
		System.out.println("Model:           " + airplaneModel);
		System.out.println("Type:            " + airplaneType);
		System.out.println("Tank Size:       " + airplaneTankSize);
		System.out.println("Liters per Hour: " + airplaneLitersPerHour);
		System.out.println("Cruise Speed:    " + airplaneCruiseSpeed);
		System.out.println();
		
		System.out.println("Available Options: ");
		System.out.println("1) Accept and Save this Airplane");
		System.out.println("2) Discard this Airplane");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					try {
						//creates new airplane based on user input
						airplane = new Airplane(airplaneMake, 
												airplaneModel, 
												airplaneType, 
												airplaneTankSize, 
												airplaneLitersPerHour, 
												airplaneCruiseSpeed, 
												flightPlan);
						
						//adds airplane to flightPlan airplane list
						flightPlan.addAirplane(airplane);
					} catch (FlightPlanException exception) {
						
						//if error occurs displays exception error message
						System.err.println(exception.getMessage());
					}
					// Fall Through
				case 2:
					
					//display manage airplane screen by creating new thread
					new Thread() {
						public void run() {
							manageAirplanes();
						}
					}.start();
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add Airplane confirm screen
			new Thread() {
				public void run() {
					addAirplaneConfirm();
				}
			}.start();
		}
	}
	
	/**
	 * Manages the {@link NAVBeacon}s in the database
	 */
	public void manageNAVBeacons() {
		System.out.println();
		System.out.println("***************************");
		System.out.println("* Manage NAV Beacons Menu *");
		System.out.println("***************************");
		System.out.println();
		
		System.out.println("Available Options: ");
		System.out.println("1) Query Existing NAV Beacons");
		System.out.println("2) Add a New NAV Beacon");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							queryNAVBeacons();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							addNAVBeaconICAOID();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							displayManageDatabase();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
		
			//redisplay manage NAV Beacons screen
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
		}
	}
	
	/**
	 * Queries any of the existing {@link NAVBeacon}s in the database
	 */
	public void queryNAVBeacons() {
		System.out.println();
		System.out.println("*********************");
		System.out.println("* Query NAV Beacons *");
		System.out.println("*********************");
		System.out.println();
		
		// Make sure there is a NAVBeacon to query
		if (flightPlan.getNAVBeaconCount() == 0) {
			
			//displays an error to the user if input is invalid
			System.err.println("There are no NAV Beacons in the database.");
			System.out.print("Press <ENTER> to continue ...");
			
			// Wait for the user to acknowledge the message
			input.nextLine();
			
			// Return to the manageNAVBeacons() method
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
			
			// End the method here
			return;
		}
		
		System.out.print("Enter the [partial] ICAO ID or Name: ");
		String uI = input.nextLine().trim();
		
		//creates an array of NAVBeacons based on flightPlan NAVBeacons list
		NAVBeacon[] navbeacons = flightPlan.getNAVBeaconsByNameOrICAOid(uI);
		
		//checks validity of NAVBeacon array length
		switch (navbeacons.length) {
			case 0:
				
				//displays an error to the user if input is invalid
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				
				//allow user to confirm error
				input.nextLine();
				
				//display queryNAVBeacons() method by creating new thread
				new Thread() {
					public void run() {
						queryNAVBeacons();
					}
				}.start();
				
				return;
			case 1:
				//if NAVBeacon array length is 1 set current NAV Beacon to the NAV Beacon in the array
				navbeacon = navbeacons[0];
				break;
			default:
				System.out.println("NAV Beacons:");
				System.out.printf("%-7s    %-8s    %s\n", "Number:", "ICAO ID:", "Model:");
				
				int counter = 1;
				//loop through NAV Beacon array and display each NAV Beacon
				for (NAVBeacon navbeacon : navbeacons) {
					System.out.printf("%-7s    %-8s    %s\n", counter + ")", navbeacon.getICAOid(), navbeacon.getName());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct NAV Beacon: ");
					int index = Integer.parseInt(input.nextLine());
					//sets current navBeacon to user input
					navbeacon = navbeacons[index - 1];
				} catch (RuntimeException exception) {
					
					//displays an error to the user if input is invalid
					System.err.println("\nYou must select a number corresponding to one of the NAV Beacons.");
					System.out.print("Press <ENTER> to continue ...");
					
					//allow user to confirm error
					input.nextLine();
					
					//redisplays queryNAVBeacon() method by creating new thread
					new Thread() {
						public void run() {
							queryNAVBeacons();
						}
					}.start();
					
					return;
				}
		}
		
		//displays displayNAVBeacon() method by creating new thread
		new Thread() {
			public void run() {
				displayNAVBeacon();
			}
		}.start();
	}
	
	/**
	 * Displays information about the selected {@link NAVBeacon}
	 */
	public void displayNAVBeacon() {
		System.out.println();
		System.out.println("***************************");
		System.out.println("* NAV Beacon Query Result *");
		System.out.println("***************************");
		System.out.println();
		
		System.out.println("NAV Beacon: " + navbeacon);
		System.out.println();
		
		System.out.println("Available Options: ");
		System.out.println("1) Delete this NAV Beacon");
		System.out.println("2) Modify this NAV Beacon");
		System.out.println("3) Return to the Previous Menu");
		System.out.println("4) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							deleteNAVBeacon();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyNAVBeacon();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							manageNAVBeacons();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay display NAV Beacon screen
			new Thread() {
				public void run() {
					displayNAVBeacon();
				}
			}.start();
		}
	}
	
	/**
	 * Deletes the selected {@link NAVBeacon}
	 */
	public void deleteNAVBeacon() {
		System.out.println();
		System.out.println("***************************");
		System.out.println("* NAV Beacon Query Result *");
		System.out.println("***************************");
		System.out.println();
		
		System.out.println("NAV Beacon: " + navbeacon);
		System.out.println();
		
		System.out.print("Do you want to delete this NAV Beacon? [NO|yes] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity and either removes current NAV Beacon or calls appropriate method
		if (uI.matches("^(?i)y(?:es)?$")) {
			try {
				
				//removes current NAV Beacon from flightPlan NAVBeacon list
				flightPlan.removeNAVBeacon(navbeacon);
			} catch (FlightPlanException exception) {
				
				//if error occurs display exception error message
				System.err.println(exception.getMessage());
			}
		}
		
		//display manage NAV Beacons screen by creating new thread
		new Thread() {
			public void run() {
				manageNAVBeacons();
			}
		}.start();
	}
	
	/**
	 * Modifies the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeacon() {
		System.out.println();
		System.out.println("**************************");
		System.out.println("* NAV Beacon Information *");
		System.out.println("**************************");
		System.out.println();
		
		//displays NAV Beacon Name
		System.out.println("NAV Beacon: " + navbeacon);
		System.out.println();
		
		System.out.println("Available Options: ");
		System.out.println("1) Modify NAV Beacon ICAO ID");
		System.out.println("2) Modify NAV Beacon Name");
		System.out.println("3) Modify NAV Beacon Latitude");
		System.out.println("4) Modify NAV Beacon Longitude");
		System.out.println("5) Modify NAV Beacon Type");
		System.out.println("6) Return to the Previous Menu");
		System.out.println("7) Return to the Main Menu");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					new Thread() {
						public void run() {
							modifyNAVBeaconICAOID();
						}
					}.start();
					break;
				case 2:
					new Thread() {
						public void run() {
							modifyNAVBeaconName();
						}
					}.start();
					break;
				case 3:
					new Thread() {
						public void run() {
							modifyNAVBeaconLat();
						}
					}.start();
					break;
				case 4:
					new Thread() {
						public void run() {
							modifyNAVBeaconLong();
						}
					}.start();
					break;
				case 5:
					new Thread() {
						public void run() {
							modifyNAVBeaconType();
						}
					}.start();
					break;
				case 6:
					new Thread() {
						public void run() {
							displayNAVBeacon();
						}
					}.start();
					break;
				case 7:
					new Thread() {
						public void run() {
							displayMainMenu();
						}
					}.start();
					break;
				default:
					
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay display NAV Beacon screen
			new Thread() {
				public void run() {
					modifyNAVBeacon();
				}
			}.start();
		}
	}
	
	/**
	 * Modifies the {@link NAVBeacon#ICAOid} attribute of the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeaconICAOID() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Modify NAV Beacon Information *");
		System.out.println("*********************************");
		System.out.println();
		
		try {
			System.out.print("Enter the new ICAO ID: ");
			String uI = Vertex.getValidICAOid(input.nextLine(), navbeacon, flightPlan);
			
			//sets current NAV Beacon ICAO ID to user input
			navbeacon.setICAOid(uI);
		} catch (FlightPlanException exception) {
			
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//dipslays displayNAVBeacon screen with update by creating new thread
		new Thread() {
			public void run() {
				displayNAVBeacon();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link NAVBeacon#name} of the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeaconName() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Modify NAV Beacon Information *");
		System.out.println("*********************************");
		System.out.println();
		
		try {
			System.out.print("Enter the new name: ");
			String uI = Vertex.getValidName(input.nextLine(), navbeacon, flightPlan);
		
			navbeacon.setName(uI);
		} catch (FlightPlanException exception) {
			
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		
		//redisplays displayNAVBeacon screen with update by creating new thread
		new Thread() {
			public void run() {
				displayNAVBeacon();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Coordinate#latitude} of the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeaconLat() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Modify NAV Beacon Information *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.print("Enter the new latitude: ");
		String uI = input.nextLine();
		
		try {
			Coordinate.setLatitude(uI, navbeacon, flightPlan);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		//redisplays displayNAVBeacon screen with update by creating new thread
		new Thread() {
			public void run() {
				displayNAVBeacon();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link Coordinate#longitude} of the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeaconLong() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Modify NAV Beacon Information *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.print("Enter the new longitude: ");
		String uI = input.nextLine();
		
		try {
			Coordinate.setLongitude(uI, navbeacon, flightPlan);
		} catch (FlightPlanException exception) {
			//if error occurs display exception error message
			System.err.println(exception.getMessage());
		}
		//redisplays displayNAVBeacon screen with update by creating new thread
		new Thread() {
			public void run() {
				displayNAVBeacon();
			}
		}.start();
	}
	
	/**
	 * Modifies the {@link NAVBeacon#type} of the selected {@link NAVBeacon}
	 */
	public void modifyNAVBeaconType() {
		System.out.println();
		System.out.println("*********************************");
		System.out.println("* Modify NAV Beacon Information *");
		System.out.println("*********************************");
		System.out.println();
		
		System.out.println("Available NAV Beacon Types:");
		System.out.println("1) VOR");
		System.out.println("2) VORTAC");
		System.out.println("3) NDB");
		System.out.println("4) LORAN");
		System.out.println();
		
		try {
			System.out.print("Enter the number corresponding to the new type: ");
			int uI = Integer.parseInt(input.nextLine());
		
			//check user input for validity
			switch (uI) {
				case 1:
					navbeaconType = NAVBeacon.NAVBeaconType.VOR;
					break;
				case 2:
					navbeaconType = NAVBeacon.NAVBeaconType.VORTAC;
					break;
				case 3:
					navbeaconType = NAVBeacon.NAVBeaconType.NDB;
					break;
				case 4:
					navbeaconType = NAVBeacon.NAVBeaconType.LORAN;
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
			
			navbeacon.setType(navbeaconType);
			
			new Thread() {
				public void run() {
					displayNAVBeacon();
				}
			}.start();
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay deleteAirplane screen
			new Thread() {
				public void run() {
					modifyNAVBeaconType();
				}
			}.start();
		}
	}
	
	/**
	 * Sets the {@link NAVBeacon#ICAOid} of the {@link NAVBeacon} being added to the database
	 */
	public void addNAVBeaconICAOID() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("ICAO ID:   ");
		System.out.println("Name:      ");
		System.out.println("Type:      ");
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println();
		
		try {
			System.out.println("Question 1 of 5");
			System.out.print("Enter the ICAO ID: ");
			
			navbeaconICAOid = Vertex.getValidICAOid(input.nextLine(), null, flightPlan);
		} catch (VertexException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
			
			return;
		}
		
		//displays addNAVBeaconName screen by creating new thread
		new Thread() {
			public void run() {
				addNAVBeaconName();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link NAVBeacon#name} of the {@link NAVBeacon} being added to the database
	 *
	 * @see Vertex#getValidName(String, Vertex, FlightPlan)
	 */
	public void addNAVBeaconName() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("ICAO ID:   " + navbeaconICAOid);
		System.out.println("Name:      ");
		System.out.println("Type:      ");
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println();
		
		try {
			System.out.println("Question 2 of 5");
			System.out.print("Enter the name: ");
			
			// Get the new NAVBeacon name and make sure it's valid
			navbeaconName = Vertex.getValidName(input.nextLine(), null, flightPlan);
		} catch (VertexException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
			
			return;
		}
		
		//displays addNAVBeaconType screen by creating new thread
		new Thread() {
			public void run() {
				addNAVBeaconType();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link NAVBeacon#type} of the {@link NAVBeacon} being added to the database
	 */
	public void addNAVBeaconType() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("ICAO ID:   " + navbeaconICAOid);
		System.out.println("Name:      " + navbeaconName);
		System.out.println("Type:      ");
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println();
		
		System.out.println("Available NAV Beacon Types:");
		System.out.println("1) VOR");
		System.out.println("2) VORTAC");
		System.out.println("3) NDB");
		System.out.println("4) LORAN");
		System.out.println();
		
		try {
			System.out.println("Question 3 of 5");
			System.out.print("Enter the number corresponding to the type: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					navbeaconType = NAVBeacon.NAVBeaconType.VOR;
					break;
				case 2:
					navbeaconType = NAVBeacon.NAVBeaconType.VORTAC;
					break;
				case 3:
					navbeaconType = NAVBeacon.NAVBeaconType.NDB;
					break;
				case 4:
					navbeaconType = NAVBeacon.NAVBeaconType.LORAN;
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
			
			//displays addNAVBeaconLat screen by creating new thread
			new Thread() {
				public void run() {
					addNAVBeaconLat();
				}
			}.start();
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add NAV Beacon Type screen
			new Thread() {
				public void run() {
					addNAVBeaconType();
				}
			}.start();
		}
	}
	
	/**
	 * Sets the {@link Coordinate#latitude} of the {@link NAVBeacon} being added to the database
	 */
	public void addNAVBeaconLat() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("ICAO ID:   " + navbeaconICAOid);
		System.out.println("Name:      " + navbeaconName);
		System.out.println("Type:      " + navbeaconType);
		System.out.println("Latitude:  ");
		System.out.println("Longitude: ");
		System.out.println();
		
		try {
			System.out.println("Question 4 of 5");
			System.out.print("Enter the latitude: ");
			
			latitude = Coordinate.getValidLatitude(input.nextLine());
		} catch (CoordinateException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
			
			return;
		}
		
		//displays addNAVBeaconLong screen by creating new thread
		new Thread() {
			public void run() {
				addNAVBeaconLong();
			}
		}.start();
	}
	
	/**
	 * Sets the {@link Coordinate#longitude} of the {@link NAVBeacon} being added to the database
	 */
	public void addNAVBeaconLong() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("ICAO ID:   " + navbeaconICAOid);
		System.out.println("Name:      " + navbeaconName);
		System.out.println("Type:      " + navbeaconType);
		System.out.println("Latitude:  " + latitude);
		System.out.println("Longitude: ");
		System.out.println();
		
		try {
			System.out.println("Question 5 of 5");
			System.out.print("Enter the longitude: ");
			
			longitude = Coordinate.getValidLongitude(input.nextLine());
			
			coordinate = Coordinate.getValidCoordinate(latitude, longitude, null, flightPlan);
			
			latitude = new Float(coordinate.getLatitude()).toString();
			longitude = new Float(coordinate.getLongitude()).toString();
		} catch (CoordinateException exception) {
			System.err.println(exception.getMessage());
			
			new Thread() {
				public void run() {
					manageNAVBeacons();
				}
			}.start();
			
			return;
		}
		
		//displays addNAVBeaconConfirm screen by creating new thread
		new Thread() {
			public void run() {
				addNAVBeaconConfirm();
			}
		}.start();
	}
	
	/**
	 * Confirms the user want to add the current {@link NAVBeacon} to the database
	 */
	public void addNAVBeaconConfirm() {
		System.out.println();
		System.out.println("************************");
		System.out.println("* Add a New NAV Beacon *");
		System.out.println("************************");
		System.out.println();
		
		System.out.println("Are these details correct?");
		System.out.println("ICAO ID:   " + navbeaconICAOid);
		System.out.println("Name:      " + navbeaconName);
		System.out.println("Type:      " + navbeaconType);
		System.out.println("Latitude:  " + latitude);
		System.out.println("Longitude: " + longitude);
		System.out.println();
	
		System.out.println("Available Options: ");
		System.out.println("1) Accept and Save this NAV Beacon");
		System.out.println("2) Discard this NAV Beacon");
		System.out.println();
		
		try {
			System.out.print("Enter an option: ");
			int uI = Integer.parseInt(input.nextLine());
			
			//check user input for validity
			switch (uI) {
				case 1:
					try {
						navbeacon = new NAVBeacon(flightPlan,
						                          navbeaconICAOid,
						                          navbeaconName,
						                          navbeaconType,
						                          coordinate);
						
						//add new NAVBeacon to flightPlan NAVBeacon list
						flightPlan.addNAVBeacon(navbeacon);
					} catch (FlightPlanException exception) {
						//if error occurs display exception error message
						System.err.println(exception.getMessage());
					}
					// Fall Through
				case 2:
					//display manageNAVBeacons screen by creating new thread
					new Thread() {
						public void run() {
							manageNAVBeacons();
						}
					}.start();
					break;
				default:
					//throws number format exception if allowed input is not entered
					throw new NumberFormatException();
			}
		} catch (NumberFormatException exception) {
			//if user input is invalid, display error, and allow user to input again
			System.err.println("\nYou must enter a valid option.");
			System.out.print("Press <ENTER> to continue ...");
			
			//allow user to confirm error
			input.nextLine();
			
			//redisplay add NAV Beacon Confirm screen
			new Thread() {
				public void run() {
					addNAVBeaconConfirm();
				}
			}.start();
		}
	}
	
	/**
	 * Selects the {@link Airplane} to use during the flight
	 */
	public void flightPlannerSelectPlane() {
		System.out.println();
		System.out.println("*****************************************");
		System.out.println("* Flight Planner - Select Your Airplane *");
		System.out.println("*****************************************");
		System.out.println();
		
		// Make sure the database has at least one airplane and airport
		if (flightPlan.getAirplaneCount() == 0 || flightPlan.getAirportCount() == 0) {
			System.err.println("The database must have at least one Airplane and Airport to proceed.");
			System.out.print("Press <ENTER> to continue ...");
			
			// Wait for the user to confirm he has seen the error message
			input.nextLine();
			
			// Return the user to the main menu
			new Thread() {
				public void run() {
					displayMainMenu();
				}
			}.start();
			
			// End the method here
			return;
		}
		
		System.out.print("Enter the [partial] Make or Model: ");
		String uI = input.nextLine().trim();
		//creates array of airplane objects from current flightPlan airplane list based on user input
		Airplane[] airplanes = flightPlan.getAirplanesByMakeOrModel(uI);
		//checks length of airplane array for validity and either redisplays FlightPlanSelectPlane screen or sets current airplane to user input
		switch (airplanes.length) {
			case 0:
				//if user input is invalid, display error, and allow user to input again
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				
				//allow user to confirm error
				input.nextLine();
				
				//redisplays flightPlannerSelectPlane screen by creating new thread
				new Thread() {
					public void run() {
						flightPlannerSelectPlane();
					}
				}.start();
				
				return;
			case 1:
				//sets current airplane to the only object in the array
				airplane = airplanes[0];
				break;
			default:
				System.out.println("Airplanes:");
				System.out.printf("%-7s    %-20s    %s\n", "Number:", "Make:", "Model:");
				
				int counter = 1;
				//loop that displays all airplanes matching user input
				for (Airplane airplane : airplanes) {
					System.out.printf("%-7s    %-20s    %s\n", counter + ")", airplane.getMake(), airplane.getModel());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Airplane: ");
					int index = Integer.parseInt(input.nextLine());
					//sets airplane to the airplane object at index[user input - 1]
					airplane = airplanes[index - 1];
				} catch (RuntimeException exception) {
					//if user input is invalid, display error, allow user to input again
					System.err.println("\nYou must select a number corresponding to one of the Airplanes.");
					System.out.print("Press <ENTER> to continue ...");
					
					//allow user to confirm error
					input.nextLine();
					
					//redisplay flight Planner Select Plane screen by creating new thread
					new Thread() {
						public void run() {
							flightPlannerSelectPlane();
						}
					}.start();
					
					return;
				}
		}
		//display flightPlannerBeginDest screen by creating new thread
		new Thread() {
			public void run() {
				flightPlannerBeginDest();
			}
		}.start();
	}
	
	/**
	 * Selects the {@link Airport} from which to begin the flight
	 */
	public void flightPlannerBeginDest() {
		System.out.println();
		System.out.println("**************************************************");
		System.out.println("* Flight Planner - Select Your Departure Airport *");
		System.out.println("**************************************************");
		System.out.println();
		
		System.out.print("Enter the [partial] ICAO ID or Name: ");
		String uI = input.nextLine().trim();
		//create array of airport objects based on current flightPlan airport list
		Airport[] airports = flightPlan.getAirportsByNameOrICAOid(uI);
		//checks length of airport array for validity and either redisplays FlightPlanBeginDest screen or sets current airport to user input
		switch (airports.length) {
			case 0:
				//if user input is invalid, display error, and allow user to input again
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				//allow user to confirm error
				input.nextLine();
				//redisplays flightPlannerBeginDest screen by creating new thread
				new Thread() {
					public void run() {
						flightPlannerBeginDest();
					}
				}.start();
				
				return;
			case 1:
				//sets startDestingation to only airport in airports array
				startDestination = airports[0];
				break;
			default:
				System.out.println("Airports:");
				System.out.printf("%s    %s    %s\n", "Number:", "ICAO ID:", "Name:");
				
				int counter = 1;
				//loop that displays all airports that match user input
				for (Airport airport : airports) {
					System.out.printf("%-7s    %-8s    %s\n", counter + ")", airport.getICAOid(), airport.getName());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Airport: ");
					int index = Integer.parseInt(input.nextLine());
					//sets startDestination to the airport object at index[user input - 1] 
					startDestination = airports[index - 1];
				} catch (RuntimeException exception) {
					//if user input is invalid, display error, and allow user to input again
					System.err.println("\nYou must select a number corresponding to one of the Airports.");
					System.out.print("Press <ENTER> to continue ...");
					//allow user to confirm error
					input.nextLine();
					//redisplays flightPlannerBeginDest screen by creating new thread
					new Thread() {
						public void run() {
							flightPlannerBeginDest();
						}
					}.start();
					
					return;
				}
		}
		//displays flightPlannerEndDest screen by creating new thread
		new Thread() {
			public void run() {
				flightPlannerEndDest();
			}
		}.start();
	}
	
	/**
	 * Selects the {@link Airport} at which to end the flight
	 */
	public void flightPlannerEndDest() {
		System.out.println();
		System.out.println("****************************************************");
		System.out.println("* Flight Planner - Select Your Destination Airport *");
		System.out.println("****************************************************");
		System.out.println();
		
		System.out.print("Enter the [partial] ICAO ID or Name: ");
		String uI = input.nextLine().trim();
		//create array of airport objects based on current flightPlan airport list
		Airport[] airports = flightPlan.getAirportsByNameOrICAOid(uI);
		//checks length of airport array for validity and either redisplays FlightPlanBeginDest screen or sets current airport to user input
		switch (airports.length) {
			case 0:
				//if user input is invalid, display error, and allow user to input again
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				//allow user to confirm error
				input.nextLine();
				//redisplays flightPlannerEndDest screen by creating new thread
				new Thread() {
					public void run() {
						flightPlannerEndDest();
					}
				}.start();
				
				return;
			case 1:
				//sets endDestingation to only airport in airports array
				endDestination = airports[0];
				break;
			default:
				System.out.println("Airports:");
				System.out.printf("%s    %s    %s\n", "Number:", "ICAO ID:", "Name:");
				
				int counter = 1;
				//loop that displays all airports that match user input
				for (Airport airport : airports) {
					System.out.printf("%-7s    %-8s    %s\n", counter + ")", airport.getICAOid(), airport.getName());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Airport: ");
					int index = Integer.parseInt(input.nextLine());
					//sets startDestination to the airport object at index[user input - 1] 
					endDestination = airports[index - 1];
				} catch (RuntimeException exception) {
					//if user input is invalid, display error and allow user to input again
					System.err.println("\nYou must select a number corresponding to one of the Airports.");
					System.out.print("Press <ENTER> to continue ...");
					
					//allow user to confirm error
					input.nextLine();
					
					//redisplays flightPlannerEndDest screen by creating new thread
					new Thread() {
						public void run() {
							flightPlannerEndDest();
						}
					}.start();
					
					return;
				}
		}
		//displays flightPlannerAddDest screen by creating new thread
		new Thread() {
			public void run() {
				flightPlannerAddDest();
			}
		}.start();
	}
	
	/**
	 * Requests whether the user would like to add an additional {@link Vertex destination}
	 */
	public void flightPlannerAddDest() {
		System.out.println();
		System.out.println("*******************************************");
		System.out.println("* Flight Planner - Add Another Destinaton *");
		System.out.println("*******************************************");
		System.out.println();
		
		System.out.println("Would you like to specify another destination? [NO|yes] ");
		String uI = input.nextLine().trim();
		
		//checks user input for validity and calls the appropriate method
		if (uI.matches("^(?i)y(?:es)?$")) {
			new Thread() {
				public void run() {
					flightPlannerAddDestLocation();
				}
			}.start();
		} else {
			new Thread() {
				public void run() {
					flightPlannerDisplayPlan();
				}
			}.start();
		}
	}
	
	/**
	 * Appends an additional {@link Vertex destination} to the flight plan
	 */
	public void flightPlannerAddDestLocation() {
		System.out.println();
		System.out.println("***************************************************************");
		System.out.println("* Flight Planner - Select Your Additional Destination Airport *");
		System.out.println("***************************************************************");
		System.out.println();
		
		System.out.print("Enter the [partial] ICAO ID or Name: ");
		String uI = input.nextLine().trim();
		//creates an array of vertex objects from flightPlan's current Vertex list based on user input
		Vertex[] vertices = flightPlan.getVerticesByNameOrICAOid(uI);
		
		switch (vertices.length) {
			case 0:
				//if user input is invalid, display error, and allow user to input again
				System.err.println("\nYour query returned no results.");
				System.out.print("Press <ENTER> to continue ...");
				//allow user to confirm error
				input.nextLine();
				//redisplays flightPlannerAddDestLocation screen by creating new thread
				new Thread() {
					public void run() {
						flightPlannerAddDestLocation();
					}
				}.start();
				
				return;
			case 1:
				//adds the only object in vertices array to additionalDestinations array list
				additionalDestinations.add(vertices[0]);
				break;
			default:
				System.out.println("Vertices:");
				System.out.printf("%s    %s    %s\n", "Number:", "ICAO ID:", "Name:");
				
				int counter = 1;
				//loop that displays all vertices that match user input
				for (Vertex vertex : vertices) {
					System.out.printf("%-7s    %-8s    %s\n", counter + ")", vertex.getICAOid(), vertex.getName());
					counter ++;
				}
				System.out.println();
				
				try {
					System.out.print("Enter the number corresponding to the correct Vertex: ");
					int index = Integer.parseInt(input.nextLine());
					//adds vertex from vertices array to object at [user input - 1] to additionalDestinations array list
					additionalDestinations.add(vertices[index - 1]);
				} catch (RuntimeException exception) {
					//if user input is invalid, display error, and allow user to input again
					System.err.println("\nYou must select a number corresponding to one of the Vertices.");
					System.out.print("Press <ENTER> to continue ...");
					//allow user to confirm error
					input.nextLine();
					//redisplays flightPlannerAddDestLocation screen by creating new thread
					new Thread() {
						public void run() {
							flightPlannerAddDestLocation();
						}
					}.start();
					
					return;
				}
		}
		//displays flightPlannerAddDest screen by creating new thread
		new Thread() {
			public void run() {
				flightPlannerAddDest();
			}
		}.start();
	}
	
	/**
	 * Displays the flight plan to the user
	 */
	public void flightPlannerDisplayPlan() {
		System.out.println();
		System.out.println("*************************");
		System.out.println("* Flight Plan - Results *");
		System.out.println("*************************");
		System.out.println();
		
		try {
			// Sets the current flightPlan to user input information
			flightPlan.setFlightPlan(startDestination, endDestination, additionalDestinations, airplane);
		
			// Prints the Flight Plan
			System.out.println(flightPlan);
			System.out.println();
		} catch (FlightPlanException exception) {
			//if error occurs, display exception error message
			System.err.println(exception.getMessage());
		}
		
		// Reset the list of additional destinations
		additionalDestinations.clear();
		
		//allow user to return to the main menu to exit the program
		System.out.print("Press <ENTER> to return to the Main Menu ...");
		
		//allow user to acknowledge the message
		input.nextLine();
		
		//display main menu to user
		new Thread() {
			public void run() {
				displayMainMenu();
			}
		}.start();
	}
}