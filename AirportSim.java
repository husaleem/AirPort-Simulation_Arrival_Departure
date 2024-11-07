import java.util.Scanner;
import java.util.ArrayList;

public class AirportSim{
	public static void main(String[] args) {
		Scanner x = new Scanner(System.in);
		int endTime, landingSize, arrivingPlane = 0, departingPlane = 0, flightN = 0;
		double arrivalRate, departureRate;
		
		System.out.println("Please enter an end time: ");
		endTime = x.nextInt();
		System.out.println("Please enter the landing size: ");
		landingSize = x.nextInt();
		System.out.println("Please enter an arrival rate: ");
		arrivalRate = x.nextDouble();
		System.out.println("Please enter a departure rate: ");
		departureRate = x.nextDouble();
		
		if ((arrivalRate + departureRate) > 1) {
			System.out.println("The sum of \"arrival rate\" and \"departure rate\"" + " cannot be more than 1.");
			x.close();
			return;
			
		}
		
		Runway runway = new Runway(landingSize);
		
		
		for(int i = 0; i <= endTime; i++) {
			arrivingPlane = poisson(arrivalRate);
			departingPlane = poisson(departureRate);
			
			for(int y = 1; y <= arrivingPlane; y++) {
				runway.addPlane(new Plane(++flightN, i, 1));
			}
			
			for(int y = 1; y<= departingPlane; y++) {
				runway.addTakeoff(new Plane(++flightN, i, 2));
			}
			
			if((runway.landing.size()) != 0) runway.activityLand(i);
			
			else if(runway.takeoff.size() !=0) runway.activityTakeoff(i);
			
			else runway.runIdle(i);
			x.close();
		}
		
		System.out.println("\n---------------------------------------------\n");
		
		runway.shutdown(endTime);
	}
	
	
	public static int poisson(double rate) {
		double limit = Math.exp(-rate);
		double product = Math.random();
		int count = 0;
		while (product > limit) {
			count++;
			product *= Math.random();
		}
		return (int)count;
	}
}
class Plane{
	
	int flightNumber, clockStart, Status;
	
	Plane(){
		flightNumber = 0;
		clockStart = 0;
		Status = 0;
	}
	
	Plane(int fltNumber, int time, int status){
		this.flightNumber = fltNumber;
		this.clockStart = time;
		this.Status = status;
	}
	
	void refuse() {
		System.out.println("Flight " + flightNumber + ": directed to another airport.");
	}
	
	void land(int time) {
		System.out.println("Flight " + flightNumber + ": landed at " + time + " minutes and waited " + (time - clockStart) + "minutes");
	}
	
	void fly(int time) {
		System.out.println("Flight " + flightNumber + ": took off at " + time + " minutes and waited " + (time - clockStart) +"minutes");
	}
	
	int started() {
		return clockStart;
	}
	
}

//import java.util.ArrayList;

class Runway{
	
	ArrayList<Plane> landing;
	ArrayList<Plane> takeoff;
	int listLimit = 0, requestedLanding = 0, requestedTakeOff = 0, redirected = 0, takeOff= 0, landed = 0,
		waitLanding = 0, waitTakeOff = 0, idleTime = 0;
	
	Runway(int limit){
		this.listLimit = limit;
		landing = new ArrayList<Plane>();
		takeoff = new ArrayList<Plane>();
	}
	
	boolean canLand(Plane current) {
		if(landing.size() <= listLimit) return true;
		return false;
	}
	void addPlane(Plane current) {
		if (canLand(current)) {
			landing.add(current);
			requestedLanding++;
		}
		else {
			current.refuse();
			redirected++;
		}
	}
	
	void addTakeoff(Plane current) {
		takeoff.add(current);
		requestedTakeOff++;
	}
	
	void activityLand(int time) {
		landing.get(0).land(time);
		waitLanding += (time - (landing.get(0)).started());
		landing.remove(0);
		landed++;
	}
	
	void activityTakeoff(int time) {
		takeoff.get(0).fly(time);
		waitTakeOff += (time - takeoff.get(0).started());
		takeoff.remove(0);
		takeOff++;
	}
	
	void runIdle(int time) {
		idleTime++;
		System.out.println("Runway is idle.");
	}
	
	void shutdown(int time) {
		
		redirected = redirected + landing.size();
		for(int i= 0; i < landing.size(); i++) {
			landing.remove(0);
		}
		
		
		double requestedTimeL = (requestedLanding/(double)time) * 100;
		
		System.out.println("Redirected planes: " + redirected);
		System.out.println("Requested landing: " + requestedLanding);
		System.out.println("Accepted requests: " + (requestedLanding - redirected));
		System.out.println("Successful landing: " + landed);
		System.out.println("Amount of planes served: " + (landed + takeOff));
		System.out.println("The average rate of planes wanting to land: " + String.format("%.1f", requestedTimeL));
		System.out.println("The average rate of planes wanting to takeoff: " + (requestedTakeOff/(double)time) * 100 + "%");
		System.out.println("The average wait in the landing line: " + String.format("%.1f", waitLanding/(double)landed) + " minutes");
		System.out.println("The average wait in the takeoff line: " + String.format("%.1f",waitTakeOff/(double)takeOff) + " minutes");
		System.out.println("Percentage of runway staying idle is about: " + (idleTime/(double)time) * 100 + "%");
	}
}
