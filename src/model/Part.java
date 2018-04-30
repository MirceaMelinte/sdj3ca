package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"partId", "palletId", "productId", "type", "weight", "car"})
public class Part implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private int partId, palletId, productId;
	private String type;
	private double weight;
	private String chassisNumber;
	
	public Part() {}
	
	public Part(int partId, String type, double weight, String chassisNumber)
   {
      this.partId = partId;
      this.palletId = -1;
      this.productId = -1;
      this.type = type;
      this.weight = weight;
      this.chassisNumber = chassisNumber;
   } 
	
	public int getPartId() { return partId; }
	public void setPartId(int partId) { this.partId = partId; }
	
   public int getPalletId() { return palletId; }
   public void setPalletId(int palletId) { this.palletId = palletId; }
   
   public int getProductId() { return productId; }
   public void setProductId(int productId) { this.productId = productId; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public double getWeight() { return weight; }
	public void setWeight(double weight) { this.weight = weight; }	
	
	public String getChassisNumber() { return chassisNumber; }
	public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

	@Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      s.append("Part id: " + partId);
      s.append(", Pallet id: " + palletId);
      s.append(", Product id: " + productId);
      s.append(", Part type: " + type);
      s.append(", Weight: " + weight);
      s.append(", Chassis Number: " + chassisNumber);
      
      return s.toString();
   }
}
