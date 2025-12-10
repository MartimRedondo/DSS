package data;

import business.clientes.FichaVeiculo;
import business.clientes.Veiculo;
import business.funcionarios.EmTurno;
import business.funcionarios.FazerServico;
import business.funcionarios.Funcionario;
import business.funcionarios.Posto_Trabalho;
import business.servicos.Servico;
import business.workshop.Horario;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class FuncionariosDAO implements Map<String, Funcionario>{
    private static FuncionariosDAO singleton = null;

    private FuncionariosDAO(){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                    "nome VARCHAR(150) NOT NULL," +
                    "idade INT NOT NULL," +
                    "usename VARCHAR(50) NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "isAdmin INT NOT NULL," +
                    "emTurno INT NOT NULL," +
                    "posto INT," +
                    "inicio DATETIME," +
                    "fim DATETIME," +
                    "fazerServico INT NOT NULL," +
                    "carro VARCHAR(8)," +
                    "servico INT,"+
                    "FOREIGN KEY (carro) REFERENCES veiculos (matricula),"+
                    "FOREIGN KEY (servico) REFERENCES servicos (id),"+
                    "FOREIGN KEY (posto) REFERENCES postoTrabalho(id),"+
                    "PRIMARY KEY (usename));";

            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar a tabela Funcionarios: " + e.getMessage());
        }
    }

    public static FuncionariosDAO getInstance() {
        if (FuncionariosDAO.singleton == null)
            FuncionariosDAO.singleton = new FuncionariosDAO();
        return FuncionariosDAO.singleton;
    }

    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM funcionarios")) {
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
                     stm.executeQuery("SELECT username FROM funcionarios WHERE username='"+key.toString()+"'")) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    public boolean containsValue(Object value) {
        Funcionario t = (Funcionario) value;
        return this.containsKey(t.getUsername());
    }
    public Set<String> keySet() {
        Set<String> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT username FROM funcionarios")) {
            while (rs.next()) {
                String idu = rs.getString("username");
                res.add(idu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    public Set<Map.Entry<String, Funcionario>> entrySet() {
        return this.keySet().stream().map(k -> Map.entry(k, this.get(k))).collect(Collectors.toSet());
    }

    public Funcionario get(Object key) {
        Funcionario f = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM funcionarios WHERE username='"+key+"'")) {
            if (rs.next())
            {
                f = new Funcionario(rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("isAdmin") == 1 ? true : false,
                        new EmTurno(rs.getInt("emTurno") == 1 ? true : false, new Posto_Trabalho(rs.getInt("posto"), getservicos(rs.getInt("posto")), getveiculos(rs.getInt("posto"))),
                        new Horario(rs.getTimestamp("inicio").toLocalDateTime(),
                        rs.getTimestamp("fim").toLocalDateTime()),
                        new FazerServico (rs.getInt("fazerServico") == 1 ? true : false,getveiculo(rs.getString("carro")), getservico(rs.getInt("servico")))),
                        getservicos3(rs.getString("username")),
                        getpostos(rs.getString("username")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return f;
    }

    public Map <Horario, Posto_Trabalho> getpostos (String key){
        Map <Horario, Posto_Trabalho> postos = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT *\n" +
                     "FROM funcionario_postoTrabalho\n" +
                     "WHERE funcionario_username = '"+key+"'")){
            while (rs.next()){
                Horario h = new Horario(rs.getTimestamp("inicio").toLocalDateTime(),
                        rs.getTimestamp("fim").toLocalDateTime());
                Posto_Trabalho p = new Posto_Trabalho(rs.getInt("posto_id"), getservicos(rs.getInt("posto_id")), getveiculos(rs.getInt("posto_id")));
                postos.put(h, p);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return postos;
    }

    public Map <Veiculo, List<Servico>> getveiculos (int key){
        Map <Veiculo, List<Servico>> veiculos = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT veiculo_id\n" +
                     "FROM posto_veiculo\n" +
                     "WHERE posto_id = '"+key+"'")){
             while (rs.next()){
                 Veiculo v = getveiculo(rs.getString("veiculo_id"));
                 List<Servico> servicos = getservicos2(v.getMatricula());
                 veiculos.put(v, servicos);
             }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return veiculos;
    }

    public Veiculo getveiculo (String key){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT matricula\n" +
                     "FROM veiculos\n" +
                     "WHERE matricula = '"+key+"'")){
            if (rs.next()){
                return VeiculosDAO.getInstance().get(rs.getString("matrciula"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    public Map<Integer, Servico> getservicos3 (String key){
        Map<Integer, Servico> servicos = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT servico_id\n" +
                     "FROM funcionario_servico\n" +
                     "WHERE funcionario_username = '"+key+"'")) {
            while (rs.next()) {

                Servico s = getservico(rs.getInt("servico_id"));

                servicos.put(s.getId() ,s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return servicos;
    }

    public List<Servico> getservicos2 (String key){
        List<Servico> servicos = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT servico_id\n" +
                     "FROM veiculos_servicos\n" +
                     "WHERE veiculo_matricula = '"+key+"'")) {
            while (rs.next()) {
                servicos.add(getservico(rs.getInt("servico_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return servicos;
    }

    public Map <Integer, Servico> getservicos (int key){
        Map<Integer, Servico> servicos = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT servico_id\n" +
                     "FROM posto_servico\n" +
                     "WHERE posto_id = '"+key+"'")) {
            while (rs.next()) {

                Servico s = getservico(rs.getInt("servico_id"));

                servicos.put(s.getId() ,s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return servicos;
    }

    public Servico getservico (int key){
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT *\n" +
                     "FROM servicos\n" +
                     "WHERE id = '"+key+"';")){
            if (rs.next()) {
                Servico servico = new Servico(rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("duracao_media"),
                        rs.getFloat("preco"));
                return servico;
            }
        } catch (SQLException e) {
        e.printStackTrace();
        throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    public Funcionario put(String key, Funcionario f) {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("INSERT INTO funcionarios (nome,idade,username,password,isAdmin) VALUES (?,?,?,?,?)")){
                    pstm.setString(1,f.getNome());
                    pstm.setInt(2, f.getIdade());
                    pstm.setString(3, f.getUsername());
                    pstm.setString(4, f.getPassword());
                    pstm.setInt(5, f.isAdmin() ? 1 : 0);
                    pstm.setString(2,key);
                    pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return f;
    }

    public void putAll(Map<? extends String, ? extends Funcionario> f) {
        f.keySet().forEach(i -> this.put(i, f.get(i)));
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE funcionarios");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public Funcionario remove(Object key) {
        Funcionario f = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            try (PreparedStatement pstm = conn.prepareStatement("DELETE FROM funcionarios WHERE username = ?")){
                f = this.get(key);
                pstm.setString(1,(String)key);
                pstm.executeUpdate();
            }
        }catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return f;
    }

    public Collection<Funcionario> values() {
        Collection<Funcionario> res = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT username FROM funcionarios")) {
            while (rs.next()) {
                String idt = rs.getString("username");
                Funcionario f = this.get(idt);
                res.add(f);
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }
}
