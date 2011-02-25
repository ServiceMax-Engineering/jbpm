/**
 * Copyright 2010 JBoss Inc
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.workflow.core.node.DataAssociation.Direction;

public class Trigger implements Mappable, Serializable {
	
	private static final long serialVersionUID = 510l;
	
	private List<DataAssociation> inMapping = new ArrayList<DataAssociation>();

	public DataAssociation addInMapping(String parameterName, String variableName) {
        DataAssociation da = new DataAssociation(variableName, parameterName, Direction.INPUT);
        inMapping.add(da);
        return da;
    }
	
    public List<DataAssociation> getInMappings() {
        return Collections.unmodifiableList(inMapping);
    }

    public DataAssociation addOutMapping(String subVariableName, String variableName) {
        throw new IllegalArgumentException(
    		"A trigger does not support out mappings");
    }
    
    public List<DataAssociation> getOutMappings() {
        throw new IllegalArgumentException(
        	"A trigger does not support out mappings");
    }

}
