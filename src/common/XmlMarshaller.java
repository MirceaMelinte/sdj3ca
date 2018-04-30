package common;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Generic helper class for XML/Object and Object/XML marshalling
//Methods are generic so they can be reused on any type of objects
//in the system as long as a XmlMarshaller object is of a corresponging
//class of the object to be serialized

public class XmlMarshaller<T>
{
   @SuppressWarnings("unchecked")
   public T createObjectFromXMLString(String xml, Class<?> cls) throws JAXBException
   {
      if(!(xml == (null)))
      {
         JAXBContext jaxbContext = JAXBContext.newInstance(cls);
         Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
         StringReader reader = new StringReader(xml);
         
         return (T)jaxbUnmarshaller.unmarshal(reader);
      }
      return null;
   }   
   
   // First parameter is the object to be marshalled and the second parameter
   // is the class type of the object to be marshalled e.g. PartList.class
   public String createXMLString(T obj, Class<?> cls) throws JAXBException
   {
      if(!(obj == (null)))
      {
         JAXBContext jaxbContext = JAXBContext.newInstance(cls);
         Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
         jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);       
         StringWriter sw = new StringWriter();
         jaxbMarshaller.marshal(obj, sw);
         String xmlString = sw.toString();    
            
         return xmlString;
      }
      
      return null;
   }   
}
