package org.eclipse.epsilon.emc.git;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevBlob;

public class Blob extends RevBlob {

	public Blob(AnyObjectId id) {
		super(id);
	}
	
	public Blob(RevBlob revBlob) {
		super(revBlob.getId());
	}
}
