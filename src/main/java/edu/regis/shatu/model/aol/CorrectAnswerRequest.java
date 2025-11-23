/*
 * SHATU: SHA-256 Tutor
 *
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 *
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 *
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.model.aol;

/**
 * Encapsulates a request to record that the student asked to see the correct answer.
 */
public class CorrectAnswerRequest {
    /**
     * The knowledge component identifier associated with the completed step.
     */
    private int knowledgeComponentId;

    public CorrectAnswerRequest() {
    }

    public CorrectAnswerRequest(int knowledgeComponentId) {
        this.knowledgeComponentId = knowledgeComponentId;
    }

    public int getKnowledgeComponentId() {
        return knowledgeComponentId;
    }

    public void setKnowledgeComponentId(int knowledgeComponentId) {
        this.knowledgeComponentId = knowledgeComponentId;
    }
}
