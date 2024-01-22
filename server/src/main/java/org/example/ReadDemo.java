package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class ReadDemo {
    public static void main(String[] args) {
        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Read system list from JSON content
            SystemList systemList = objectMapper.readValue(
                    "{\"systems\":[" +
                            "{\"name\":\"AHU-2-01\"}," +
                            "{\"name\":\"AHU-2M-01\"}," +
                            "{\"name\":\"AHU-2M-02\"}," +
                            "{\"name\":\"AHU-2M-03\"}," +
                            "{\"name\":\"AHU-2M-04\"}" +
                            "]}",
                    SystemList.class);

            // Access the system list
            List<SystemList.SystemInfo> systems = systemList.getSystems();

            // Display system names
            for (SystemList.SystemInfo system : systems) {
                System.out.println("System Name from ReadDemo: " + system.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
