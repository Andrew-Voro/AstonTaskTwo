package aston;

import aston.db.ConnectionManager;
import aston.db.ConnectionManagerImpl;
import aston.util.InitSqlScheme;


public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        InitSqlScheme.initSqlScheme(connectionManager);
        InitSqlScheme.initSqlData(connectionManager);
    }
}