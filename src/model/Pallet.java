package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "palletId", "partType", "weight", "maxWeight", "state", "partList" })
public class Pallet implements Serializable {
	private static final long serialVersionUID = 1L;

	private int palletId;
	private String partType, state;
	private double weight, maxWeight;
	private PartList partList = new PartList();

	public final static String AVAILABLE = "Available";
	public final static String FINISHED = "Finished";

	public Pallet() {
	}

	public Pallet(int palletId, double maxWeight, String state) {
		this.palletId = palletId;
		this.weight = 0;
		this.maxWeight = maxWeight;
		this.state = state;
	}

	public int getPalletId() {
		return palletId;
	}

	public void setPalletId(int palletId) {
		this.palletId = palletId;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public PartList getPartList() {
		return partList;
	}

	public void setPartList(PartList partList) {
		this.partList = partList;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append("Pallet id: " + palletId);
		s.append(", Pallet part type: " + partType);
		s.append(", Pallet weight: " + weight);
		s.append(", Pallet max weight: " + maxWeight);
		s.append(", Pallet state: " + state);
		s.append("\n[" + partList.toString() + "]");

		return s.toString();
	}
}