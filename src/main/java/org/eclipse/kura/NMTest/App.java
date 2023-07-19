package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

import fi.w1.Wpa_supplicant1;
import fi.w1.wpa_supplicant1.Interface;

public class App {
    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConn = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            Wpa_supplicant1 wpaSupplicant = dbusConn.getRemoteObject("fi.w1.wpa_supplicant1", "/fi/w1/wpa_supplicant1", Wpa_supplicant1.class);
            
            DBusPath iface = wpaSupplicant.GetInterface("wlan0");
            System.out.println("Found dbus path: " + iface.getPath());
            
            Interface foundIface = dbusConn.getRemoteObject("fi.w1.wpa_supplicant1", iface.getPath(), Interface.class);

            Map<String, Variant<?>> settings = new HashMap<>();
            settings.put("Type", new Variant<>("active"));
            foundIface.Scan(settings);

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}