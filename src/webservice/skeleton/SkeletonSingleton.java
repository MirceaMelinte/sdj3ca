package webservice.skeleton;

import interfaces.ILogicServer;

import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import model.Car;
import model.PartList;
import webservice.TraceCar;
import webservice.TraceCarResponse;

import common.XmlMarshaller;

public class SkeletonSingleton implements ICarServiceSkeletonInterface
{
   public static SkeletonSingleton skeleton = null;
   private ILogicServer logicServer;
   
   private SkeletonSingleton()
   {
      begin();
   }

   @Override
   public TraceCarResponse traceCar(TraceCar traceCar)
   {  
      TraceCarResponse res = new TraceCarResponse(); // Create web service response object
      
      String xmlCar = traceCar.getArgs0(); // Create XML String representation of the Car object
      
      XmlMarshaller<Car> serCar = new XmlMarshaller<>(); // Create XmlMarshaller object of type Car
      XmlMarshaller<PartList> serPartList = new XmlMarshaller<>(); // Create XmlMarshaller object of type PartList
      
      try
      {
         Car car = serCar.createObjectFromXMLString(xmlCar, Car.class); // Create car object from XML string      
         PartList partList = logicServer.validateGetStolenParts(car); // Call RMI method and return PartList object
         String xmlPartList = serPartList.createXMLString(partList, PartList.class); // Xreate XML string representation of PartList object
         res.set_return(xmlPartList); // Set return value to the web service response object created in step 1
      }
      catch (JAXBException | RemoteException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return res; // Return the xml version of the PartList
   }

   public static SkeletonSingleton getInstance()
   {
      if(skeleton == null)
      {
         skeleton = new SkeletonSingleton();
      }
      return skeleton;
   }
   
   public void begin()
   {
      try {
         String ip = "localhost";
         String URL = "rmi://" + ip + "/" + "logicServer";
         
         logicServer = (ILogicServer) Naming.lookup( URL );
         
         System.out.println("Car Client connection established");
      } catch( Exception ex ) {
         ex.printStackTrace();
      }
   }
}
