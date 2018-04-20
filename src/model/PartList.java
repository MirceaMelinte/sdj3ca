package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import common.Serialization;

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
   
//   // Small test
//   public static void main(String[] args) throws JAXBException
//   {
//      Part p = new Part();
//      Part p2 = new Part();
//      
//      p.setPartId(123);
//      p.setName("Steering wheel");
//      p.setType("Unit");
//      p.setWeight(3.4);
//      
//      p2.setPartId(456);
//      p2.setName("Engine");
//      p2.setType("Unit");
//      p2.setWeight(113.4);
//      
//      Product pr = new Product();
//      Pallet pl = new Pallet();
//      Car c = new Car();
//      
//     
//     
//      pr.setProductId(23232);
//      pl.setPalletId(22222);
//      c.setCarId(564646);
//      c.setChassisNumber("AX2323JA");
//      
////      pr.setName("Steering system");
////      pr.setType("bundle");
//      
//      p.setProduct(pr);
//      p.setPallet(pl);
//      p.setCar(c);
//      
//      p2.setProduct(pr);
//      p2.setPallet(pl);
//      p2.setCar(c);
//      
//      PartList l = new PartList();
//      
//      l.addPart(p);
//      l.addPart(p2);
//      
//      //System.out.println(l.toString());
//      
//      //Serialization<PartList> sr = new Serialization<>();
//      
//      //System.out.println(sr.createXMLString(l, PartList.class));
//      //System.out.println();
//      
//      Serialization<Car> sr2 = new Serialization<>();
//      
//      System.out.println(sr2.createXMLString(c, Car.class));
//      
//      Car c2 = sr2.createObjectFromXMLString(sr2.createXMLString(c, Car.class));
//      
//      System.out.println();
//      
//      System.out.println(c2.getCarId() + ", " + c2.getChassisNumber());
//   }
}
