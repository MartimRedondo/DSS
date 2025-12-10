package business.workshop;

import business.clientes.Cliente;
import business.clientes.Veiculo;
import data.WorkshopsDAO;

import java.util.Map;
import java.util.Objects;

public class SSWorkshopFacade implements ISSWorkshop{
    private Map<Integer, Workshop> workshops;

    public SSWorkshopFacade() {
        this.workshops = WorkshopsDAO.getInstance();
    }

    public Map<Integer, Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(Map<Integer, Workshop> workshops) {
        this.workshops = workshops;
    }

    public void registacliente (int id, String nif, String nome, String morada, String contacto, Map<String, Veiculo> veiculos){
        this.workshops.get(id).registacliente(nif,  new Cliente(nif, nome, morada, contacto, veiculos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SSWorkshopFacade that)) return false;
        return Objects.equals(getWorkshops(), that.getWorkshops());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkshops());
    }

}
