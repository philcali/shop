package calico.appengine.shop.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class ShopList {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private String name;
	
	@Persistent
	private Date createdAt;
	
	public ShopList(String name) {
		this.name = name;
		this.createdAt = new Date();
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public String getName() {
		return name;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
