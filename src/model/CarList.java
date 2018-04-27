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
public class CarList implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private List<Car> cars = new ArrayList<Car>();
   
   public CarList() {}
   
   public void addCar(Car p)
   {
      cars.add(p);
   }
   
   public Car removeCar(String chassisNumber)
   {
      for (int i = 0; i < cars.size(); i++)
      {
         if(cars.get(i).getChassisNumber() == chassisNumber)
         {
            return cars.remove(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public Car getCar(String chassisNumber)
   {
      for (int i = 0; i < cars.size(); i++)
      {
         if(cars.get(i).getChassisNumber() == chassisNumber)
         {
            return cars.get(i);
         }
      }
      
      throw new InputMismatchException();
   }
   
   public List<Car> getList()
   {
      return cars;
   }
   
   public int count()
   {
      return cars.size();
   }
   
   public boolean contains(Car car)
   {
      return cars.contains(car);
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      
      for (Car car : cars)
      {
         s.append(car.toString() + "\n");
      }
      
      return s.toString();
   }
}