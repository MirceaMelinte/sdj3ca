package model.cache;

import java.io.Serializable;

import model.Car;
import model.Pallet;
import model.Part;
import model.Product;
import model.Transaction;

public class Cache implements Serializable {
	private static final long serialVersionUID = 1L;

	private CarCache carCache;
	private PartCache partCache;
	private PalletCache palletCache;
	private ProductCache productCache;

	public Cache() {
		carCache = new CarCache();
		partCache = new PartCache();
		palletCache = new PalletCache();
		productCache = new ProductCache();
	}

	public CarCache getCarCache() { return carCache; }
	public void setCarCache(CarCache carCache) { this.carCache = carCache; }

	public PartCache getPartCache() { return partCache; }
	public void setPartCache(PartCache partCache) { this.partCache = partCache; }

	public PalletCache getPalletCache() { return palletCache; }
	public void setPalletCache(PalletCache palletCache) { this.palletCache = palletCache; }

	public ProductCache getProductCache() { return productCache; }
	public void setProductCache(ProductCache productCache) { this.productCache = productCache; }
	
	public <T> void updateCache(Transaction<T> t) {
   	Car car;
   	Part part;
   	Product product;
   	Pallet pallet;
   	
   	switch(t.getType()) {
   	   
      	case "REGISTER_CAR":
            car = (Car) t.getLoad();
            if(!carCache.contains(car.getChassisNumber())) {
               carCache.addCar(car);
            }  
            break;
         case "REGISTER_PART":
            car = (Car) t.getLoad();
            part = car.getPartList().getList().get(0);
            carCache.getCache().get(car.getChassisNumber()).
               getPartList().addPart(part);
            
            // if the car is AVAILABLE change to IN_PROGRESS
            if (carCache.getCache().get(car.getChassisNumber()).getState().equals(Car.AVAILABLE))
            	carCache.getCache().get(car.getChassisNumber()).setState(Car.IN_PROGRESS);
            partCache.getCache().put(part.getPartId(), part);
            break;
         case "REGISTER_PRODUCT":
            product = (Product) t.getLoad();
            
            Product fullNewProduct = new Product(product.getProductId(), product.getType(), product.getName());
            
            if(!productCache.contains(fullNewProduct.getProductId())) { 
            	productCache.addProduct(fullNewProduct);
            
            	for (Part part2 : product.getPartList().getList()) 
            		fullNewProduct.getPartList().addPart(partCache.getPart(part2.getPartId()));
            }
            break;
         case "REGISTER_PALLET":
            pallet = (Pallet) t.getLoad();
            if(!palletCache.contains(pallet.getPalletId())) {
               palletCache.addPallet(pallet);
            }
            break;
         case "UPDATE_PALLET_PART":
            pallet = (Pallet) t.getLoad();
            part = pallet.getPartList().getList().get(0);
            palletCache.getCache().get(pallet.getPalletId()).getPartList().addPart(part);
            palletCache.getCache().get(pallet.getPalletId()).setWeight(pallet.getWeight());
            if (palletCache.getCache().get(pallet.getPalletId()).getPartType().equals("no type")) 
            	palletCache.getCache().get(pallet.getPalletId()).setPartType(pallet.getPartType());
            break;
         case "UPDATE_PRODUCT_PART":
            product = (Product) t.getLoad();
            part = product.getPartList().getList().get(0);
            palletCache.getCache().get(product.getProductId()).getPartList().addPart(part);
            break;
         case "UPDATE_PALLET_WEIGHT":
            pallet = (Pallet) t.getLoad();
            double newWeight = pallet.getWeight();
            palletCache.getCache().get(pallet.getPalletId()).setWeight(newWeight);
            break;
         case "UPDATE_FINISH_PALLET":
            pallet = (Pallet) t.getLoad();
            palletCache.getCache().get(pallet.getPalletId()).setState(Pallet.FINISHED);
            break;
         case "UPDATE_FINISH_CAR":
            car = (Car) t.getLoad();
            carCache.getCache().get(car.getChassisNumber()).setState(Car.FINISHED);
            break;
      }
	}
}