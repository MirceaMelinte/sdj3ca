package webservice;

public interface ICarService
{
   String traceStolenParts(String carXml);
   String traceStolenProducts(String carXml);
   String traceStolenPallets(String carXml);
   String traceStolenCar(String chassisNumber);
}
