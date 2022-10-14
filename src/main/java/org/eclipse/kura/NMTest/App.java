package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.Map;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
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

                // Critical stuff
                System.out.println("Address-data Settings.Connection[ipv4][address-data]");
                System.out.print("    ");
                System.out.println(connectionSettings.get("ipv4").get("address-data"));
                // Will output [[{ address => [192.168.1.223],prefix => [24] }]]
            }

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}
