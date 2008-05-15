package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

public class CorchyAutoEditStrategy implements IAutoEditStrategy {
    
    public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
        if (command.length == 0 && command.text.equals("\n"))
            customizeNewLineInsertion(document, command);
    }
    
    private void customizeNewLineInsertion(IDocument document, DocumentCommand command) {
        try {
            int line = document.getLineOfOffset(command.offset);
            int lineStart = document.getLineOffset(line);
            int lineLength = document.getLineLength(line);
            String lineText = document.get(lineStart, lineLength);
            if (lineText.trim().equals("-")) {
                // remove "- " on the second Enter hit
                command.offset = lineStart;
                command.length = 2;
                command.text = "";
            } else if (lineText.startsWith("- ") || lineText.trim().endsWith(":")) {
                command.text = "\n- ";
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
    }
    
}
