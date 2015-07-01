package org.eclipse.epsilon.emc.git.diff;

public class DifferenceCount {
	private final int linesAdded;
	private final int linesRemoved;
	private final int filesAdded;
	private final int filesRemoved;
	private final int filesRenamed;
	private final int filesCopied;
	private final int filesModified;
	
	public DifferenceCount(int linesAdded, int linesRemoved, int filesAdded, int filesRemoved, int filesRenamed, int filesCopied, int filesModified) {
		this.linesAdded = linesAdded;
		this.linesRemoved = linesRemoved;
		this.filesAdded = filesAdded;
		this.filesRemoved = filesRemoved;
		this.filesRenamed = filesRenamed;
		this.filesCopied = filesCopied;
		this.filesModified = filesModified;
	}
	
	public int getLinesAdded() {
		return linesAdded;
	}
	
	public int getLinesRemoved() {
		return linesRemoved;
	}
	
	public int getFilesAdded() {
		return filesAdded;
	}
	
	public int getFilesRemoved() {
		return filesRemoved;
	}
	
	public int getFilesRenamed() {
		return filesRenamed;
	}
	
	public int getFilesCopied() {
		return filesCopied;
	}
	
	public int getFilesModified() {
		return filesModified;
	}
}
