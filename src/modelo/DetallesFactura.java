package modelo;

import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class DetallesFactura extends Conexiondb {
	private int factura,
		producto;
	private float precio,
		cantidad,
		total;

	String consulta;
	DefaultTableModel modelo;
	Connection cn;
	PreparedStatement pst;
	DecimalFormat formato;
	int banderin;

	public DetallesFactura() {

	}

	public int getFactura() {
		return factura;
	}

	public void setFactura(int factura) {
		this.factura = factura;
	}

	public int getProducto() {
		return producto;
	}

	public void setProducto(int producto) {
		this.producto = producto;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public float getCantidad() {
		return cantidad;
	}

	public void setCantidad(float cantidad) {
		this.cantidad = cantidad;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public void DetalleFactura() {
		cn = Conexion();
		this.consulta = "INSERT INTO detalleFactura(factura, producto, precioProducto, cantidadProducto, totalVenta)"
			+ " VALUES(?,?,?,?,?)";
		try {
			pst = this.cn.prepareStatement(this.consulta);
			pst.setInt(1, this.factura);
			pst.setInt(2, this.producto);
			pst.setFloat(3, this.precio);
			pst.setFloat(4, this.cantidad);
			pst.setFloat(5, this.total);
			this.banderin = pst.executeUpdate();
			if (banderin > 0) {
				//JOptionPane.showMessageDialog(null, "detalle guardado");
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}
}
