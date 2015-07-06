package org.eclipse.epsilon.emc.git.codegen;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

public class CreateGitModelAntTask extends Task {
	private String src;
	
	public String getSrc() {
		return src;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	@Override
	public void execute() {
		GitModel model = new GitModel();
		StringProperties properties = new StringProperties();
		properties.setProperty(GitModel.PROPERTY_LOCATION, src);
		try {
			model.load(properties);
		} catch (EolModelLoadingException e) {
			throw new BuildException(e);
		}
	}
}
