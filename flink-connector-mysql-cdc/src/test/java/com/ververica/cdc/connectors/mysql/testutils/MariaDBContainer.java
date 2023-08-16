package com.ververica.cdc.connectors.mysql.testutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class MariaDBContainer extends org.testcontainers.containers.MariaDBContainer {

    protected static final Logger LOG = LoggerFactory.getLogger(MariaDBContainer.class);

    static final String DEFAULT_USER = "flinkuser";

    static final String DEFAULT_PASSWORD = "flinkpwd";
    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASSWORD;
    private String databaseName = "flink-test";
    private static final String SETUP_SQL_PARAM_NAME = "SETUP_SQL";
    private static final String MARIADB_ROOT_USER = "root";

    public MariaDBContainer(String dockerImageName) {
        super(dockerImageName);
    }

    @Override
    public String getDriverClassName() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return "com.mysql.cj.jdbc.Driver";
        } catch (ClassNotFoundException e) {
            return "com.mysql.jdbc.Driver";
        }
    }

    //    @Override
    //    protected void configure() {
    //        optionallyMapResourceParameterAsVolume(MY_CNF_CONFIG_OVERRIDE_PARAM_NAME,
    // "/etc/mysql/conf", "mariadb-default-conf");
    ////        optionallyMapResourceParameterAsVolume(
    ////                MY_CNF_CONFIG_OVERRIDE_PARAM_NAME, "/etc/mysql/", "mysql-default-conf");
    //        addEnv("MYSQL_DATABASE", databaseName);
    //        addEnv("MYSQL_USER", username);
    //        if (password != null && !password.isEmpty()) {
    //            addEnv("MYSQL_PASSWORD", password);
    //            addEnv("MYSQL_ROOT_PASSWORD", password);
    //        } else if (MARIADB_ROOT_USER.equalsIgnoreCase(username)) {
    //            addEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes");
    //        } else {
    //            throw new ContainerLaunchException("Empty password can be used only with the root
    // user");
    //        }
    //        setStartupAttempts(3);
    //    }

    @Override
    public String getJdbcUrl() {
        return getJdbcUrl(getDatabaseName());
    }

    public String getJdbcUrl(String databaseName) {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:mysql://"
                + getHost()
                + ":"
                + getMappedPort(3306)
                + "/"
                + databaseName
                + additionalUrlParams;
    }

    public static MariaDBContainer createMariaDBContainer() {
        MariaDBContainer mariaDBContainer =
                (MariaDBContainer)
                        new MariaDBContainer("mariadb:10.3.18")
                                .withConfigurationOverride("docker/mariadb")
                                .withDatabaseName("flink-test")
                                .withUsername("flinkuser")
                                .withPassword("flinkpwd")
                                .withEnv("MYSQL_ROOT_PASSWORD", "123456")
                                .withLogConsumer(new Slf4jLogConsumer(LOG));
        //        mariaDBContainer.withCommand()
        //        mariaDBContainer.withSetupSQL("docker/setup.sql");
        return mariaDBContainer;
    }
}
