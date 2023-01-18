package org.eclipse.kura.NMTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

public class NMSettingsConverter {
    
    public static Map<String, Variant<?>> buildIpv4Settings(Map<String, Object> networkConfiguration, String iface) {
        Map<String, Variant<?>> ipv4Map = new HashMap<>();

        String dhcpClient4EnabledProperty = String.format("net.interface.%s.config.dhcpClient4.enabled", iface);
        Boolean dhcpClient4Enabled = (Boolean) networkConfiguration.get(dhcpClient4EnabledProperty);

        // Should handle net.interface.eth0.config.ip4.status here

        if (Boolean.FALSE.equals(dhcpClient4Enabled)) {
            ipv4Map.put("method", new Variant<>("manual"));

            String dhcpClient4AddressProperty = String.format("net.interface.%s.config.ip4.address", iface);
            String dhcpClient4Address = (String) networkConfiguration.get(dhcpClient4AddressProperty);

            String dhcpClient4PrefixProperty = String.format("net.interface.%s.config.ip4.prefix", iface);
            Short dhcpClient4Prefix = (Short) networkConfiguration.get(dhcpClient4PrefixProperty);

            Map<String, Variant<?>> address = new HashMap<>();
            address.put("address", new Variant<>(dhcpClient4Address));
            address.put("prefix", new Variant<>(new UInt32(dhcpClient4Prefix)));

            List<Map<String, Variant<?>>> addressData = Arrays.asList(address);
            ipv4Map.put("address-data", new Variant<>(addressData, "aa{sv}"));

            String dhcpClient4DNSProperty = String.format("net.interface.%s.config.ip4.dnsServers", iface);
            if (networkConfiguration.containsKey(dhcpClient4DNSProperty)) {
                String dhcpClient4DNS = (String) networkConfiguration.get(dhcpClient4DNSProperty);
                ipv4Map.put("dns-search", new Variant<>(getDNSServers(dhcpClient4DNS)));
            }
            ipv4Map.put("ignore-auto-dns", new Variant<>(true));

            String dhcpClient4GatewayProperty = String.format("net.interface.%s.config.ip4.gateway", iface);
            if (networkConfiguration.containsKey(dhcpClient4GatewayProperty)) {
                String dhcpClient4Gateway = (String) networkConfiguration.get(dhcpClient4GatewayProperty);
                ipv4Map.put("gateway", new Variant<>(dhcpClient4Gateway));
            }
        } else {
            ipv4Map.put("method", new Variant<>("auto"));

            String dhcpClient4DNSProperty = String.format("net.interface.%s.config.ip4.dnsServers", iface);
            if (networkConfiguration.containsKey(dhcpClient4DNSProperty)) {
                String dhcpClient4DNS = (String) networkConfiguration.get(dhcpClient4DNSProperty);
                ipv4Map.put("ignore-auto-dns", new Variant<>(true));
                ipv4Map.put("dns-search", new Variant<>(getDNSServers(dhcpClient4DNS)));
            }
        }

        return ipv4Map;
    }
    
    public static List<String> getNetworkInterfaces(String netInterfaces) {
        List<String> netInterfacesNames = new ArrayList<>();
        Pattern comma = Pattern.compile(",");
        if (netInterfaces != null) {
            comma.splitAsStream(netInterfaces).filter(s -> !s.trim().isEmpty()).forEach(netInterfacesNames::add);
        }

        return netInterfacesNames;
    }

    private static List<String> getDNSServers(String dnsServersString) {
        List<String> dnsServers = new ArrayList<>();
        Pattern comma = Pattern.compile(",");
        if (dnsServersString != null) {
            comma.splitAsStream(dnsServersString).filter(s -> !s.trim().isEmpty()).forEach(dnsServers::add);
        }

        return dnsServers;
    }
}
