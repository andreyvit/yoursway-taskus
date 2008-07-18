package com.mkalugin.corchy.internal.ui.editor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.ADocumentLevelVisitor;
import com.mkalugin.pikachu.core.ast.AEmptyLine;
import com.mkalugin.pikachu.core.ast.ANode;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.ast.ATag;
import com.mkalugin.pikachu.core.ast.ATaskDescriptionFragment;
import com.mkalugin.pikachu.core.ast.ATaskLeader;
import com.mkalugin.pikachu.core.ast.ATaskLevelNode;
import com.mkalugin.pikachu.core.ast.ATaskLevelVisitor;
import com.mkalugin.pikachu.core.ast.ATaskLine;
import com.mkalugin.pikachu.core.ast.ATaskName;
import com.mkalugin.pikachu.core.ast.ATextLine;
import com.mkalugin.pikachu.core.controllers.search.SearchResult;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;

public class SwtCocoaSourceView implements SourceView {

	private CorchyViewer sourceViewer;
	private Document document;
	private DocumentStylesheet stylesheet;
	private SourceViewCallback callback;

	public SwtCocoaSourceView(Composite parent) {
		createControls(parent);
	}

	private void createControls(Composite parent) {
		stylesheet = new DefaultDocumentStylesheet(parent.getDisplay());
		sourceViewer = new CorchyViewer(parent);
		sourceViewer.getTextWidget().setFocus();
		document = createDocument();
		sourceViewer.setDocument(document);
		document.addDocumentListener(new IDocumentListener() {

			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			public void documentChanged(DocumentEvent event) {
				saveDocument();
			}

		});
		sourceViewer.getTextWidget().addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent e) {
				if (callback != null) {
					Point range = sourceViewer.getTextWidget().getSelectionRange();
					callback.selectionChanged(range.x, range.x + range.y);
				}
			}

		});
		sourceViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (callback != null && selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					callback.selectionChanged(textSelection.getOffset(), textSelection.getOffset()
							+ textSelection.getLength() - 1);
				}
			}

		});
	}

	protected void saveDocument() {
		callback.setText(document.get());
	}

	private Document createDocument() {
		return new Document("");
	}

	public void setLayoutData(Object editorData) {
		sourceViewer.getControl().setLayoutData(editorData);
	}

	public boolean isActive() {
		return sourceViewer.getControl().isFocusControl();
	}

	public void undo() {
		if (sourceViewer.getUndoManager().undoable())
			sourceViewer.getUndoManager().undo();
	}

	public void redo() {
		if (sourceViewer.getUndoManager().redoable())
			sourceViewer.getUndoManager().redo();
	}

	private boolean settingPresentationFirstTime = true;
	private List<Control> perProjectConrols = new ArrayList<Control>();

	@SuppressWarnings("unchecked")
	protected void updateHighlighting(final ADocument document) {
		TextPresentation presentation = new TextPresentation();
		for (ADocumentLevelNode p : document.getChildren())
			highlight(presentation, p);
		final ArrayList<StyleRange> ranges = newArrayList((Iterator<StyleRange>) presentation
				.getAllStyleRangeIterator());
		final List<Integer> offsets = new ArrayList<Integer>();
		final List<ARange> projectLines = new ArrayList<ARange>();
		final boolean[] flag = { false };
		for (ADocumentLevelNode p : document.getChildren()) {
			p.accept(new ADocumentLevelVisitor() {

				public void visitEmptyLine(AEmptyLine line) {
					if (flag[0])
						offsets.add(line.range().start());
				}

				public void visitProjectLine(AProjectLine line) {
					flag[0] = true;
					projectLines.add(line.range());
				}

				public void visitTaskLine(ATaskLine line) {
					if (flag[0])
						offsets.add(line.range().start());
				}

				public void visitTextLine(ATextLine line) {
					if (flag[0])
						offsets.add(line.range().start());
				}

			});
		}

		Runnable runnable = new Runnable() {

			public void run() {
				StyledText textWidget = sourceViewer.getTextWidget();

				textWidget.setLineIndent(0, textWidget.getLineCount(), 0);
				IDocument doc = sourceViewer.getDocument();
				for (int off : offsets) {
					try {
						int ln = doc.getLineOfOffset(off);
						textWidget.setLineIndent(ln, 1, 20);
					} catch (BadLocationException e) {
					}

				}
				textWidget.setStyleRanges((StyleRange[]) ranges.toArray(new StyleRange[ranges
						.size()]));

				for (Control c : perProjectConrols)
					c.dispose();

				perProjectConrols.clear();

				Rectangle clientArea = textWidget.getClientArea();
				for (int i = 0; i < projectLines.size(); i++) {
					final ARange r = projectLines.get(i);
					final ARange r2 = (i < projectLines.size() - 1) ? projectLines.get(i + 1)
							: null;
					Rectangle bounds = textWidget.getTextBounds(r.end(), r.end());
					InEditorButton button = new InEditorButton(textWidget);
					button.setText("sync it");
					button.setBounds(clientArea.width - 150, bounds.y + 5, 70, 20);
					button.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW));
					perProjectConrols.add(button);

					final InEditorButton button2 = new InEditorButton(textWidget);
					button2.setText("focus");
					button2.setBounds(clientArea.width - 70, bounds.y + 5, 65, 20);
					button2.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW));
					button2.addMouseListener(new MouseListener() {

						boolean active = false;

						public void mouseDoubleClick(MouseEvent e) {
							// TODO Auto-generated method stub

						}

						public void mouseDown(MouseEvent e) {
							// TODO Auto-generated method stub

						}

						public void mouseUp(MouseEvent e) {
							if (!active) {
								int end = (r2 == null) ? (document.range().end() - 1)
										: (r2.start() - 1);
								sourceViewer.setFocusRange(r.start(), end);
								button2.setText("unfocus");
								sourceViewer.getTextWidget().redraw();
							} else {
								sourceViewer.resetFocusRange();
								button2.setText("focus");
								sourceViewer.getTextWidget().redraw();
							}
							active = !active;
						}

					});
					perProjectConrols.add(button2);
				}
			}

		};
		if (settingPresentationFirstTime) {
			sourceViewer.getTextWidget().getDisplay().asyncExec(runnable);
			settingPresentationFirstTime = false;
		} else
			runnable.run();
	}

	private void highlight(final TextPresentation presentation, ADocumentLevelNode node) {
		node.accept(new ADocumentLevelVisitor() {

			public void visitEmptyLine(AEmptyLine line) {
				highlightText(presentation, line);
			}

			public void visitProjectLine(AProjectLine line) {
				highlightProject(presentation, line);
			}

			public void visitTaskLine(ATaskLine line) {
				highlightTask(presentation, line);
			}

			public void visitTextLine(ATextLine line) {
				highlightText(presentation, line);
			}

		});
	}

	protected void highlightProject(TextPresentation presentation, AProjectLine project) {
		StyleRange style = new StyleRange();
		ARange range = project.range();
		style.start = range.start();
		style.length = range.length();
		stylesheet.styleProject(style);
		presentation.addStyleRange(style);
	}

	protected void highlightText(TextPresentation presentation, ANode node) {
		StyleRange style = new StyleRange();
		ARange range = node.range();
		style.start = range.start();
		style.length = range.length();
		stylesheet.styleText(style);
		presentation.addStyleRange(style);
	}

	protected void highlightTask(TextPresentation presentation, ATaskLine task) {
		boolean isDone = task.isDone();
		for (ATaskLevelNode node : task.getChildren())
			highlight(presentation, node, isDone);
	}

	private void highlight(final TextPresentation presentation, ATaskLevelNode node,
			final boolean isDone) {
		node.accept(new ATaskLevelVisitor() {

			public void visitDescriptionFragment(ATaskDescriptionFragment fragment) {
				highlightTaskText(presentation, fragment, false);
			}

			public void visitLeader(ATaskLeader leader) {
				highlightTaskText(presentation, leader, false);
			}

			public void visitName(ATaskName name) {
				highlightTaskText(presentation, name, isDone);
			}

			public void visitTag(ATag tag) {
				highlightTaskTag(presentation, tag);
			}

		});
	}

	protected void highlightTaskText(TextPresentation presentation, ATaskLevelNode tag,
			boolean isDone) {
		StyleRange style = new StyleRange();
		ARange range = tag.range();
		style.start = range.start();
		style.length = range.length();
		if (isDone)
			stylesheet.styleDoneTask(style);
		else
			stylesheet.styleTask(style);
		presentation.addStyleRange(style);
	}

	protected void highlightTaskTag(TextPresentation presentation, ATag tag) {
		StyleRange style = new StyleRange();
		ARange range = tag.range();
		style.start = range.start();
		style.length = range.length();
		stylesheet.styleTag(style);
		presentation.addStyleRange(style);
	}

	public void dispose() {
		stylesheet.dispose();
	}

	public void setText(final String text) {
		document.set(text);
		sourceViewer.getTextWidget().notifyListeners(SWT.Modify, new Event());
	}

	public SourceView bind(SourceViewCallback callback) {
		if (callback == null)
			throw new NullPointerException("callback is null");
		if (this.callback != null)
			throw new IllegalStateException("callback is already set");
		this.callback = callback;
		return this;
	}

	public void highlightAccordingTo(ADocument documentNode) {
		updateHighlighting(documentNode);
	}

	public void highlightSearchResults(SearchResult result) {
		sourceViewer.highlightSearchResults(result);
	}

	public void highlightMatch(int number) {
		sourceViewer.highlightMatch(number);
	}

	public void setSelection(int start, int end) {
		sourceViewer.setSelectionTo(start, end);
	}

	public void setFocus() {
		sourceViewer.getControl().setFocus();
	}

}
