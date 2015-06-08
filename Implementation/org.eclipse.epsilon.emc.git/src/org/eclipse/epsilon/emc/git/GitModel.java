package org.eclipse.epsilon.emc.git;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GitModel extends CachedModel {
	
	private File repositoryLocation;
	private Git git;
	private Repository repository;
	
	public static String RepositoryLocationViaDT = "Repository Location Hash Key";
	
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
		ObjectId elementId = ObjectId.fromString(id);
		ObjectReader objectReader = repository.newObjectReader();
		ObjectLoader objectLoader = null;
		RevWalk revWalk = new RevWalk(repository);
		try {
			objectLoader = objectReader.open(elementId);
			switch(objectLoader.getType()) {
				case Constants.OBJ_BLOB:
					RevBlob blob = revWalk.lookupBlob(elementId);
					return blob;
				case Constants.OBJ_TREE:
					RevTree tree = revWalk.parseTree(elementId);
					return tree;
				case Constants.OBJ_COMMIT:
					RevCommit commit = revWalk.parseCommit(elementId);
					return commit;
				case Constants.OBJ_TAG:
					RevTag tag = revWalk.parseTag(elementId);
					return tag;
			}
		} 
		catch (IOException e) {
			return null;
		}
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		if(instance instanceof RevObject) {
			RevObject revObjectInstance = (RevObject)instance;
			return revObjectInstance.getId().toString();
		}
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		// Can't change the object ID of commits, tags etc.
		throw new UnsupportedOperationException();
	}

	/**
	 * Determines if a Java object is part of this GitModel. (Only RevTree, RevBlob, RevCommit and RevTag can be)
	 * 
	 * 		e.g. Is this commit part of the model this GitModel represents?
	 * 			 Does this git repository contain this tag?
	 * 
	 * @param instance The Java object (of any type) that may be part of the model.
	 * @return True if, and only if, the Java object is part of the model. False otherwise.
	 */
	@Override
	public boolean owns(Object instance) {
		if(instance instanceof RevTree) {
			RevTree revTreeInstance = (RevTree)instance;
			return repository.hasObject(revTreeInstance.toObjectId());
		}
		
		if(instance instanceof RevBlob) {
			RevBlob revBlobInstance = (RevBlob)instance;
			return repository.hasObject(revBlobInstance.toObjectId());
		}
		
		if(instance instanceof RevCommit) {
			RevCommit revCommitInstance = (RevCommit)instance;
			return repository.hasObject(revCommitInstance.toObjectId());
		}
		
		if(instance instanceof RevTag) {
			RevTag revTagInstance = (RevTag)instance;
			return repository.hasObject(revTagInstance.toObjectId());
		}
		
		return false;
	}

	@Override
	public boolean isInstantiable(String type) {
		return false;
	}

	@Override
	public boolean hasType(String type) {
		return RevCommit.class.getSimpleName().equals(type) ||
				RevTag.class.getSimpleName().equals(type) ||
				RevBlob.class.getSimpleName().equals(type) ||
				RevTree.class.getSimpleName().equals(type);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		switch(type) {
			case "RevCommit":
				return getAllCommits();
			case "RevTag":
				return getAllTags();
			default:
				throw new EolModelElementTypeNotFoundException("GitModel", type);
		}
	}

	@Override
	protected Collection getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		//TODO: Question this (difference between type and kind..? guessing kind includes subclasses)
		return getAllOfTypeFromModel(kind);
	}

	@Override
	protected Object createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//Method called when attempting loading from configuration dialog
	public void load(StringProperties properties, String basePath)
			throws EolModelLoadingException {
		super.load(properties, basePath);
		repositoryLocation = new File(properties.getProperty(GitModel.RepositoryLocationViaDT));
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Object getCacheKeyForType(String type)
			throws EolModelElementTypeNotFoundException {
		return type; // Each type must have a different key for the hashmap that caches its objects.
					 // Each type name is unique so we will use this.
	}

	@Override
	protected Collection getAllTypeNamesOf(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Collection<RevCommit> getAllCommits() {
		try {
			//Use porcelain api to get all commits.
			Iterable<RevCommit> allCommitsIterable = git.log().all().call();
			Collection<RevCommit> collection = new LinkedList<RevCommit>();
			for(RevCommit commit : allCommitsIterable) {
				collection.add(commit);
			}
			return collection;
		} 
		catch (GitAPIException | IOException e) {
			return null;
		}
	}
	
	private Collection<RevTag> getAllTags() {
		RevWalk revWalk = new RevWalk(repository);
		try {
			List<Ref> tagListAsRefs = git.tagList().call();
			Collection<RevTag> collection = new LinkedList<RevTag>();
			for(Ref ref : tagListAsRefs) {
				collection.add(revWalk.parseTag(ref.getObjectId()));
			}
			return collection;
		} catch (GitAPIException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}
}
