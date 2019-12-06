package com.jacob.rootinsurance.takehomeassignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class App 
{
	public static void main( String[] args ) throws ParseException, Exception {
		String fileName = "";
		if (args.length > 0) {
			fileName = args[0];
		}
		File inputFile = new File(fileName);
		Scanner input = new Scanner(inputFile);	

		SimpleDateFormat hoursMinutes = new SimpleDateFormat("HH:mm");
		List<Driver> listOfDrivers = new ArrayList<Driver>();
		// Read the file
		while (input.hasNext()) {

			String[] nextRow = input.nextLine().split(" ");

			// each Driver just registers a new Driver and adds to List
			if (nextRow[0].equals("Driver")) {
				String driverName = nextRow[1];
				listOfDrivers.add(new Driver(driverName));
			}
			/* Each new Trip should do the following: 
			 * Populate new Trip object (time math and mph calculation incl.)
			 * Check: mph >= 5, mph <= 100
			 * Assign the trip to the driver
			 */
			if (nextRow[0].equals("Trip")) {
				Trip newTrip = new Trip(nextRow[1]);
				newTrip.startTime = hoursMinutes.parse(nextRow[2]);
				newTrip.endTime = hoursMinutes.parse(nextRow[3]);
				newTrip.distanceInMiles = Double.parseDouble(nextRow[4]);

				// if trip is valid, add its values to appropriate Driver
				if (newTrip.checkTime()) {
					for (Driver entry : listOfDrivers) {
						if (entry.name.equals(newTrip.driverName)) {
							entry.totalDistance += newTrip.distanceInMiles;
							entry.totalTime += (newTrip.endTime.getTime() - newTrip.startTime.getTime());
							entry.updateSpeed();
						}
					}
				}

			}
		}	 // <-- end of processing Input



		File summaryFile = new File("tripReport.txt");
		try{summaryFile.createNewFile();}
		catch (IOException e){System.out.println("There was an error creating your file");
		}
		
		PrintWriter theOutput = new PrintWriter(summaryFile);
		App.printOutReport(listOfDrivers, theOutput);
		System.out.println("Your report has been created. Thank you and safe traveling!");

		
	}


	static class Driver {
		public String name;
		public double totalDistance;
		public double totalTime;
		public double speedInMph;

		public Driver(String name) {
			this.name = name;
			this.totalDistance = 0;
			this.totalTime = 0;
			this.speedInMph = 0;
		}

		public void updateSpeed() {
			double newSpeed = this.totalDistance / (this.totalTime / 60);
			speedInMph = newSpeed;
		}
	}

	static class Trip {
		public String driverName;
		public Date startTime;
		public Date endTime;
		public double distanceInMiles;

		public Trip(String driver) {
			this.driverName = driver;
		}

		public boolean checkTime() {
			double travelTime = this.startTime.getTime() - this.endTime.getTime();
			double testSpeed = (this.distanceInMiles / (travelTime/60));
			return (testSpeed >= 5 && testSpeed <= 100);
		}

	}
	
	static void cleanUpNumberValues(List<Driver> listOfDrivers) {
		for (Driver entry : listOfDrivers) {
			entry.speedInMph = Math.round(entry.speedInMph);
			entry.totalDistance = Math.round(entry.totalDistance);
		}
	}
	
	static void printOutReport(List<Driver> listOfDrivers, PrintWriter theOutput) {
		App.cleanUpNumberValues(listOfDrivers);
		for (Driver entry : listOfDrivers) {
			String thisLineOut = entry.name + ": " + entry.totalDistance 
					+ " miles traveled @" + entry.speedInMph + " mph";
			theOutput.println(thisLineOut);
		}
	}
}