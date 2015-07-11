package org.eclipse.epsilon.emc.git;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.git.diff.CommitDifference;
import org.eclipse.epsilon.emc.git.diff.FileDifference;
import org.eclipse.epsilon.emc.git.filesystem.FileFinderPredicate;
import org.eclipse.epsilon.emc.git.filesystem.GitFile;
import org.eclipse.epsilon.emc.git.objectmodel.Blob;
import org.eclipse.epsilon.emc.git.objectmodel.Branch;
import org.eclipse.epsilon.emc.git.objectmodel.Commit;
import org.eclipse.epsilon.emc.git.objectmodel.Tag;
import org.eclipse.epsilon.emc.git.objectmodel.Tree;
import org.eclipse.epsilon.emc.git.people.Author;
import org.eclipse.epsilon.emc.git.people.Committer;
import org.eclipse.epsilon.emc.git.people.Person;
import org.eclipse.epsilon.emc.git.people.PersonFinderPredicate;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GitModel extends CachedModel {
	
	private File repositoryLocation;
	private Git git;
	private Repository repository;
	
	public static String PROPERTY_LOCATION = "Repository Location Hash Key";
	
	public GitModel() {
		//A default constructor is required for now...
	}
	
	public GitModel(File repositoryLocation) {
		this.repositoryLocation = repositoryLocation;
	}
	
	//
	// -- Methods used for tests --
	//
	public Repository getJGitRepository() {
		return repository;
	}
	
	public Git getJGitPorcelain() {
		return git;	
	}
	
	//
	// -- CachedModel methods --
	//
	@Override
	public Object getEnumerationValue(String enumeration, String label)
			throws EolEnumerationValueNotFoundException {
		// No enumerations
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeNameOf(Object instance) {
		return instance.getClass().getSimpleName();
	}

	@Override
	public Object getElementById(String id) {
		if(ObjectId.isId(id)) {
			//The Id passed in is the object id of a git object.
			ObjectId elementId = ObjectId.fromString(id);			
			ObjectReader objectReader = repository.newObjectReader();
			ObjectLoader objectLoader = null;
			RevWalk revWalk = new RevWalk(repository);
			try {
				objectLoader = objectReader.open(elementId);
				switch(objectLoader.getType()) {
					case Constants.OBJ_BLOB:
						Blob blob = new Blob(revWalk.lookupBlob(elementId));
						revWalk.parseBody(blob);
						return blob;
					case Constants.OBJ_TREE:
						Tree tree = new Tree(revWalk.lookupTree(elementId));
						revWalk.parseBody(tree);
						return tree;
					case Constants.OBJ_COMMIT:
						Commit commit = new Commit(revWalk.lookupCommit(elementId), this);
						revWalk.parseBody(commit);
						return commit;
					case Constants.OBJ_TAG:
						Tag tag = new Tag(revWalk.lookupTag(elementId));
						revWalk.parseBody(tag);
						return tag;
				}
			} 
			catch (IOException e) {
				return null;
			}
		}
		else {
			//The Id passed in was not a git object id. It may well be a persons email address
			if(id.startsWith("Person:")) {
				return (Person)CollectionUtils.find(getAllPeople(), new PersonFinderPredicate(id));
			}
			else if(id.startsWith("Author:")) {
				return (Author)CollectionUtils.find(getAllPeople(), new PersonFinderPredicate(id));
			}
			else if(id.startsWith("Committer:")) {
				return (Committer)CollectionUtils.find(getAllPeople(), new PersonFinderPredicate(id));
			}
			else if (id.startsWith("File:")) {
				return (File)CollectionUtils.find(getAllFiles(), new FileFinderPredicate(id));
			}
		}
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		if(instance instanceof RevObject) {
			RevObject revObjectInstance = (RevObject)instance;
			return revObjectInstance.getId().toString();
		}
		
		if(instance instanceof Author) {
			Author authorInstance = (Author)instance;
			return authorInstance.getId();
		}
		
		if(instance instanceof Committer) {
			Committer committerInstance = (Committer)instance;
			return committerInstance.getId();
		}
		
		if(instance instanceof GitFile) {
			GitFile file = (GitFile)instance;
			return file.getAbsolutePath();
		}
		
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		// Can't change the object ID of commits, tags etc.
		throw new UnsupportedOperationException();
	}

	/**
	 * Determines if a Java object is part of this GitModel. (Only classes on emc.git.objectmodel and emc.git.people
	 * 														 packages can be)
	 * 
	 * 		e.g. Is this commit part of the model this GitModel represents?
	 * 			 Does this git repository contain this tag?
	 * 
	 * @param instance The Java object (of any type) that may be part of the model.
	 * @return True if, and only if, the Java object is part of the model. False otherwise.
	 */
	@Override
	public boolean owns(Object instance) {
		if(instance instanceof Tree) {
			Tree treeInstance = (Tree)instance;
			return repository.hasObject(treeInstance.toObjectId());
		}
		
		if(instance instanceof Blob) {
			Blob blobInstance = (Blob)instance;
			return repository.hasObject(blobInstance.toObjectId());
		}
		
		if(instance instanceof Commit) {
			RevCommit commitInstance = (Commit)instance;
			return repository.hasObject(commitInstance.toObjectId());
		}
		
		if(instance instanceof Tag) {
			Tag tagInstance = (Tag)instance;
			return repository.hasObject(tagInstance.toObjectId());
		}
		
		if(instance instanceof Author) {
			Author authorInstance = (Author)instance;
			try {
				return getAllOfType("Author").contains(authorInstance);
			} catch (EolModelElementTypeNotFoundException e) {
				return false;
			}
		}
		
		if(instance instanceof Committer) {
			Committer committerInstance = (Committer)instance;
			try {
				return getAllOfType("Committer").contains(committerInstance);
			} catch (EolModelElementTypeNotFoundException e) {
				return false;
			}
		}
		
		if(instance instanceof GitFile) {
			GitFile fileInstance = (GitFile)instance;
			try {
				return getAllOfType("GitFile").contains(fileInstance);
			} catch(EolModelElementTypeNotFoundException e) {
				return false;
			}
		}
		
		return false;
	}

	@Override
	public boolean isInstantiable(String type) {
		return false;
	}

	@Override
	public boolean hasType(String type) {
		return Commit.class.getSimpleName().equals(type) ||
				Tag.class.getSimpleName().equals(type) ||
				Blob.class.getSimpleName().equals(type) ||
				Tree.class.getSimpleName().equals(type) ||
				Person.class.getSimpleName().equals(type) ||
				Author.class.getSimpleName().equals(type) ||
				Committer.class.getSimpleName().equals(type) ||
				GitFile.class.getSimpleName().equals(type);
	}

	@Override
	public boolean store(String location) {
		// Store isn't analogous to commit, so doesn't make sense to include 
		// Additionally, the aim of this module is to provide query and analysis
		// so nothing to change and need to be stored.
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean store() {
		// Store isn't analogous to commit, so doesn't make sense to include 
		// Additionally, the aim of this module is to provide query and analysis
		// so nothing to change and need to be stored.
		throw new UnsupportedOperationException();
	}

	@Override
	protected Collection allContentsFromModel() {
		try {
			//Using getAllOfType rather than getAllCommits() etc to utilize caching
			Collection<Object> allContents = new LinkedList<Object>();
			allContents.addAll(getAllOfType("Commit"));
			allContents.addAll(getAllOfType("Tree"));
			allContents.addAll(getAllOfType("Blob"));
			allContents.addAll(getAllOfType("Tag"));
			allContents.addAll(getAllOfType("Author"));
			allContents.addAll(getAllOfType("Committer"));
			allContents.addAll(getAllOfType("GitFile"));
			
			return allContents;
		} catch (EolModelElementTypeNotFoundException e) {
			return null;
		}
	}

	@Override
	protected Collection getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		switch(type) {
			case "Commit":
				return getAllCommits();
			case "Tag":
				return getAllTags();
			case "Author":
				return getAllAuthors();
			case "Committer":
				return getAllCommitters();
			case "Blob":
				return getAllBlobs();
			case "Tree":
				return getAllTrees();
			case "GitFile":
				return getAllFiles();
			default:
				throw new EolModelElementTypeNotFoundException("GitModel", type);
		}
	}

	@Override
	protected Collection getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		switch(kind) {
			case "Person":
				return getAllPeople();
			default:
				return getAllOfTypeFromModel(kind);
		}
	}

	@Override
	protected Object createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		//Nothing is instantiable
		throw new EolNotInstantiableModelElementTypeException("GitModel", type);
	}

	@Override
	//Method called when attempting loading from configuration dialog
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		super.load(properties, resolver);
		repositoryLocation = new File(properties.getProperty(GitModel.PROPERTY_LOCATION));
		loadModel();
	}
	
	@Override
	protected void loadModel() throws EolModelLoadingException {
		try {
			repository = new FileRepositoryBuilder()
							.setMustExist(true)
							.setGitDir(repositoryLocation)
							.build();
			
			git = new Git(repository);
		}
		catch(IOException ioException) {
			throw new EolModelLoadingException(ioException, this);
		}
	}

	@Override
	protected void disposeModel() {
		if(git != null) {
			git.close();
		}
		if(repository != null) {
			repository.close();
		}
	}

	@Override
	protected boolean deleteElementInModel(Object instance)
			throws EolRuntimeException {
		throw new EolRuntimeException("Cannot delete elements. GitModels are read-only for now.");
	}

	@Override
	protected Object getCacheKeyForType(String type)
			throws EolModelElementTypeNotFoundException {
		return type; // Each type must have a different key for the hashmap that caches its objects.
					 // Each type name is unique so we will use this.
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		if(instance.getClass().getSimpleName().equals(Author.class.getSimpleName()) || 
		   instance.getClass().getSimpleName().equals(Committer.class.getSimpleName())) {
			return Arrays.asList(instance.getClass().getSimpleName(), Person.class.getSimpleName());
		}
		else {
			return Arrays.asList(instance.getClass().getSimpleName());
		}
	}
	
	//
	// -- Helper methods to support CachedModel methods --
	//
	private Collection<Branch> getAllBranches() {
		List<Ref> allBranches;
		try {
			allBranches = git.branchList().call();
			Collection<Branch> collection = new LinkedList<Branch>();
			for(Ref ref : allBranches) {
				collection.add(new Branch(ref));
			}
			return collection;
		} catch (GitAPIException e) {
			return null;
		}
	}
	
	private Collection<Commit> getAllCommits() {
		RevWalk revWalk = new RevWalk(repository);
		try {
			//Use porcelain api to get all commits.
			Iterable<RevCommit> allCommitsIterable = git.log().all().call();
			Collection<Commit> collection = new LinkedList<Commit>();
			for(RevCommit commit : allCommitsIterable) {
				Commit c = new Commit(commit.getId(), this);
				revWalk.parseBody(c);
				collection.add(c);
			}
			return collection;
		} 
		catch (GitAPIException | IOException e) {
			return null;
		}
	}
	
	private Collection<Tag> getAllTags() {
		RevWalk revWalk = new RevWalk(repository);
		try {
			List<Ref> tagListAsRefs = git.tagList().call();
			Collection<Tag> collection = new LinkedList<Tag>();
			for(Ref ref : tagListAsRefs) {
				Tag t = new Tag(revWalk.lookupTag(ref.getObjectId()));
				try {
					revWalk.parseBody(t);
				} catch (IOException e) {
				}
				collection.add(t);
			}
			return collection;
		} catch (GitAPIException e) {
			return null;
		}
	}
	
	private Collection<Blob> getAllBlobs() {
		//TODO: Implement
		return null;
	}
	
	private Collection<Tree> getAllTrees() {
		//http://www.massapi.com/class/org/eclipse/jgit/treewalk/AbstractTreeIterator.html
		//TODO: Finish implementation
		return null;
		
		/*TreeWalk walk = new TreeWalk(repository);
		Collection<Tree> collection = new LinkedList<Tree>();
		try {
			while(walk.next()) {
				for(int i  = 0; i<walk.getTreeCount(); i++) {
					//collection.add() walk.getTree(i, WorkingTreeIterator.class));
				}
			}
			
			return collection;
		} catch (IOException e) {
			return null;
		}*/
	}
	
	private Collection<Author> getAllAuthors() {
		try {
			LinkedList<Commit> commits = (LinkedList<Commit>) getAllOfKind("Commit");
			Set<Author> set = new HashSet<Author>();
			for(Commit commit : commits) {
				set.add(new Author(commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress(), this));
			}
			return set;
		} catch (EolModelElementTypeNotFoundException e) {
			return null;
		}
	}
	
	private Collection<Committer> getAllCommitters() {
		try {
			LinkedList<Commit> commits = (LinkedList<Commit>) getAllOfKind("Commit");
			Set<Committer> set = new HashSet<Committer>();
			for(Commit commit : commits) {
				set.add(new Committer(commit.getCommitterIdent().getName(), commit.getCommitterIdent().getEmailAddress(), this));
			}
			return set;
		} catch (EolModelElementTypeNotFoundException e) {
			return null;
		}         
	}
	
	private Collection<Person> getAllPeople() {
		Set<Person> allPeople = new HashSet<Person>();
		allPeople.addAll(getAllCommitters());
		allPeople.addAll(getAllAuthors());
		return allPeople;
	}
	
	private Collection getAllFiles() {
		Map<String, GitFile> map = new HashMap<String, GitFile>();
		try {
			for(Commit commit : (LinkedList<Commit>) getAllOfKind("Commit")) {
				CommitDifference commitDiff = commit.getDifferenceFromParent();
				
				for(FileDifference difference : commitDiff.addedFileDifferences) {
					if(map.containsKey(difference.fileName)) {
						GitFile file = map.get(difference.fileName);
						file.addCommitWhereAdded(commit);
						map.put(difference.fileName, file);
					}
					else {
						GitFile file = new GitFile(difference.fileName);
						file.addCommitWhereAdded(commit);
						map.put(difference.fileName, file);
					}
				}
				
				for(FileDifference difference : commitDiff.removedFileDifferences) {
					if(map.containsKey(difference.fileName)) {
						GitFile file = map.get(difference.fileName);
						file.addCommitWhereDeleted(commit);
						map.put(difference.fileName, file);
					}
					else {
						GitFile file = new GitFile(difference.fileName);
						file.addCommitWhereDeleted(commit);
						map.put(difference.fileName, file);
					}
				}
				
				for(FileDifference difference : commitDiff.modifiedFileDifferences) {
					if(map.containsKey(difference.fileName)) {
						GitFile file = map.get(difference.fileName);
						file.addCommitWhereModified(commit);
						map.put(difference.fileName, file);
					}
					else {
						GitFile file = new GitFile(difference.fileName);
						file.addCommitWhereModified(commit);
						map.put(difference.fileName, file);
					}
				}
				
				for(FileDifference difference : commitDiff.renamedFileDifferences) {
					if(map.containsKey(difference.fileName)) {
						GitFile file = map.get(difference.fileName);
						file.addCommitWhereRenamed(commit);
						map.put(difference.fileName, file);
					}
					else {
						GitFile file = new GitFile(difference.fileName);
						file.addCommitWhereRenamed(commit);
						map.put(difference.fileName, file);
					}
				}
				
				for(FileDifference difference : commitDiff.copiedFileDifferences) {
					if(map.containsKey(difference.fileName)) {
						GitFile file = map.get(difference.fileName);
						file.addCommitWhereCopied(commit);
						map.put(difference.fileName, file);
					}
					else {
						GitFile file = new GitFile(difference.fileName);
						file.addCommitWhereCopied(commit);
						map.put(difference.fileName, file);
					}
				}
			}
		} catch (EolModelElementTypeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map.values();
	} 
}
