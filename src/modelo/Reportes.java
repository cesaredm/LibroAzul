/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Date;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Reportes extends Conexiondb {

    String consulta;
    Connection cn;
    PreparedStatement pst;
    int banderin;
    DefaultTableModel modelo;
    private float Dolares,EgresoDolares,dolaresComprados, precioDolar;

    public Reportes() {
        this.pst = null;
        this.consulta = null;
        this.cn = null;
    }

    public void setPrecioDolar(float precio){
        this.precioDolar = precio;
    }
    
    public float getDolares() {
        return Dolares;
    }
    
    public float getEgresoDolares(){
        return this.EgresoDolares;
    }

    public float getDolaresComprados(){
        return this.dolaresComprados;
    }
    
    //mostrar facturas realizadas por dia 
    public DefaultTableModel ReporteDiario(Date Fecha) {
        cn = Conexion();
        this.consulta = "SELECT facturas.id,facturas.fecha AS fechaFactura, impuestoISV, totalFactura, nombre_comprador, formapago.tipoVenta, creditos.id as idCredito, cajas.caja from facturas LEFT JOIN formapago ON(formapago.id = facturas.tipoVenta) LEFT JOIN creditos ON(facturas.credito = creditos.id) LEFT JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha = ? ORDER BY facturas.id DESC";
        String[] Resultados = new String[8];
        String[] titulos = {"Factura", "Fecha", "IVA", "Total", "Comprador", "Forma Pago", "N° Credito", "Caja"};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            pst = this.cn.prepareStatement(consulta);
            pst.setDate(1, Fecha);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Resultados[0] = rs.getString("id");
                Resultados[1] = rs.getString("fechaFactura");
                Resultados[2] = rs.getString("impuestoISV");
                Resultados[3] = rs.getString("totalFactura");
                Resultados[4] = rs.getString("nombre_comprador");
                Resultados[5] = rs.getString("tipoVenta");
                Resultados[6] = rs.getString("idCredito");
                Resultados[7] = rs.getString("caja");
                modelo.addRow(Resultados);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion reporteDiario");
        }
        return modelo;
    }

    //mostrar facturas por rango de fechas
    public DefaultTableModel ReporteMensual(Date fecha1, Date fecha2) {
        cn = Conexion();
        String[] registros = new String[8];
        String[] titulos = {"Factura", "Fecha", "Iva", "Total", "Comprador", "Forma Pago", "N° Credito"};
        this.consulta = "SELECT facturas.id,facturas.fecha AS fechaFactura, impuestoISV, totalFactura, nombre_comprador, formapago.tipoVenta, creditos.id AS idCredito, cajas.caja from facturas LEFT JOIN formapago ON(formapago.id = facturas.tipoVenta) LEFT JOIN creditos ON(facturas.credito = creditos.id) LEFT JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha BETWEEN ? AND ? ORDER BY facturas.id";
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("fechaFactura");
                registros[2] = rs.getString("impuestoISV");
                registros[3] = rs.getString("totalFactura");
                registros[4] = rs.getString("nombre_comprador");
                registros[5] = rs.getString("tipoVenta");
                registros[6] = rs.getString("idCredito");
                this.modelo.addRow(registros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return modelo;
    }

    //mostrar detalles de facturas
    public DefaultTableModel DetalleFactura(int id) {
        cn = Conexion();
        this.consulta = "SELECT productos.id,codigoBarra,nombre, detallefactura.id AS idDetalle,precioProducto,cantidadProducto,totalVenta FROM productos RIGHT JOIN detallefactura ON(productos.id = detallefactura.producto) LEFT JOIN facturas ON(detallefactura.factura = facturas.id) WHERE facturas.id = "+id+" AND detallefactura.cantidadProducto > 0";
        String[] registros = new String[7];
        String[] titulos = {"Detalle", "Id Producto", "Codigo Barra", "Producto", "Cantidad", "Precio", "Total"};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery(this.consulta);
            while (rs.next()) {
                registros[0] = rs.getString("idDetalle");
                registros[1] = rs.getString("id");
                registros[2] = rs.getString("codigoBarra");
                registros[3] = rs.getString("nombre");
                registros[4] = rs.getString("cantidadProducto");
                registros[5] = rs.getString("precioProducto");
                registros[6] = rs.getString("totalVenta");
                this.modelo.addRow(registros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return modelo;
    }

    //mostrar total de creditos realizados diariamente
    public float TotalCreditosDiario(Date fecha) {
        cn = Conexion();
        float totalCreditos = 0;
        this.consulta = "SELECT SUM(totalFactura) AS totalCredito FROM facturas INNER JOIN creditos ON(facturas.credito = creditos.id) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha = ? AND cajas.caja='CAJA1'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalCreditos = rs.getFloat("totalCredito");
            }
            if (totalCreditos == 0) {
                totalCreditos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " credito diario");
        }
        return totalCreditos;
    }

    public float TotalCreditosGlobal() {
        cn = Conexion();
        float totalCreditos = 0;
        this.consulta = "SELECT SUM(totalFactura) AS totalCredito FROM facturas INNER JOIN creditos ON(facturas.credito = creditos.id) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE cajas.caja='CAJA1'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalCreditos = rs.getFloat("totalCredito");
            }
            if (totalCreditos == 0) {
                totalCreditos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " credito global");
        }
        return totalCreditos;
    }

    //mostrar creditso realizados segun rango de fechas
    public float TotalCreditosMensual(Date fechaInicio, Date fechaFinal) {
        cn = Conexion();
        float totalCreditos = 0;
        //this.consulta = "SELECT SUM(totalFactura) AS totalCreditoMensual FROM facturas INNER JOIN creditos ON(facturas.credito = creditos.id) WHERE creditos.estado = 'Pendiente' AND facturas.fecha BETWEEN ? AND ?";
        this.consulta = "SELECT SUM(totalFactura) AS totalCreditoMensual FROM facturas INNER JOIN creditos ON(facturas.credito = creditos.id) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha BETWEEN ? AND ? AND cajas.caja='CAJA1'";
        //consulta = "SELECT SUM(totalFactura) AS totalCreditoMensual FROM facturas INNER JOIN creditos ON(facturas.credito = creditos.id) INNER JOIN cajas ON(facturas.caja=caja.id) WHERE facturas.fecha = ? AND cajas.caja='CAJA1'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fechaInicio);
            pst.setDate(2, fechaFinal);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalCreditos = rs.getFloat("totalCreditoMensual");
            }
            if (totalCreditos == 0) {
                totalCreditos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " funcion creditoMensual");
        }
        return totalCreditos;
    }

    //mostrar los Egresos por rango de fechas
    public float TotalGastos(Date fecha1, Date fecha2) {
        cn = Conexion();
        float totalGasto = 0;
        this.consulta = "SELECT SUM(monto) AS totalGasto FROM transaccion INNER JOIN cajas ON(transaccion.caja=cajas.id) WHERE fecha BETWEEN ? AND ? AND tipoTransaccion = 'Egreso' AND cajas.caja = 'CAJA1'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalGasto = rs.getFloat("totalGasto");
            }
            if (totalGasto == 0) {
                totalGasto = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " funcion totalgastos");
        }
        return totalGasto;
    }

    //mostrar los egresos por dias
    public float TotalGastosDiario(Date fecha1) {
        cn = Conexion();
        float totalGasto = 0;
        this.consulta = "SELECT SUM(monto) AS totalGasto FROM transaccion INNER JOIN cajas ON(transaccion.caja = cajas.id) WHERE fecha = ? AND cajas.caja='CAJA1' AND transaccion.tipoTransaccion = 'Egreso'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalGasto = rs.getFloat("totalGasto");
            }
            if (totalGasto == 0) {
                totalGasto = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " gasto diario");
        }
        return totalGasto;
    }

    public float TotalGastosGlobal() {
        cn = Conexion();
        float totalGasto = 0;
        this.consulta = "SELECT SUM(monto) AS totalGasto FROM transaccion INNER JOIN cajas ON(transaccion.caja = cajas.id) WHERE cajas.caja='CAJA1' AND transaccion.tipoTransaccion = 'Egreso'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                totalGasto = rs.getFloat("totalGasto");
            }
            if (totalGasto == 0) {
                totalGasto = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " gasto diario");
        }
        return totalGasto;
    }
    
    public float TotalIngresoEfectivoGlobal()
    {
        float ingresos = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(monto) AS totalGasto FROM transaccion INNER JOIN cajas ON(transaccion.caja = cajas.id) WHERE cajas.caja='CAJA1' AND transaccion.tipoTransaccion = 'Ingreso'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ingresos = rs.getFloat("totalGasto");
            }
            if (ingresos == 0) {
                ingresos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " funcion TotalIngresosEfectivos");
        }
        return ingresos;
    }
    
    public float ingresoDiarioEfectivo(Date fecha)
    {
        this.cn = Conexion();
        float ingresos = 0;
        this.consulta = "SELECT SUM(t.monto) AS total FROM transaccion AS t INNER JOIN cajas AS c ON(t.caja = c.id) WHERE t.fecha = ? AND c.caja = 'CAJA1' AND t.tipoTransaccion = 'Ingreso'";
        try{
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                ingresos = rs.getFloat("total");
            }
            this.cn.close();
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e+ " en la funcion IngresoDiarioEfectivo en modelo Reportes");
        }
        return ingresos;
    }
    public float ingresoEfecitivoRango(Date fecha1, Date fecha2)
    {
        this.cn = Conexion();
        float ingresos = 0;
        this.consulta = "SELECT SUM(t.monto) AS total FROM transaccion AS t INNER JOIN cajas AS c ON(t.caja = c.id) WHERE t.fecha BETWEEN ? AND ? AND c.caja = 'CAJA1' AND t.tipoTransaccion = 'Ingreso'";
        try{
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha1);
            this.pst.setDate(2, fecha2);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                ingresos = rs.getFloat("total");
            }
            this.cn.close();
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e+ " en la funcion IngresoDiarioEfectivo en modelo Reportes");
        }
        return ingresos;
    }
    //metodo para Obtener el total de ingreso por pagos de creditos en efectivo por rangos de fechas
    public float totalPagosEfectivo(Date fecha1, Date fecha2) {
        cn = Conexion();
        float pagos = 0;
        /*this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN creditos ON(pagoscreditos.credito = creditos.id) "
                + "WHERE creditos.estado = 'Pendiente' AND pagoscreditos.fecha BETWEEN ? AND ?";*/
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Efectivo' AND pagoscreditos.fecha BETWEEN ? AND ?";
        //this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Efectivo' AND pagoscreditos.fecha = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la totalpagosefectivo");
        }
        return pagos;
    }

    //metodo para Obtener el total de Ingresos por pagos en efectivo por dia 
    public float totalPagosEfectivoDiario(Date fecha1) {
        cn = Conexion();
        float pagos = 0;
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Efectivo' AND pagoscreditos.fecha = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " totalpagoefectivodiario");
        }
        return pagos;
    }

    public float totalPagosEfectivoGlobal() {
        cn = Conexion();
        float pagos = 0;
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Efectivo'";
        try {
            pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " totalpagoefectivodiario");
        }
        return pagos;
    }

    //metodo para Obtener el total de ingreso por pagos de creditos con tarjeta por dia
    public float totalPagosTarjetaDiario(Date fecha1) {
        cn = Conexion();
        float pagos = 0;
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Tarjeta' AND pagoscreditos.fecha = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " totalpagotarjetadiario");
        }
        return pagos;
    }

    public float totalPagosTarjetaGlobal() {
        cn = Conexion();
        float pagos = 0;
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Tarjeta'";
        try {
            pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " totalpagotarjetadiario");
        }
        return pagos;
    }

    //metodo para Obtener el total de ingreso por pagos de creditos con tarjeta  por rango de fechas
    public float totalPagosTarjeta(Date fecha1, Date fecha2) {
        cn = Conexion();
        float pagos = 0;
        /*this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN creditos ON(pagoscreditos.credito = creditos.id) "
                + "WHERE creditos.estado = 'Pendiente' AND pagoscreditos.fecha BETWEEN ? AND ?";*/
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Tarjeta' AND pagoscreditos.fecha BETWEEN ? AND ?";
        //this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN formapago ON(pagoscreditos.formaPago = formapago.id) WHERE formapago.tipoVenta = 'Efectivo' AND pagoscreditos.fecha = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getFloat("totalPagos");
            }
            if (pagos == 0) {
                pagos = 0;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " funcion totalPagosTarjeta");
        }
        return pagos;
    }

    //metodo para obtener todos los pagos de forma global sumando los pagos con tarjeta y en efectivo
    public String totalPagos(Date fecha1, Date fecha2) {
        cn = Conexion();
        String pagos = "";
        /*this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos INNER JOIN creditos ON(pagoscreditos.credito = creditos.id) "
                + "WHERE creditos.estado = 'Pendiente' AND pagoscreditos.fecha BETWEEN ? AND ?";*/
        this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos WHERE pagoscreditos.fecha BETWEEN ? AND ?";
        //this.consulta = "SELECT SUM(monto) AS TotalPagos FROM pagoscreditos WHERE pagoscreditos.fecha = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                pagos = rs.getString("totalPagos");
            }
            if (pagos == null) {
                pagos = "0";
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " totalpagos");
        }
        return pagos;
    }

    //obtener nombres de cliente segun el id
    public String nombreCliente(String id) {
        cn = Conexion();
        String Nombres = "";
        this.consulta = "SELECT clientes.nombres FROM clientes INNER JOIN creditos ON(clientes.id = creditos.cliente) WHERE creditos.id = ?";
        try {
            this.pst = this.cn.prepareStatement(consulta);
            this.pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Nombres = rs.getString("nombres");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion nombres clientes");
        }
        return Nombres;
    }

    public String apellidoCliente(String id) {
        cn = Conexion();
        String apellidos = "";
        this.consulta = "SELECT clientes.apellidos FROM clientes INNER JOIN creditos ON(clientes.id = creditos.cliente) WHERE creditos.id = ?";
        try {
            this.pst = this.cn.prepareStatement(consulta);
            this.pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                apellidos = rs.getString("apellidos");
            }

            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion apellidos clientes");
        }
        return apellidos;
    }

    //metodo para obtener el total de inversion en el negocio precio de compra en cordobas
    public float inversion() {
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion inversion en modelo");
        }
        return inversion;
    }

    //total de efectivo envertido en productos comprados en cordobas 
    public float proyeccionVentaCordobas() {
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioVenta*stock) AS inversion FROM productos WHERE monedaVenta = 'Córdobas'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion inversion en modelo");
        }
        return inversion;
    }

    //total de efectivo envertido en productos comprados en dolar
    public float proyeccionVentaDolar() {
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioVenta*stock) AS inversion FROM productos WHERE monedaVenta = 'Dolar'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion inversion en modelo");
        }
        return inversion;
    }

    public float inversionDolar(){
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos WHERE monedaCompra = 'Dolar'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion inversion en modelo");
        }
        return inversion;
    }
    
    public float inversionCordobas(){
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos WHERE monedaCompra = 'Córdobas'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion inversion en modelo");
        }
        return inversion;
    }
    
    //Obtener Ingresos efectivo en caja por rango de fechas
    public float ingresoEfectivoCaja(Date fecha1, Date fecha2) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE fecha BETWEEN ? AND ? AND formapago.tipoVenta = 'Efectivo' AND cajas.caja='CAJA1'";
        //this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) WHERE fecha = ? AND formapago.tipoVenta = 'Efectivo'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresoEfectivoCaja en modelo");
        }
        return total;
    }

    //obtener Ingresos en efectivo a cajas por dia
    public float ingresoEfectivoCajaDiario(Date fecha1) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE fecha = ? AND formapago.tipoVenta = 'Efectivo' AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresoEfectivoCaja en modelo");
        }
        return total;
    }

    public float ingresoEfectivoCajaGlobal() {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE formapago.tipoVenta = 'Efectivo' AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresoEfectivoCaja en modelo");
        }
        return total;
    }

    //obtener los ingresos totales osea todo lo vendido seun rango de fechas
    public float IngresosTotales(Date fecha1, Date fecha2) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE fecha BETWEEN ? AND ? AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresosTotales en modelo");
        }
        return total;
    }

    //obtener los ingresos totales osea todo lo vendido por dia
    public float IngresosTotalesDiario(Date fecha1) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE fecha = ? AND cajas.caja = 'CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresosTotales en modelo");
        }
        return total;
    }

    public float IngresosTotalesGlobal() {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE cajas.caja = 'CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion IngresosTotales en modelo");
        }
        return total;
    }

    //Ingresos totales a bancos segun rangos de fecha (ventas con tarjeta de credito o debito)
    public float IngresoAbancos(Date fecha1, Date fecha2) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE fecha BETWEEN ? AND ? AND formapago.tipoVenta = 'Tarjeta' AND cajas.caja = 'CAJA1'";
        //this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha = ? AND formapago.tipoVenta = 'Tarjeta' AND cajas.caja = 'CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion Ingreso A bancos en modelo");
        }
        return total;
    }

    //Ingresos totales a bancos por dia (ventas con tarjeta de credito o debito)
    public float IngresoAbancosDiario(Date fecha1) {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.fecha = ? AND formapago.tipoVenta = 'Tarjeta' AND cajas.caja = 'CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion Ingreso A bancos en modelo");
        }
        return total;
    }

    public float IngresoAbancosGlobal() {
        float total = 0;
        cn = Conexion();
        this.consulta = "SELECT SUM(totalFactura) AS total FROM facturas INNER JOIN formapago ON(formapago.id=facturas.tipoVenta) INNER JOIN cajas ON(facturas.caja=cajas.id) WHERE formapago.tipoVenta = 'Tarjeta' AND cajas.caja = 'CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion Ingreso A bancos en modelo");
        }
        return total;
    }

    public float baseEfectivoDiario(Date fecha) {
        float base = 0;
        this.cn = Conexion();
        this.consulta = "SELECT efectivo FROM aperturas INNER JOIN cajas ON(aperturas.caja=cajas.id) WHERE aperturas.fecha = ? AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                base = rs.getFloat("efectivo");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion baseEfectivoDiario");
        }
        return base;
    }

    //monto de apertura por dia
    public float TotalAperturasCaja(Date fecha1, Date fecha2) {
        float monto = 0;
        this.cn = Conexion();
        this.consulta = "SELECT SUM(efectivo) total FROM aperturas INNER JOIN cajas ON(aperturas.caja=cajas.id) WHERE aperturas.fecha BETWEEN ? AND ? AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setDate(1, fecha1);
            pst.setDate(2, fecha2);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                monto = rs.getFloat("total");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion TotalAperturasCaja");
        }

        return monto;
    }

    public float TotalAperturasCajaGlobal() {
        float monto = 0;
        this.cn = Conexion();
        this.consulta = "SELECT SUM(efectivo) total FROM aperturas INNER JOIN cajas ON(aperturas.caja=cajas.id) WHERE cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                monto = rs.getFloat("total");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion TotalAperturasCaja");
        }

        return monto;
    }

    public float PrimeraApertura() {
        float monto = 0;
        this.cn = Conexion();
        this.consulta = "SELECT efectivo FROM aperturas INNER JOIN cajas ON(aperturas.caja=cajas.id) WHERE aperturas.id = 1 AND cajas.caja='CAJA1'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                monto = rs.getFloat("efectivo");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion UltimaApertura");
        }

        return monto;
    }

    public DefaultTableModel productosMasVendidos(Date fecha1, Date fecha2) {
        int n = 1;
        this.cn = Conexion();
        this.consulta = "SELECT p.id,p.nombre, m.nombre AS marca, p.descripcion, SUM(d.cantidadProducto) AS vendido FROM productos AS p "
                + "LEFT JOIN detalleFactura AS d ON(p.id=d.producto) LEFT JOIN marca AS m ON(p.marca=m.id) LEFT JOIN facturas AS f ON(d.factura = f.id) "
                + "WHERE f.fecha BETWEEN ? AND ? GROUP BY p.id ORDER BY vendido DESC";
        String[] registros = new String[5];
        String[] title = {"ID", "Nombre", "Marca", "Descripción", "Vendido"};
        this.modelo = new DefaultTableModel(null, title) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1, fecha1);
            this.pst.setDate(2, fecha2);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("nombre");
                registros[2] = rs.getString("marca");
                registros[3] = rs.getString("descripcion");
                registros[4] = rs.getString("vendido") + " Unidades";
                this.modelo.addRow(registros);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion productosMasVendidos");
        }

        return this.modelo;

    }

    public DefaultTableModel BuscarFactura(int id) {
        this.cn = Conexion();
        this.consulta = "SELECT facturas.id,facturas.fecha AS fechaFactura, impuestoISV, totalFactura, nombre_comprador, formapago.tipoVenta, creditos.id as idCredito, cajas.caja from facturas LEFT JOIN formapago ON(formapago.id = facturas.tipoVenta) LEFT JOIN creditos ON(facturas.credito = creditos.id) LEFT JOIN cajas ON(facturas.caja=cajas.id) WHERE facturas.id = ?";        
        String[] facturas = new String[8];
        String[] titulos = {"Factura", "Fecha", "IVA", "Total", "Comprador", "Forma Pago", "N° Credito", "Caja"};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
//            this.pst.setDate(1, fecha);
            this.pst.setInt(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                facturas[0] = rs.getString("id");
                facturas[1] = rs.getString("fechaFactura");
                facturas[2] = rs.getString("impuestoISV");
                facturas[3] = rs.getString("totalFactura");
                facturas[4] = rs.getString("nombre_comprador");
                facturas[5] = rs.getString("tipoVenta");
                facturas[6] = rs.getString("idCredito");
                facturas[7] = rs.getString("caja");
                modelo.addRow(facturas);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+ "Error en la funcion BuscarFactura en modelo Reportes");
        }
        return this.modelo;
    }

    public void MonedasRecibidas(Date fecha){
        this.cn = Conexion();
        this.consulta = "SELECT SUM(cantDolares) AS Dolares FROM monedasRecibidas WHERE fecha = ? AND tipoMovimiento = 'Ingreso' AND compra_venta = 'venta'";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                this.Dolares = rs.getFloat("Dolares");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion MonedasRecibidas en modelo Reportes");
        }
    }
    
    public void MonedasRecibidasCompra(Date fecha){
        this.cn = Conexion();
        this.consulta = "SELECT SUM(cantDolares) AS Dolares FROM monedasRecibidas WHERE fecha = ? AND tipoMovimiento = 'Ingreso' AND compra_venta = 'compra'";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1, fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                this.dolaresComprados = rs.getFloat("Dolares");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion MonedasRecibidasCompra en modelo");
        }
    }
    
    public void EgresoMonedas(Date fecha){
        this.cn = Conexion();
        this.consulta = "SELECT SUM(cantDolares) AS Dolares FROM monedasRecibidas WHERE fecha = ? AND tipoMovimiento = 'Salida'";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                this.EgresoDolares = rs.getFloat("Dolares");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion MonedasRecibidas en modelo Reportes");
        }
    }
    
    public float precioVenta(Date fecha){
        float total = 0, totalUtilidades = 0;
        this.cn = Conexion();
        this.consulta = "select sum(f.totalFactura) as total from facturas as f where f.fecha=?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            this.cn.close();
            totalUtilidades = total - (precioCompraDolar(fecha)+precioCompraCordobas(fecha));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioVenta en el modelo de reporte");
        }
        return totalUtilidades;
    }
    
    public float precioCompraCordobas(Date fecha){
        float total = 0;
        this.cn = Conexion();
        this.consulta = "select sum(df.cantidadProducto*p.precioCompra) as total from detalleFactura as df"
                + " inner join facturas as f on(f.id=df.factura) inner join productos as p on(p.id=df.producto)"
                + " where f.fecha = ? and p.monedaCompra='Córdobas'";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioCompra en el modelo de reporte");
        }
        return total;
    }
    
    public float precioCompraDolar(Date fecha){
        float total = 0;
        this.cn = Conexion();
        this.consulta = "select sum(df.cantidadProducto*p.precioCompra) as total from detalleFactura as df"
                + " inner join facturas as f on(f.id=df.factura) inner join productos as p on(p.id=df.producto)"
                + " where f.fecha = ? and p.monedaCompra='Dolar'";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioCompra en el modelo de reporte");
        }
        return total*this.precioDolar;
    }
    
}
