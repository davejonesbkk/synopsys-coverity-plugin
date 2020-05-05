/**
 * synopsys-coverity
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.jenkins.coverity.extensions.pipeline;

import com.synopsys.integration.coverity.ws.WebServiceFactory;
import com.synopsys.integration.jenkins.coverity.CoverityJenkinsStepWorkflow;
import com.synopsys.integration.jenkins.coverity.exception.CoverityJenkinsException;
import com.synopsys.integration.jenkins.coverity.stepworkflow.CoverityWorkflowStepFactory;
import com.synopsys.integration.jenkins.extensions.JenkinsIntLogger;
import com.synopsys.integration.stepworkflow.StepWorkflow;

import hudson.AbortException;

public class CheckForIssuesStepWorkflow extends CoverityJenkinsStepWorkflow<Integer> {
    private final CoverityWorkflowStepFactory coverityWorkflowStepFactory;
    private final String coverityInstanceUrl;
    private final String projectName;
    private final String viewName;
    private final Boolean returnIssueCount;

    public CheckForIssuesStepWorkflow(final JenkinsIntLogger jenkinsIntLogger, final WebServiceFactory webServiceFactory, final CoverityWorkflowStepFactory coverityWorkflowStepFactory, final String coverityInstanceUrl,
        final String projectName, final String viewName, final Boolean returnIssueCount) {
        super(jenkinsIntLogger, webServiceFactory);
        this.coverityWorkflowStepFactory = coverityWorkflowStepFactory;
        this.coverityInstanceUrl = coverityInstanceUrl;
        this.projectName = projectName;
        this.viewName = viewName;
        this.returnIssueCount = returnIssueCount;
    }

    @Override
    protected StepWorkflow<Integer> buildWorkflow() throws AbortException {
        return StepWorkflow.just(coverityWorkflowStepFactory.createStepGetIssuesInView(coverityInstanceUrl, projectName, viewName));
    }

    @Override
    public Integer perform() throws Exception {
        final Integer defectCount = runWorkflow().getDataOrThrowException();
        if (defectCount > 0) {
            final String defectMessage = String.format("[Coverity] Found %s issues in view.", defectCount);
            if (Boolean.TRUE.equals(returnIssueCount)) {
                logger.error(defectMessage);
            } else {
                throw new CoverityJenkinsException(defectMessage);
            }
        }
        return defectCount;
    }

}