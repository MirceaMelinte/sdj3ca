package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Pallet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int palletId;
	private String partType;
	private double maxWeight;
	private boolean isFinished;
	
	public int getPalletId() 
	{
		return palletId;
	}
	
	public void setPalletId(int palletId) 
	{
		this.palletId = palletId;
	}
	
	@XmlTransient
	public String getPartType() 
	{
		return partType;
	}
	public void setPartType(String partType) 
	{
		this.partType = partType;
	}
	
	@XmlTransient
	public double getMaxWeight() 
	{
		return maxWeight;
	}
	
	public void setMaxWeight(double maxWeight) 
	{
		this.maxWeight = maxWeight;
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
