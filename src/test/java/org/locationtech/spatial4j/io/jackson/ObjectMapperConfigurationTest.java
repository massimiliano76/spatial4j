/*******************************************************************************
 * Copyright (c) 2020 David Smiley
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0 which
 * accompanies this distribution and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/

package org.locationtech.spatial4j.io.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObjectMapperConfigurationTest {

  public static class GeomWrapper {

    private Geometry geometry;

    public Geometry getGeometry() {
      return geometry;
    }

    public void setGeometry(Geometry geometry) {
      this.geometry = geometry;
    }
  }

  private static Geometry createGeometry(String wkt) throws ParseException {
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    WKTReader wktReader = new WKTReader(geometryFactory);
    return wktReader.read(wkt);
  }

  @Test
  public void test() throws Exception {
    final String wkt = "POINT (30 10)";
    final Geometry geometry = createGeometry(wkt);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.registerModule(new ShapesAsWKTModule());
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    final GeomWrapper toSerialize = new GeomWrapper();
    toSerialize.setGeometry(geometry);
    String json = objectMapper.writeValueAsString(toSerialize);
    System.out.println(json);
    assertEquals("{\"geometry\":\"POINT (30 10)\"}", json);

    final GeomWrapper deserialized = objectMapper.readValue(json, GeomWrapper.class);
    assertNotNull(deserialized.getGeometry());
  }
}