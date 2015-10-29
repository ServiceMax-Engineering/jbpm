package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.DataTransformer;

public class EventNodeBuilder implements ProcessNodeBuilder {

	@Override
	public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node) {
		 Transformation transformation = (Transformation) node.getMetaData().get("Transformation");
			if (transformation != null) {
				WorkflowProcess wfProcess = (WorkflowProcess) process;
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("imports", wfProcess.getImports());
				parameters.put("classloader", context.getConfiguration().getClassLoader());
				
				DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
				transformation.setCompiledExpression(transformer.compile(transformation.getExpression(), parameters));
			}
	        ExtendedNodeImpl extendedNode = ( ExtendedNodeImpl ) node;
	        for (String type: extendedNode.getActionTypes()) {
	        	List<DroolsAction> actions = extendedNode.getActions(type);
	        	if (actions != null) {
		        	for (DroolsAction droolsAction: actions) {
		                buildAction(droolsAction, context, (NodeImpl) node);
		        	}
	        	}
	        }
	}
	
	
	protected void buildAction(DroolsAction droolsAction, ProcessBuildContext context, NodeImpl node) {
    	DroolsConsequenceAction action = (DroolsConsequenceAction) droolsAction;
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );            
        if (actionDescr.getText() != null) {
    	  dialect.getActionBuilder().build( context, action, actionDescr, node);
        }
    }

}
