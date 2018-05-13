package webservice.test;

import java.rmi.RemoteException;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import model.Car;
import model.PartList;
import model.ProductList;
import webservice.stub.ICarServiceStub;
import common.XmlMarshaller;

public class WebServiceTest
{
   private static Scanner in = new Scanner(System.in);
   private static String URL = "http://localhost:8080/axis2/services/ICarService";
   private static ICarServiceStub stub;
   private static ICarServiceStub.TraceStolenParts traceStolenParts = new ICarServiceStub.TraceStolenParts();
   private static ICarServiceStub.TraceStolenProducts traceStolenProducts = new ICarServiceStub.TraceStolenProducts();
   private static ICarServiceStub.TraceStolenCar traceStolenCar = new ICarServiceStub.TraceStolenCar();
   
   private static XmlMarshaller<PartList> serPartList = new XmlMarshaller<>();
   private static XmlMarshaller<ProductList> serProductList = new XmlMarshaller<>();
   private static XmlMarshaller<Car> serCar = new XmlMarshaller<>();
   
   private static String chassisNumber;
   
   public static void main(String[] args) throws RemoteException, JAXBException
   {
      stub = new ICarServiceStub( URL );  
      System.out.println("Enter car chassis number: ");
      chassisNumber = in.nextLine();
      start(stub);
   } 
   
   public static void start(ICarServiceStub stub) throws RemoteException, JAXBException {
      int menuSelection;
      do {
         menuSelection = menu();
         switch (menuSelection) {
         case 1:
            traceStolenParts.setArgs0(chassisNumber);
            ICarServiceStub.TraceStolenPartsResponse res = stub.traceStolenParts(traceStolenParts);
            String partListXml = res.get_return();
            if(partListXml != null)
            {
               PartList partList = serPartList.createObjectFromXMLString(partListXml, PartList.class);
               System.out.println("XML Version of PartList: \n");
               System.out.println("=================================================================");
               System.out.println(partListXml);
               System.out.println("=================================================================");
               System.out.println("toString() Version of PartList: \n");
               System.out.println(partList.toString());
               System.out.println("=================================================================");
            }
            else
            {
               System.out.println("Invalid car chassis number");
            }
            break;
         case 2:
            traceStolenProducts.setArgs0(chassisNumber);
            ICarServiceStub.TraceStolenProductsResponse res2 = stub.traceStolenProducts(traceStolenProducts);
            String productListXml = res2.get_return();
            if(productListXml != null)
            {
               ProductList productList = serProductList.createObjectFromXMLString(productListXml, ProductList.class);
               System.out.println("XML Version of ProductList: \n");
               System.out.println("=================================================================");
               System.out.println(productListXml);
               System.out.println("=================================================================");
               System.out.println("toString() Version of ProductList: \n");
               System.out.println(productList.toString());
               System.out.println("=================================================================");
            }
            else
            {
               System.out.println("Invalid car chassis number");
            }
            break;
         case 3:
            traceStolenCar.setArgs0(chassisNumber);
            ICarServiceStub.TraceStolenCarResponse res3 = stub.traceStolenCar(traceStolenCar);
            String carDetailstXml = res3.get_return();
            Car carDetails = serCar.createObjectFromXMLString(carDetailstXml, Car.class);
            if(carDetails != null)
            {
               System.out.println("XML Version of ProductList: \n");
               System.out.println("=================================================================");
               System.out.println(carDetailstXml);
               System.out.println("=================================================================");
               System.out.println("toString() Version of ProductList: \n");
               System.out.println(carDetails.toString());
               System.out.println("=================================================================");
            }
            else
            {
               System.out.println("Invalid car chassis number");
            }
            break;
         case 4:
            break;
         default:
            System.out.println("Invalid input");
            break;
         }
         System.out.println("\nPress ENTER to continue...");
         in.nextLine();
      } while (menuSelection != 4);
      
   }
   
   private static int menu() {
      System.out.println("Stolen items tracking console");
      System.out.println("-----------------------------");
      System.out.println("1) Trace stolen parts");
      System.out.println("2) Trace stolen products");
      System.out.println("3) Trace stolen car");
      System.out.println("4) Quit");
      System.out.println();
      System.out.print("Select an item 1-4: ");
      int selection = in.nextInt();
      in.nextLine();
      return selection;
   }
}