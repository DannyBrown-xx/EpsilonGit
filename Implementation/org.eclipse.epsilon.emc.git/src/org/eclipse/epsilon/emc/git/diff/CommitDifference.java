package org.eclipse.epsilon.emc.git.diff;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CommitDifference {
	public Set<FileDifference> addedFileDifferences;
	public Set<FileDifference> removedFileDifferences;
	public Set<FileDifference> modifiedFileDifferences;
	public Set<FileDifference> copiedFileDifferences;
	public Set<FileDifference> renamedFileDifferences;
	
	public CommitDifference() {
		addedFileDifferences = new HashSet<FileDifference>();
		removedFileDifferences = new HashSet<FileDifference>();
		modifiedFileDifferences = new HashSet<FileDifference>();
		copiedFileDifferences = new HashSet<FileDifference>();
		renamedFileDifferences = new HashSet<FileDifference>();
	}
	
	public Collection<FileDifference> getAddedFileDifferences() {
		return addedFileDifferences;
	}
	
	public Collection<FileDifference> getRemovedFileDifferences() {
		return removedFileDifferences;
	}
	
	public Collection<FileDifference> getModifiedFileDifferences() {
		return modifiedFileDifferences;
	}
	
	public Collection<FileDifference> getCopiedFileDifferences() {
		return copiedFileDifferences;
	}
	
	public Collection<FileDifference> getRenamedFileDifferences() {
		return renamedFileDifferences;
	}
}
