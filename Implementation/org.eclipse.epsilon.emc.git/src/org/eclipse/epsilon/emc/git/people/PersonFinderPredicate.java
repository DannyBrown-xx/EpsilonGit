package org.eclipse.epsilon.emc.git.people;

import org.apache.commons.collections.Predicate;

public class PersonFinderPredicate implements Predicate {

    private String emailAddress; 

    public PersonFinderPredicate(String emailAddress) {
        super();
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean evaluate(Object o) {
        Person p = (Person)o;
        return p.getEmailAddress().equals(this.emailAddress);
    }
}
