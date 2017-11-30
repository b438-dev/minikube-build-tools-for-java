/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.crepecake.json.templates;

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.json.JsonParser;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link V21ManifestTemplate}. */
public class V21ManifestTemplateTest {

  @Test
  public void testFromJson() throws URISyntaxException, IOException, DigestException {
    // Loads the JSON string.
    File jsonFile =
        new File(getClass().getClassLoader().getResource("json/v21manifest.json").toURI());

    // Deserializes into a manifest JSON object.
    V21ManifestTemplate manifestJson =
        JsonParser.readJsonFromFile(jsonFile, V21ManifestTemplate.class);

    Assert.assertEquals(
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad"),
        manifestJson.getLayerDigest(0));

    Assert.assertEquals("some v1-compatible object", manifestJson.getV1Compatibility(0));
  }
}