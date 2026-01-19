package com.monarchsolutions.sms.etl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class LegacyPagoRowMapper implements RowMapper<LegacyPagoRecord> {

    @Override
    public LegacyPagoRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LegacyPagoRecord(
                rs.getLong("idPago"),
                rs.getObject("idAlumno", Long.class),
                rs.getString("concepto"),
                rs.getBigDecimal("monto"),
                rs.getObject("fechaRegistro", java.time.LocalDate.class),
                rs.getObject("fechaPago", java.time.LocalDate.class),
                rs.getString("formaPago"),
                rs.getString("estatusPago"),
                rs.getString("comprobante"),
                rs.getString("observaciones"),
                rs.getObject("idUsuarioAprobo", Long.class),
                rs.getObject("fechaAprobacion", java.time.LocalDate.class)
        );
    }
}
