/*
 * (C) Copyright 2006-2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.core.event.test.virusscan;

public interface VirusScanConsts {

    public static final String VIRUS_SCAN_NEEDED_EVENT = "virusScanNeeded";

    public static final String VIRUSSCAN_FACET = "VIRUSSCAN";

    public static final String VIRUSSCAN_STATUS_PROP = "vscan:scanStatus";

    public static final String VIRUSSCAN_STATUS_PENDING = "pending";

    public static final String VIRUSSCAN_STATUS_DONE = "done";

    public static final String VIRUSSCAN_STATUS_FAILED = "failed";

    public static final String VIRUSSCAN_DATE_PROP = "vscan:scanDate";

    public static final String VIRUSSCAN_INFO_PROP = "vscan:scanInfo";

    public static final String VIRUSSCAN_OK_PROP = "vscan:virusFree";

    public static final String DISABLE_VIRUSSCAN_LISTENER = "disableVirusScanListener";

}
