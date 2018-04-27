package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CarCache implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, Car> cars = new HashMap<>();

	public CarCache() {
	}

	public void addCar(Car c) {
		cars.put(c.getChassisNumber(), c);
	}

	public Car removeCar(String chassisNumber) {
		return cars.remove(chassisNumber);
	}

	public Car getCar(String chassisNumber) {
		return cars.get(chassisNumber);
	}

	public Map<String, Car> getCache() {
		return cars;
	}

	public int count() {
		return cars.size();
	}

	public boolean contains(String chassisNumber) {
		return cars.containsKey(chassisNumber);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		for (Map.Entry<String, Car> car : cars.entrySet()) {
			s.append(car.getValue().toString() + "\n");
		}

		return s.toString();
	}
}