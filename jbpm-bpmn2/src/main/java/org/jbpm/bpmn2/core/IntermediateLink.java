package org.jbpm.bpmn2.core;

import java.io.Serializable;

public class IntermediateLink implements Serializable {

	private String id;
	private String name;
	private String target;
	// TODO: should be a list of sources
	private String sources;

	public IntermediateLink() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getSources() {
		return sources;
	}

}
