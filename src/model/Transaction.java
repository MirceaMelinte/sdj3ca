package model;

import java.io.Serializable;

public class Transaction<T> implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String type;
   private T load;
   
   public final static String REGISTER_CAR = "REGISTER_CAR";
   public final static String REGISTER_PART = "REGISTER_PART";
   public final static String REGISTER_PRODUCT = "REGISTER_PRODUCT";
   public final static String REGISTER_PALLET = "REGISTER_PALLET"; // Should happen only once
   public final static String UPDATE_PALLET_PART = "UPDATE_PALLET_PART";
   public final static String UPDATE_PALLET_WEIGHT = "UPDATE_PALLET_WEIGHT";
   public final static String UPDATE_FINISH_PALLET = "UPDATE_FINISH_PALLET";
   public final static String UPDATE_FINISH_CAR = "UPDATE_FINISH_CAR";
   
   
   public Transaction(String type, T load) {
      this.type = type;
      this.load = load;
   }
   
   public String getType() { return type; }
   public void setType(String type) { this.type = type; }
   
   public T getLoad() { return load; }
   public void setLoad(T load) { this.load = load; }
}
