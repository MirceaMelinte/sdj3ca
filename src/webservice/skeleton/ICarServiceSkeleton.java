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
public class ICarServiceSkeleton implements ICarServiceSkeletonInterface {
   
    private SkeletonSingleton skeleton = SkeletonSingleton.getInstance();
    
    /**
     * Auto generated method signature
     *
     * @param traceStolenProducts0
     * @return traceStolenProductsResponse1
     */
    public webservice.TraceStolenProductsResponse traceStolenProducts(
        webservice.TraceStolenProducts traceStolenProducts0) {
        
       return skeleton.traceStolenProducts(traceStolenProducts0);
    }

    /**
     * Auto generated method signature
     *
     * @param traceStolenParts2
     * @return traceStolenPartsResponse3
     */
    public webservice.TraceStolenPartsResponse traceStolenParts(
        webservice.TraceStolenParts traceStolenParts2) {
        
       return skeleton.traceStolenParts(traceStolenParts2);
    }

    /**
     * Auto generated method signature
     *
     * @param traceStolenCar4
     * @return traceStolenCarResponse5
     */
    public webservice.TraceStolenCarResponse traceStolenCar(
        webservice.TraceStolenCar traceStolenCar4) {
       
        return skeleton.traceStolenCar(traceStolenCar4);
    }
}
