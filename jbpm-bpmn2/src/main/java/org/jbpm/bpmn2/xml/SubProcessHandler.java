/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.kie.api.definition.process.Connection;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubProcessHandler extends AbstractNodeHandler {

	private Map<String, String> dataInputs = new HashMap<String, String>();
	private Map<String, String> dataOutputs = new HashMap<String, String>();
    
    
    @Override
    protected Node createNode(Attributes attrs) {
    	CompositeContextNode subProcessNode = new CompositeContextNode();    	
        String eventSubprocessAttribute = attrs.getValue("triggeredByEvent");
        if (eventSubprocessAttribute != null && Boolean.parseBoolean(eventSubprocessAttribute)) {            
            subProcessNode = new EventSubProcessNode();
    	}
        VariableScope variableScope = new VariableScope();
        subProcessNode.addContext(variableScope);
        subProcessNode.setDefaultContext(variableScope);
        
        String compensation = attrs.getValue("isForCompensation");
        if( compensation != null ) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if( isForCompensation ) { 
                subProcessNode.setMetaData("isForCompensation", isForCompensation );
            }
        }        
        subProcessNode.setAutoComplete(true);
        return subProcessNode;
    }
    
    @Override
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
		return CompositeContextNode.class;
	}

	@Override
    protected void handleNode(final Node node, final Element element,
			final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		super.handleNode(node, element, uri, localName, parser);
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("ioSpecification".equals(nodeName)) {
				readIoSpecification(xmlNode, dataInputs, dataOutputs);
			} else if ("dataInputAssociation".equals(nodeName)) {
				readDataInputAssociation(xmlNode, (CompositeNode) node,
						dataInputs);
			}
			xmlNode = xmlNode.getNextSibling();
		}
	}

	@Override
    protected void readIoSpecification(org.w3c.dom.Node xmlNode,
			Map<String, String> dataInputs, Map<String, String> dataOutputs) {
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		while (subNode instanceof Element) {
			String subNodeName = subNode.getNodeName();
			if ("dataInput".equals(subNodeName)) {
				String id = ((Element) subNode).getAttribute("id");
				String inputName = ((Element) subNode).getAttribute("name");
				dataInputs.put(id, inputName);
			}
			if ("dataOutput".equals(subNodeName)) {
				String id = ((Element) subNode).getAttribute("id");
				String outputName = ((Element) subNode).getAttribute("name");
				dataOutputs.put(id, outputName);
			}
			subNode = subNode.getNextSibling();
		}
	}

	protected void readDataInputAssociation(org.w3c.dom.Node xmlNode,
			CompositeNode node, Map<String, String> dataInputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		if ("sourceRef".equals(subNode.getNodeName())) {
			String source = subNode.getTextContent();
			// targetRef
			subNode = subNode.getNextSibling();
			String target = subNode.getTextContent();
			subNode = subNode.getNextSibling();
			List<Assignment> assignments = new LinkedList<Assignment>();
			while (subNode != null) {
				org.w3c.dom.Node ssubNode = subNode.getFirstChild();
				String from = ssubNode.getTextContent();
				String to = ssubNode.getNextSibling().getTextContent();
				assignments.add(new Assignment(((Element) xmlNode).getAttribute("language"), from, to));

				subNode = subNode.getNextSibling();
			}
			node.addInAssociation(new DataAssociation(source, dataInputs
					.get(target), assignments, null));
		}
	}

	@Override
    public Object end(final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Node node = (Node) parser.getCurrent();
		
		// determine type of event definition, so the correct type of node can be generated
		boolean found = false;		
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			CompositeContextNode subProcess = (CompositeContextNode) node;
			 if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
			    Boolean isAsync = Boolean.parseBoolean((String)node.getMetaData().get("customAsync"));
				// create new timerNode
				ForEachNode forEachNode = new ForEachNode();
				forEachNode.setId(node.getId());
				forEachNode.setName(node.getName());
				
				forEachNode.setAutoComplete(subProcess.isAutoComplete());
				
				for (org.kie.api.definition.process.Node subNode: subProcess.getNodes()) {
			
					forEachNode.addNode(subNode);
				}
				forEachNode.setMetaData("UniqueId", subProcess.getMetaData("UniqueId"));
				forEachNode.setMetaData(ProcessHandler.CONNECTIONS, subProcess.getMetaData(ProcessHandler.CONNECTIONS));
				VariableScope v = (VariableScope) subProcess.getDefaultContext(VariableScope.VARIABLE_SCOPE);
				((VariableScope) ((CompositeContextNode) forEachNode.internalGetNode(2)).getDefaultContext(VariableScope.VARIABLE_SCOPE)).setVariables(v.getVariables());
				node = forEachNode;
				handleForEachNode(node, element, uri, localName, parser, isAsync);
				found = true;
				break;
			}
			if ("standardLoopCharacteristics".equals(nodeName)) {
				CompositeNode composite = new CompositeContextNode();
				composite.setId(node.getId());
				composite.setName(node.getName());
				composite.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));

				StartNode start = new StartNode();
				composite.addNode(start);

				Join join = new Join();
				join.setType(Join.TYPE_XOR);
				composite.addNode(join);

				Split split = new Split(Split.TYPE_XOR);
				composite.addNode(split);

				node.setId(4);
				composite.addNode(node);

				EndNode end = new EndNode();
				composite.addNode(end);
				end.setTerminate(false);

				new ConnectionImpl(composite.getNode(1), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
						composite.getNode(2), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
				new ConnectionImpl(composite.getNode(2), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
						composite.getNode(3), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
				Connection c1 = new ConnectionImpl(composite.getNode(3),
						org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, composite.getNode(4),
						org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
				new ConnectionImpl(composite.getNode(4), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
						composite.getNode(2), org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
				Connection c2 = new ConnectionImpl(composite.getNode(3),
						org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, composite.getNode(5),
						org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);

				start.setMetaData("hidden", true);
				join.setMetaData("hidden", true);
				split.setMetaData("hidden", true);
				end.setMetaData("hidden", true);

				String language = "XPath";
				if (((Element) xmlNode.getFirstChild()).getAttribute("language") != null
						&& !"".equals(((Element) xmlNode.getFirstChild()).getAttribute("language"))) {
					language = ((Element) xmlNode.getFirstChild()).getAttribute("language");
				}
				ConstraintImpl cons1 = new ConstraintImpl();
				cons1.setDialect(language);
				cons1.setConstraint(xmlNode.getFirstChild().getTextContent());
				cons1.setType("code");
				cons1.setName("");
				split.setConstraint(c1, cons1);

				ConstraintImpl cons2 = new ConstraintImpl();
				cons2.setDialect(language);
				cons2.setConstraint("");
				cons2.setType("code");
				cons2.setDefault(true);

				cons2.setName("");
				split.setConstraint(c2, cons2);

				super.handleNode(node, element, uri, localName, parser);

				List<SequenceFlow> connections = (List<SequenceFlow>) node.getMetaData()
						.get(ProcessHandler.CONNECTIONS);

				ProcessHandler.linkConnections(subProcess, connections);

				ProcessHandler.linkBoundaryEvents(subProcess);

				node = composite;
				found = true;
				break;
			}
			xmlNode = xmlNode.getNextSibling();
		}
		if (!found) {
			handleCompositeContextNode(node, element, uri, localName, parser);
		}
		
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);

		return node;
	}
    
	protected void readDataInputAssociation(org.w3c.dom.Node xmlNode,
			ForEachNode forEachNode) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String inputVariable = subNode.getTextContent();
		if (inputVariable != null && inputVariable.trim().length() > 0) {
			forEachNode.setCollectionExpression(inputVariable);
		}
	}

	@Override
    @SuppressWarnings("unchecked")
	protected void readMultiInstanceLoopCharacteristics(
			org.w3c.dom.Node xmlNode, ForEachNode forEachNode,
			ExtensibleXmlParser parser) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		while (subNode != null) {
			String nodeName = subNode.getNodeName();
			if ("inputDataItem".equals(nodeName)) {
				String variableName = ((Element) subNode).getAttribute("id");
				String itemSubjectRef = ((Element) subNode)
						.getAttribute("itemSubjectRef");
				DataType dataType = null;
				Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>) ((ProcessBuildData) parser
						.getData()).getMetaData("ItemDefinitions");
				if (itemDefinitions != null) {
					ItemDefinition itemDefinition = itemDefinitions
							.get(itemSubjectRef);
					if (itemDefinition != null) {
						dataType = new ObjectDataType(
								itemDefinition.getStructureRef());
					}
				}
				if (dataType == null) {
					dataType = new ObjectDataType("java.lang.Object");
				}
				if (variableName != null && variableName.trim().length() > 0) {
					forEachNode.setVariable(variableName, dataType);
				}
			} else if ("loopDataInputRef".equals(nodeName)) {
				String inputVariable = subNode.getFirstChild().getTextContent();
				if (inputVariable != null && inputVariable.trim().length() > 0) {
//					forEachNode.setCollectionExpression(dataInputs
//							.get(inputVariable));
					forEachNode.setCollectionExpression(inputVariable);
				}
			}
			subNode = subNode.getNextSibling();
		}
	}

	protected void handleForEachNodeWithDataAssociations(final Node node,
			final Element element, final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		ForEachNode forEachNode = (ForEachNode) node;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
				readMultiInstanceLoopCharacteristicsWithDataAssociations(
						xmlNode, forEachNode, parser);
			}
			xmlNode = xmlNode.getNextSibling();
		}
		List<SequenceFlow> connections = (List<SequenceFlow>) forEachNode
				.getMetaData(ProcessHandler.CONNECTIONS);
		ProcessHandler.linkConnections(forEachNode, connections);
		ProcessHandler.linkBoundaryEvents(forEachNode);
	}

	protected void readMultiInstanceLoopCharacteristicsWithDataAssociations(
			org.w3c.dom.Node xmlNode, ForEachNode forEachNode,
			ExtensibleXmlParser parser) {
	    String isParallel = xmlNode.getAttributes().getNamedItem("isParallel").getNodeValue();
	    if(isParallel != null) {
	    	forEachNode.setParallel(isParallel.equals("true"));
	    }
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		while (subNode != null) {
			String nodeName = subNode.getNodeName();
			if ("inputDataItem".equals(nodeName)) {
				String variableName = ((Element) subNode).getAttribute("id");
				String itemSubjectRef = ((Element) subNode)
						.getAttribute("itemSubjectRef");
				DataType dataType = null;
				Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>) ((ProcessBuildData) parser
						.getData()).getMetaData("ItemDefinitions");
				if (itemDefinitions != null) {
					ItemDefinition itemDefinition = itemDefinitions
							.get(itemSubjectRef);
					if (itemDefinition != null) {
						dataType = new ObjectDataType(
								itemDefinition.getStructureRef());
					}
				}
				if (dataType == null) {
					dataType = new ObjectDataType("java.lang.Object");
				}
				if (variableName != null && variableName.trim().length() > 0) {
					forEachNode.setVariable(variableName, dataType);
				}
			} else if ("loopDataInputRef".equals(nodeName)) {
				String inputVariable = subNode.getFirstChild().getTextContent();
				if (inputVariable != null && inputVariable.trim().length() > 0) {
//					forEachNode.setCollectionExpression(dataInputs
//							.get(inputVariable));
					forEachNode.setCollectionExpression(inputVariable);
				}
			}
			subNode = subNode.getNextSibling();
		}
	}

    @SuppressWarnings("unchecked")
	protected void handleCompositeContextNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			compositeNode.getMetaData(ProcessHandler.CONNECTIONS);
    	
    	handleScript(compositeNode, element, "onEntry");
        handleScript(compositeNode, element, "onExit");
    	
    	List<IntermediateLink> throwLinks = (List<IntermediateLink>) compositeNode.getMetaData(ProcessHandler.LINKS);
    	ProcessHandler.linkIntermediateLinks(compositeNode, throwLinks);	
    	
    	ProcessHandler.linkConnections(compositeNode, connections);
    	ProcessHandler.linkBoundaryEvents(compositeNode);
    	
        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) compositeNode.getMetaData(ProcessHandler.ASSOCIATIONS);
        ProcessHandler.linkAssociations((Definitions) compositeNode.getMetaData("Definitions"), compositeNode, associations);
        
        // TODO: do we fully support interruping ESP's? 
        /** 
        for( org.kie.api.definition.process.Node subNode : compositeNode.getNodes() ) { 
            if( subNode instanceof StartNode ) { 
                if( ! ((StartNode) subNode).isInterrupting() ) { 
                    throw new IllegalArgumentException("Non-interrupting event subprocesses are not yet fully supported." );
                }
            }
        }
        */
        
    }
    
    @SuppressWarnings("unchecked")
	protected void handleForEachNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, boolean isAsync) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	ForEachNode forEachNode = (ForEachNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("ioSpecification".equals(nodeName)) {
                readIoSpecification(xmlNode, dataInputs, dataOutputs);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, inputAssociation);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, outputAssociation);
            } else if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
            	readMultiInstanceLoopCharacteristics(xmlNode, forEachNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
        }
        handleScript(forEachNode, element, "onEntry");
        handleScript(forEachNode, element, "onExit");
        
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			forEachNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(forEachNode, connections);
    	ProcessHandler.linkBoundaryEvents(forEachNode);
    
    	
        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) forEachNode.getMetaData(ProcessHandler.ASSOCIATIONS);
        ProcessHandler.linkAssociations((Definitions) forEachNode.getMetaData("Definitions"), forEachNode, associations);
        applyAsync(node, isAsync);
    }  
    
    protected void applyAsync(Node node, boolean isAsync) {
        for (org.kie.api.definition.process.Node subNode: ((CompositeContextNode) node).getNodes()) {
            if (isAsync) {
                List<Connection> incoming = subNode.getIncomingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE);
                if (incoming != null) {
                    for (Connection con : incoming) {
                        if (con.getFrom() instanceof StartNode) {
                            ((Node)subNode).setMetaData("customAsync", Boolean.toString(isAsync));
                            return;
                        }
                    }
                }
                
            }            
        }
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
