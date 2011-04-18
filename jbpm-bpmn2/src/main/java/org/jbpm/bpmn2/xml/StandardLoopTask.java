/**
 * Copyright (C) 1999-2011, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 */
package org.jbpm.bpmn2.xml;

import java.io.Serializable;

/**
 * 
 * @author Richie
 * 
 * This class represents the attributes of standard loop task.
 *
 */

public class StandardLoopTask implements Serializable {

    private String condition;
    private String language;
    private String type;
    private int loopCount;
    private Integer loopMaximum;
    private boolean testBefore;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public Integer getLoopMaximum() {
        return loopMaximum;
    }

    public void setLoopMaximum(Integer loopMaximum) {
        this.loopMaximum = loopMaximum > 0 ? loopMaximum : 1;
    }

    public boolean isTestBefore() {
        return testBefore;
    }

    public void setTestBefore(boolean testBefore) {
        this.testBefore = testBefore;
    }
}
