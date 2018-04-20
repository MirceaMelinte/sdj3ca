package common;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import model.Car;

public class Serialization<T>
{
   @SuppressWarnings("unchecked")
   public T createObjectFromXMLString(String xml) throws JAXBException
   {
      JAXBContext jaxbContext = JAXBContext.newInstance(Car.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      StringReader reader = new StringReader(xml);
      
      return (T)jaxbUnmarshaller.unmarshal(reader);
   }   
   
   public String createXMLString(T obj, Class<?> cls) throws JAXBException
   {
      JAXBContext jaxbContext = JAXBContext.newInstance(cls);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);       
      StringWriter sw = new StringWriter();
      jaxbMarshaller.marshal(obj, sw);
      String xmlString = sw.toString();    
         
      return xmlString;
   }   
}
