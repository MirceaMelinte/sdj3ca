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
public class ProductList implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private List<Product> products = new ArrayList<Product>();
   
   public ProductList() {}
   
   public void addProduct(Product p)
   {
      products.add(p);
   }
   
   public Product removeProduct(int productId)
   {
      for (int i = 0; i < products.size(); i++)
      {
         if(products.get(i).getProductId() == productId)
         {
            return products.remove(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public Product getProduct(int productId)
   {
      for (int i = 0; i < products.size(); i++)
      {
         if(products.get(i).getProductId() == productId)
         {
            return products.get(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public List<Product> getList()
   {
      return products;
   }
   
   public int count()
   {
      return products.size();
   }
   
   public boolean contains(Product product)
   {
      return products.contains(product);
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Product product : products)
      {
         s.append(product.toString() + "\n");
      }
      
      return s.toString();
   }
}