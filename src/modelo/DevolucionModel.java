/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class DevolucionModel extends Conexiondb {

	private int idProducto,
		idDetalle,
		idFactura,
		banderin;
	private float sacarImpuesto,
		impuesto,
		importeActual,
		cantidadDevolver,
		cantidadUpdate,
		precio,
		cantidadActual,
		totalFactura,
		tasaCambio,
		importe,
		ivaUpdate,
		subTotalUpdate,
		diferencia,
		totalUpdate;
	String monedaVenta,
		diferenciaString;

	Productos producto;
	DecimalFormat formato;
	Connection cn;
	PreparedStatement pst;
	ResultSet rs;
	String consulta;

	public DevolucionModel() {
		this.producto = new Productos();
		this.formato = new DecimalFormat("##########0.00");
	}

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public int getIdDetalle() {
		return idDetalle;
	}

	public void setIdDetalle(int idDetalle) {
		this.idDetalle = idDetalle;
	}

	public int getIdFactura() {
		return idFactura;
	}

	public void setIdFactura(int idFactura) {
		this.idFactura = idFactura;
	}

	public float getSacarImpuesto() {
		return sacarImpuesto;
	}

	public void setSacarImpuesto(float sacarImpuesto) {
		this.sacarImpuesto = sacarImpuesto;
	}

	public float getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(float impuesto) {
		this.impuesto = impuesto;
	}

	public float getCantidadDevolver() {
		return cantidadDevolver;
	}

	public void setCantidadDevolver(float cantidadDevolver) {
		this.cantidadDevolver = cantidadDevolver;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public float getCantidadActual() {
		return cantidadActual;
	}

	public void setCantidadActual(float cantidadActual) {
		this.cantidadActual = cantidadActual;
	}

	public float getTotalFactura() {
		return totalFactura;
	}

	public void setTotalFactura(float totalFactura) {
		this.totalFactura = totalFactura;
	}

	public float getTasaCambio() {
		return tasaCambio;
	}

	public void setTasaCambio(float tasaCambio) {
		this.tasaCambio = tasaCambio;
	}

	public float getIvaUpdate() {
		return ivaUpdate;
	}

	public void setIvaUpdate(float ivaUpdate) {
		this.ivaUpdate = ivaUpdate;
	}

	public float getSubTotalUpdate() {
		return subTotalUpdate;
	}

	public void setSubTotalUpdate(float subTotalUpdate) {
		this.subTotalUpdate = subTotalUpdate;
	}

	public float getImporteActual() {
		return importeActual;
	}

	public void setImporteActual(float importeActual) {
		this.importeActual = importeActual;
	}

	public void devolver() {
		if (cantidadDevolver <= cantidadActual) {
			cantidadUpdate = cantidadActual - cantidadDevolver;
			this.obtenerTotalFacturaSeleccionada(idFactura);
			//validar que moneda
			this.monedaVentaProducto(this.idProducto);
			if (monedaVenta.equals("Dolar")) {
				//validar que precioDolar sea numerico
				tasaCambio = (importeActual / cantidadActual) / precio;
				importe = (cantidadUpdate * precio) * tasaCambio;
				diferencia = (cantidadDevolver * precio) * tasaCambio;
				diferenciaString = this.formato.format(diferencia);
				diferencia = Float.parseFloat(diferenciaString);
				totalUpdate = totalFactura - diferencia;

			} else {
				importe = cantidadUpdate * precio;
				totalUpdate = totalFactura - (cantidadDevolver * precio);
			}
			//calcular el nuevo impuesto
			this.ivaUpdate = (totalUpdate * impuesto) / 100;
			//calcular el nuevo subtotal
			subTotalUpdate = totalUpdate - ivaUpdate;
			//llamar las funciones para actualizar los datos correpondientes
			this.ActualizarDetalle(
				idDetalle,
				idProducto,
				precio,
				cantidadUpdate,
				importe
			);
			this.producto.AgregarProductoStock(String.valueOf(idProducto), String.valueOf(cantidadDevolver));
			ActualizarDevolucion(
				idFactura,
				ivaUpdate,
				totalUpdate
			);
			
		}
	}
	
	public void obtenerTotalFacturaSeleccionada(int id) {
		float total = 0;
		this.cn = Conexion();
		this.consulta = "SELECT totalFactura FROM facturas WHERE id = ?";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setInt(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				this.totalFactura = rs.getFloat("totalFactura");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en la funcion obtenerTotalFacturaSeleccionada en modelo Facturacion");
		}
	}

	public void monedaVentaProducto(int id) {
		this.cn = Conexion();
		this.consulta = "SELECT monedaVenta FROM productos WHERE id = ?";

		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setInt(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				this.monedaVenta = rs.getString("monedaVenta");
			}
			this.cn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void ActualizarDetalle(int id, int producto, float precio, float cant, float total) {
		cn = Conexion();
		this.consulta = "UPDATE detalleFactura SET producto = ?, precioProducto = ?, cantidadProducto = ?, totalVenta = ? WHERE id=?";
		try {
			pst = this.cn.prepareStatement(this.consulta);
			pst.setInt(1, producto);
			pst.setFloat(2, precio);
			pst.setFloat(3, cant);
			pst.setFloat(4, total);
			pst.setFloat(5, id);
			pst.execute();
			this.banderin = pst.executeUpdate();
			if (this.banderin > 0) {

			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}

	}

	public void ActualizarDevolucion(int id, float iva, float total) {
		this.cn = Conexion();
		String IVA = formato.format(iva), TOTAL = formato.format(total);
		this.consulta = "UPDATE facturas SET impuestoISV = ?, totalFactura = ? WHERE id = ?";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, IVA);
			this.pst.setString(2, TOTAL);
			this.pst.setInt(3, id);
			this.pst.executeUpdate();
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en la funcion ActualizarDevolucion en modelo Facturacion");
		}
	}
}
