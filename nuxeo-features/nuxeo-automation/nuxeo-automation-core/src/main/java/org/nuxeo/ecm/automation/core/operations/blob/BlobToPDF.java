/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Tiry
 *     bstefanescu <bs@nuxeo.com>
 *     Estelle Giuly <egiuly@nuxeo.com>
 */
package org.nuxeo.ecm.automation.core.operations.blob;

import java.io.IOException;
import java.util.Collections;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;

/**
 * Save the input document
 */
@Operation(id = BlobToPDF.ID, category = Constants.CAT_CONVERSION, label = "Convert To PDF", description = "Convert the input file to a PDF and return the new file.")
public class BlobToPDF {

    public static final String ID = "Blob.ToPDF";

    @Context
    protected ConversionService service;

    @OperationMethod
    public Blob run(DocumentModel doc) throws IOException {
        BlobHolder bh = doc.getAdapter(BlobHolder.class);
        if (bh == null) {
            return null;
        }
        return service.convertToMimeType(MimetypeRegistry.PDF_MIMETYPE, bh, Collections.emptyMap()).getBlob();
    }

    @OperationMethod
    public Blob run(Blob blob) throws IOException {
        return service.convertToMimeType(MimetypeRegistry.PDF_MIMETYPE, new SimpleBlobHolder(blob),
                Collections.emptyMap()).getBlob();
    }

    @OperationMethod
    public BlobList run(BlobList blobs) throws IOException {
        BlobList bl = new BlobList();
        for (Blob blob : blobs) {
            bl.add(this.run(blob));
        }
        return bl;
    }

}
