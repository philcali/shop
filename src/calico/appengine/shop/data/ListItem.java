package calico.appengine.shop.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class ListItem {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private ShopList list;
	
	@Persistent
	private Item item;
	
	public ListItem(ShopList list, Item item) {
		this.list = list;
		this.item = item;
	}
	
	public ShopList getListid() {
		return list;
	}
	
	public Item getItem() {
		return item;
	}
	
	public Long getId() {
		return id;
	}
}
