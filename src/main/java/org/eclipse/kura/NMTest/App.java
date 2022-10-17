package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import org.freedesktop.networkmanager.Settings;
import org.freedesktop.networkmanager.settings.Connection;

public class App {
    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConn = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            // Get /org/freedesktop/NetworkManager/Settings
            Settings settings = dbusConn.getRemoteObject("org.freedesktop.NetworkManager",
                    "/org/freedesktop/NetworkManager/Settings", Settings.class);

            // Iterate over connections
            for (DBusInterface connectionIf : settings.ListConnections()) {
                // Get /org/freedesktop/NetworkManager/Settings/Connection
                Connection connection = dbusConn.getRemoteObject("org.freedesktop.NetworkManager",
                        connectionIf.getObjectPath().toString(), Connection.class);

                // Call /org/freedesktop/NetworkManager/Settings/Connection/GetSettings
                Map<String, Map<String, Variant<?>>> connectionSettings = connection.GetSettings();

                System.out.println("Connection id: " + connectionSettings.get("connection").get("id"));

                // Deep copy
                Map<String, Variant<?>> connectionMap = new HashMap<>();
                for (String key : connectionSettings.get("connection").keySet()) {
                    connectionMap.put(key, connectionSettings.get("connection").get(key));
                }
                Map<String, Variant<?>> ipv4Map = new HashMap<>();
                for (String key : connectionSettings.get("ipv4").keySet()) {
                    ipv4Map.put(key, connectionSettings.get("ipv4").get(key));
                }

                // Update DNS with 8.8.8.8
                UInt32[] newDNS = { new UInt32(0x08080808) };
                ipv4Map.put("dns", new Variant<UInt32[]>(newDNS));

                Map<String, Map<String, Variant<?>>> newConnectionSettings = new HashMap<>();
                newConnectionSettings.put("ipv4", ipv4Map);
                newConnectionSettings.put("connection", connectionMap);

                System.out.println("DNS: " + newConnectionSettings.get("ipv4").get("dns"));

                // Update & save connection settings
                connection.Update(newConnectionSettings);
                connection.Save();
            }

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}