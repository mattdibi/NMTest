package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

public class App {
    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConnection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            
            NMDbusConnector nm = NMDbusConnector.createInstance();
            nm.checkPermissions();
            
            Map<String, Object> config = new HashMap<>();
            config.put("net.interfaces", "eth0");
            config.put("net.interface.eth0.config.dhcpClient4.enabled", false);
            config.put("net.interface.eth0.config.ip4.address", "192.168.1.24");
            config.put("net.interface.eth0.config.ip4.prefix", (short) 24);
            
            nm.apply(config);

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}