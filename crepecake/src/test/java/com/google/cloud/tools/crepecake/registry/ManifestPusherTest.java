/*
 * Copyright 2018 Google Inc.
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

package com.google.cloud.tools.crepecake.registry;

import com.google.cloud.tools.crepecake.http.BlobHttpContent;
import com.google.cloud.tools.crepecake.http.Response;
import com.google.cloud.tools.crepecake.image.json.V22ManifestTemplate;
import com.google.cloud.tools.crepecake.json.JsonTemplateMapper;
import com.google.common.io.Resources;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link ManifestPusher}. */
@RunWith(MockitoJUnitRunner.class)
public class ManifestPusherTest {

  private Path v22manifestJsonFile;
  private V22ManifestTemplate fakeManifestTemplate;
  private ManifestPusher testManifestPusher;

  @Before
  public void setUp() throws URISyntaxException, IOException {
    v22manifestJsonFile = Paths.get(Resources.getResource("json/v22manifest.json").toURI());
    fakeManifestTemplate =
        JsonTemplateMapper.readJsonFromFile(v22manifestJsonFile, V22ManifestTemplate.class);

    testManifestPusher =
        new ManifestPusher(
            new RegistryEndpointProperties("someServerUrl", "someImageName"),
            fakeManifestTemplate,
            "test-image-tag");
  }

  @Test
  public void testGetBodyContent() throws IOException {
    BlobHttpContent body = testManifestPusher.getBodyContent();

    Assert.assertNotNull(body);
    Assert.assertEquals(V22ManifestTemplate.MEDIA_TYPE, body.getType());

    ByteArrayOutputStream bodyCaptureStream = new ByteArrayOutputStream();
    body.writeTo(bodyCaptureStream);
    String v22manifestJson =
        new String(Files.readAllBytes(v22manifestJsonFile), StandardCharsets.UTF_8);
    Assert.assertEquals(
        v22manifestJson, new String(bodyCaptureStream.toByteArray(), StandardCharsets.UTF_8));
  }

  @Test
  public void testHandleResponse() {
    Assert.assertNull(testManifestPusher.handleResponse(Mockito.mock(Response.class)));
  }

  @Test
  public void testApiRoute() throws MalformedURLException {
    Assert.assertEquals(
        new URL("http://someApiBase/manifests/test-image-tag"),
        testManifestPusher.getApiRoute("http://someApiBase"));
  }

  @Test
  public void testGetHttpMethod() {
    Assert.assertEquals("PUT", testManifestPusher.getHttpMethod());
  }

  @Test
  public void testGetActionDescription() {
    Assert.assertEquals(
        "push image manifest for someServerUrl/someImageName:test-image-tag",
        testManifestPusher.getActionDescription());
  }
}