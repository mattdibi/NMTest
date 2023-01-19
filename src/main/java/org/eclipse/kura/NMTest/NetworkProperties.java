package org.eclipse.kura.NMTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class NetworkProperties {
    
    private Map<String, Object> properties;

    public NetworkProperties(Map<String, Object> rawProperties) {
        this.properties = Objects.requireNonNull(rawProperties);
    }
    
    public <T> T get(Class<T> clazz, String key, Object ... args) {
        String formattedKey = String.format(key, args);
        return clazz.cast(this.properties.get(formattedKey));
    }

    public <T> Optional<T> getOpt(Class<T> clazz, String key, Object ... args) {
        String formattedKey = String.format(key, args);
        if(this.properties.containsKey(formattedKey)) {
            return Optional.of(clazz.cast(this.properties.get(formattedKey)));
        } else {
            return Optional.empty();
        }
    }
    
    public List<String> getStringList(String key, Object ... args) {
        String commaSeparatedString = get(String.class, key, args);
        
        List<String> stringList = new ArrayList<>();
        Pattern comma = Pattern.compile(",");
        if (Objects.nonNull(commaSeparatedString) && !commaSeparatedString.isEmpty()) {
            comma.splitAsStream(commaSeparatedString).filter(s -> !s.trim().isEmpty()).forEach(stringList::add);
        }

        return stringList;
    }
    
    public Optional<List<String>> getOptStringList(String key, Object ... args) {
        String formattedKey = String.format(key, args);
        if(this.properties.containsKey(formattedKey)) {
            return Optional.of(getStringList(formattedKey, args));
        } else {
            return Optional.empty();
        }
    }
}
