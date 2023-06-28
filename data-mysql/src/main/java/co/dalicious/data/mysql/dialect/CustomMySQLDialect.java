package co.dalicious.data.mysql.dialect;

import co.dalicious.data.mysql.type.BigIntegerUserType;
import org.hibernate.spatial.dialect.mysql.MySQL8SpatialDialect;

import java.sql.Types;

public class CustomMySQLDialect extends MySQL8SpatialDialect {

    public CustomMySQLDialect() {
        super();
        registerHibernateType(Types.BIGINT, BigIntegerUserType.class.getName());
    }
}