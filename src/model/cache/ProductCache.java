package model.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.Product;

public class ProductCache implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<Integer, Product> products = new HashMap<>();

	public void addProduct(Product p) {
		products.put(p.getProductId(), p);
	}

	public Product removeProduct(int ProductId) {
		return products.remove(ProductId);
	}

	public Product getProduct(int ProductId) {
		return products.get(ProductId);
	}

	public Map<Integer, Product> getCache() {
		return products;
	}

	public int count() {
		return products.size();
	}

	public boolean contains(int ProductId) {
		return products.containsKey(ProductId);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		for (Map.Entry<Integer, Product> product : products.entrySet()) {
			s.append(product.getValue().toString() + "\n");
		}

		return s.toString();
	}
}