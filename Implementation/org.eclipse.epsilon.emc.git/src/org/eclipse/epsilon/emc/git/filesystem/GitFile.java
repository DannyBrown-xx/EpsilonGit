package org.eclipse.epsilon.emc.git.filesystem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epsilon.emc.git.objectmodel.Commit;

public class GitFile extends File {
	private static final long serialVersionUID = 1392626043233352332L;
	
	private Set<Commit> relatedCommits = new HashSet<Commit>();
	private boolean isInWorkingDirectory = false;
	
	public GitFile(String pathname) {
		super(pathname);
	}
	
	public String getId() {
		return "File:" + this.getAbsolutePath(); 
	}
	
	public void addRelatedCommit(Commit commit) {
		relatedCommits.add(commit);
	}
	
	public int getNumberOfRelatedCommits() {
		return relatedCommits.size();
	}
	
	public boolean isInWorkingDirectory() {
		return isInWorkingDirectory;
	}

	public void setIsInWorkingDirectory(boolean isIt) {
		isInWorkingDirectory = isIt;
	}
}
