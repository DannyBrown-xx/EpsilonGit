package org.eclipse.epsilon.emc.git.objectmodel;

import org.eclipse.jgit.lib.Ref;

public class Branch {
	private Ref ref;
	
	public Branch(Ref ref) {
		this.ref = ref;
		ref.getLeaf();
	}
	
	public Commit getStartCommit() {
		return null;
	}
	
	public int getNumberCommitsInBranch() {
		return 0;
	}
}
