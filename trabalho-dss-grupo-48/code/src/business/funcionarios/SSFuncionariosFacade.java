package business.funcionarios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import business.clientes.Veiculo;
import business.servicos.Servico;
import business.workshop.Horario;
import data.FuncionariosDAO;

public class SSFuncionariosFacade implements ISSFuncionarios{
    private Map <String, Funcionario> funcionarios;

    private Map <Integer, Posto_Trabalho> postos;

    public SSFuncionariosFacade() {
        this.funcionarios = FuncionariosDAO.getInstance();
    }

    public boolean existefuncionario (String username) {
        return this.funcionarios.containsKey(username);
    }

    public void registafuncionario (String nome, int idade, String username, String password, boolean isAdmin, EmTurno emTurno, Map<Integer, Servico> servicos, Map<Horario, Posto_Trabalho> horario) {
        this.funcionarios.putIfAbsent(username, new Funcionario(nome, idade, username, password, isAdmin, emTurno, servicos, horario));
    }

    public boolean validafuncionariopassword (String username, String password) {
        Funcionario f = this.funcionarios.get(username);
        return f != null && f.getPassword().compareTo(password) == 0;
    }

    public boolean iniciarTurno (String username, Posto_Trabalho posto) {
        Funcionario f = this.funcionarios.get(username);
        boolean b = true;
        for (Map.Entry <Integer, Servico> entry : posto.getServicos().entrySet()){
            if (!f.getServicos().containsKey(entry.getKey())){
                b = false;
                break;
            }
        }
        if(b) {
            f.getEmTurno().setEmTurno(b);
            f.getEmTurno().setInicioH(LocalDateTime.now());
        }
        return b;
    }
    public void finalizarTurno (String username) {
        this.funcionarios.get(username).getEmTurno().setEmTurno(false);
        this.funcionarios.get(username).getEmTurno().setFimH(LocalDateTime.now());
        this.funcionarios.get(username).addHorario();
    }

    public void iniciarServico (Veiculo veiculo, Servico servico, String username){
        Funcionario f = this.funcionarios.get(username);
        f.getEmTurno().getFazerServico().setFazerServico(true);
        f.getEmTurno().getFazerServico().setVeiculo(veiculo);
        f.getEmTurno().getFazerServico().setServico(servico);
        veiculo.getFicha().setData_ultima_atualizacao(LocalDateTime.now());
        veiculo.getFicha().getServicosporfazer().remove(servico);
    }
    public void finalizarServico (String username){
        Funcionario f = this.funcionarios.get(username);
        f.getEmTurno().getPosto().getServicosPorFazer().get(this.funcionarios.get(username).getEmTurno().getFazerServico().getVeiculo()).remove(f.getEmTurno().getFazerServico().getServico());
        f.getEmTurno().getFazerServico().getVeiculo().getFicha().addServicos(LocalDateTime.now(), f.getEmTurno().getFazerServico().getServico(), f);
        f.getEmTurno().getFazerServico().setFazerServico(false);
    }
    public void PedirServico (Veiculo veiculo, Servico servico){
        Posto_Trabalho posto = null;
        int i = 0;
        int min = 0;
        if (servico.getId() == 1){
            this.postos.get(1).addServicos(veiculo,servico);
        }
        else {
            for (Map.Entry<Integer, Posto_Trabalho> entry : this.postos.entrySet()) {
                if (entry.getValue().getServicos().containsKey(servico.getId())) {
                    i = calcula_tempo(entry.getValue());
                    if (i < min || min == 0) {
                        posto = entry.getValue();
                        min = i;
                    }
                }
            }
            posto.addServicos(veiculo, servico);
        }
    }
    private int calcula_tempo (Posto_Trabalho posto){
        int i = 0;
        for (Map.Entry<Veiculo, List<Servico>> entry : posto.getServicosPorFazer().entrySet()){
            for(Servico s : entry.getValue()){
                i += s.getDuracao_media();
            }
        }
        return i;
    }
}
