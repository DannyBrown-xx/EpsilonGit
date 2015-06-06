package org.eclipse.epsilon.emc.git.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.BeforeClass;
import org.junit.Test;

// -- Assumptions --
// This JUnit test harness assumes you have `git clone` the following git repositories to
// EpsilonGit/Tests/Repositories:
// * https://github.com/epsilonlabs/emc-json.git
// * https://github.com/torvalds/linux

public class GitModelTests {
	//Assumes working directory is project home
	private static String TEST_GIT_REPOSITORIES_LOCATION = "../../Tests/Repositories/";
	private static GitModel emcJsonGitModel;
	private static GitModel linuxGitModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		emcJsonGitModel = new GitModel(new File(TEST_GIT_REPOSITORIES_LOCATION + "emc-json/.git"));
		emcJsonGitModel.load();
		
		linuxGitModel = new GitModel(new File(TEST_GIT_REPOSITORIES_LOCATION + "linux/.git"));
		linuxGitModel.load();
	}
	
	//region load tests
	@Test(expected = EolModelLoadingException.class)  
	public void loadNonVersionedDirectory() throws EolModelLoadingException, IOException {
		GitModel gitModel = new GitModel(new File(TEST_GIT_REPOSITORIES_LOCATION + "not-a-git-repository"));
		gitModel.load();
	}
	
	@Test
	public void loadValidRepository() throws EolModelLoadingException {
		final File simpleGitRepositoryLocation = new File(TEST_GIT_REPOSITORIES_LOCATION + "emc-json/.git");
		GitModel gitModel = new GitModel(simpleGitRepositoryLocation);
		gitModel.load();
		assertEquals(simpleGitRepositoryLocation, gitModel.getJGitRepository().getDirectory());
	}
	//endregion;
	
	//region getTypeNameOf tests
	@Test
	public void getTypeNameOfRevCommit() {
		RevCommit testCommit = (RevCommit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5bffe");
		linuxGitModel.getTypeNameOf(testCommit);
	}
	
	@Test
	public void getTypeNameOfRevTree() {
		RevTree tree = (RevTree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf734af40cc06ba5");
		linuxGitModel.getTypeNameOf(tree);
	}
	
	@Test
	public void getTypeNameOfRevBlob() {
		RevBlob blob = (RevBlob) linuxGitModel.getElementById("ddfcaaa86623a0c02b68bc93a62c3d54bbe51762");
		linuxGitModel.getTypeNameOf(blob);
	}
	
	@Test
	public void getTypeNameOfRevTag() {
		RevTag tag = (RevTag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a7f");
		linuxGitModel.getTypeNameOf(tag);
	}
	//endregion;
	
	//region getElementById tests
	@Test
	public void getElementByIdValidCommit() {
		RevCommit commit = (RevCommit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5bffe");
		assertEquals("Added first version of the JSON driver development tools", commit.getFullMessage());
	}
	
	@Test
	public void getElementByIdNonExistantCommit() {
		RevCommit commit = (RevCommit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5baaa"); //Non-existent hash
		assertEquals(null, commit);
	}
	
	@Test
	public void getElementByIdValidTag() {
		RevTag tag = (RevTag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a7f");
		assertEquals("v4.1-rc6", tag.getTagName());
	}
	
	@Test
	public void getElementByIdNonExistantTag() {
		RevTag tag = (RevTag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a4a"); //Non-existent hash
		assertNull(tag);
	}
	
	@Test
	public void getElementByIdValidTree() throws Exception {
		RevTree tree = (RevTree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf734af40cc06ba5");
		TreeWalk treeWalk = new TreeWalk(linuxGitModel.getJGitRepository());
		treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        while(treeWalk.next()) {
        	//Walk to end
        }
        String lastObjectInTree = treeWalk.getPathString();
		assertEquals("workqueue_internal.h", lastObjectInTree);
	}
	
	@Test
	public void getElementByIdNonExistantTree() {
		RevTree tree = (RevTree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf555af40cc06ba5"); //Non-existent hash
		assertNull(tree);
	}
	
	@Test
	public void getElementByIdValidBlob() {
		RevBlob blob = (RevBlob) linuxGitModel.getElementById("dad10656bf144ea4b9c9e792b2e2186db88e7c07");
		assertNotNull(blob);
	}
	
	@Test(expected = IllegalArgumentException.class) 
	public void getElementByIdInvalidIdHash() {
		RevCommit commit = (RevCommit) linuxGitModel.getElementById("This isn't a valid @£$%%^&&*( hash");
		assertNull(commit);
	}
	
	//endregion;
	
	//region hasType tests
	@Test
	public void hasTypeRevCommit() {
		assertTrue(linuxGitModel.hasType("RevCommit"));
	}
	
	@Test
	public void hasTypeRevTag() {
		assertTrue(linuxGitModel.hasType("RevTag"));
	}
	
	@Test
	public void hasTypeRevTree() {
		assertTrue(linuxGitModel.hasType("RevTree"));
	}
	
	@Test
	public void hasTypeRevBlob() {
		assertTrue(linuxGitModel.hasType("RevBlob"));
	}
	
	@Test
	public void hasTypeTypeNotHad() {
		assertFalse(linuxGitModel.hasType("Array"));
	}
	
	//endregion;
	
	//region getAllOfTypeFromModel tests
	@Test	
	public void getAllOfTypeFromModelRevCommit() throws EolModelElementTypeNotFoundException {
		LinkedList<RevCommit> allCommits = (LinkedList<RevCommit>) emcJsonGitModel.getAllOfType("RevCommit");
		assertTrue(allCommits.size() == 4);
	}
	
	//Caching issue.
	@Test(expected = EolModelElementTypeNotFoundException.class)
	public void getAllOfTypeFromModelUnknownType() throws EolModelElementTypeNotFoundException {
		LinkedList<File> allFiles = (LinkedList<File>) emcJsonGitModel.getAllOfType("File");
		assertNull(allFiles);
	}
	
	//endregion;
}
