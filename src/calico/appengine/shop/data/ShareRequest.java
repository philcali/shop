package calico.appengine.shop.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class ShareRequest {
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key key;
	
	@Persistent
	private Long listid;
	
	@Persistent
	private String requester;
	
	@Persistent
	private String requestee;
	
	@Persistent
	private Boolean accepted;
	
	@Persistent
	private Date requestedAt;
	
	public ShareRequest(Long listid, String requester, String requestee) {
		this.accepted = false;
		this.listid = listid;
		this.requestee = requestee;
		this.requester = requester;
		this.requestedAt = new Date();
		this.key = KeyFactory.createKey(ShareRequest.class.getSimpleName(), listid + requester + requestee);
	}
	
	public Date getCreatedAt() {
		return requestedAt;
	}
	
	public String getRequestee() {
		return requestee;
	}
	
	public String getRequester() {
		return requester;
	}
	
	public Boolean getAccepted() {
		return accepted;
	}
	
	public Key getKey() {
		return key;
	}
	
	public Long getListid() {
		return listid;
	}
	
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
}
