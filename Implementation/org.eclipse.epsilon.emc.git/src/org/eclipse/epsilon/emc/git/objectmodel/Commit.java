package org.eclipse.epsilon.emc.git.objectmodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.diff.DifferenceCount;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
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
					{
						filesAdded++;
						LineChanges lineChanges = getLineChanges(diffEntry, df);
						linesAdded += lineChanges.Additions;
						linesRemoved += lineChanges.Removals;
					}
						break;
					case COPY:
					{
						filesCopied++;
						LineChanges lineChanges = getLineChanges(diffEntry, df);
						linesAdded += lineChanges.Additions;
						linesRemoved += lineChanges.Removals;
					}
						break;
					case DELETE:
						filesRemoved++;
						break;
					case MODIFY:
					{
						filesModified++;
						LineChanges lineChanges = getLineChanges(diffEntry, df);
						linesAdded += lineChanges.Additions;
						linesRemoved += lineChanges.Removals;
					}
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
	
	private class LineChanges {
		int Additions = 0;
		int Removals = 0;
	}
	
	private LineChanges getLineChanges(DiffEntry diffEntry, DiffFormatter diffFormatter) {
		FileHeader fileHeader = null;
		  	try {
			fileHeader = diffFormatter.toFileHeader(diffEntry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	
		LineChanges lineChanges = new LineChanges();
	  	List<? extends HunkHeader> hunks = fileHeader.getHunks();
		for(HunkHeader hunk : hunks ) {
			lineChanges.Additions += hunk.getNewLineCount();
		}
		return lineChanges;
	}
}
