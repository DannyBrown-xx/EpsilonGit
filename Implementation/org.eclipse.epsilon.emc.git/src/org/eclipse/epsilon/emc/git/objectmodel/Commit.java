package org.eclipse.epsilon.emc.git.objectmodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.epsilon.emc.git.GitCalendar;
import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.diff.CommitDifference;
import org.eclipse.epsilon.emc.git.diff.FileDifference;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
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
	
	public boolean isHead() {
		// TODO Implement
		return false;
	}
	
	public boolean isMergeCommit() {
		return getParentCount() > 1;
	}
		
	// - Nicer time stuff for models, DateTimes in Java are just a cluster... - //
	public GitCalendar getCommitCalendar() {
		GitCalendar c = new GitCalendar();
		c.setTimeInMillis(getCommitTime() * 1000L);
		return c;
	}
	
	public GitCalendar getCommitCalendarDate() {
		GitCalendar c = getCommitCalendar();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
	public GitCalendar getCommitCalendarTime() {
		GitCalendar c = getCommitCalendar();
		c.set(Calendar.YEAR, 0);
		c.set(Calendar.DAY_OF_MONTH, 0);
		c.set(Calendar.MONTH, 0);
		return c;
	}
	
	// - Methods to get differences from parent commits - //
	
	public CommitDifference getDifferenceFromParent() {
		try {
			Collection<DiffEntry> diffEntries = getDiffEntriesFromParents();
			return collateDiffEntries(diffEntries);
		} 
		catch (IOException | GitAPIException e) {
			return null;
		}
	}
	
	private CommitDifference collateDiffEntries(Collection<DiffEntry> diffEntries) {
		//Diff formatting tools
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);
        df.setRepository(owner.getJGitRepository());
        
        //CommitDifference holds all information about differences in this commit
        CommitDifference differences = new CommitDifference();
        
		for(DiffEntry diffEntry : diffEntries) {
			FileDifference fd = new FileDifference();
			fd.fileName = diffEntry.getNewPath();
			
			//TODO: Think about improvements to rename and copy
			switch(diffEntry.getChangeType()) {
				case ADD:
					fd.lineAdditions = getLineChanges(diffEntry, df).linesAdded;
					differences.addedFileDifferences.add(fd);
					break;
				case COPY:
					//TODO: does a copy add lines? discuss
					differences.copiedFileDifferences.add(fd);
					break;
				case DELETE:
					fd.lineRemovals = getLineChanges(diffEntry, df).linesRemoved;
					differences.removedFileDifferences.add(fd);
					break;
				case MODIFY:
					DiffLineChanges dlc = getLineChanges(diffEntry, df);
					fd.lineAdditions = dlc.linesAdded;
					fd.lineRemovals = dlc.linesRemoved;
					differences.modifiedFileDifferences.add(fd);
					break;
				case RENAME:
					differences.renamedFileDifferences.add(fd);
					break;
			}
		}
		return differences;
	}
	
	private Collection<DiffEntry> getDiffEntriesFromParents() throws IncorrectObjectTypeException, IOException, GitAPIException {
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
		
		return allDiffs;
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
