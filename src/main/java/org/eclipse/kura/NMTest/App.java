package org.eclipse.kura.NMTest;

import java.io.IOException;

import java.util.Map;
import java.util.Map.Entry;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import org.freedesktop.NetworkManager;
import org.freedesktop.networkmanager.Settings;
import org.freedesktop.networkmanager.device.Wireless;
import org.freedesktop.networkmanager.settings.Connection;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConn = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            // Get /org/freedesktop/NetworkManager object
            // See: https://developer-old.gnome.org/NetworkManager/stable/gdbus-org.freedesktop.NetworkManager.html
            NetworkManager nm = dbusConn.getRemoteObject("org.freedesktop.NetworkManager",
                    "/org/freedesktop/NetworkManager", NetworkManager.class);

            System.out.println("------ Permissions -------");
            Map<CharSequence, CharSequence> getPermissions = nm.GetPermissions();
            for (Entry<CharSequence, CharSequence> entry : getPermissions.entrySet()) {
                System.out.println("Permission: " + entry.getKey() + " = " + entry.getValue());
            }

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
        System.out.println( "Hello World!" );
    }
}
