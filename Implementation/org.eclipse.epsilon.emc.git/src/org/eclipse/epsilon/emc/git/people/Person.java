package org.eclipse.epsilon.emc.git.people;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.eclipse.epsilon.emc.git.GitModel;

public abstract class Person {
	private final String name;
	private final String emailAddress;
	protected final GitModel owner;
	
	public Person(String name, String emailAddress, GitModel owner) {
		this.name = name;
		this.emailAddress = emailAddress;
		this.owner = owner;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	//Credit: http://stackoverflow.com/questions/27581/what-issues-should-be-considered-when-overriding-equals-and-hashcode-in-java
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(name).
            append(emailAddress).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Person))
            return false;
        if (obj == this)
            return true;

        Person rhs = (Person) obj;
        return new EqualsBuilder().
            append(name, rhs.name).
            append(emailAddress, rhs.emailAddress).
            isEquals();
    }
}
