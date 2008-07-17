/**
 * 
 */
package com.mkalugin.corchy.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class BasicAlert {

	private final Shell parent;
	private String messageText;
	private String informativeText;
	private List<String> buttons = new ArrayList<String>();
	private MessageDialog messageDialog;

	public BasicAlert(Shell parent) {
		this.parent = parent;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public void setInformativeText(String informativeText) {
		this.informativeText = informativeText;
	}

	protected void addButton(String title) {
		buttons.add(title);
	}

	public void openModal() {
		finished(openModalReturnButton());
	}

	public int openModalReturnButton() {
		if (parent == null)
			throw new NullPointerException("parent shell is null");

		messageDialog = new MessageDialog(parent, messageText, null, informativeText, MessageDialog.QUESTION, buttons.toArray(new String[buttons.size()]), 0);
		return messageDialog.open();
	}

	public void open() {
		if (parent == null)
			throw new NullPointerException("parent shell is null");

		messageDialog = new MessageDialog(parent, messageText, null, informativeText, MessageDialog.QUESTION, buttons.toArray(new String[buttons.size()]), 0);		
		finished(messageDialog.open());
	}

	protected abstract void finished(int button);

	public void dismiss() {
		messageDialog.close();
	}

}
