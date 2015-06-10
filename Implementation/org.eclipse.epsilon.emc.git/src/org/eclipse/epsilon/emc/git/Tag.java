package org.eclipse.epsilon.emc.git;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevTag;

public class Tag extends RevTag {

	public Tag(AnyObjectId id) {
		super(id);
	}
	
	public Tag(RevTag revTag) {
		super(revTag.getId());
	}
}
