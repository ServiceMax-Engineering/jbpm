/**
 * Copyright 2011 Intalio Inc
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Antoine Toulme
 * <p>
 * Data associations for BPMN 2.0, mapping
 * to dataInputAssociations or dataOutputAssociations.
 * </p>
 */
public class DataAssociation {

    public enum Direction {
        INPUT,OUTPUT;
    }
    
    private String _from;
    private String _to;
    private Direction _direction;
    private List<Assignment> _assignments = new ArrayList<Assignment>();
    
    
    /**
     * @param parameterName
     * @param variableName
     * @param direction
     */
    public DataAssociation(String from, String to, 
            Direction direction) {
        _from = from;
        _to = to;
        _direction = direction;
    }
    
    public String getFrom() {
        return _from;
    }
    
    public String getTo() {
        return _to;
    }
    
    public Direction getDirection() {
        return _direction;
    }
    
    public void addAssignment(Assignment assignment) {
        _assignments.add(assignment);
    }
    
    public void addAssignment(String from, String to) {
        _assignments.add(new Assignment(from, to));
    }
    
    public String getDataInput() {
        if (_direction == Direction.INPUT) {
            return _to;
        }
        return null;
    }
    
    public String getDataOutput() {
        if (_direction == Direction.OUTPUT) {
            return _from;
        }
        return null;
    }
    
    public String getVariable() {
        if (_direction == Direction.INPUT) {
            return _from;
        } else {
            return _to;
        }
    }

    public boolean add(Assignment e) {
        return _assignments.add(e);
    }

    public boolean addAll(Collection<? extends Assignment> c) {
        return _assignments.addAll(c);
    }
    
    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(_assignments);
    }
}
