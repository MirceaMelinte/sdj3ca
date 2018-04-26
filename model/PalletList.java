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
public class PalletList implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private List<Pallet> pallets = new ArrayList<Pallet>();
   
   public PalletList() {}
   
   public void addPallet(Pallet p)
   {
      pallets.add(p);
   }
   
   public Pallet removePallet(int palletId)
   {
      for (int i = 0; i < pallets.size(); i++)
      {
         if(pallets.get(i).getPalletId() == palletId)
         {
            return pallets.remove(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public Pallet getPallet(int palletId)
   {
      for (int i = 0; i < pallets.size(); i++)
      {
         if(pallets.get(i).getPalletId() == palletId)
         {
            return pallets.get(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public List<Pallet> getList()
   {
      return pallets;
   }
   
   public int count()
   {
      return pallets.size();
   }
   
   public boolean contains(Pallet Pallet)
   {
      return pallets.contains(Pallet);
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Pallet pallet : pallets)
      {
         s.append(pallet.toString() + "\n");
      }
      
      return s.toString();
   }
}