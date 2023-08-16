package io.debezium.connector.mysql.binlogclient;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.network.protocol.command.Command;
import com.github.shyiko.mysql.binlog.network.protocol.command.DumpBinaryLogCommand;
import com.github.shyiko.mysql.binlog.network.protocol.command.QueryCommand;

import java.io.IOException;
import java.util.logging.Logger;

public class MariaDBBinaryLogClient extends BinaryLogClient {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public MariaDBBinaryLogClient(String hostname, int port, String username, String password) {
        super(hostname, port, username, password);
    }

    @Override
    protected void requestBinaryLogStreamMaria(long serverId) throws IOException {
        Command dumpBinaryLogCommand;

        /*
           https://jira.mariadb.org/browse/MDEV-225
        */
        //        channel.write(new QueryCommand("SET @mariadb_slave_capability=1"));
        channel.write(new QueryCommand("SET @mariadb_slave_capability=4"));
        checkError(channel.read());

        synchronized (gtidSetAccessLock) {
            if (null != gtidSet) {
                logger.info(gtidSet.toString());
                channel.write(
                        new QueryCommand(
                                "SET @slave_connect_state = '" + gtidSet.toString() + "'"));
                checkError(channel.read());
                channel.write(new QueryCommand("SET @slave_gtid_strict_mode = 0"));
                checkError(channel.read());
                channel.write(new QueryCommand("SET @slave_gtid_ignore_duplicates = 0"));
                checkError(channel.read());
                dumpBinaryLogCommand =
                        new DumpBinaryLogCommand(serverId, "", 0L, isUseSendAnnotateRowsEvent());
            } else {
                dumpBinaryLogCommand =
                        new DumpBinaryLogCommand(
                                serverId, getBinlogFilename(), getBinlogPosition());
            }
        }
        channel.write(dumpBinaryLogCommand);
    }
}
