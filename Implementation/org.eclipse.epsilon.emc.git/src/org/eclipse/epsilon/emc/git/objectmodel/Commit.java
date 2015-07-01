package org.eclipse.epsilon.emc.git.objectmodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.diff.DifferenceCount;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
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
	
	public boolean isMergeCommit() {
		return getParentCount() > 1;
	}
	
	public DifferenceCount getDifferenceCountFromParent() {
		try {
			//Get this commits tree, for comparisons
			ObjectReader reader = owner.getJGitRepository().newObjectReader();
			RevWalk revWalk = new RevWalk(owner.getJGitRepository());
			
			CanonicalTreeParser thisCommitsTree = new CanonicalTreeParser();
			thisCommitsTree.reset(reader, this.getTree().getId());
		
			//Each commit can have multiple parents (e.g. in a merge). Get differences from
			//all of them by comparing trees
			Collection<DiffEntry> allDiffs = new LinkedList<DiffEntry>();
			
			for(RevCommit revCommit : getParents()) {
				CanonicalTreeParser parentTree = new CanonicalTreeParser();
				revWalk.parseBody(revCommit);
				parentTree.reset(reader, revCommit.getTree().getId());
			
				Collection<DiffEntry> diffs = owner.getJGitPorcelain()
										.diff()
										.setNewTree(thisCommitsTree)
										.setOldTree(parentTree)
										.call();
				allDiffs.addAll(diffs);
			}
			
			//Diff formatting tools
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        DiffFormatter df = new DiffFormatter(out);
	        df.setRepository(owner.getJGitRepository());
	        
	        int linesAdded = 0, linesRemoved = 0, filesAdded = 0, filesRemoved = 0, 
	        	filesRenamed = 0, filesModified = 0, filesCopied = 0;
	        
			for(DiffEntry diffEntry : allDiffs) {
				switch(diffEntry.getChangeType()) {
					case ADD:
						filesAdded++;
						linesAdded += getLineChanges(diffEntry, df).linesAdded;
						break;
					case COPY:
						filesCopied++;
						break;
					case DELETE:
						filesRemoved++;
						linesRemoved += getLineChanges(diffEntry, df).linesRemoved;
						break;
					case MODIFY:
						filesModified++;
						DiffLineChanges dlc = getLineChanges(diffEntry, df);
						linesAdded += dlc.linesAdded;
						linesRemoved += dlc.linesRemoved;
						break;
					case RENAME:
						filesRenamed++;
						break;
				}
			}
			
			DifferenceCount dc = new DifferenceCount(linesAdded, linesRemoved, filesAdded, filesRemoved, filesRenamed, filesCopied, filesModified);
			return dc;
		} 
		catch (IOException | GitAPIException e) {
			return null;
		}
	}
	
	private class DiffLineChanges {
		public int linesAdded;
		public int linesRemoved;
	}
	
	private DiffLineChanges getLineChanges(DiffEntry diffEntry, DiffFormatter diffFormatter) {
		FileHeader fileHeader = null;
		  	try {
			fileHeader = diffFormatter.toFileHeader(diffEntry);
		} catch (IOException e) {
			return null;
		}
	  	
		DiffLineChanges dlc = new DiffLineChanges();
		  	
	  	EditList editList = fileHeader.toEditList();
	  	for(Edit e : editList) {
	  		switch(e.getType()) {
				case DELETE:
					dlc.linesRemoved += e.getLengthA();
					break;
				case EMPTY:
					//Describes nothing
					break;
				case INSERT:
					dlc.linesAdded += e.getLengthB();
					break;
				case REPLACE:
					dlc.linesRemoved += e.getLengthA();
					dlc.linesAdded += e.getLengthB();
					break;
	  		}
	  	}
		return dlc;
	}
}
