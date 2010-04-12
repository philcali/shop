package calico.appengine.shop.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Item {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long listid;
	
	@Persistent
	private String name;
	
	@Persistent
	private Integer quantity;
	
	@Persistent
	private Double price;
	
	public Item(String name, Long listid) {
		this.name = name;
		this.price = 0.00;
		this.quantity = 1;
		this.listid = listid;
		this.key = KeyFactory.createKey(Item.class.getSimpleName(), listid + String.valueOf(new Date().getTime()));
	}
	
	public Key getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Long getListid() {
		return listid;
	}
}
