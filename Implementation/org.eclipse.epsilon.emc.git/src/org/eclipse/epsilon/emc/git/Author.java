package org.eclipse.epsilon.emc.git;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class Author {
	private final String name;
	private final String emailAddress;
	
	public Author(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
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
       if (!(obj instanceof Author))
            return false;
        if (obj == this)
            return true;

        Author rhs = (Author) obj;
        return new EqualsBuilder().
            append(name, rhs.name).
            append(emailAddress, rhs.emailAddress).
            isEquals();
    }
}
