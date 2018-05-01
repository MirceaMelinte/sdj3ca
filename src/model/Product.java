package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"productId", "type", "name", "partList"})
public class Product implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private int productId;
	private String type, name;
	private PartList partList = new PartList();
	
	public Product() {}
	
	public Product(int productId, String type, String name)
	{
	   this.productId = productId;
	   this.type = type;
	   this.name = name; 
	}
	
	public int getProductId() { return productId; }
	public void setProductId(int productId) {this.productId = productId; }
	
   public String getType() { return type; }
   public void setType(String type) { this.type = type; }
   
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
   public PartList getPartList() { return partList; }
   public void setPartList(PartList partList) { this.partList = partList; }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      s.append("Product id: " + productId);
      s.append(", Product type: " + type);
      s.append(", Product name: " + name);
	  s.append("\n[" + partList.toString() + "]");
		
      return s.toString();
   }
}
