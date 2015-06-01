package org.eclipse.epsilon.emc.git;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@SuppressWarnings("rawtypes")
public class GitModel extends CachedModel {
	
	private File repositoryLocation;
	private Repository repository;
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeNameOf(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getElementById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasType(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean store(String location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean store() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Collection allContentsFromModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadModel() throws EolModelLoadingException {
		try {
			repository = new FileRepositoryBuilder()
							.setMustExist(true)
							.setGitDir(repositoryLocation)
							.build();
		}
		catch(IOException ioException) {
			throw new EolModelLoadingException(ioException, this);
		}
	}

	@Override
	protected void disposeModel() {
		repository.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection getAllTypeNamesOf(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
