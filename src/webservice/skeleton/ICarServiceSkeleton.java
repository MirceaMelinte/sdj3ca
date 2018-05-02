/**
 * ICarServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.7  Built on : Nov 20, 2017 (11:41:20 GMT)
 */
package webservice.skeleton;

public class ICarServiceSkeleton implements ICarServiceSkeletonInterface {

   private SkeletonSingleton skeleton = SkeletonSingleton.getInstance();
   
   public webservice.TraceStolenProductsResponse traceStolenProducts(
         webservice.TraceStolenProducts traceStolenProducts0) {
         
      return skeleton.traceStolenProducts(traceStolenProducts0);
   }

   public webservice.TraceStolenPartsResponse traceStolenParts(
         webservice.TraceStolenParts traceStolenParts2) {
      
      return skeleton.traceStolenParts(traceStolenParts2);
   }  

   public webservice.TraceStolenCarResponse traceStolenCar(
         webservice.TraceStolenCar traceStolenCar4) {
      
      return skeleton.traceStolenCar(traceStolenCar4);
   }  
}
