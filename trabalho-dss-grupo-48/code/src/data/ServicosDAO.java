package data;

import business.servicos.Servico;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServicosDAO implements Map<Integer, Servico>{
    private static ServicosDAO singleton = null;

    private ServicosDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS servicos(" +
                    "id INT NOT NULL,"+
                    "nome VARCHAR(50) NOT NULL,"+
                    "duracao_media INT NOT NULL," +
                    "preco DECIMAL(8,2) NOT NULL," +
                    "PRIMARY KEY (id));";

            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar a tabela Servicos: " + e.getMessage());
        }
    }

    public static ServicosDAO getInstance() {
        if (ServicosDAO.singleton == null)
            ServicosDAO.singleton = new ServicosDAO();
        return ServicosDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM servicos")) {
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
                     stm.executeQuery("SELECT id FROM servicos WHERE id='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public boolean containsValue(Object value) {
        Servico s = (Servico) value;
        return this.containsKey(s.getId());
    }
    public Set<Integer> keySet() {
        Set<Integer> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM servicos")) {
            while (rs.next()) {
                int idu = rs.getInt("nif");
                res.add(idu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Set<Map.Entry<Integer, Servico>> entrySet() {
        return this.keySet().stream().map(k -> Map.entry(k, this.get(k))).collect(Collectors.toSet());
    }


    public Servico get(Object key) {
        Servico s = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM servicos WHERE id='"+key+"'")) {
            if (rs.next())
            {
                s = new Servico(rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("duracao_media"),
                        rs.getFloat("preco"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return s;
    }

    @Override
    public Servico put(Integer integer, Servico servico) {
        return null;
    }

    public Servico put(String key, Servico s) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO servicos (id,nome,duracao_media,preco) VALUES (?,?,?,?)")){
                pstm.setInt(1,s.getId());
                pstm.setString(2, s.getNome());
                pstm.setInt(3, s.getDuracao_media());
                pstm.setFloat(4, s.getPreco());
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return s;
    }

    public void putAll(Map<? extends Integer, ? extends Servico> s) {
        s.keySet().forEach(i -> this.put(String.valueOf(i), s.get(i)));
    }

    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE servicos");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public Servico remove(Object key) {
        Servico s = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("DELETE FROM servicos WHERE id = ?")){
                s = this.get(key);
                pstm.setString(1,(String)key);
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return s;
    }

    public Collection<Servico> values() {
        Collection<Servico> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM servicos")) {
            while (rs.next()) {
                String idt = rs.getString("id");
                Servico s = this.get(idt);
                res.add(s);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
