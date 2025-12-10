package data;

import java.sql.Date;
import java.util.*;

import business.clientes.Cliente;
import business.clientes.FichaVeiculo;
import business.clientes.Veiculo;
import business.funcionarios.Funcionario;

import java.sql.*;
import java.util.stream.Collectors;

public class ClientesDAO implements Map<String, Cliente> {
    private static ClientesDAO singleton = null;

    private ClientesDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS clientes(" +
                    "nif VARCHAR(9) NOT NULL,"+
                    "nome VARCHAR(150) NOT NULL,"+
                    "morada VARCHAR(150) NOT NULL," +
                    "contacto VARCHAR(12) NOT NULL," +
                    "carros VARCHAR(8) NOT NULL," +
                    "FOREIGN KEY (carros) REFERENCES veiculos (matricula)," +
                    "PRIMARY KEY (nif));";

            stm.executeUpdate(sql);

            String sql1 = "CREATE TABLE IF NOT EXISTS veiculos(" +
                    "type INT NOT NULL," +
                    "matricula VARCHAR(8) NOT NULL,"+
                    "marca VARCHAR(50) NOT NULL,"+
                    "modelo VARCHAR(50) NOT NULL," +
                    "quilometragem INT NOT NULL," +
                    "ano INT NOT NULL,"+
                    "data_revisao DATETIME NOT NULL," +
                    "PRIMARY KEY (matricula));";

            stm.executeUpdate(sql1);


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar a tabela Clientes: " + e.getMessage());
        }
    }

    public static ClientesDAO getInstance() {
        if (ClientesDAO.singleton == null)
            ClientesDAO.singleton = new ClientesDAO();
        return ClientesDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM clientes")) {
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
                     stm.executeQuery("SELECT nif FROM clientes WHERE nif='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public boolean containsValue(Object value) {
        Cliente c = (Cliente) value;
        return this.containsKey(c.getNif());
    }
    public Set<String> keySet() {
        Set<String> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nif FROM clientes")) {
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

    public Set<Map.Entry<String, Cliente>> entrySet() {
        return this.keySet().stream().map(k -> Map.entry(k, this.get(k))).collect(Collectors.toSet());
    }

    public Cliente get(Object key) {
        Cliente c = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM clientes WHERE nif='" + key + "'")) {
            if (rs.next()) {
                c = new Cliente(rs.getString("nif"),
                        rs.getString("nome"),
                        rs.getString("morada"),
                        rs.getString("contacto"),
                        getVeiculosPorNif(rs.getString("nif")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    public Cliente put(String key, Cliente c) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO clientes (nif, nome, morada, contacto) VALUES (?, ?, ?, ?)")) {
                pstm.setString(1, c.getNif());
                pstm.setString(2, c.getNome());
                pstm.setString(3, c.getMorada());
                pstm.setString(4, c.getContacto());
                pstm.executeUpdate();
            }

            // Inserir veículos associados ao cliente
            inserirVeiculos(c.getNif(), c.getVeiculos());
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    // Método para obter veículos por NIF do cliente
    private Map<String, Veiculo> getVeiculosPorNif(String nif) {
        Map<String, Veiculo> veiculos = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM veiculos WHERE cliente_nif='" + nif + "'")) {
            while (rs.next()) {
                Veiculo v = VeiculosDAO.getInstance().get(rs.getString("matricula"));

                veiculos.put(v.getMatricula() ,v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return veiculos;
    }

    // Método para inserir veículos associados ao cliente
    private void inserirVeiculos(String nif, Map<String, Veiculo> veiculos) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            for (Veiculo v : veiculos.values()) {
                try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO veiculos (cliente_nif, tipo, matricula, marca, modelo, quilometragem, ano, data_revisao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstm.setString(1, nif);
                    pstm.setInt(2, v.getType());
                    pstm.setString(3, v.getMatricula());
                    pstm.setString(4, v.getMarca());
                    pstm.setString(5, v.getModelo());
                    pstm.setInt(6, v.getQuilometragem());
                    pstm.setInt(7, v.getAno());
                    pstm.setTimestamp(8, Timestamp.valueOf(v.getData_revisao()));
                    pstm.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public void putAll(Map<? extends String, ? extends Cliente> c) {
        c.keySet().forEach(i -> this.put(i, c.get(i)));
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE clientes");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public Cliente remove(Object key) {
       Cliente c = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("DELETE FROM clientes WHERE nif = ?")){
                c = this.get(key);
                pstm.setString(1,(String)key);
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return c;
    }

    public Collection<Cliente> values() {
        Collection<Cliente> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT nif FROM clientes")) {
            while (rs.next()) {
                String idt = rs.getString("nif");
                Cliente c = this.get(idt);
                res.add(c);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
