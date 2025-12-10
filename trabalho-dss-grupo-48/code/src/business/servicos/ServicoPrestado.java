package business.servicos;

import business.funcionarios.Funcionario;
import business.workshop.Horario;

import java.time.LocalDateTime;

public class ServicoPrestado {
    private Funcionario funcionario;
    private Servico servico;
    private Horario horario;

    public ServicoPrestado(Funcionario funcionario, Servico servico, Horario horario) {
        this.funcionario = funcionario;
        this.servico = servico;
        this.horario = horario;
    }
}
