package model.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.Pallet;

public class PalletCache implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Integer, Pallet> pallets = new HashMap<>();

	public void addPallet(Pallet p) {
		pallets.put(p.getPalletId(), p);
	}

	public Pallet removePallet(int palletId) {
		return pallets.remove(palletId);
	}

	public Pallet getPallet(int palletId) {
		return pallets.get(palletId);
	}

	public Map<Integer, Pallet> getCache() {
		return pallets;
	}

	public int count() {
		return pallets.size();
	}

	public boolean contains(int palletId) {
		return pallets.containsKey(palletId);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		for (Map.Entry<Integer, Pallet> pallet : pallets.entrySet()) {
			s.append(pallet.getValue().toString() + "\n");
		}

		return s.toString();
	}
}