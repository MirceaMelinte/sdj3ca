package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CarCache implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private Map<String, Car> cars = new HashMap<>();
   
   public CarCache() {}
   
   public void addCar(Car c)
   {
      cars.put(c.getChassisNumber(), c);
   }
   
   public Car removeCar(String chassisNumber)
   {
      return cars.remove(chassisNumber);
   }
   
   public Car getCar(String chassisNumber)
   {
      return cars.get(chassisNumber);
   }
   
   public Map<String, Car> getCache()
   {
      return cars;
   }
   
   public int count()
   {
      return cars.size();
   }
   
   public boolean contains(String chassisNumber)
   {
      return cars.containsKey(chassisNumber);
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Map.Entry<String, Car> car : cars.entrySet())
      {
         s.append(car.getValue().toString() + "\n");
      }
      
      return s.toString();
   }
   
   public static void main(String[] args)
   {
      Car c1 = new Car("AX2334", "X5", "BMW", 2013, 2431.3, "In progress");
      Car c2 = new Car("AD3834", "TT", "AUDI", 2014, 2122.2, "In progress");
      Car c3 = new Car("AK4344", "X3", "BMW", 2012, 2411.2, "In progress");
      
      CarCache carCache = new CarCache();
      
      carCache.getCache().put(c1.getChassisNumber(), c1);
      carCache.getCache().put(c2.getChassisNumber(), c2);
      carCache.getCache().put(c3.getChassisNumber(), c3);
      
      System.out.println(carCache.toString());
   }
}
