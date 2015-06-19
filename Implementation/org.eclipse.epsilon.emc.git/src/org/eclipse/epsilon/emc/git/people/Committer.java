package org.eclipse.epsilon.emc.git.people;

import org.eclipse.epsilon.emc.git.GitModel;

public class Committer extends Person {

	public Committer(String name, String emailAddress, GitModel owner) {
		super(name, emailAddress, owner);
	}
}
