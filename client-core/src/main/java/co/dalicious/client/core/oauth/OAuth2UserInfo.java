package co.dalicious.client.core.oauth;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttribute() {
        return this.attributes;
    }

    public abstract String getEmail();
    public abstract String getPhone();
    public abstract String getName();
    public abstract String getProvider();
}
