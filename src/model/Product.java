package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Product implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private int productId;
	private String name, type;
	
	public int getProductId() 
	{
		return productId;
	}
	
	public void setProductId(int productId) 
	{
		this.productId = productId;
	}
	
	@XmlTransient
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	@XmlTransient
	public String getType() 
   {
      return type;
   }
   
   public void setType(String type) 
   {
      this.type = type;
   }
}
