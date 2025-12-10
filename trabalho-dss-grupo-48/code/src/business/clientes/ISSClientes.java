package business.clientes;

import business.servicos.Servico;

import java.util.Map;

public interface ISSClientes {
    boolean existecliente (String username);
    void registacliente (String nif, String nome, String morada, String contacto, Map<String, Veiculo> veiculos);
    Map<String, Cliente> getClientes();
    void PedirServico(String nif, String matricula, Servico servico);<-----------------------------------------
}
