package data;

import business.clientes.Cliente;
import business.clientes.FichaVeiculo;
import business.clientes.Veiculo;
import business.funcionarios.Funcionario;
import business.servicos.Servico;
import business.workshop.Horario;
import business.workshop.Workshop;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class WorkshopsDAO implements Map<Integer, Workshop> {
    private static WorkshopsDAO singleton = null;

    private WorkshopsDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS workshops(" +
                    "id INT NOT NULL," +
                    "nome VARCHAR(50) NOT NULL,"+
                    "localidade VARCHAR(50) NOT NULL,"+
                    "inicio DATETIME NOT NULL," +
                    "fim DATETIME NOT NULL," +
                    "PRIMARY KEY (id));";

            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar a tabela Workshops: " + e.getMessage());
        }
    }

    public static WorkshopsDAO getInstance() {
        if (WorkshopsDAO.singleton == null)
            WorkshopsDAO.singleton = new WorkshopsDAO();
        return WorkshopsDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM workshops")) {
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
                     stm.executeQuery("SELECT id FROM workshops WHERE id='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public boolean containsValue(Object value) {
        Workshop w = (Workshop) value;
        return this.containsKey(w.getId());
    }
    public Set<Integer> keySet() {
        Set<Integer> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM workshops")) {
            while (rs.next()) {
                int idu = rs.getInt("id");
                res.add(idu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Set<Entry<Integer, Workshop>> entrySet() {
        return this.keySet().stream().map(k -> Map.entry(k, this.get(k))).collect(Collectors.toSet());
    }

    public Workshop get(Object key) {
        Workshop w = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM workshops WHERE id='"+key+"'")) {
            if (rs.next())
            {
                w = new Workshop(rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("localidade"),
                        new Horario(rs.getTimestamp("inicio").toLocalDateTime(),
                                rs.getTimestamp("fim").toLocalDateTime())
                        getclientes(rs.getInt("id")),
                        getfuncionarios(rs.getInt("id")),
                        getpostos(rs.getInt("id")));
                //TODO: não está bem por causa da compatibilidade de tipos, corrigir tambem na proxima funcao
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return w;
    }

    public Map <String, Cliente> getclientes (int key){
        Map <String, Cliente> clientes = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT cliente_nif\n" +
                     "FROM workshops_clientes\n" +
                     "WHERE workshop_id = '"+key+"'")){
            while (rs.next()){
                Cliente c = ClientesDAO.getInstance().get(rs.getString("cliente_nif"));
                clientes.put(c.getNif(), c);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return clientes;
    }

    public Map <String, Funcionario> getfuncionarios (int key){
        Map <String, Funcionario> funcionarios = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT funcionario_id\n" +
                     "FROM workshops_funcionarios\n" +
                     "WHERE workshop_id = '"+key+"'")){
            while (rs.next()){
                Funcionario f = FuncionariosDAO.getInstance().get(rs.getString("funcionario_id"));
                funcionarios.put(f.getUsername(), f);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return funcionarios;
    }

    public Workshop put(Integer key, Workshop w) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO workshops (id,nome,localidade,inicio,fim) VALUES (?,?,?,?,?)")){
                pstm.setInt(1,w.getId());
                pstm.setString(2, w.getNome());
                pstm.setString(3, w.getLocalidade());

                Horario horario = w.getHorario();

                // Convert LocalDateTime to java.sql.Timestamp
                pstm.setTimestamp(4, Timestamp.valueOf(horario.getInicio()));
                pstm.setTimestamp(5, Timestamp.valueOf(horario.getFim()));
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return w;
    }

    public void putAll(Map<? extends String, ? extends Workshop> w) {
        w.keySet().forEach(i -> this.put(i, w.get(i)));
    }

    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE workshops");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public Workshop remove(Object key) {
        Workshop w = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("DELETE FROM workshops WHERE id = ?")){
                w = this.get(key);
                pstm.setString(1,(String)key);
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return w;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Workshop> map) {

    }

    public Collection<Workshop> values() {
        Collection<Workshop> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id FROM workshops")) {
            while (rs.next()) {
                String idt = rs.getString("matricula");
                Workshop w = this.get(idt);
                res.add(w);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
