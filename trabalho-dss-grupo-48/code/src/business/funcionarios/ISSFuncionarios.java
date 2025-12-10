package business.funcionarios;

import business.clientes.Veiculo;
import business.servicos.Servico;
import business.workshop.Horario;

import java.time.LocalDateTime;
import java.util.Map;

public interface ISSFuncionarios {
    boolean existefuncionario (String username);
    --->public void registafuncionario (String nome, int idade, String username, String password, boolean isAdmin, EmTurno emTurno, Map<Integer, Servico> servicos, Map<Horario, Posto_Trabalho> horario);
    boolean validafuncionariopassword (String username, String password);
    ---->boolean iniciarTurno (String username, Posto_Trabalho posto);
    void finalizarTurno (String username);

    void iniciarServico (Veiculo veiculo, Servico servico, String username);
    void finalizarServico (String username);
    void PedirServico (Veiculo veiculo, Servico servico);

}
