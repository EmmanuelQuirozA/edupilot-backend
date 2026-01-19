package com.monarchsolutions.sms.etl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LegacyPagoRecord {

    private final long idPago;
    private final Long idAlumno;
    private final String concepto;
    private final BigDecimal monto;
    private final LocalDate fechaRegistro;
    private final LocalDate fechaPago;
    private final String formaPago;
    private final String estatusPago;
    private final String comprobante;
    private final String observaciones;
    private final Long idUsuarioAprobo;
    private final LocalDate fechaAprobacion;

    public LegacyPagoRecord(
            long idPago,
            Long idAlumno,
            String concepto,
            BigDecimal monto,
            LocalDate fechaRegistro,
            LocalDate fechaPago,
            String formaPago,
            String estatusPago,
            String comprobante,
            String observaciones,
            Long idUsuarioAprobo,
            LocalDate fechaAprobacion
    ) {
        this.idPago = idPago;
        this.idAlumno = idAlumno;
        this.concepto = concepto;
        this.monto = monto;
        this.fechaRegistro = fechaRegistro;
        this.fechaPago = fechaPago;
        this.formaPago = formaPago;
        this.estatusPago = estatusPago;
        this.comprobante = comprobante;
        this.observaciones = observaciones;
        this.idUsuarioAprobo = idUsuarioAprobo;
        this.fechaAprobacion = fechaAprobacion;
    }

    public long getIdPago() {
        return idPago;
    }

    public Long getIdAlumno() {
        return idAlumno;
    }

    public String getConcepto() {
        return concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public String getEstatusPago() {
        return estatusPago;
    }

    public String getComprobante() {
        return comprobante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Long getIdUsuarioAprobo() {
        return idUsuarioAprobo;
    }

    public LocalDate getFechaAprobacion() {
        return fechaAprobacion;
    }
}
