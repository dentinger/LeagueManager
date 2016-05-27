package org.dentinger.tutorial.util;

import java.util.UUID;
import org.neo4j.ogm.typeconversion.AttributeConverter;

/**
 * Created by ss65560 on 5/25/16.
 */
public class UUIDConverter implements AttributeConverter<UUID, String>
{
  @Override public String toGraphProperty(UUID value) {
    return value.toString();
  }

  @Override public UUID toEntityAttribute(String value) {
    return UUID.fromString(value);
  }
}