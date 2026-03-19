package org.xiaoyu.xchatmind.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * @author xiaoyu
 * @desciption 处理pg vector类型转换
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(float[].class)
public class PgVectorTypeHandler extends BaseTypeHandler<float[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int j = 0; j < parameter.length; j++) {
            sb.append(parameter[j]);
            if (j < parameter.length - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        ps.setObject(i, sb.toString(), Types.OTHER);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    /** helper method to parse string to byte[] */
    private float[] parse(String vectorText) {
        if (vectorText == null) {
            return null;
        }
        vectorText = vectorText.replace("[", "").replace("]", "");
        if (vectorText.isBlank()) return new float[0];
        String[] values = vectorText.split(",");
        float[] vector = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            vector[i] = Float.parseFloat(values[i]);
        }
        return vector;
    }
}
