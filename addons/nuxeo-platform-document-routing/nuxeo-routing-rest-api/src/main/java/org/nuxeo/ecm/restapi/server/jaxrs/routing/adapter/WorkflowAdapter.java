/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     <a href="mailto:grenard@nuxeo.com">Guillaume Renard</a>
 *
 */

package org.nuxeo.ecm.restapi.server.jaxrs.routing.adapter;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.ecm.platform.routing.core.io.WorkflowRequest;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;
import org.nuxeo.runtime.api.Framework;

/**
 * @since 7.2
 */
@WebAdapter(name = WorkflowAdapter.NAME, type = "workflowAdapter")
public class WorkflowAdapter extends DefaultAdapter {

    public static final String NAME = "workflow";

    @POST
    public Response doPost(WorkflowRequest routingRequest) {
        DocumentModel doc = getTarget().getAdapter(DocumentModel.class);
        final String workflowInstanceId = Framework.getService(DocumentRoutingService.class)
                                                   .createNewInstance(routingRequest.getWorkflowModelName(),
                                                           Collections.singletonList(doc.getId()),
                                                           routingRequest.getVariables(), ctx.getCoreSession(), true);
        DocumentModel result = getContext().getCoreSession().getDocument(new IdRef(workflowInstanceId));
        DocumentRoute route = result.getAdapter(DocumentRoute.class);
        return Response.ok(route).status(Status.CREATED).build();
    }

    @GET
    public List<DocumentRoute> doGet() {
        DocumentModel doc = getTarget().getAdapter(DocumentModel.class);
        return Framework.getService(DocumentRoutingService.class).getDocumentRelatedWorkflows(doc,
                getContext().getCoreSession());
    }

    @GET
    @Path("{workflowInstanceId}/task")
    public List<Task> doGetTasks(@PathParam("workflowInstanceId") String workflowInstanceId) {
        DocumentModel doc = getTarget().getAdapter(DocumentModel.class);
        return Framework.getService(DocumentRoutingService.class).getTasks(doc,
                null , workflowInstanceId, null, getContext().getCoreSession());
    }

}
