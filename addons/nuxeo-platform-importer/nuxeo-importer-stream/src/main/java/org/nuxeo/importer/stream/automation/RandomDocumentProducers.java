/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Benoit Delbosc
 */
package org.nuxeo.importer.stream.automation;

import static org.nuxeo.importer.stream.automation.BlobConsumers.DEFAULT_LOG_CONFIG;

import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.importer.stream.message.DocumentMessage;
import org.nuxeo.importer.stream.producer.RandomDocumentMessageProducerFactory;
import org.nuxeo.lib.stream.log.LogManager;
import org.nuxeo.lib.stream.pattern.producer.ProducerPool;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.stream.StreamService;

/**
 * @since 9.1
 */
@Operation(id = RandomDocumentProducers.ID, category = Constants.CAT_SERVICES, label = "Produces random blobs", since = "9.1", description = "Produces random blobs in a Log.")
public class RandomDocumentProducers {
    private static final Log log = LogFactory.getLog(RandomDocumentProducers.class);

    public static final String ID = "StreamImporter.runRandomDocumentProducers";

    public static final String DEFAULT_DOC_LOG_NAME = "import-doc";

    @Context
    protected OperationContext ctx;

    @Param(name = "nbDocuments")
    protected Integer nbDocuments;

    @Param(name = "nbThreads", required = false)
    protected Integer nbThreads = 8;

    @Param(name = "avgBlobSizeKB", required = false)
    protected Integer avgBlobSizeKB = 1;

    @Param(name = "lang", required = false)
    protected String lang = "en_US";

    @Param(name = "logName", required = false)
    protected String logName;

    @Param(name = "logSize", required = false)
    protected Integer logSize;

    @Param(name = "logBlobInfo", required = false)
    protected String logBlobInfoName;

    @Param(name = "logConfig", required = false)
    protected String logConfig;

    @Param(name = "countFolderAsDocument", required = false)
    protected Boolean countFolderAsDocument = true;

    @OperationMethod
    public void run() throws OperationException {
        RandomBlobProducers.checkAccess(ctx);
        StreamService service = Framework.getService(StreamService.class);
        LogManager manager = service.getLogManager(getLogConfig());
        manager.createIfNotExists(getLogName(), getLogSize());
        RandomDocumentMessageProducerFactory factory;
        if (logBlobInfoName != null) {
            factory = new RandomDocumentMessageProducerFactory(nbDocuments, lang, manager, logBlobInfoName,
                    countFolderAsDocument);
        } else {
            factory = new RandomDocumentMessageProducerFactory(nbDocuments, lang, avgBlobSizeKB, countFolderAsDocument);
        }
        try (ProducerPool<DocumentMessage> producers = new ProducerPool<>(getLogName(), manager, factory,
                nbThreads.shortValue())) {
            producers.start().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Operation interrupted");
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            log.error("Operation fails", e);
            throw new OperationException(e);
        }
    }

    protected String getLogConfig() {
        if (logConfig != null) {
            return logConfig;
        }
        return DEFAULT_LOG_CONFIG;
    }

    protected String getLogName() {
        if (logName != null) {
            return logName;
        }
        return DEFAULT_DOC_LOG_NAME;
    }

    protected int getLogSize() {
        if (logSize != null && logSize > 0) {
            return logSize;
        }
        return nbThreads;
    }

}
