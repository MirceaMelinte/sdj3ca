package client;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import model.Car;
import webservice.stub.ICarServiceStub;
import common.Serialization;

public class WebServiceClient
{
   public static void main(String[] args) throws RemoteException, JAXBException
   {
      String URL = "http://localhost:8080/axis2/services/ICarService";
      ICarServiceStub stub = new ICarServiceStub( URL );
      ICarServiceStub.TraceCar traceCar = new ICarServiceStub.TraceCar();
      
      Serialization<Car> ser = new Serialization<>();
      Car c = new Car();
      c.setCarId(1);
      
      String carXml = ser.createXMLString(c, Car.class);
      traceCar.setArgs0(carXml);
      ICarServiceStub.TraceCarResponse res = stub.traceCar(traceCar);
      
      String partListXml = res.get_return();
      
      System.out.println(partListXml);
   } 
}
