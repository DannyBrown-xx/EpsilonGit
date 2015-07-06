package org.eclipse.epsilon.emc.git.people;

import org.apache.commons.collections.Predicate;

public class PersonFinderPredicate implements Predicate {

    private String id; 

    public PersonFinderPredicate(String id) {
        super();
        this.id = id;
    }

    @Override
    public boolean evaluate(Object o) {
        Person p = (Person)o;
        return p.getId().equals(this.id);
    }
}
