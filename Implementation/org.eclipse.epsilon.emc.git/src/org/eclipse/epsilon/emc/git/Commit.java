package org.eclipse.epsilon.emc.git;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class Commit extends RevCommit {

	public Commit(AnyObjectId id) {
		super(id);
	}
	
	public Commit(RevCommit revCommit) {
		super(revCommit.getId());
	}
}
