package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Part implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int partId;
	private String name, type;
	private double weight;
	
	private Car car;
	private Pallet pallet;
	private Product product;
	
	public int getPartId() 
	{
		return partId;
	}
	
	public void setPartId(int partId) 
	{
		this.partId = partId;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public void setType(String type) 
	{
		this.type = type;
	}
	
	public double getWeight() 
	{
		return weight;
	}
	
	public void setWeight(double weight) 
	{
		this.weight = weight;
	}
	
	public Car getCar() 
	{
		return car;
	}
	
	public void setCar(Car car) 
	{
		this.car = car;
	}
	
	public Pallet getPallet() 
	{
		return pallet;
	}
	
	public void setPallet(Pallet pallet) 
	{
		this.pallet = pallet;
	}
	
	public Product getProduct() 
	{
		return product;
	}
	
	public void setProduct(Product product) 
	{
		this.product = product;
	}
}
