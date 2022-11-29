package co.dalicious.domain.user.entity;
public enum Role {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN),
    GUEST(Authority.GUEST);

    private final String authority;

    Role(String authority){
        this.authority = authority;
    }

    public String getAuthority(){
        return this.authority;
    }

    public static class Authority{
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String GUEST = "ROLE_GUEST";
    }
}
