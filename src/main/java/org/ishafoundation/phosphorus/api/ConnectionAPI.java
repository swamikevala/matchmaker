package org.ishafoundation.phosphorus.api;

import java.util.Set;

public interface ConnectionAPI {
	
	public String openSustainedConnection();
	
	public boolean closeSustainedConnection(String connId);
	
}

