/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Class for reading yaml files
 */
package main.java;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import main.java.map.Street;
import main.java.transport.Line;

import java.io.File;
import java.util.List;

/**
 * Used for reading streets from yaml files
 */
public class Reader {
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Reads a list of streets from a yaml file and stores them in a list
     * @param ymlFile The yaml file with streets
     * @return List of streets
     */
    public List<Street> readStreets(File ymlFile) {
        List<Street> streets = null;
        try {
            streets = mapper.readValue(ymlFile, new TypeReference<List<Street>>() {});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return streets;
    }

    /**
     * Reads a list of lines from a yaml file and stores them in a list
     * @param ymlFile The yaml file with lines
     * @return List of lines
     */
    public List<Line> readLines(File ymlFile) {
        List<Line> lines = null;
        try {
            lines = mapper.readValue(ymlFile, new TypeReference<List<Line>>() {});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

}
