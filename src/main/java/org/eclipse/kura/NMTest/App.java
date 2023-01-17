package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.freedesktop.NetworkManager;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final String NM_BUS_NAME = "org.freedesktop.NetworkManager";
    private static final String NM_BUS_PATH = "/org/freedesktop/NetworkManager";
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConnection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            
            NetworkManager nm = dbusConnection.getRemoteObject(NM_BUS_NAME, NM_BUS_PATH, NetworkManager.class);
    
            Map<String, String> getPermissions = nm.GetPermissions();
            for (Entry<String, String> entry : getPermissions.entrySet()) {
                if (!entry.getValue().equals("yes")) {
                    logger.warn("Missing permission for \"{}\"", entry.getKey());
                }
            }

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}