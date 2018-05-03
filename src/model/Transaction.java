package model;

import java.io.Serializable;

public class Transaction<T> implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String type;
   private T load;
   
   public final static String REGISTER = "REGISTER";
   public final static String UPDATE = "UPDATE";
   
   public Transaction(String type, T load) {
      this.type = type;
      this.load = load;
   }
   
   public String getType() { return type; }
   public void setType(String type) { this.type = type; }
   
   public T getLoad() { return load; }
   public void setLoad(T load) { this.load = load; }
}
