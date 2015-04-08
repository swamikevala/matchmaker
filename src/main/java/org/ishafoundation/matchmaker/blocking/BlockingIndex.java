package org.ishafoundation.matchmaker.blocking;

import org.ishafoundation.matchmaker.blocking.method.BlockingMethod;
import org.ishafoundation.matchmaker.blocking.keygen.KeyGenerator;
import org.ishafoundation.matchmaker.blocking.basekeygen.BaseKeyGenerator;

public class BlockingIndex {
	
	private BaseKeyGenerator baseKeyGenerator;
	private KeyGenerator keyGenerator;
	private BlockingMethod blockingMethod;
	
	public BlockingIndex(BaseKeyGenerator baseKeyGenerator, KeyGenerator keyGenerator, BlockingMethod blockingMethod) {
		this.baseKeyGenerator = baseKeyGenerator;
		this.keyGenerator = keyGenerator;
		this.blockingMethod = blockingMethod;
	}
	
	public BaseKeyGenerator getBaseKeyGenerator() {
		return baseKeyGenerator;
	}
	
	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}
	
	public BlockingMethod getBlockingMethod() {
		return blockingMethod;
	}
	
}


