package data;

import business.clientes.Cliente;
import business.clientes.FichaVeiculo;
import business.clientes.Veiculo;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VeiculosDAO implements Map<String,Veiculo>{
    private static VeiculosDAO singleton = null;

    private VeiculosDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS veiculos(" +
                    "type INT NOT NULL," +
                    "matricula VARCHAR(8) NOT NULL,"+
                    "marca VARCHAR(50) NOT NULL,"+
                    "modelo VARCHAR(50) NOT NULL," +
                    "quilometragem INT NOT NULL," +
                    "ano INT NOT NULL,"+
                    "data_revisao DATETIME NOT NULL," +
                    "PRIMARY KEY (matricula));";

            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar a tabela Veiculos: " + e.getMessage());
        }
    }

    public static VeiculosDAO getInstance() {
        if (VeiculosDAO.singleton == null)
            VeiculosDAO.singleton = new VeiculosDAO();
        return VeiculosDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM veiculos")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs =
                     stm.executeQuery("SELECT matricula FROM veiculos WHERE matricula='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public boolean containsValue(Object value) {
        Veiculo v = (Veiculo) value;
        return this.containsKey(v.getMatricula());
    }
    public Set<String> keySet() {
        Set<String> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT maticula FROM veiculos")) {
            while (rs.next()) {
                String idu = rs.getString("nif");
                res.add(idu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Set<Map.Entry<String, Veiculo>> entrySet() {
        return this.keySet().stream().map(k -> Map.entry(k, this.get(k))).collect(Collectors.toSet());
    }

    public Veiculo get(Object key) {
        Veiculo v = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM veiculos WHERE matricula='"+key+"'")) {
            if (rs.next())
            {
                v = new Veiculo(
                        new FichaVeiculo(),
                        rs.getInt("type"),
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("quilometragem"),
                        rs.getInt("ano"),
                        rs.getTimestamp("data_revisao").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return v;
    }

    public Veiculo put(String key, Veiculo v) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO veiculos (type,matricula,marca,modelo,quilometragem,ano,data_revisao) VALUES (?,?,?,?,?,?,?)")){
                pstm.setInt(1,v.getType());
                pstm.setString(2, v.getMatricula());
                pstm.setString(3, v.getMarca());
                pstm.setString(4, v.getModelo());
                pstm.setInt(5, v.getQuilometragem());
                pstm.setInt(6, v.getAno());
                pstm.setDate(7, v.getData_revisao());
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return v;
    }

    public void putAll(Map<? extends String, ? extends Veiculo> v) {
        v.keySet().forEach(i -> this.put(i, v.get(i)));
    }

    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE veiculos");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public Veiculo remove(Object key) {
       Veiculo v = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("DELETE FROM veiculos WHERE matricula = ?")){
                v = this.get(key);
                pstm.setString(1,(String)key);
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return v;
    }

    public Collection<Veiculo> values() {
        Collection<Veiculo> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT matricula FROM veiculos")) {
            while (rs.next()) {
                String idt = rs.getString("matricula");
                Veiculo v = this.get(idt);
                res.add(v);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
