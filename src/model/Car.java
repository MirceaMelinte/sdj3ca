package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Car implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int carId, year;
	private String chassisNumber, model, manufacturer;
	private double weight;
	
	@XmlTransient
	private boolean isReady, isFinished;
	
	public int getCarId() 
	{
		return carId;
	}
	
	public void setCarId(int carId) 
	{
		this.carId = carId;
	}
	
	@XmlTransient
	public String getManufacturer() 
	{
		return manufacturer;
	}
	
	public void setManufacturer(String manufacturer) 
	{
		this.manufacturer = manufacturer;
	}
	
	@XmlTransient
	public String getModel() 
	{
		return model;
	}
	
	public void setModel(String model) 
	{
		this.model = model;
	}
	
	@XmlTransient
	public int getYear() 
	{
		return year;
	}
	
	public void setYear(int year) 
	{
		this.year = year;
	}
	
	@XmlTransient
	public double getWeight() 
	{
		return weight;
	}
	
	public void setWeight(double weight) 
	{
		this.weight = weight;
	}
	
	@XmlElement
	public String getChassisNumber() 
	{
		return chassisNumber;
	}
	
	public void setChassisNumber(String chassisNumber) 
	{
		this.chassisNumber = chassisNumber;
	}
	
	@XmlTransient
	public boolean isReady() 
	{
		return isReady;
	}
	
	public void setReady(boolean isReady) 
	{
		this.isReady = isReady;
	}
	
	@XmlTransient
	public boolean isFinished() 
	{
		return isFinished;
	}
	
	public void setFinished(boolean isFinished) 
	{
		this.isFinished = isFinished;
	}
}
