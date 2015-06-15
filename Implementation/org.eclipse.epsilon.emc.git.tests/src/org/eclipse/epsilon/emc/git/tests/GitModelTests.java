package org.eclipse.epsilon.emc.git.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.emc.git.objectmodel.Blob;
import org.eclipse.epsilon.emc.git.objectmodel.Commit;
import org.eclipse.epsilon.emc.git.objectmodel.Tag;
import org.eclipse.epsilon.emc.git.objectmodel.Tree;
import org.eclipse.epsilon.emc.git.people.Author;
import org.eclipse.epsilon.emc.git.people.Person;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.jgit.revwalk.RevCommit;
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
		Commit testCommit = (Commit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5bffe");
		linuxGitModel.getTypeNameOf(testCommit);
	}
	
	@Test
	public void getTypeNameOfRevTree() {
		Tree tree = (Tree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf734af40cc06ba5");
		linuxGitModel.getTypeNameOf(tree);
	}
	
	@Test
	public void getTypeNameOfRevBlob() {
		Blob blob = (Blob) linuxGitModel.getElementById("ddfcaaa86623a0c02b68bc93a62c3d54bbe51762");
		linuxGitModel.getTypeNameOf(blob);
	}
	
	@Test
	public void getTypeNameOfRevTag() {
		Tag tag = (Tag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a7f");
		linuxGitModel.getTypeNameOf(tag);
	}
	//endregion;
	
	//region getElementById tests
	@Test
	public void getElementByIdValidCommit() {
		Commit commit = (Commit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5bffe");
		assertEquals("Added first version of the JSON driver development tools", commit.getFullMessage());
	}
	
	@Test
	public void getElementByIdNonExistantCommit() {
		RevCommit commit = (RevCommit)emcJsonGitModel.getElementById("079fa68889e1c25649cf02341d79d0b0c4d5baaa"); //Non-existent hash
		assertEquals(null, commit);
	}
	
	@Test
	public void getElementByIdValidTag() {
		Tag tag = (Tag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a7f");
		assertEquals("v4.1-rc6", tag.getTagName());
	}
	
	@Test
	public void getElementByIdNonExistantTag() {
		Tag tag = (Tag) linuxGitModel.getElementById("986aef29e5bff76d9844d62218b9b7c264531a4a"); //Non-existent hash
		assertNull(tag);
	}
	
	@Test
	public void getElementByIdValidTree() throws Exception {
		Tree tree = (Tree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf734af40cc06ba5");
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
		Tree tree = (Tree) linuxGitModel.getElementById("0059e6d4b4c6cc34465ed6c4cf555af40cc06ba5"); //Non-existent hash
		assertNull(tree);
	}
	
	@Test
	public void getElementByIdValidBlob() {
		Blob blob = (Blob) linuxGitModel.getElementById("dad10656bf144ea4b9c9e792b2e2186db88e7c07");
		assertNotNull(blob);
	}
	
	@Test
	public void getElementByIdInvalidIdHash() {
		Commit commit = (Commit) linuxGitModel.getElementById("This isn't a valid @£$%%^&&*( hash");
		assertNull(commit);
	}
	
	@Test
	public void getElementByIdValidPerson() throws EolModelElementTypeNotFoundException {
		Person p = (Person) emcJsonGitModel.getElementById("dimitris.kolovos@york.ac.uk");
		assertEquals("kolovos", p.getName());
	}
	
	@Test
	public void getElementByIdNonExistantPerson() {
		Person p = (Person) emcJsonGitModel.getElementById("not-a-real-contributor@fakemail.net");
		assertNull(p);
	}
	
	//endregion;
	
	//region hasType tests
	@Test
	public void hasTypeRevCommit() {
		assertTrue(linuxGitModel.hasType("Commit"));
	}
	
	@Test
	public void hasTypeRevTag() {
		assertTrue(linuxGitModel.hasType("Tag"));
	}
	
	@Test
	public void hasTypeRevTree() {
		assertTrue(linuxGitModel.hasType("Tree"));
	}
	
	@Test
	public void hasTypeRevBlob() {
		assertTrue(linuxGitModel.hasType("Blob"));
	}
	
	@Test
	public void hasTypeTypeNotHad() {
		assertFalse(linuxGitModel.hasType("Array"));
	}
	
	//endregion;
	
	//region getAllOfTypeFromModel tests
	@SuppressWarnings("unchecked")
	@Test	
	public void getAllOfTypeFromModelCommit() throws EolModelElementTypeNotFoundException {
		LinkedList<Commit> allCommits = (LinkedList<Commit>) emcJsonGitModel.getAllOfType("Commit");
		assertTrue(allCommits.size() == 4);
	}
	
	//Caching issue.
	@SuppressWarnings("unchecked")
	@Test(expected = EolModelElementTypeNotFoundException.class)
	public void getAllOfTypeFromModelUnknownType() throws EolModelElementTypeNotFoundException {
		LinkedList<File> allFiles = (LinkedList<File>) emcJsonGitModel.getAllOfType("File");
		assertNull(allFiles);
	}
	
	//endregion;
	@Test
	public void authorSet() {
		Author danny = new Author("Daniel Brown", "d.t.brown@outlook.com");
		Author danny2 = new Author("Daniel Brown", "d.t.brown@outlook.com");
		
		Set<Person> authorsDeDuped = new HashSet<Person>();
		authorsDeDuped.add(danny);
		authorsDeDuped.add(danny2);
		
		assertEquals(1, authorsDeDuped.size());
	}
}
