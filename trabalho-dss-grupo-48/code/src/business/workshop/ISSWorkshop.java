package business.workshop;

import business.clientes.Veiculo;

import java.util.Map;

public interface WoISSWorkshop {
    Map<Integer, Workshop> getWorkshops();
    void registacliente (int id, String nif, String nome, String morada, String contacto, Map<String, Veiculo> veiculos);
}
