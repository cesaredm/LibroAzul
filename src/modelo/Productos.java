package modelo;

import controlador.CtrlProducto;
import java.awt.List;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Productos extends Conexiondb {

	private String codigoBarra,
		nombre,
		monedaCompra,
		monedaVenta,
		ubicacion,
		descripcion;
	private Date fechaVencimiento;
	private int categoria,
		laboratorio,
		id;
	private float precioCompra,
		precioVenta,
		precioMinimoVenta,
		stock,
		utilidad;
	DefaultTableModel modelo;
	DefaultComboBoxModel combo;
	Connection cn;
	PreparedStatement pst;
	String consulta;
	int banderin;
	private boolean existe = true;
	private float precioMinimo;

	public Productos() {
		this.cn = null;
		this.combo = new DefaultComboBoxModel();
		this.pst = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMonedaCompra() {
		return monedaCompra;
	}

	public void setMonedaCompra(String monedaCompra) {
		this.monedaCompra = monedaCompra;
	}

	public String getMonedaVenta() {
		return monedaVenta;
	}

	public void setMonedaVenta(String monedaVenta) {
		this.monedaVenta = monedaVenta;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public int getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(int laboratorio) {
		this.laboratorio = laboratorio;
	}

	public float getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(float precioCompra) {
		this.precioCompra = precioCompra;
	}

	public float getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(float precioVenta) {
		this.precioVenta = precioVenta;
	}

	public float getPrecioMinimoVenta() {
		return precioMinimoVenta;
	}

	public void setPrecioMinimoVenta(float precioMinimoVenta) {
		this.precioMinimoVenta = precioMinimoVenta;
	}

	public float getStock() {
		return stock;
	}

	public void setStock(float stock) {
		this.stock = stock;
	}

	public float getUtilidad() {
		return utilidad;
	}

	public void setUtilidad(float utilidad) {
		this.utilidad = utilidad;
	}

	public boolean isExiste() {
		return existe;
	}

	public void setExiste(boolean existe) {
		this.existe = existe;
	}

	public float getPrecioMinimo() {
		return this.precioMinimo;
	}

	public void Guardar() {
		cn = Conexion();
		this.consulta = "INSERT INTO productos(codigoBarra, nombre, precioCompra, monedaCompra, precioVenta, precioMinimo, monedaVenta,"
			+ " fechaVencimiento, stock, categoria, marca, ubicacion, descripcion, utilidad) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			pst = this.cn.prepareStatement(this.consulta);
			pst.setString(1, this.codigoBarra);
			pst.setString(2, this.nombre);
			pst.setFloat(3, this.precioCompra);
			pst.setString(4, this.monedaCompra);
			pst.setFloat(5, this.precioVenta);
			pst.setFloat(6, this.precioMinimoVenta);
			pst.setString(7, this.monedaVenta);
			pst.setDate(8, this.fechaVencimiento);
			pst.setFloat(9, this.stock);
			pst.setInt(10, this.categoria);
			pst.setInt(11, this.laboratorio);
			pst.setString(12, this.ubicacion);
			pst.setString(13, this.descripcion);
			pst.setFloat(14, this.utilidad);
			this.banderin = pst.executeUpdate();
			if (this.banderin > 0) {
				JOptionPane.showMessageDialog(null, "Producto guardado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	public void guardarKardexIncial(int producto, String user, float cantidad, String anotacion, String otrasDescripciones) {
		this.cn = Conexion();
		this.consulta = "INSERT INTO kardexentradas(producto,usuario,cantidad,anotacion,otrasDescripciones) VALUES(?,?,?,?,?)";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setInt(1, producto);
			this.pst.setString(2, user);
			this.pst.setFloat(3, cantidad);
			this.pst.setString(4, anotacion);
			this.pst.setString(5, otrasDescripciones);
			this.banderin = this.pst.executeUpdate();
			if (this.banderin > 0) {
				//JOptionPane.showMessageDialog(null, "Kardex Actualizado exitosamente");
			} else {
				JOptionPane.showMessageDialog(null, "Error al actualizar kardex");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en la funcion guardarKardex en modelo producto.");
		}
	}
	public void Actualizar(String id, String codigoBarra, String nombre, String precioCompra, String monedaCompra, String precioVenta, String monedaVenta, String precioMinimo, Date fechaVencimiento, String stock, String categoria, String laboratorio, String ubicacion, String descripcion, float utilidad) {
		cn = Conexion();
		this.consulta = "UPDATE productos SET codigoBarra=?, nombre=?, precioCompra=?, monedaCompra=?, precioVenta=?, precioMinimo=?, monedaVenta=?, fechaVencimiento=?, stock=?, categoria=?, marca=?, ubicacion=?, descripcion=?, utilidad=? WHERE id = ?";

		float compra = Float.parseFloat(precioCompra), venta = Float.parseFloat(precioVenta), cantidad = Float.parseFloat(stock), ventaMin = Float.parseFloat(precioMinimo);
		int Idcategoria = Integer.parseInt(categoria), Idlaboratorio = Integer.parseInt(laboratorio);
		try {
			pst = this.cn.prepareStatement(this.consulta);
			pst.setString(1, codigoBarra);
			pst.setString(2, nombre);
			pst.setFloat(3, compra);
			pst.setString(4, monedaCompra);
			pst.setFloat(5, venta);
			pst.setFloat(6, ventaMin);
			pst.setString(7, monedaVenta);
			pst.setDate(8, fechaVencimiento);
			pst.setFloat(9, cantidad);
			pst.setInt(10, Idcategoria);
			pst.setInt(11, Idlaboratorio);
			pst.setString(12, ubicacion);
			pst.setString(13, descripcion);
			pst.setFloat(14, utilidad);
			pst.setString(15, id);
			this.banderin = pst.executeUpdate();
			if (this.banderin > 0) {
				JOptionPane.showMessageDialog(null, "Producto actualizado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	public void Eliminar(String id) {
		cn = Conexion();
		this.consulta = "DELETE FROM productos WHERE id=" + id;
		try {
			pst = this.cn.prepareStatement(consulta);
			this.banderin = pst.executeUpdate();
			if (banderin > 0) {
				JOptionPane.showMessageDialog(null, "Dato borrado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	public DefaultTableModel Consulta(String buscar) {
		cn = Conexion();
		this.consulta = "SELECT productos.id, productos.codigoBarra, productos.nombre AS nombreProducto, precioCompra, monedaCompra, precioVenta, monedaVenta, precioMinimo,fechaVencimiento, stock, ubicacion, productos.descripcion, categorias.nombre AS nombreCategoria, marca.nombre as nombreMarca, productos.utilidad FROM productos LEFT JOIN categorias ON(productos.categoria=categorias.id) LEFT JOIN marca ON(productos.marca=marca.id) WHERE CONCAT(productos.codigoBarra, productos.nombre, categorias.nombre, marca.nombre) LIKE '%" + buscar + "%' ORDER BY productos.id DESC";
		String[] registros = new String[15];
		String[] titulos = {"Id", "Codigo Barra", "Nombre", "precioCompra", "Moneda", "precioVenta", "Moneda", "P. venta Min", "Descripci??n", "Fecha Vencimiento", "Stock", "Presentaci??n", "Laboratorio", "Ubicaci??n", "Utlidad%"};
		modelo = new DefaultTableModel(null, titulos) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(consulta);
			while (rs.next()) {
				registros[0] = rs.getString("id");
				registros[1] = rs.getString("codigoBarra");
				registros[2] = rs.getString("nombreProducto");
				registros[3] = rs.getString("precioCompra");
				registros[4] = rs.getString("monedaCompra");
				registros[5] = rs.getString("precioVenta");
				registros[6] = rs.getString("monedaVenta");
				registros[7] = rs.getString("precioMinimo");
				registros[8] = rs.getString("descripcion");
				registros[9] = rs.getString("fechaVencimiento");
				registros[10] = rs.getString("stock");
				registros[11] = rs.getString("nombreCategoria");
				registros[12] = rs.getString("nombreMarca");
				registros[13] = rs.getString("ubicacion");
				registros[14] = rs.getString("utilidad");
				this.modelo.addRow(registros);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}

		return modelo;
	}
	//Mostrar todas la categorias para agregra al producto

	public DefaultTableModel MostrarCategorias(String nombre) {
		cn = Conexion();
		this.consulta = "SELECT * FROM categorias WHERE nombre LIKE '%" + nombre + "%'";
		String[] resultados = new String[3];
		String[] titulos = {"Id", "Nombre", "Descripcion"};
		modelo = new DefaultTableModel(null, titulos) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(consulta);
			while (rs.next()) {

				resultados[0] = rs.getString("id");
				resultados[1] = rs.getString("nombre");
				resultados[2] = rs.getString("descripcion");
				this.modelo.addRow(resultados);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return modelo;
	}
	//Mostrar todas la Laboratorio para agregra al producto

	public DefaultTableModel MostrarMarca(String nombre) {
		cn = Conexion();
		this.consulta = "SELECT * FROM marca WHERE nombre LIKE '%" + nombre + "%'";
		String[] resultados = new String[3];
		String[] titulos = {"Id", "Nombre", "Descripcion"};
		modelo = new DefaultTableModel(null, titulos) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(consulta);
			while (rs.next()) {

				resultados[0] = rs.getString("id");
				resultados[1] = rs.getString("nombre");
				resultados[2] = rs.getString("descripcion");
				modelo.addRow(resultados);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return modelo;
	}

	public String ObtenerIdMarca(String nombre)//metodo para obtener Id de laboratorio para modificar producto
	{
		String id = "";
		cn = Conexion();
		this.consulta = "SELECT id FROM marca WHERE nombre='" + nombre + "'";
		try {
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(this.consulta);
			while (rs.next()) {
				id = rs.getString("id");
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return id;
	}

	public String ObtenerIdCategoria(String nombre)//metodo para obtener Id de categoria para modificar producto
	{
		String id = "";
		cn = Conexion();
		this.consulta = "SELECT id FROM categorias WHERE nombre='" + nombre + "'";
		try {
			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(this.consulta);
			while (rs.next()) {
				id = rs.getString("id");
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return id;
	}

	public void AgregarProductoStock(String id, String cantidad)//metodo para agregar producto al stock
	{
		cn = Conexion();
		float c = Float.parseFloat(cantidad);
		int idP = Integer.parseInt(id);
		this.consulta = "{CALL agregarProductoStock(?,?)}";
		try {
			CallableStatement cst = this.cn.prepareCall(this.consulta);
			cst.setInt(1, idP);
			cst.setFloat(2, c);
			this.banderin = cst.executeUpdate();
			if (banderin > 0) {
				//JOptionPane.showMessageDialog(null, "Se Agrego Exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}

	public DefaultTableModel MinimoStock(String categoria, float cantidad) {
		cn = Conexion();
		//Agregar precioVenta y MonedaVenta a la consulta y al titulo de la tabla
		this.consulta = "SELECT productos.id, productos.codigoBarra, productos.nombre AS nombreProducto, precioVenta, monedaVenta, fechaVencimiento,stock, ubicacion, productos.descripcion, categorias.nombre AS nombreCategoria, marca.nombre as nombreMarca FROM productos INNER JOIN categorias ON(productos.categoria=categorias.id) INNER JOIN marca ON(productos.marca=marca.id) WHERE productos.stock < " + cantidad + " AND categorias.nombre LIKE '%" + categoria + "%' ORDER BY productos.stock";
		String[] titulos = {"Id", "Codigo Barra", "Nombre", "precioVenta", "Moneda", "Fecha Vencimiento", "Stock", "Categoria", "Marca", "Ubicacion", "Descripcion"};
		String[] registros = new String[12];
		modelo = new DefaultTableModel(null, titulos) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			Statement pst = this.cn.createStatement();
			//pst.setInt(1, cantidad);
			//pst.setString(2, categoria);
			ResultSet rs = pst.executeQuery(consulta);
			while (rs.next()) {
				registros[0] = rs.getString("id");
				registros[1] = rs.getString("codigoBarra");
				registros[2] = rs.getString("nombreProducto");
				registros[3] = rs.getString("precioVenta");
				registros[4] = rs.getString("monedaVenta");
				registros[5] = rs.getString("fechaVencimiento");
				registros[6] = rs.getString("stock");
				registros[7] = rs.getString("nombreCategoria");
				registros[8] = rs.getString("nombreMarca");
				registros[9] = rs.getString("ubicacion");
				registros[10] = rs.getString("descripcion");
				this.modelo.addRow(registros);
			}
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return this.modelo;
	}

	public void GenerarReporteStockMin(String categ, float cantidad) throws SQLException {
		try {
			this.cn = Conexion();
			JasperReport Reporte = null;
			String path = "/Reportes/minStock.jasper";
			Map parametros = new HashMap();
			parametros.put("cantidad", cantidad);
			parametros.put("categoria", categ);
			//Reporte = (JasperReport) JRLoader.loadObject(path);
			Reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/minStock.jasper"));
			JasperPrint jprint = JasperFillManager.fillReport(Reporte, parametros, cn);
			JasperViewer vista = new JasperViewer(jprint, false);
			vista.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			vista.setVisible(true);
			cn.close();
		} catch (JRException ex) {
			Logger.getLogger(CtrlProducto.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	//metodo para obtener el total de inversion en el negocio precio de compra

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

	public float inversionCordobas() {
		cn = Conexion();
		float inversion = 0;
		this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos WHERE monedaCompra = 'C??rdobas'";
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

	public float inversionDolar() {
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

	public void ExitsCodBarra(String codBarra) {
		String producto = "";
		this.cn = Conexion();
		this.consulta = "SELECT codigoBarra FROM productos WHERE codigoBarra = ?";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, codBarra);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				producto = rs.getString("codigoBarra");
			}
			if (producto.equals("")) {
				setExiste(false);
			} else {
				setExiste(true);
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en la funcion ExistCodBarra en modelo productos");
		}
	}

	public void precioMinimo(String id) {
		this.cn = Conexion();
		this.consulta = "SELECT precioMinimo FROM productos WHERE id = ?";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				this.precioMinimo = rs.getFloat("precioMinimo");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + "en la funcion precioMinimo en el modelo producto");
		}
	}

	public DefaultTableModel kardexOtrasSalidas() {
		this.cn = Conexion();
		this.consulta = "SELECT productos.codigoBarra, nombre, k.cantidad,fecha,usuario,anotacion,"
			+ "otrasDescripciones FROM productos"
			+ " INNER JOIN kardexentradas AS k ON(productos.id=k.producto) WHERE k.producto = ? AND"
			+ " k.anotacion = 'Salida de stock'";
		String[] titulos = {"COD. BARRA", "PRODUCTO", "CANTIDAD", "F. SALIDA", "USUARIO", "MOV", "ANOTACION"};
		String[] datos = new String[7];
		this.modelo = new DefaultTableModel(null, titulos) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setInt(1, this.id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				datos[0] = rs.getString("codigoBarra");
				datos[1] = rs.getString("nombre");
				datos[2] = rs.getString("cantidad");
				datos[3] = rs.getString("fecha");
				datos[4] = rs.getString("usuario");
				datos[5] = rs.getString("anotacion");
				datos[6] = rs.getString("otrasDescripciones");
				this.modelo.addRow(datos);
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,e + "ERROR: en el metodo kardexOtrasSalidas en el modelo productos.");
		}

		return this.modelo;
	}

	public DefaultTableModel kardexSalidas(String id) {
		this.cn = Conexion();
		this.consulta = "SELECT P.ID,P.CODIGOBARRA,P.NOMBRE,DF.CANTIDADPRODUCTO AS CANTIDADDESALIDA, F.FECHA AS FECHASALIDA,"
			+ " F.ID AS NUMEROFACTURA FROM"
			+ " PRODUCTOS AS P INNER JOIN DETALLEFACTURA AS DF ON(P.ID=DF.PRODUCTO) INNER JOIN FACTURAS AS F ON(DF.FACTURA=F.ID)"
			+ " WHERE P.ID = ? AND DF.CANTIDADPRODUCTO != 0";
		String[] titulos = {"ID", "COD. BARRA", "NOMBRE", "CANTIDAD SALIDA", "FECHA SALIDA", "N. FACTURA"};
		String[] datos = new String[6];
		this.modelo = new DefaultTableModel(null, titulos) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				datos[0] = rs.getString("ID");
				datos[1] = rs.getString("CODIGOBARRA");
				datos[2] = rs.getString("NOMBRE");
				datos[3] = rs.getString("CANTIDADDESALIDA");
				datos[4] = rs.getString("FECHASALIDA");
				datos[5] = rs.getString("NUMEROFACTURA");
				this.modelo.addRow(datos);
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en el metodo kardex en modelo productos");
		}
		return this.modelo;
	}

	public String kardexInicial(String id) {
		String kardexInicial = "";
		this.consulta = "SELECT cantidad FROM kardexentradas WHERE producto = ? AND anotacion = 'inicial'";
		this.cn = Conexion();
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				kardexInicial = rs.getString("cantidad");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en la funcion kardexInicial en modelo productos");
		}
		return kardexInicial;
	}

	public String countKardexSalidas(String id) {
		this.cn = Conexion();
		this.consulta = "SELECT SUM(DF.CANTIDADPRODUCTO) AS SALIDA FROM DETALLEFACTURA AS DF INNER JOIN PRODUCTOS AS P ON(DF.PRODUCTO=P.ID)"
			+ " WHERE P.ID = ?";
		String salidas = "";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				salidas = rs.getString("salida");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en el metodo kardex en modelo productos");
		}
		return salidas;
	}

	public DefaultTableModel kardexEntradas(String id) {
		this.cn = Conexion();
		this.consulta = "SELECT P.ID,P.CODIGOBARRA,P.NOMBRE,K.CANTIDAD,K.FECHA, K.ANOTACION,K.USUARIO FROM PRODUCTOS AS P INNER JOIN"
			+ " KARDEXENTRADAS AS K ON(P.ID=K.PRODUCTO) WHERE P.ID = ? AND (K.ANOTACION = 'add' OR K.ANOTACION = 'edicion stock')";
		String[] titulos = {"ID", "COD. BARRA", "NOMBRE", "CANTIDAD ENTRADA", "FECHA ENTRADA", "ACCION", "USUARIO"};
		String[] datos = new String[7];
		this.modelo = new DefaultTableModel(null, titulos) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setString(1, id);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				datos[0] = rs.getString("ID");
				datos[1] = rs.getString("CODIGOBARRA");
				datos[2] = rs.getString("NOMBRE");
				datos[3] = rs.getString("CANTIDAD");
				datos[4] = rs.getString("FECHA");
				datos[5] = rs.getString("ANOTACION");
				datos[6] = rs.getString("USUARIO");
				this.modelo.addRow(datos);
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en el metodo kardexEntradas en modelo productos");
		}
		return this.modelo;
	}

	public int ultimoRegistro() {
		int id = 0;
		this.cn = Conexion();
		this.consulta = "SELECT MAX(id) as ID FROM productos";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			ResultSet rs = this.pst.executeQuery();
			while (rs.next()) {
				id = rs.getInt("ID");
			}
			this.cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e + " en el metodo ultimoRegistro en modelo productos");
		}
		return id;
	}

	public void Vender(String id, String cantidad) {
		cn = Conexion();
		Float cantidadP = Float.parseFloat(cantidad);
		int idP = Integer.parseInt(id);
		this.consulta = "{CALL venderProductoStock(?,?)}";
		try {
			CallableStatement cst = this.cn.prepareCall(this.consulta);
			cst.setInt(1, idP);
			cst.setFloat(2, cantidadP);
			cst.execute();
			cn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		}
	}
}
