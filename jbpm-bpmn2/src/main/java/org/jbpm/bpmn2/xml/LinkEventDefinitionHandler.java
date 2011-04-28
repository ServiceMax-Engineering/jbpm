package org.jbpm.bpmn2.xml;

import org.jbpm.workflow.core.Node;
import org.xml.sax.Attributes;

public class LinkEventDefinitionHandler extends AbstractNodeHandler {

	public Class<?> generateNodeFor() {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

}
