package org.eclipse.epsilon.emc.git.people;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.objectmodel.Commit;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;

public class Committer extends Person {

	public Committer(String name, String emailAddress, GitModel owner) {
		super(name, emailAddress, owner);
	}
	
	public Collection<Commit> getAllCommittedCommits() {
		try {
			@SuppressWarnings("unchecked")
			LinkedList<Commit> allCommits = (LinkedList<Commit>) this.owner.getAllOfType("Commit");
			Collection<Commit> thisCommittersCommits = new LinkedList<Commit>();
			for(Commit commit : allCommits) {
				if(commit.getCommitterIdent().getEmailAddress().equals(this.getEmailAddress()) &&
					commit.getCommitterIdent().getName().equals(this.getName())) {
					thisCommittersCommits.add(commit);
				}
			}
			return thisCommittersCommits;
		} 
		catch (EolModelElementTypeNotFoundException e) {
			return null;
		}
	}
}
