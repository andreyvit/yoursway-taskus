/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.model.Document;

public class DocumentSavingAgent implements DocumentListener {
    
    /**
     * Wait for this many milliseconds after a text change until saving.
     */
    private final static int SAVE_DELAY = 500;
    
    /**
     * Even if we continue active typing, save nevertheless if this many
     * milliseconds have passed.
     */
    private final static int SAVE_DEADLINE = 2000;
    
    private final Document document;
    private SaveThread thread;
    
    public DocumentSavingAgent(Document document) {
        this.document = document;
        document.addListener(this);
        thread = new SaveThread(document);
        thread.start();
    }
    
    public void bindingChanged() {
    }
    
    public void contentChanged(Object sender) {
        thread.wakeUpNeoFollowTheWhiteRabbitKnockKnock();
    }
    
    public void dispose() {
        document.removeListener(this);
    }
    
    public void closed(boolean discarded) {
        thread.iWannaGoHomeTakeOffThisUniformAndLeaveTheShow();
    }
    
    public void emptinessChanged() {
    }
    
    static class SaveThread extends Thread {
        
        private final Document document;
        
        private boolean saveRequested = false;
        
        private boolean disposed = false;
        
        public SaveThread(Document document) {
            if (document == null)
                throw new NullPointerException("document is null");
            this.document = document;
            setDaemon(true);
            setName("Autosave");
        }
        
        public synchronized void wakeUpNeoFollowTheWhiteRabbitKnockKnock() {
            saveRequested = true;
            notify();
        }
        
        public synchronized void iWannaGoHomeTakeOffThisUniformAndLeaveTheShow() {
            disposed = true;
            notify();
        }
        
        @Override
        public void run() {
            long nextSaveDeadline = -1;
            long nextSaveDelayExpiry = -1;
            while (true) {
                synchronized (this) {
                    long now = System.currentTimeMillis();
                    while (!disposed
                            && (nextSaveDeadline < 0 || (now < nextSaveDeadline && now < nextSaveDelayExpiry))) {
                        try {
                            if (nextSaveDeadline < 0)
                                wait();
                            else
                                wait(Math.min(nextSaveDeadline, nextSaveDelayExpiry) - now);
                        } catch (InterruptedException e) {
                        }
                        now = System.currentTimeMillis();
                        if (saveRequested) {
                            if (nextSaveDeadline < 0)
                                nextSaveDeadline = now + SAVE_DEADLINE;
                            nextSaveDelayExpiry = now + SAVE_DELAY;
                            saveRequested = false;
                        }
                    }
                    if (disposed)
                        return;
                    nextSaveDeadline = -1;
                    nextSaveDelayExpiry = -1;
                }
                try {
                    document.save();
                } catch (IOException e) {
                    // FIXME handle saving errors more gracefully
                    e.printStackTrace(System.err);
                }
            }
        }
        
    }
    
}