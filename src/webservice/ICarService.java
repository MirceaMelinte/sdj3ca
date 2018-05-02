package webservice;

public interface ICarService
{
   String traceStolenParts(String chassisNumber);
   String traceStolenProducts(String chassisNumber);
   String traceStolenCar(String chassisNumber);
}
