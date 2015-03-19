package org.ishafoundation.phosphorus.blocking.keygen;

import java.util.Set;

import org.ishafoundation.phosphorus.blocking.IndexKey;
import org.ishafoundation.phosphorus.blocking.BaseKey;

public interface KeyGenerator {

	public Set<IndexKey> generateKeys(BaseKey baseKey);

}