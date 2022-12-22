package co.dalicious.client.core.oauth;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo{

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return attributes.get("id").toString();
    }

    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getProvider() {
        return null;
    }
}
