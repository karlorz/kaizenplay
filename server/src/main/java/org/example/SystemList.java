package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

class SystemList {
    @JsonProperty("systems")
    private List<SystemInfo> systems;

    public List<SystemInfo> getSystems() {
        return systems;
    }

    public static class SystemInfo {
        private String name;

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
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
            List<SystemInfo> systems = systemList.getSystems();

            // Display system names
            for (SystemInfo system : systems) {
                System.out.println("System Name: " + system.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

