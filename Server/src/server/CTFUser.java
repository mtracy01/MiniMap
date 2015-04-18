package server;

import java.net.Socket;

public class CTFUser extends User {

	boolean inJail;
	boolean hasFlag;
	boolean isRetreating;
	
	public CTFUser(Socket socket, Server server) {
		super(socket, server);
		this.inJail = false;
		this.hasFlag = false;
		this.isRetreating = false;
	}

	public boolean isInJail() {
		return inJail;
	}

	public boolean hasFlag() {
		return hasFlag;
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

	public boolean isRetreating() {
		return isRetreating;
	}
	
	
}
