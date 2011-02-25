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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * 
 * @author Antoine Toulme
 * 
 * A concrete implementation of assignments for dataInputAssociation and
 * dataOutputAssociation.
 *
 */
public class Assignment {

    private String _from;
    private String _to;
    
    public Assignment(String from, String to) {
        _from = from;
        _to = to;
    }
    
    public Object evaluate(Object source, Document target) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression fromExpr = xpath.compile(_from);
        System.err.println(source);
        Node selectedSource = (Node) fromExpr.evaluate(source, XPathConstants.NODE);
        
        System.err.println(selectedSource);
        XPathExpression toExpr = xpath.compile(_to);
        Node selectedTarget = (Node) toExpr.evaluate(target, XPathConstants.NODE);
        if (selectedSource instanceof Attr || selectedSource instanceof Text) {
            if (selectedTarget instanceof Attr) {
                ((Attr) selectedTarget).setValue(selectedSource.getTextContent());
            } else {
                Node copied = target.importNode(selectedSource, true);
                selectedTarget.appendChild(copied);
            }
        } else {
            Node copied = target.importNode(selectedSource, true);
            selectedTarget.getParentNode().appendChild(copied);
        }
        return target;
    }

    public String getFrom() {
        return _from;
    }
    
    public String getTo() {
        return _to;
    }
    
}
