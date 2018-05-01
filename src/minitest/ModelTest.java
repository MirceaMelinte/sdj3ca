package minitest;

import model.Car;
import model.CarList;
import model.Pallet;
import model.PalletList;
import model.Part;
import model.PartList;
import model.Product;
import model.ProductList;

public class ModelTest {

	public static void main(String[] args) {

		// new objects
		
		Car car1 = new Car("1234567a", "bmw", "100", 2011, 3500, Car.AVAILABLE);
		Car car2 = new Car("1234567b", "mercedes", "200", 2014, 3400, Car.FINISHED);
		Car car3 = new Car("1234567c", "porshe", "300", 2001, 2400, Car.IN_PROGRESS);
		
		Pallet pallet1 = new Pallet(2222, 1000, Pallet.AVAILABLE);
		pallet1.setPartType("wheel");
		Pallet pallet2 = new Pallet(3333, 1000, Pallet.AVAILABLE);
		pallet2.setPartType("hood");
		Pallet pallet3 = new Pallet(4444, 1000, Pallet.FINISHED);
		pallet3.setPartType("wheel");
		
		Part part1 = new Part(11110001, "wheel", 3);
		Part part2 = new Part(11110002, "wheel", 3);
		Part part3 = new Part(11110003, "wheel", 3);
		
		Product product1 = new Product(99990001, "wheel set", "premium winter set");
		

		
		car1.getPartList().addPart(part1);
		car3.getPartList().addPart(part2);
		car3.getPartList().addPart(part3);
		
		pallet1.getPartList().addPart(part1);
		pallet3.getPartList().addPart(part2);
		pallet3.getPartList().addPart(part3);
		
		product1.getPartList().addPart(part2);
		product1.getPartList().addPart(part3);
		
		

		
		CarList carList = new CarList();
		carList.addCar(car1);
		carList.addCar(car2);
		carList.addCar(car3);

		PalletList palletList = new PalletList();
		palletList.addPallet(pallet1);
		palletList.addPallet(pallet2);
		palletList.addPallet(pallet3);
		
		PartList partList = new PartList();
		partList.addPart(part1);
		partList.addPart(part2);
		partList.addPart(part3);	
		
		ProductList productList = new ProductList();
		productList.addProduct(product1);
		


		
		System.out.println(partList);
		System.out.println(carList);
		System.out.println(palletList);
		System.out.println(productList);
		
		part3.setType("NEW TYPE");
		
		System.out.println("\nAfter a change\n");
		
		System.out.println(partList);
		System.out.println(carList);
		System.out.println(palletList);
		System.out.println(productList);
		
		

		
	}

}
