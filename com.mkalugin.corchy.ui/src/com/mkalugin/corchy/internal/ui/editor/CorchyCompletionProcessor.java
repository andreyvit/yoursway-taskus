package com.mkalugin.corchy.internal.ui.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class CorchyCompletionProcessor implements IContentAssistProcessor {

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		String string = viewer.getDocument().get();
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		Set<String> tags = new HashSet<String>();

		String prefix = string.substring(Math.max(0, offset - 10), offset);
		int atin = prefix.indexOf("@");
		if (atin == -1)
			return new ICompletionProposal[0];

		prefix = prefix.substring(atin);

		Pattern pattern = Pattern.compile("@\\w+");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			String group = matcher.group();
			if (group.startsWith(prefix) && group.length() > prefix.length())
				tags.add(group);
		}

		for (String tag : tags) {
			proposals.add(new CompletionProposal(tag.substring(prefix.length()), offset, 0, tag
					.length()
					- prefix.length()));
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '@' };
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

}
