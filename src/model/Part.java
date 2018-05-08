package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "partId", "type", "weight" })
public class Part implements Serializable {
	private static final long serialVersionUID = 1L;

	private int partId;
	private String type;
	private double weight;

	public Part() {
	}

	public Part(int partId, String type, double weight) {
		this.partId = partId;
		this.type = type;
		this.weight = weight;
	}

	public int getPartId() {
		return partId;
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append("Part id: " + partId);
		s.append(", Part type: " + type);
		s.append(", Weight: " + weight);

		return s.toString();
	}
}