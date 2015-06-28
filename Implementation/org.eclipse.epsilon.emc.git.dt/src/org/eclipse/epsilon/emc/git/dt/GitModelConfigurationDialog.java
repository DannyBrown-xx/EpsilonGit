package org.eclipse.epsilon.emc.git.dt;

import org.eclipse.epsilon.common.dt.launching.dialogs.AbstractCachedModelConfigurationDialog;
import org.eclipse.epsilon.emc.git.GitModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GitModelConfigurationDialog extends
		AbstractCachedModelConfigurationDialog {

	private Text fileText;
	
	@Override
	protected String getModelName() {
		return "Git Repository";
	}

	@Override
	protected String getModelType() {
		return "Git";
	}
	
	@Override
	// Groups are areas of the UI with a title and some controls
	protected void createGroups(Composite control) {
		super.createGroups(control);
		createFileGroup(control);
	}
	
	private void createFileGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Repositories", 3);
		
		final Label modelFileLabel = new Label(groupContent, SWT.NONE);
		modelFileLabel.setText("Repository (.git) location: ");
		
		fileText = new Text(groupContent, SWT.BORDER);
		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Button browseFile = new Button(groupContent, SWT.NONE);
		browseFile.setText("Browse...");
		browseFile.addSelectionListener(new SelectionListener() {
			//Reference: http://www.programcreek.com/2010/11/add-a-file-chooserselector-for-eclipse-rcp-development/
			public void widgetDefaultSelected(SelectionEvent e) { }
 
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(browseFile.getShell(), SWT.OPEN);
				dlg.setText("Open");
				String path = dlg.open();
				if (path == null) return;
				fileText.setText(path);
			}
		});
	}
	
	@Override
	protected void loadProperties() {
		super.loadProperties();
		if(properties == null) {
			return;
		}
		else {
			fileText.setText(properties.getProperty(GitModel.PROPERTY_LOCATION));
		}
	}
	
	@Override
	protected void storeProperties() {
		super.storeProperties();
		properties.put(GitModel.PROPERTY_LOCATION, fileText.getText());
	}
}
