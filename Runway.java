package edu.usca.acsc492l.flightplanner;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A class to define and construct Runway objects.
 *
 * @author Johnathan Morgan
 * @author Chris Zietlow
 * @author Dylon Edwards
 */
public class Runway {
	
	/** Represents the different types of Runways */
	public static enum RunwayType {
		
		/** Represents Visual runways */
		VISUAL_RUNWAY("Visual Runway"),
		
		/** Represents Non-Precision runways */
		NONPRECISION_RUNWAY("Non-Precision Runway"),
		
		/** Represents Precision runways */
		PRECISION_RUNWAY("Precision Runway");
		
		/** Holds the String to return when {@link #toString()} is called */
		private String toString;
		
		/** Holds the longest length of all the {@link #toString}s */
		private static int greatestToStringLength;
		
		/** 
		 * Constructs a RunwayType Enumeration
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		RunwayType(String toString) {
			setToString(toString);
		}
		
		/**
		 * Sets the String to return when {@link #toString()} is called
		 *
		 * @param toString The String to return when {@link #toString()} is called
		 */
		private void setToString(String toString) {
			this.toString = toString;
			
			// If the length of this toString is greater than the greatest one recorded,
			// set this toString length as the greatest one
			int toStringLength = toString.length();
			if (toStringLength > greatestToStringLength) {
				greatestToStringLength = toStringLength;
			}
		}
		
		/**
		 * Returns the longest length of all the {@link #toString}s
		 *
		 * @return The {@link #greatestToStringLength greatest toString length}
		 */
		public static int greatestToStringLength() {
			return greatestToStringLength;
		}
		
		/**
		 * Overrides the toString() method in the Object class
		 *
		 * @return The {@link #toString} representation of this RunwayType
		 */
		@Override
		public String toString() {
			return toString;
		}
	};

	/** Holds the number of the Runway */
	protected int number;

	/** Holds the length of the Runway */
	protected float length;

	/** Holds the type of the Runway */
	protected RunwayType type;
	
	/**
	 * Constructs a Runway object with the given number, length, and type
	 *
	 * @param number The number to assign this Runway
	 * @param length The length of this Runway
	 * @param type   The {@link RunwayType type} of this Runway
	 * @see #setNumber(String)
	 * @see #setLength(String)
	 * @see #setType(RunwayType)
	 */
	public Runway(String number, String length, RunwayType type) throws RunwayException {
		setNumber(number);
		setLength(length);
		setType(type);
	}

	/**
	 * Sets the number of this Runway
	 *
	 * @param number The number to assign this Runway
	 * @throws NullPointerException When number is null
	 * @throws RunwayException When number is invalid
 	 */
	public void setNumber(String number) throws RunwayException {
		try {
			// Try to create a new BigInteger with number
			BigInteger tmpInteger = new BigInteger(number);
			
			// Make sure number is at least zero and less than the
			// maximum int value to prevent a buffer overflow
			if (tmpInteger.compareTo(BigInteger.ZERO) < 0 ||
			    tmpInteger.compareTo(FlightPlan.MAX_INTEGER) > 0) {
				
				// If it is not, throw a NumberFormatException that
				// will be caught in the next block
				throw new NumberFormatException();
			}
			
			// If number is acceptable, assign it to this.number
			this.number = tmpInteger.intValue();
		} catch (NumberFormatException exception) {
			// Let the user know number is invalid, and prompt him to change it
			System.err.println("\nInvalid runway number: " + number);
			System.err.println("The runway number may not be less than zero.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user specified, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new RunwayException("Invalid runway number: " + number, exception);
			}
				
			// Get the new number
			System.out.print("Enter the new runway number: ");
			number = FlightPlan.input.nextLine();
			
			// Try to set the new number
			setNumber(number);
		} catch (NullPointerException exception) {
			throw new NullPointerException("number may not be null");
		}
	}

	/**
	 * Returns the number of this Runway
	 *
	 * @return The {@link #number} of this Runway
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the length of this Runway
	 *
	 * @param length is the runways's length
	 * @throws RunwayException When length is not greater than zero
	 */
	public void setLength(String length) throws RunwayException {
		try {
			// Try to instantiate a new BigDecimal with length
			BigDecimal tmpDecimal = new BigDecimal(length);
			
			// Make sure length is greater than zero and less than the
			// maximum float value to prevent a buffer overflow
			if (tmpDecimal.compareTo(BigDecimal.ZERO) < 1 ||
			    tmpDecimal.compareTo(FlightPlan.MAX_DECIMAL) > 0) {
			
				// If it is not, throw a NumberFormatException that
				// will be caught in the next block
				throw new NumberFormatException();
			}
			
			// If number is acceptable, set it as this.number
			this.length = tmpDecimal.floatValue();
		} catch (NumberFormatException exception) {
			// Let the user know number is invalid, and prompt him to change it
			System.err.println("\nInvalid runway length: " + length);
			System.err.println("Runway length must be greater than zero.");
			System.out.print("Would you like to change it? [YES|no] ");
			
			// Get the user's response
			String response = FlightPlan.input.nextLine().trim();
			
			// If the user specified, "no", throw an Exception and end the method here
			if (response.matches("^(?i)n(?:o)?$")) {
				throw new RunwayException("Invalid runway length: " + length, exception);
			}
				
			// Get the new length
			System.out.print("Enter the new length: ");
			length = FlightPlan.input.nextLine();
			
			// Try to set the new length
			setLength(length);
		} catch (NullPointerException exception) {
			throw new NullPointerException("length may not be null");
		}
	}

	/**
	 * Returns the length of this Runway
	 *
	 * @return The {@link #length} of this Runway
	 */
	public float getLength() {
		return length;
	}

	/**
	 * Sets the type of this Runway
	 *
	 * @param type The {@link RunwayType} of this Runway
	 * @throws NullPointerException When type is null
	 */
	public void setType(RunwayType type) {
		if (type == null) {
			throw new NullPointerException("type may not be null");
		}
		
		this.type = type;
	}

	/**
	 * Returns the type of this Runway
	 *
	 * @return The {@link RunwayType type} of this Runway
	 */
	public RunwayType getType() {
		return type;
	}
	
	/**
	 * Overrides the toString() method in the Object class
	 *
	 * @return The {@link #number}, {@link #type}, and {@link #length} of this Runway
	 */
	@Override
	public String toString() {
		return String.format("%s\n" +
		                     " => %-7s %d\n" +
		                     " => %-7s %s Meters",
		                     type.toString(),
		                     "Number:", number,
		                     "Length:", FlightPlan.formatter.format(length));
	}
}
