package co.dalicious.data.mysql.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BigIntegerUserType implements UserType {

    public int[] sqlTypes() { return new int[]{Types.BIGINT}; }

    public Class returnedClass() { return BigInteger.class; }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        BigInteger bx = (BigInteger) x;
        BigInteger by = (BigInteger) y;
        return bx.equals(by);
    }

    public int hashCode(Object x) throws HibernateException {
        return ((BigInteger) x).hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        long value = rs.getLong(names[0]);
        return rs.wasNull() ? null : BigInteger.valueOf(value);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.BIGINT);
        } else {
            st.setLong(index, ((BigInteger) value).longValue());
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() { return false; }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}

