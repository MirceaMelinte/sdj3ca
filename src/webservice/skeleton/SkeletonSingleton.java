package webservice.skeleton;

import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import model.Car;
import model.PalletList;
import model.PartList;
import model.ProductList;
import remote.interfaces.ILogicServer;
import webservice.TraceStolenCar;
import webservice.TraceStolenCarResponse;
import webservice.TraceStolenPallets;
import webservice.TraceStolenPalletsResponse;
import webservice.TraceStolenParts;
import webservice.TraceStolenPartsResponse;
import webservice.TraceStolenProducts;
import webservice.TraceStolenProductsResponse;

import common.XmlMarshaller;

public class SkeletonSingleton implements ICarServiceSkeletonInterface
{
   public static SkeletonSingleton skeleton = null;
   private ILogicServer logicServer;
   
   private SkeletonSingleton()
   {
      begin();
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
         
         System.out.println("Stolen car tracking web service connection established");
      } catch( Exception ex ) {
         ex.printStackTrace();
      }
   }

   @Override
   public TraceStolenPartsResponse traceStolenParts(
         TraceStolenParts traceStolenParts)
   {
      TraceStolenPartsResponse res = new TraceStolenPartsResponse();
      
      String xmlCar = traceStolenParts.getArgs0(); // Create XML String representation of the Car object
      
      XmlMarshaller<Car> serCar = new XmlMarshaller<>(); // Create XmlMarshaller object of type Car
      XmlMarshaller<PartList> serPartList = new XmlMarshaller<>(); // Create XmlMarshaller object of type PartList
      
      try
      {
         Car car = serCar.createObjectFromXMLString(xmlCar, Car.class); // Create car object from XML string      
         PartList partList = logicServer.validateGetStolenParts(car); // Call RMI method and return PartList object
         
         if(partList != null)
         {
            String xmlPartList = serPartList.createXMLString(partList, PartList.class); // Xreate XML string representation of PartList object
            res.set_return(xmlPartList); // Set return value to the web service response object created in step 1
            System.out.println("Trace stolen parts service is called, chassisNumber: " + car.getChassisNumber());
         }
         else
         {
            res.set_return(null);
         }         
      }
      catch (JAXBException | RemoteException e)
      {
         e.printStackTrace();
      }
      
      return res; // Return the xml version of the PartList
   }
   
   @Override
   public TraceStolenProductsResponse traceStolenProducts(
         TraceStolenProducts traceStolenProducts)
   {
      TraceStolenProductsResponse res = new TraceStolenProductsResponse();
      
      String xmlCar = traceStolenProducts.getArgs0(); 
      
      XmlMarshaller<Car> serCar = new XmlMarshaller<>(); 
      XmlMarshaller<ProductList> serPalletList = new XmlMarshaller<>(); 
      
      try
      {
         Car car = serCar.createObjectFromXMLString(xmlCar, Car.class);     
         ProductList productList = logicServer.validateGetStolenProducts(car); 
         if(productList != null)
         {
            String xmlProductList = serPalletList.createXMLString(productList, ProductList.class); 
            res.set_return(xmlProductList); 
            System.out.println("Trace stolen products service is called, chassisNumber: " + car.getChassisNumber());
         }
         else
         {
            res.set_return(null);
         }         
      }
      catch (JAXBException | RemoteException e)
      {
         e.printStackTrace();
      }
      
      return res; 
   }

   @Override
   public TraceStolenCarResponse traceStolenCar(TraceStolenCar traceStolenCar)
   {
      TraceStolenCarResponse res = new TraceStolenCarResponse();
      
      String chassisNumber = traceStolenCar.getArgs0(); 
      
      XmlMarshaller<Car> serCar = new XmlMarshaller<>(); 
      
      try
      {     
         Car car = logicServer.validateGetStolenCar(chassisNumber); 
         if(car != null)
         {
            String xmlCar = serCar.createXMLString(car, Car.class); 
            res.set_return(xmlCar); 
            System.out.println("Trace stolen car service is called, chassisNumber: " + car.getChassisNumber());
         }
         else
         {
            res.set_return(null);
         }    
      }
      catch (JAXBException | RemoteException e)
      {
         e.printStackTrace();
      }
      
      return res; 
   }

   @Override
   public TraceStolenPalletsResponse traceStolenPallets(
         TraceStolenPallets traceStolenPallets)
   {
      TraceStolenPalletsResponse res = new TraceStolenPalletsResponse();
      
      String xmlCar = traceStolenPallets.getArgs0(); 
      
      XmlMarshaller<Car> serCar = new XmlMarshaller<>(); 
      XmlMarshaller<PalletList> serPalletList = new XmlMarshaller<>(); 
      
      try
      {
         Car car = serCar.createObjectFromXMLString(xmlCar, Car.class);     
         PalletList palletList = logicServer.validateGetStolenPallets(car); 
         if(palletList != null)
         {
            String xmlPalletList = serPalletList.createXMLString(palletList, PalletList.class); 
            res.set_return(xmlPalletList); 
            System.out.println("Trace stolen pallets service is called, chassisNumber: " + car.getChassisNumber());
         }
         else
         {
            res.set_return(null);
         }         
      }
      catch (JAXBException | RemoteException e)
      {
         e.printStackTrace();
      }
      
      return res; 
   }
}
