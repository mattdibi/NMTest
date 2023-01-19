package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

public class App {
    public static void main( String[] args ) throws DBusException {
        NMDbusConnector nm = null;
        try (DBusConnection dbusConnection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            
            nm = NMDbusConnector.createInstance();
            // nm.checkPermissions();
            
            Map<String, Object> config = new HashMap<>();
            config.put("net.interfaces", "wlan0");
            config.put("net.interface.wlan0.config.dhcpClient4.enabled", true);
            //config.put("net.interface.wlan0.config.ip4.address", "192.168.1.24");
            //config.put("net.interface.wlan0.config.ip4.prefix", (short) 24);

            config.put("net.interface.wlan0.config.wifi.mode", "INFRA"); // -> 802-11-wireless.mode
            config.put("net.interface.wlan0.config.wifi.infra.ssid", "kura_gateway_raspberry_pi");
            config.put("net.interface.wlan0.config.wifi.infra.radioMode", "RADIO_MODE_80211b"); // -> 802-11-wireless.band
            config.put("net.interface.wlan0.config.wifi.infra.passphrase", "testtesttest"); // -> 802-11-wireless-security.psk
            config.put("net.interface.wlan0.config.wifi.infra.securityType", "SECURITY_WPA2"); // -> 802-11-wireless-security.key-mgmt
            config.put("net.interface.wlan0.config.wifi.infra.groupCiphers", "CCMP"); // -> 802-11-wireless-security.group
            
            nm.apply(config);

        } catch (IOException _ex) {
            _ex.printStackTrace();
        } finally {
            if(Objects.nonNull(nm)) {
                nm.closeConnection();
            }
        }
    }
}