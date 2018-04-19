package model;

import java.io.Serializable;

public class Part implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String type;
	private double weight;
	
	
	private Car car;
	private Pallet pallet;
	private Package packageField; // package is keyword
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Pallet getPallet() {
		return pallet;
	}
	public void setPallet(Pallet pallet) {
		this.pallet = pallet;
	}
	public Package getPackage() {
		return packageField;
	}
	public void setPackage(Package package_field) {
		this.packageField = package_field;
	}
	
	
	
	
}
