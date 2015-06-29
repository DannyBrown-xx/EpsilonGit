package org.eclipse.epsilon.emc.git.objectmodel;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class Commit extends RevCommit {
	public GitModel owner;
	
	public Commit(AnyObjectId id, GitModel owner) {
		super(id);
		this.owner = owner;
	}
	
	public Commit(RevCommit revCommit, GitModel owner) {
		super(revCommit.getId());
		this.owner = owner;
	}
	
	public boolean isRoof() {
		return getParentCount() == 0;
	}
	
	public Collection<DiffEntry> getChangesFromParents() {
		try {
			//Get this commits tree, for comparisons
			ObjectReader reader = owner.getJGitRepository().newObjectReader();
			RevWalk revWalk = new RevWalk(owner.getJGitRepository());
			
			CanonicalTreeParser thisCommitsTree = new CanonicalTreeParser();
			thisCommitsTree.reset(reader, this.getTree().getId());
		
			//Each commit can have multiple parents (e.g. in a merge). Get differences from
			//all of them by comparing trees
			for(RevCommit revCommit : getParents()) {
				CanonicalTreeParser parentTree = new CanonicalTreeParser();
				revWalk.parseBody(revCommit);
				parentTree.reset(reader, revCommit.getTree().getId());
			
				Collection<DiffEntry> diffs = owner.getJGitPorcelain()
										.diff()
										.setNewTree(thisCommitsTree)
										.setOldTree(parentTree)
										.call();
				return diffs;
			}
			
		} 
		catch (IOException | GitAPIException e) {
			return null;
		}
		return null;
	}
}
