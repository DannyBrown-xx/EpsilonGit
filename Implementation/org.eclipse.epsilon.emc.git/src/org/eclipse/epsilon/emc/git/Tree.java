package org.eclipse.epsilon.emc.git;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevTree;

public class Tree extends RevTree {

	public Tree(AnyObjectId id) {
		super(id);
	}

	public Tree(RevTree revTree) {
		super(revTree.getId());
	}
}
