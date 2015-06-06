package org.eclipse.epsilon.emc.git.dt;

import org.eclipse.epsilon.common.dt.launching.dialogs.AbstractCachedModelConfigurationDialog;

public class GitModelConfigurationDialog extends
		AbstractCachedModelConfigurationDialog {

	@Override
	protected String getModelName() {
		return "Git Repository";
	}

	@Override
	protected String getModelType() {
		return "Git";
	}

}
