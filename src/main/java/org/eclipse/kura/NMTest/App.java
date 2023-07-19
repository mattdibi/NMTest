package org.eclipse.kura.NMTest;

import java.io.IOException;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;

public class App {
    public static void main( String[] args ) throws DBusException {
        try (DBusConnection dbusConn = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            Properties wpaSupplicantProperties = dbusConn.getRemoteObject("fi.w1.wpa_supplicant1", "/fi/w1/wpa_supplicant1", Properties.class);

            String debugLevel = wpaSupplicantProperties.Get("fi.w1.wpa_supplicant1", "DebugLevel");
            System.out.println("WPA supplicant debug level: " + debugLevel);

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}