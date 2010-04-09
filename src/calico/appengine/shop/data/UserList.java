package calico.appengine.shop.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class UserList {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String email;
	
	@Persistent
	private Long listid;
	
	public UserList(String email, Long listid) {
		this.email = email;
		this.listid = listid;
		this.key = KeyFactory.createKey(UserList.class.getSimpleName(), listid + email);
	}
	
	public Key getKey() {
		return key;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Long getListId() {
		return listid;
	}
}
