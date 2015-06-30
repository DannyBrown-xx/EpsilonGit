package org.eclipse.epsilon.emc.git.diff;

public class DifferenceCount {
	public final int LinesAdded;
	public final int LinesRemoved;
	public final int FilesAdded;
	public final int FilesRemoved;
	public final int FilesRenamed;
	public final int FilesCopied;
	public final int FilesModified;
	
	public DifferenceCount(int linesAdded, int linesRemoved, int filesAdded, int filesRemoved, int filesRenamed, int filesCopied, int filesModified) {
		LinesAdded = linesAdded;
		LinesRemoved = linesRemoved;
		FilesAdded = filesAdded;
		FilesRemoved = filesRemoved;
		FilesRenamed = filesRenamed;
		FilesCopied = filesCopied;
		FilesModified = filesModified;
	}
}
