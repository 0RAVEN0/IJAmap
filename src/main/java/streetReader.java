/**
 * Authors: Michal Vanka (xvanka00)
 * Contents: Class for reading yaml files
 */
package main.java;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.List;

/**
 * Used for reading streets from yaml files
 */
public class streetReader {
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Reads a list of streets from a yaml file and stores them in a list
     * @param ymlFile The yaml file with streets
     * @return List of streets
     */
    public List<Street> read(File ymlFile) {
        List<Street> streets = null;
        try {
            streets = mapper.readValue(ymlFile, new TypeReference<List<Street>>() {});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return streets;
    }

}
