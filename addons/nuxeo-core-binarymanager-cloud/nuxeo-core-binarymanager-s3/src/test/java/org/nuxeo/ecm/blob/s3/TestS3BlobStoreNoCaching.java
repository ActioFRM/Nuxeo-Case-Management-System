/*
 * (C) Copyright 2019 Nuxeo (http://nuxeo.com/) and others.
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
 *     Florent Guillaume
 */
package org.nuxeo.ecm.blob.s3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.nuxeo.runtime.test.runner.Deploy;

@Deploy("org.nuxeo.ecm.core.storage.binarymanager.s3.tests:OSGI-INF/test-blob-provider-s3-nocaching.xml")
public class TestS3BlobStoreNoCaching extends TestS3BlobStoreAbstract {

    // this no-caching setup is not expected to be used in production,
    // but it's still useful to test the compliance of the base S3 implementation

    @Test
    public void testFlags() {
        assertFalse(bp.isTransactional());
        assertFalse(bp.isRecordMode());
        assertTrue(bs.getKeyStrategy().useDeDuplication());
    }

    @Override
    public void testBlobGetFile() {
        throw new AssumptionViolatedException("S3BlobStore with no caching has no File");
    }

}
