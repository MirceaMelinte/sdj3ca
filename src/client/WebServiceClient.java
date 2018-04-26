package client;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import model.Car;
import webservice.stub.ICarServiceStub;
import common.XmlMarshaller;;

public class WebServiceClient
{
   public static void main(String[] args) throws RemoteException, JAXBException
   {
      String URL = "http://localhost:8080/axis2/services/ICarService";
      ICarServiceStub stub = new ICarServiceStub( URL );
      ICarServiceStub.TraceCar traceCar = new ICarServiceStub.TraceCar();
      
      XmlMarshaller<Car> ser = new XmlMarshaller<>();
      Car c = new Car();
      c.setChassisNumber("1JDDM85EABT002212");
      
      String carXml = ser.createXMLString(c, Car.class);
      traceCar.setArgs0(carXml);
      ICarServiceStub.TraceCarResponse res = stub.traceCar(traceCar);
      
      String partListXml = res.get_return();
      
      System.out.println(partListXml);
   } 
}
