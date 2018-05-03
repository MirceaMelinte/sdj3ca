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
	   switch(t.getType()) {
	      
         case "REGISTER":
            if(t.getLoad() instanceof Car) {
               Car car = (Car) t.getLoad();
               if(!carCache.contains(car.getChassisNumber())) {
                  carCache.addCar(car);
               }
            }
            else if (t.getLoad() instanceof Part) {
               Part part = (Part) t.getLoad();
               if(!partCache.contains(part.getPartId())) {
                  partCache.addPart(part);
               }
            }
            else if (t.getLoad() instanceof Pallet) {
               Pallet pallet = (Pallet) t.getLoad();
               if(!palletCache.contains(pallet.getPalletId())) {
                  palletCache.addPallet(pallet);
               }
            }
            else if (t.getLoad() instanceof Product) {
               Product product = (Product) t.getLoad();
               if(!productCache.contains(product.getProductId())) {
                  productCache.addProduct(product);
               }
            }
         case "UPDATE":
            
      }
	}
}