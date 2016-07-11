package org.dentinger.tutorial.controller;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dentinger.tutorial.domain.Team;
import org.dentinger.tutorial.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
public class TeamController {

  @Autowired
  private TeamRepository teamRepository;

  @RequestMapping(value = "/leaguemanager/teams")
  public MappingJacksonValue getSomething2(@RequestParam(value = "fields", required = false) String fields) {
    Page<Team> r = teamRepository.findAll(new PageRequest(0, 20), 1);

    MappingJacksonValue response = new MappingJacksonValue(r);
    if (fields != null) {
      response.setFilters(processFilterFields(fields, Team.class));
    }

    return response;
  }


  private FilterProvider processFilterFields(String fields, Class<?> clazz) {
    JsonFilter annotation = clazz.getAnnotation(JsonFilter.class);
    final String defaultFilterName = annotation.value();

    SimpleFilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);
    Map<String, Set<String>> filterMap = new HashMap<>();
    filterMap.put(defaultFilterName, new HashSet<>());

    // this is really ugly and needs refactoring...
    if (fields != null) {
      String[] strings = fields.split(",(?![^()]*\\))");
      for (String s : strings) {
        if (s.contains("(")) {
          String[] split = s.replace("(", ",").replace(")", ",").split(",");

          Class<?> definedClass = getDefinedClass(FieldUtils.getField(clazz, split[0], true));
          JsonFilter jsonFilter = definedClass.getAnnotation(JsonFilter.class);

          filterMap.get(defaultFilterName).add(split[0]);
          filterMap.put(jsonFilter.value(), new HashSet<>(Arrays.asList(split).subList(1, split.length)));
        }
        else {
          filterMap.get(defaultFilterName).add(s);
        }
      }

      for (String s : filterMap.keySet()) {
        filters.addFilter(s, SimpleBeanPropertyFilter.filterOutAllExcept(filterMap.get(s)));
      }
    }

    return filters;
  }

  private Class<?> getDefinedClass(Field field) {
    Class<?> clazz = field.getType();

    // check to see if this is a ParameterizedType...
    Type genericType = field.getGenericType();
    if (genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
      Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
      try {
        clazz = Class.forName(actualTypeArguments[0].getTypeName());
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    return clazz;
  }
}
