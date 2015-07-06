package org.eclipse.epsilon.emc.git.filesystem;

import org.apache.commons.collections.Predicate;

public class FileFinderPredicate implements Predicate {

    private String id; 

    public FileFinderPredicate(String id) {
        super();
        this.id = id;
    }

    @Override
    public boolean evaluate(Object o) {
        GitFile f = (GitFile)o;
        return f.getId().equals(this.id);
    }
}
