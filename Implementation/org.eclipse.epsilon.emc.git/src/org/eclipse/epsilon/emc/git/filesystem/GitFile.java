package org.eclipse.epsilon.emc.git.filesystem;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epsilon.emc.git.objectmodel.Commit;

public class GitFile extends File {
	private static final long serialVersionUID = 1392626043233352332L;
	
	private Set<Commit> commitsWhereAdded = new HashSet<Commit>();
	private Set<Commit> commitsWhereModified = new HashSet<Commit>();
	private Set<Commit> commitsWhereDeleted = new HashSet<Commit>();
	
	//TODO: Think more about these...
	private Set<Commit> commitsWhereCopied = new HashSet<Commit>();
	private Set<Commit> commitsWhereRenamed = new HashSet<Commit>();
	
	private boolean isInWorkingDirectory = false;
	
	public GitFile(String pathname) {
		super(pathname);
	}
	
	public String getId() {
		return "File:" + this.getAbsolutePath(); 
	}
	
	public void addCommitWhereModified(Commit commit) {
		commitsWhereModified.add(commit);
	}
	
	public Collection<Commit> getCommitsWhereModified() {
		return commitsWhereModified;
	}
	
	public void addCommitWhereAdded(Commit commit) {
		commitsWhereAdded.add(commit);
	}
	
	public Collection<Commit> getCommitsWhereAdded() {
		return commitsWhereAdded;
	}
	
	public void addCommitWhereDeleted(Commit commit) {
		commitsWhereDeleted.add(commit);
	}
	
	public Collection<Commit> getCommitsWhereDeleted() {
		return commitsWhereDeleted;
	}
	
	public void addCommitWhereCopied(Commit commit) {
		commitsWhereCopied.add(commit);
	}
	
	public Collection<Commit> getCommitsWhereCopied() {
		return commitsWhereCopied;
	}
	
	public void addCommitWhereRenamed(Commit commit) {
		commitsWhereRenamed.add(commit);
	}
	
	public Collection<Commit> getCommitsWhereRenamed() {
		return commitsWhereRenamed;
	}
	
	public boolean isInWorkingDirectory() {
		return isInWorkingDirectory;
	}

	public void setIsInWorkingDirectory(boolean isIt) {
		isInWorkingDirectory = isIt;
	}
}
