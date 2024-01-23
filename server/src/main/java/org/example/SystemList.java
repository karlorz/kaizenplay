package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

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

}

