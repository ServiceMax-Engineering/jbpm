/**
 * Copyright 2005 JBoss Inc
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

package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.process.core.Work;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.workflow.core.node.DataAssociation.Direction;

/**
 * Default implementation of a task node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemNode extends StateBasedNode implements Mappable {

	private static final long serialVersionUID = 510l;
	
	private Work work;
	private List<DataAssociation> inMapping = new ArrayList<DataAssociation>();
	private List<DataAssociation> outMapping = new ArrayList<DataAssociation>();
    private boolean waitForCompletion = true;
    // TODO boolean independent (cancel work item if node gets cancelled?)

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}
	
    public DataAssociation addInMapping(String parameterName, String variableName) {
        DataAssociation da = new DataAssociation(variableName, parameterName, Direction.INPUT);
        inMapping.add(da);
        return da;
    }
    
    public DataAssociation addOutMapping(String parameterName, String variableName) {
        DataAssociation da = new DataAssociation(parameterName, variableName, Direction.OUTPUT);
        outMapping.add(da);
        return da;
    }
    
    public List<DataAssociation> getInMappings() {
        return Collections.unmodifiableList(inMapping);
    }

    public List<DataAssociation> getOutMappings() {
        return Collections.unmodifiableList(outMapping);
    }
    
    public DataAssociation getInMapping(String name) {
        for (DataAssociation assoc : inMapping) {
            if (name.equals(assoc.getDataInput())) {
                return assoc;
            }
        }
        return null;
    }
    
    public DataAssociation getOutMapping(String name) {
        for (DataAssociation assoc : outMapping) {
            if (name.equals(assoc.getDataOutput())) {
                return assoc;
            }
        }
        return null;
    }
    
    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }

}
