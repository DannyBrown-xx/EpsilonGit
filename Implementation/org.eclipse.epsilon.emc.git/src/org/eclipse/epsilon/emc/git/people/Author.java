package org.eclipse.epsilon.emc.git.people;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.objectmodel.Commit;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;

public class Author extends Person {	
	public Author(String name, String emailAddress, GitModel owner) {
		super(name, emailAddress, owner);
	}
	
	public Collection<Commit> getAllAuthoredCommits() {
		try {
			@SuppressWarnings("unchecked")
			LinkedList<Commit> allCommits = (LinkedList<Commit>) this.owner.getAllOfType("Commit");
			Collection<Commit> thisAuthorsCommits = new LinkedList<Commit>();
			for(Commit commit : allCommits) {
				if(commit.getAuthorIdent().getEmailAddress().equals(this.getEmailAddress()) &&
					commit.getAuthorIdent().getName().equals(this.getName())) {
					thisAuthorsCommits.add(commit);
				}
			}
			return thisAuthorsCommits;
		} 
		catch (EolModelElementTypeNotFoundException e) {
			return null;
		}
	}
}
