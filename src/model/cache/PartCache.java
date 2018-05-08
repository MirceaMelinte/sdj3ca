package model.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.Part;

public class PartCache implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Integer, Part> parts = new HashMap<>();

	public void addPart(Part p) {
		parts.put(p.getPartId(), p);
	}

	public Part removePart(int PartId) {
		return parts.remove(PartId);
	}

	public Part getPart(int PartId) {
		return parts.get(PartId);
	}

	public Map<Integer, Part> getCache() {
		return parts;
	}

	public int count() {
		return parts.size();
	}

	public boolean contains(int PartId) {
		return parts.containsKey(PartId);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		for (Map.Entry<Integer, Part> part : parts.entrySet()) {
			s.append(part.getValue().toString() + "\n");
		}

		return s.toString();
	}
}