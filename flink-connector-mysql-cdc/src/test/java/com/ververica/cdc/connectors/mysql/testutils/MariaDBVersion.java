package com.ververica.cdc.connectors.mysql.testutils;

public enum MariaDBVersion {
    V10_6_4("10.6.4");
    private String version;

    MariaDBVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
