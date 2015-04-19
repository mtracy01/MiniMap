package server;

import java.net.Socket;

public class CTFUser {

	private User user;
	boolean inJail;
	boolean hasFlag;
	boolean isRetreating;
	
	public CTFUser(User user) {
		this.user = user;
		this.inJail = false;
		this.hasFlag = false;
		this.isRetreating = false;
	}

	public User getUser() {
		return user;
	}
	
	public boolean isInJail() {
		return inJail;
	}

	public boolean hasFlag() {
		return hasFlag;
	}
	
	public boolean isRetreating() {
		return isRetreating;
	}

	public void setInJail(boolean inJail) {
		this.inJail = inJail;
	}

	public void setHasFlag(boolean hasFlag) {
		this.hasFlag = hasFlag;
	}

	public void setRetreating(boolean isRetreating) {
		this.isRetreating = isRetreating;
	}	

	public void setUser(User user) {
		this.user = user;
	}

	
	
}
