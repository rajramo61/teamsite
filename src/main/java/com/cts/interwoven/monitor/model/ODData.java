package com.cts.interwoven.monitor.model;

/**
 * Created by RajPriyaMAC on 4/5/15.
 */
public class ODData {
    private String hostName;
    private String application;
    private String environment;
    private String type;
    private String status;

    public ODData(String hostName, String status, String application, String environment, String type) {
        this.hostName = hostName;
        this.application = application;
        this.environment = environment;
        this.type = type;
        this.status = status;
    }

    public ODData() {
    }

    @Override
    public String toString() {
        return "ODData{" +
                "hostName='" + hostName + '\'' +
                ", application='" + application + '\'' +
                ", environment='" + environment + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
