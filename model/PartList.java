package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PartList implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private List<Part> parts = new ArrayList<Part>();
   
   public PartList() {}
   
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
   
   public boolean contains(Part part)
   {
      return parts.contains(part);
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Part part : parts)
      {
         s.append(part.toString() + "\n");
      }
      
      return s.toString();
   }
}
