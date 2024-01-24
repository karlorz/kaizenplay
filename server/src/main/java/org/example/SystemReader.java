package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SystemReader {
    public static String[] readSystemNames(String jsonPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = SystemReader.class.getResourceAsStream(jsonPath);
            if (inputStream != null) {
                SystemList systemList = objectMapper.readValue(inputStream, SystemList.class);

                List<SystemList.SystemInfo> systems = systemList.getSystems();

                return systems.stream()
                        .map(SystemList.SystemInfo::getName)
                        .toArray(String[]::new);
            } else {
                System.out.println("File not found: " + jsonPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[0]; // Return empty array if there's an issue
    }
}
