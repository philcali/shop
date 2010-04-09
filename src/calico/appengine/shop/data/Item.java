package calico.appengine.shop.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Item {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	
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
	}
	
	public Long getId() {
		return id;
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
