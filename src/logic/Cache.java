package logic;

import java.io.Serializable;

import model.CarCache;
import model.PalletCache;
import model.PartCache;
import model.ProductCache;

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

	public CarCache getCarCache() {
		return carCache;
	}

	public void setCarCache(CarCache carCache) {
		this.carCache = carCache;
	}

	public PartCache getPartCache() {
		return partCache;
	}

	public void setPartCache(PartCache partCache) {
		this.partCache = partCache;
	}

	public PalletCache getPalletCache() {
		return palletCache;
	}

	public void setPalletCache(PalletCache palletCache) {
		this.palletCache = palletCache;
	}

	public ProductCache getProductCache() {
		return productCache;
	}

	public void setProductCache(ProductCache productCache) {
		this.productCache = productCache;
	}
}