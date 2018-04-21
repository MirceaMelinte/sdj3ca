/**
 * ICarServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.7  Built on : Nov 20, 2017 (11:41:20 GMT)
 */
package webservice.skeleton;

/**
 *  ICarServiceSkeleton java skeleton for the axisService
 */
public class ICarServiceSkeleton implements ICarServiceSkeletonInterface 
{
private SkeletonSingleton skeleton = SkeletonSingleton.getInstance();
   
   public ICarServiceSkeleton()
   {
   }
   
   public webservice.TraceCarResponse traceCar(webservice.TraceCar traceCar0) 
   {
        return skeleton.traceCar(traceCar0);
   }
}
