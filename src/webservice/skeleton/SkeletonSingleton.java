package webservice.skeleton;

import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import model.Car;
import model.PartList;
import model.ProductList;
import remote.interfaces.ILogicServer;
import webservice.TraceStolenCar;
import webservice.TraceStolenCarResponse;
import webservice.TraceStolenParts;
import webservice.TraceStolenPartsResponse;
import webservice.TraceStolenProducts;
import webservice.TraceStolenProductsResponse;

import common.XmlMarshaller;

public class SkeletonSingleton implements ICarServiceSkeletonInterface
{
   private static volatile SkeletonSingleton skeleton = null;
   private ILogicServer logicServer;
   
   private SkeletonSingleton(){
      begin();
   }

   public static SkeletonSingleton getInstance(){
      if(skeleton == null){
         skeleton = new SkeletonSingleton();
      }
      return skeleton;
   }
   
   public void begin(){
      try {
         String ip = "localhost";
         String URL = "rmi://" + ip + "/" + "logicServer";
         
         logicServer = (ILogicServer) Naming.lookup( URL );
         
         System.out.println("Stolen car items tracking web service connection established");
      } catch( Exception ex ) {
         ex.printStackTrace();
      }
   }

   @Override
   public TraceStolenPartsResponse traceStolenParts(
         TraceStolenParts traceStolenParts)
   {
      TraceStolenPartsResponse res = new TraceStolenPartsResponse();
      String chassisNumber = traceStolenParts.getArgs0();            // Create XML String representation of the Car object
      XmlMarshaller<PartList> serPartList = new XmlMarshaller<>();   // Create XmlMarshaller object of type PartList
      
      try
      {     
         PartList partList = logicServer.validateGetStolenParts(chassisNumber); // Call RMI method and return PartList object
         
         if(partList != null)
         {
            String xmlPartList = serPartList.createXMLString(partList, PartList.class); // Xreate XML string representation of PartList object
            res.set_return(xmlPartList); // Set return value to the web service response object created in step 1
            System.out.println("Trace stolen parts service is called, chassisNumber: " + chassisNumber);
         }
         else
         {
            System.out.println("Trace stolen parts service is called, no results returned");
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
      String chassisNumber = traceStolenProducts.getArgs0();  
      XmlMarshaller<ProductList> serPalletList = new XmlMarshaller<>(); 
      
      try
      {  
         ProductList productList = logicServer.validateGetStolenProducts(chassisNumber); 
         if(productList != null)
         {
            String xmlProductList = serPalletList.createXMLString(productList, ProductList.class); 
            res.set_return(xmlProductList); 
            System.out.println("Trace stolen products service is called, chassisNumber: " + chassisNumber);
         }
         else
         {
            System.out.println("Trace stolen products service is called, no results returned");
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
            System.out.println("Trace stolen car service is called, no results returned");
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
