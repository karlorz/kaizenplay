package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReadDemoArray {
    public static void main(String[] args) {
        // Specify the path to the JSON file on the classpath
        String filePath = "/fdda_system.json"; // Note the leading slash

        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Read system list from the JSON file on the classpath
            InputStream inputStream = ReadDemo.class.getResourceAsStream(filePath);
            if (inputStream != null) {
                SystemList systemList = objectMapper.readValue(inputStream, SystemList.class);

                // Access the system list
                List<SystemList.SystemInfo> systems = systemList.getSystems();

                // Extract system names into a String[]
                String[] nameList = systems.stream()
                        .map(SystemList.SystemInfo::getName)
                        .toArray(String[]::new);

                // Display system names
                for (String name : nameList) {
                    System.out.println("System Name from ReadDemo: " + name);
                }
                System.out.println(nameList);
            } else {
                System.out.println("File not found: " + filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
