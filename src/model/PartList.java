package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PartList implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   @XmlElement(name="part")
   private List<Part> parts;
   
   public PartList()
   {
      parts = new ArrayList<Part>();
   }
   
   public void addPart(Part p)
   {
      parts.add(p);
   }
   
   public Part removePart(int partId)
   {
      for (int i = 0; i < parts.size(); i++)
      {
         if(parts.get(i).getPartId() == partId)
         {
            return parts.remove(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public Part getPart(int partId)
   {
      for (int i = 0; i < parts.size(); i++)
      {
         if(parts.get(i).getPartId() == partId)
         {
            return parts.get(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public List<Part> getList()
   {
      return parts;
   }
   
   public int count()
   {
      return parts.size();
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Part part : parts)
      {
         s.append("Part id: " + part.getPartId());
         s.append(", Name: " + part.getName());
         s.append(", Type: " + part.getType());
         s.append(", Weight: " + part.getWeight());
         
         if(!(part.getCar() == null))
         {
            s.append(", Car id: " + part.getCar().getCarId());
         }
         
         else
         {
            s.append(", Car id: NOT ASSIGNED");
         }
         
         if(!(part.getPallet() == null))
         {
            s.append(", Pallet id: " + part.getPallet().getPalletId());
         }
         
         else
         {
            s.append(", Pallet id: NOT ASSIGNED");
         }
         
         if(!(part.getProduct() == null))
         {
            s.append(", Product id: " + part.getProduct().getProductId());
         }
         
         else
         {
            s.append(", Product id: NOT ASSIGNED");
         }
         
         s.append(System.getProperty("line.separator"));
         s.append(System.getProperty("line.separator"));
      }
      
      return s.toString();
   }
}
