package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "chassisNumber", "manufacturer", "model", "year", "weight", "state" })
public class Car implements Serializable {

	private static final long serialVersionUID = 1L;

	private String chassisNumber, manufacturer, model, state;
	private int year;
	private double weight;
	
	@XmlTransient
	private PartList parts = new PartList();

	public final static String AVAILABLE = "Available";
	public final static String IN_PROGRESS = "In progress";
	public final static String FINISHED = "Finished";

	public Car() {}

	public Car(String chassisNumber, String manufacturer, String model, int year, double weight, String state) {
		this.chassisNumber = chassisNumber;
		this.manufacturer = manufacturer;
		this.model = model;
		this.year = year;
		this.weight = weight;
		this.state = state;
	}

	public String getChassisNumber() { return chassisNumber; }
	public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
	
	public String getManufacturer() { return manufacturer; }
	public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }

	public int getYear() { return year; }
	public void setYear(int year) { this.year = year; }

	public double getWeight() { return weight; }
	public void setWeight(double weight) { this.weight = weight; }

	public String getState() { return state; }
	public void setState(String state) { this.state = state; }
	
	public PartList getPartList() { return parts; }
	public void setPartList(PartList parts) { this.parts = parts; }

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append("Chassis number: " + chassisNumber);
		s.append(", Manufacturer: " + manufacturer);
		s.append(", Model: " + model);
		s.append(", Year: " + year);
		s.append(", Weight: " + weight);
		s.append(", State: " + state);
		s.append("\n[" + parts.toString() + "]");

		return s.toString();
	}
}
