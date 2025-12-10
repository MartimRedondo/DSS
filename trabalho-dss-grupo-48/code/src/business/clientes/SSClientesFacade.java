package business.clientes;

import business.servicos.Servico;
import data.ClientesDAO;

import data.FuncionariosDAO;

import java.util.Map;
import java.util.Objects;

public class SSClientesFacade implements ISSClientes{
    private Map<String, Cliente> cliente

    public SSClientesFacade() {
        this.clientes = ClientesDAO.getInstance();
    }

    public Map<String, Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Map<String, Cliente> clientes) {
        this.clientes = clientes;
    }


    public boolean existecliente (String nif){
        return this.clientes.containsKey(nif);
    }

    public void registacliente(String nif, String nome, String morada, String contacto, Map<String, Veiculo> veiculos) {
        clientes.putIfAbsent(nif,new Cliente(nif, nome, morada, contacto, veiculos));
    }
    public void PedirServico(String nif, String matricula, Servico servico){
        Cliente c = clientes.get(nif);
        c.getVeiculos().get(matricula).getFicha().addServicosPorFazer(servico);
    }
}
