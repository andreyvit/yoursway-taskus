//package com.mkalugin.pikachu.core.workspace;
//
//import com.mkalugin.pikachu.core.astxxxxx.Project;
//import com.mkalugin.pikachu.core.model.ModelSnapshot;
//
//public class WorkspaceSnapshot implements ModelSnapshot {
//
//	private final long time;
//	private final String content;
//	private Project[] projects;
//
//	public WorkspaceSnapshot(long time, String content, Project[] projects) {
//		if (projects == null)
//			throw new NullPointerException("projects is null");
//		if (content == null)
//			throw new NullPointerException("content is null");
//		this.time = time;
//		this.content = content;
//		this.projects = projects;
//	}
//
//	public WorkspaceSnapshot(long time, String content) {
//		this(time, content, new Project[0]);
//	}
//	
//	public void setProjects(Project[] p) {
//		projects = p;
//	}
//
//	public Project[] projects() {
//		return projects;
//	}
//
//	public String content() {
//		return content;
//	}
//
//	public long timeStamp() {
//		return time;
//	}
//
//}
