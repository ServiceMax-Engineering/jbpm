package org.jbpm.bpmn2.xml;

import org.drools.xml.Handler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.xml.sax.Attributes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CatchLinkNodeHandler extends AbstractNodeHandler implements
		Handler {

	public Class<?> generateNodeFor() {
		return CatchLinkNode.class;
	}

	@Override
	protected Node createNode(Attributes attrs) {
		throw new NotImplementedException();
	}

	@Override
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		CatchLinkNode linkNode = (CatchLinkNode) node;
		xmlDump.append("<intermediateCatchEvent id=\""
				+ XmlBPMNProcessDumper.getUniqueNodeId(node)
				+ "_ThrowEvent\" name=\"\" >" + EOL);
		writeNode("linkEventDefinition", linkNode, xmlDump, metaDataType);
		xmlDump.append(">" + EOL);
		xmlDump.append(String.format("<target>%s</target>",
				linkNode.getMetaData("target"))
				+ EOL);
		endNode("linkEventDefinition", xmlDump);
		xmlDump.append("</intermediateCatchEvent>" + EOL);

	}

}
