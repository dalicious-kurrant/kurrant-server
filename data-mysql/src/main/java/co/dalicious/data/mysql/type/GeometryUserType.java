package co.dalicious.data.mysql.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.*;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
public class GeometryUserType implements UserType {

    @Override
    public int[] sqlTypes() {
        // This will map to LONGVARBINARY.
        return new int[]{Types.LONGVARBINARY};
    }

    @Override
    public Class returnedClass() {
        return Geometry.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        byte[] bytes = rs.getBytes(names[0]);
        try {
            return new WKBReader().read(bytes);
        } catch (ParseException e) {
            throw new HibernateException("Failed to convert byte array to Geometry: " + Arrays.toString(bytes), e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.LONGVARBINARY);
        } else {
            Geometry geometry = (Geometry) value;
            byte[] bytes = new WKBWriter().write(geometry);
            st.setBytes(index, bytes);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Geometry) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }
}

