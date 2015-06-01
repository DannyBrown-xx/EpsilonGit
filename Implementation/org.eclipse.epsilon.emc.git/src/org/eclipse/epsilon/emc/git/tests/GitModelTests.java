package org.eclipse.epsilon.emc.git.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.junit.Test;

// -- Assumptions --
// This JUnit test harness assumes you have `git clone` the following git repositories to
// EpsilonGit/Tests/Repositories:
// * http://github.com/epsilonlabs/emc-json.git


public class GitModelTests {
	//Assumes working directory is project home
	private static String TEST_GIT_REPOSITORIES_LOCATION = "../../Tests/Repositories/";
	
	@Test(expected = EolModelLoadingException.class)  
	public void nonGitRepositoryLoadTest() throws EolModelLoadingException, IOException {
		GitModel gitModel = new GitModel(new File(TEST_GIT_REPOSITORIES_LOCATION + "not-a-git-repository"));
		gitModel.load();
	}
	
	@Test
	public void validLoadTest() throws EolModelLoadingException {
		final File simpleGitRepositoryLocation = new File(TEST_GIT_REPOSITORIES_LOCATION + "emc-json/.git");
		GitModel gitModel = new GitModel(simpleGitRepositoryLocation);
		gitModel.load();
		assertEquals(gitModel.getJGitRepository().getDirectory(), simpleGitRepositoryLocation);
	}
}
