/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Ricardo Dias
 */

package org.nuxeo.ecm.platform.convert.tests;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestZip2HtmlConverter extends BaseConverterTest {

    @Test
    public void testZipConverter() throws IOException {
        testConvertToMimeType("application/zip", false);
        testConvertToMimeType("application/zip", true);
        testConvertToMimeType("application/x-zip-compressed", false);
        testConvertToMimeType("application/x-zip-compressed", true);
    }

    public void testConvertToMimeType(String mimeType, boolean withIndex) throws IOException {
        String converterName = cs.getConverterName(mimeType, "text/html");
        assertEquals("zip2html", converterName);

        checkConverterAvailability(converterName);

        BlobHolder htmlBH = getBlobFromPath(withIndex ? "test-docs/hello-with-index.zip" : "test-docs/hello.zip");
        htmlBH.getBlob().setMimeType(mimeType);
        Map<String, Serializable> parameters = Collections.emptyMap();

        BlobHolder result = cs.convert(converterName, htmlBH, parameters);
        assertNotNull(result);

        List<Blob> blobs = result.getBlobs();
        assertNotNull(blobs);
        assertEquals(3, blobs.size());

        Map<String, String> zipContent = new HashMap<>();
        zipContent.put("hello.xml", "Hello from a xml <b>document</b>");
        zipContent.put("hello.txt", "Hello from a text document");
        Map<String, Long> entryLengths = new HashMap<>();
        entryLengths.put("hello.xml", 85L);
        entryLengths.put("hello.txt", 28L);

        Blob index = blobs.get(0);
        assertEquals("index.html", index.getFilename());
        assertEquals("text/html", index.getMimeType());
        String content = index.getString();
        if (withIndex) {
            // index included in the ZIP
            assertTrue(content, content.contains("This is the index"));
        } else {
            // index generated by the Zip2HtmlConverter
            assertTrue(content, content.contains("<li><a href=\"hello.xml\">hello.xml</a></li>"));
            assertTrue(content, content.contains("<li><a href=\"hello.txt\">hello.txt</a></li>"));
        }

        for (int i = 1; i < blobs.size(); i++) {
            Blob blob = blobs.get(i);
            String filename = blob.getFilename();
            String expected = zipContent.get(filename);
            assertTrue("File " + i + " has unknown filename: " + filename, expected != null);
            try (InputStream in = blob.getStream()) {
                content = IOUtils.toString(in, UTF_8);
            }
            assertTrue("File " + i + " does not contain " + filename + ": " + content, content.contains(expected));
            assertEquals(entryLengths.get(filename), (Long) blob.getLength());
            zipContent.remove(filename);
        }
        assertTrue("Unexpected remaining files: " + zipContent.keySet(), zipContent.isEmpty());
    }
}
