package edu.usca.acsc492l.flightplanner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Hashtable;

/**
 * A model class that represents an Airplane object
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class Airplane {
	
	/** Represents each available type of airplane */
	public static enum AirplaneType {
		
		/** Represents jet-powered airplanes */
		JET("Jet"),
		
		/** Represents propeller-powered airplanes */
		PROP("Prop"),
		
		/** Represents turbo-prop-powered airplanes */
		TURBO_PROP("Turbo-Prop");
		
		/** Holds the String to return when {@link #toString()} is called */
		private final String toString;
		
		/** 
		 * Constructs a AirplaneType Enumeration 
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		AirplaneType(String toString) {
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
	
	/** Holds the make of this Airplane */
	protected String make = "unknown";
	
	/** Holds the model of this Airplane */
	protected String model = "unknown";
	
	/** Holds the {@link AirplaneType} of this Airplane */
	protected AirplaneType type;
	
	/** Holds the fuel tank size of this Airplane */
	protected float tankSize = 1.0f;
	
	/** Holds the liters per hour ratio of this Airplane */
	protected float litersPerHour = 1.0f;
	
	/** Holds the cruise speed of this Airplane */
	protected float cruiseSpeed = 1.0f;
	
	/** Holds the range of this Airplane */
	protected float range = 1.0f;
	
	/** Holds the {@link FlightPlan} from which this Airplane was instantiated */
	protected final FlightPlan flightPlan;
	
	/**
	 * Constructs an Airplane object.
	 *
	 * @param make          The make of the airplane
	 * @param model         The model of the airplane
	 * @param type          The type of airplane
	 * @param tankSize      The size of the airplane's fuel tank
	 * @param litersPerHour The liters of fuel the airplane uses per hour
	 * @param cruiseSpeed   The speed at which the airplane cruises
	 * @param flightPlan    The {@link FlightPlan} from which this Airplane was instantiated
	 * @see #setMake(String)
	 * @see #setModel(String)
	 * @see #setType(AirplaneType)
	 * @see #setTankSize(String)
	 * @see #setLitersPerHour(String)
	 * @see #setCruiseSpeed(String)
	 * @throws AirplaneException When any of the parameters is invalid
	 * @throws NullPointerException When any of the parameters is null
	 */
	public Airplane(String make, 
	                String model, 
	                AirplaneType type, 
	                String tankSize, 
	                String litersPerHour, 
	                String cruiseSpeed,
	                FlightPlan flightPlan)
	                throws AirplaneException {
		
		if (flightPlan == null) {
			throw new NullPointerException("flightPlan may not be null");
		}
		
		this.flightPlan = flightPlan;
		
		setMake(make);
		setModel(model);
		setType(type);
		setTankSize(tankSize);
		setLitersPerHour(litersPerHour);
		setCruiseSpeed(cruiseSpeed);
	}
	
	/**
	 * Updates the range of this Airplane according to its tank size, cruise speed,
	 * and liters per hour ratio.<br />
	 * <pre>
	 *                        Kilometers
	 * given 1 Knot =  1.852 ------------
	 *                           Hour
	 * 
	 * let {@link #tankSize} := x Liters
	 *
	 *                                        Kilometer 
	 * let {@link #cruiseSpeed} := n Knots = 1.852 * n -----------
	 *                                          Hour
	 *
	 *                         Liters
	 * let {@link #litersPerHour} := z --------
	 *                          Hour
	 * 
	 *           {@link #tankSize}
	 * {@link #range} = ------------- * {@link #litersPerHour}
	 *          {@link #cruiseSpeed}
	 *
	 *            x Liters
	 *          ------------                Kilometers 
	 *       =      Liters   * (1.852 * n) ------------
	 *           z --------                    Hour
	 *               Hour
	 *
	 *           x                       Kilometers
	 *       =  --- Hours * (1.852 * n) ------------
	 *           z                          Hour
	 *
	 *          1.852 * x * n
	 *       = --------------- Kilometers
	 *                z
	 * </pre>
	 */
	public void updateRange() {
		range = 1.852f * tankSize * cruiseSpeed / litersPerHour;
	}
	
	/**
	 * Returns the Airplane's range
	 *
	 * @return The {@link #range} attribute
	 */
	public float getRange() {
		return range;
	}
	
	/**
	 * Recursively returns a valid {@link Airplane} {@link Airplane#make}
	 *
	 * @param make       The {@link Airplane#make} to compare against the database
	 * @param model      The {@link Airplane#model} to validate against the database
	 * @param airplane   The {@link Airplane} to compare against the database
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} class
	 * @return A {@link Airplane#make} String that does not conflict with any in the database
	 * @throws AirplaneException When the user chooses not to correct an invalid make
	 */
	public static String getValidMake(String make, 
	                                  String model, 
	                                  Airplane airplane, 
	                                  FlightPlan flightPlan) 
	                                  throws AirplaneException {
		
		// Clean the make a little
		make = make.trim().replaceAll("\\s+", " ");
		
		// Get the map of Airplanes by make from flightPlan
		Hashtable<String, Airplane> airplaneMakes = flightPlan.getAirplaneMakes();
		
		// Check if make conflicts with the make of an existing Airplane, which is not this one
		Airplane conflict = airplaneMakes.get(make);
		
		if (conflict != null && conflict.getModel().equals(model) && 
		   (airplane != null ? conflict != airplane : true)) {
			
			System.out.println(conflict.getModel().equals(model));
			String cMake = conflict.getMake();    // Holds the make of the conflicting Airplane
			String cModel = conflict.getModel();  // Holds the model of the conflicting Airplane
			
			// Let the user know there is a conflict, and prompt him to change make
			System.err.printf(
				"\nThe make, %s, conflicts with the existing, %s %s\n", make, cMake, cModel
			);
			
			// Get the user's response
			System.out.print("Would you like to change it? [YES|no] ");
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user responded with "no", throw an Exception and stop the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException(make + " exists in the database.");
			}
				
			// Get the new make
			System.out.print("Enter the new make: ");
			make = FlightPlan.input.nextLine();
			
			// Try to validate the new make
			return getValidMake(make, model, airplane, flightPlan);
		}
		
		// Ensure the length of make is greater than zero, and that it is alpha-numeric
		if (make.length() == 0 || make.matches("^.*[^\\w\\d\\s\\.].*$")) {
			// Let the user know make is invalid, and give him a chance to change it
			System.err.println("\nInvalid make: " + make);
			System.err.println("The make may only characters A-Z, 0-9, periods, and spaces");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user responded with "no", throw a new Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException("Invalid make: " + make);
			}
				
			// Get the new make
			System.out.print("Enter the new make: ");
			make = FlightPlan.input.nextLine();
			
			// Try to validate the new make
			return getValidMake(make, model, airplane, flightPlan);
		}
		
		// If make is valid, return it
		return make;
	}
	
	/**
	 * Recursively returns a valid {@link Airplane} {@link Airplane#model}
	 *
	 * @param make       The {@link Airplane#make} to compare against the database
	 * @param model      The {@link Airplane#model} to validate against the database
	 * @param airplane   The {@link Airplane} to compare against the database
	 * @param flightPlan The current {@link FlightPlan} of the {@link Main} class
	 * @return A {@link Airplane#model} String that does not conflict with any in the database
	 * @throws AirplaneException When the user chooses not to correct an invalid model
	 */
	public static String getValidModel(String make, 
	                                   String model, 
	                                   Airplane airplane, 
	                                   FlightPlan flightPlan) 
	                                   throws AirplaneException {
		
		// Clean the model a little
		model = model.trim().replaceAll("\\s+", " ");
		
		// Get the map of Airplanes by model from flightPlan
		Hashtable<String, Airplane> airplaneModels = flightPlan.getAirplaneModels();
		
		// Check if there exists a conflicting Airplane with the same model, which is not this one
		Airplane conflict = airplaneModels.get(model);
		
		if (conflict != null && conflict.getMake().equals(make) && 
		   (airplane != null ? conflict != airplane : true)) {
			
			String cMake = conflict.getMake();    // Holds the make of the conflicting Airplane
			String cModel = conflict.getModel();  // Holds the model of the conflicting Airplane
			
			// Let the user know there is a conflict, and prompt him to change make
			System.err.printf("\nThe model, %s, conflicts with the existing %s %s\n", model, cMake, cModel);
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user responded with "no", throw an Exception to end the method here
			if (response.equals("^(?i)n(?:o)?$")) {
				throw new AirplaneException(model + " exists in the database.");
			}
				
			// Get the new model
			System.out.print("Enter the new model: ");
			model = FlightPlan.input.nextLine();
			
			// Try to validate the new model
			return getValidModel(make, model, airplane, flightPlan);
		}
		
		// Make sure the length of model is greater than zero and that it is alpha-numeric
		if (model.length() == 0 || model.matches("^.*[^\\w\\d\\s\\.].*$")) {
			// If it is not, let the user know the model is invalid and prompt him to change it
			System.err.println("Invalid model: " + model);
			System.err.println("The model may only contain characters A-Z, 0-9, periods, and spaces");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user responded with "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException("Invalid model: " + model);
			}
			
			// Get the new model
			System.out.print("Enter the new model: ");
			model = FlightPlan.input.nextLine();
			
			// Try to validate the new model
			return getValidModel(make, model, airplane, flightPlan);
		}
		
		// If model is valid, return it
		return model;
	}
	
	/**
	 * Sets the make of this Airplane
	 *
	 * @param make The make of this Airplane
	 * @throws NullPointerException When make is null
	 */
	public void setMake(String make) {
		try {
			// Clean the make a little
			make = make.trim().replaceAll("\\s+", " ");
			
			// Get the map of Airplanes by make from flightPlan
			Hashtable<String, Airplane> airplaneMakes = flightPlan.getAirplaneMakes();
			
			// Remove the reference to the old make from airplaneMakes
			airplaneMakes.remove(this.make);
			
			// Set this make to the new one
			this.make = make;
			
			// Replace the old make in airplaneMakes with the new one
			airplaneMakes.put(this.make, this);
		} catch (NullPointerException exception) {
			throw new NullPointerException("make may not be null");
		}
	}
	
	/**
	 * Returns the make of this Airplane
	 *
	 * @return The {@link #make} of this Airplane
	 */
	public String getMake() {
		return make;
	}
	
	/**
	 * Sets the model of this Airplane
	 *
	 * @param model The model of this Airplane
	 * @throws NullPointerException When model is null
	 */
	public void setModel(String model) {
		try {
			// Clean the model a little
			model = model.trim().replaceAll("\\s+", " ");
			
			// Get the map of Airplanes by model from flightPlan
			Hashtable<String, Airplane> airplaneModels = flightPlan.getAirplaneModels();
			
			// Remove the reference to the old model from airplaneModels
			airplaneModels.remove(this.model);
			
			// Set this model to the new one
			this.model = model;
			
			// Add the reference to the new model in airplaneModels
			airplaneModels.put(this.model, this);
		} catch (NullPointerException exception) {
			throw new NullPointerException("model may not be null");
		}
	}
	
	/**
	 * Returns the model of this Airplane
	 *
	 * @return The {@link #model} of this Airplane
	 */
	public String getModel() {
		return model;
	}
	
	/**
	 * Sets the type of this Airplane
	 *
	 * @param type The {@link AirplaneType} of this Airplane
	 * @throws NullPointerException When type is null
	 */
	public void setType(AirplaneType type) {
		if (type == null) {
			throw new NullPointerException("type may not be null");
		}
		
		this.type = type;
	}
	
	/**
	 * Returns the type of this Airplane
	 *
	 * @return The {@link #type} of this Airplane
	 */
	public AirplaneType getType() {
		return type;
	}
	
	/**
	 * Sets the fuel tank size of this Airplane
	 *
	 * @param tankSize The fuel tank size of this Airplane
	 * @throws AirplaneException When tankSize is not greater than zero
	 * @throws NullPointerException When tankSize is null
	 */
	public void setTankSize(String tankSize) throws AirplaneException {
		try {
			// Try to create a new BigDecimal with the tankSize
			BigDecimal tmpDecimal = new BigDecimal(tankSize);
			
			// Make sure the tankSize is greater than zero and less
			// than the maximum decimal value, to prevent a buffer
			// overflow
			if (tmpDecimal.compareTo(BigDecimal.ZERO) < 1 ||
			    tmpDecimal.compareTo(FlightPlan.MAX_DECIMAL) > 0) {
				
				// Throw a NumberFormatException so it will be caught in
				// the next block
				throw new NumberFormatException();
			}
			
			// If the tankSize is valid, set it as this.tankSize
			this.tankSize = tmpDecimal.floatValue();
			
			// Update range with the new tankSize
			updateRange();
		} catch (NumberFormatException exception) {
			// Let the user know tankSize is invalid, and offer to let him change it
			System.err.println("\nInvalid tank size: " + tankSize);
			System.err.println("The tank size must be greater than zero.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user stated, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException("Invalid tank size: " + tankSize, exception);
			}
			
			// Get the new tankSize
			System.out.print("Enter the new tank size: ");
			tankSize = FlightPlan.input.nextLine();
			
			// Try to set the new tankSize
			setTankSize(tankSize);
		} catch (NullPointerException exception) {
			throw new NullPointerException("tankSize may not be null");
		}
	}
	
	/**
	 * Returns the fuel tank size of this Airplane
	 *
	 * @return The {@link #tankSize} of this Airplane
	 */
	public float getTankSize() {
		return tankSize;
	}
	
	/**
	 * Sets the liters of fuel used per hour ratio of this Airplane
	 *
	 * @param litersPerHour The liters of fuel used per hour ratio for this Airplane
	 * @throws AirplaneException When litersPerHour is not greater than zero
	 * @throws NullPointerException When litersPerHour is null
	 */
	public void setLitersPerHour(String litersPerHour) throws AirplaneException {
		try {
			BigDecimal tmpDecimal = new BigDecimal(litersPerHour);
			
			if (tmpDecimal.compareTo(BigDecimal.ZERO) < 1 ||
			    tmpDecimal.compareTo(FlightPlan.MAX_DECIMAL) > 0) {
				
				throw new NumberFormatException();
			}
			
			this.litersPerHour = tmpDecimal.floatValue();
			updateRange();
		} catch (NumberFormatException exception) {
			System.err.println("\nInvalid liters per hour ratio: " + litersPerHour);
			System.err.println("The liters per hour ratio must be greater than zero.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user entered, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException("Invalid liters per hour ratio: " + litersPerHour, exception);
			}
			
			// Get the new litersPerHour
			System.out.print("Enter the new liters per hour ratio: ");
			litersPerHour = FlightPlan.input.nextLine();
			
			// Try to set the new litersPerHour
			setLitersPerHour(litersPerHour);
		} catch (NullPointerException exception) {
			throw new NullPointerException("litersPerHour may not be null");
		}
	}
	
	/**
	 * Returns the liters of fuel used per hour ratio of this Airplane
	 *
	 * @return The {@link #litersPerHour} ratio of this Airplane
	 */
	public float getLitersPerHour() {
		return litersPerHour;
	}

	/**
	 * Sets the cruise speed of this Airplane
	 *
	 * @param cruiseSpeed The cruise speed to assign this Airplane
	 * @throws AirplaneException When cruiseSpeed is not greater than zero
	 * @throws NullPointerException When cruiseSpeed is null
	 */
	public void setCruiseSpeed(String cruiseSpeed) throws AirplaneException {
		try {
			// Try to create a new BigDecimal with the cruiseSpeed
			BigDecimal tmpDecimal = new BigDecimal(cruiseSpeed);
			
			// Make sure the cruiseSpeed is greater than zero, and less than
			// the maximum float value to prevent a buffer overflow
			if (tmpDecimal.compareTo(BigDecimal.ZERO) < 1 ||
			    tmpDecimal.compareTo(FlightPlan.MAX_DECIMAL) > 0) {
				
				// Throw a NumberFormatException that will be caught
				// in the next block
				throw new NumberFormatException();
			}
			
			// If the cruiseSpeed is valid, set it as this.cruiseSpeed
			this.cruiseSpeed = tmpDecimal.floatValue();
			
			// Update range with the new cruiseSpeed
			updateRange();
		} catch (NumberFormatException exception) {
			// Let the user know the cruiseSpeed is invalid, and prompt him to change it
			System.err.println("\nInvalid cruise speed: " + cruiseSpeed);
			System.err.println("The cruise speed must be greater than zero.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user replied, "no", throw an Exception to stop the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new AirplaneException("Invalid cruise speed: " + cruiseSpeed, exception);
			}
				
			// Get the new cruiseSpeed
			System.out.print("Enter the new cruise speed: ");
			cruiseSpeed = FlightPlan.input.nextLine();
			
			// Try to set the new cruiseSpeed
			setCruiseSpeed(cruiseSpeed);
		} catch (NullPointerException exception) {
			throw new NullPointerException("cruiseSpeed may not be null");
		}
	}

	/**
	 * Returns this Airplane's cruise speed
	 *
	 * @return The {@link #cruiseSpeed} attribute
	 */
	public float getCruiseSpeed() {
		return cruiseSpeed;
	}
	
	/**
	 * Overrides the equals() method in the Object class to compare this Airplane's
	 * {@link #make}, {@link #model}, and {@link #type} with the other
	 *
	 * @param obj The Object to compare against this one
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Airplane) {
			if (((Airplane) obj).getMake().equals(make) &&
			    ((Airplane) obj).getModel().equals(model)) {
				
				// If the make, model, and type of obj is the same
				// as this one, return true
				return true;
			}
		}
		
		// Otherwise, return false
		return false;
	}
	
	/**
	 * Overrides the toString() method in the Object class to display all the attributes of 
	 * this Airplane
	 */
	@Override
	public String toString() {
		return String.format("%s %s\n" +
		                     " => %-16s  %s\n" +
		                     " => %-16s  %s Liters\n" +
		                     " => %-16s  %s Liters per Hour\n" +
		                     " => %-16s  %s Kilometers per Hour\n" +
		                     " => %-16s  %s Kilometers",
		                     make, model,
							 "Type:", type.toString(),
							 "Tank Size:", FlightPlan.formatter.format(tankSize),
							 "Liters per Hour:", FlightPlan.formatter.format(litersPerHour),
							 "Cruise Speed:", FlightPlan.formatter.format(cruiseSpeed),
		                     "Range:", FlightPlan.formatter.format(range));
	}
}
