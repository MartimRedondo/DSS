package ui;

import java.time.format.DateTimeFormatter;

import business.clientes.*;
import business.funcionarios.Funcionario;
import business.funcionarios.ISSFuncionarios;
import business.funcionarios.SSFuncionariosFacade;
import business.servicos.ISSServicos;
import business.servicos.SSServicosFacade;
import business.servicos.Servico;
import business.workshop.ISSWorkshop;
import business.workshop.SSWorkshopFacade;
import business.workshop.Workshop;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ESIdealUI{

    private Menu menu;

    private Scanner is;

    private ISSClientes clientes;
    private ISSFuncionarios funcionarios;
    private ISSServicos servicos;
    private ISSWorkshop workshops;

    private Workshop workshop;

    private Funcionario funcionario;

    public ESIdealUI(){
        this.menu = new Menu();
        this.menu.setHandler(1, 1, () -> LogIn());
        this.menu.setHandler(3, 1, () -> IniciarTurno());
        this.menu.setHandler(4, 1, () -> TerminarTurno());
        this.menu.setHandler(4, 2, () -> PedirServico());
        this.menu.setHandler(4, 3, () -> IniciarServico());
        this.menu.setHandler(4, 4, () -> RegistarCliente());
        this.menu.setHandler(4, 5, () -> AtualizarCleinte());
        this.menu.setHandler(5, 1, () -> TerminarServico());
        this.menu.setHandler(6, 1, () -> AtualizarMorada());
        this.menu.setHandler(6, 2, () -> AtualizarContacto());
        this.menu.setHandler(6, 3, () -> AtualizarVeiculos());
        this.is = new Scanner(System.in);
        this.clientes = new SSClientesFacade();
        this.funcionarios = new SSFuncionariosFacade();
        this.servicos = new SSServicosFacade();
        this.workshops = new SSWorkshopFacade();
    }

    private void LogIn() {
        Funcionario funcionario = null;
        Boolean b = false;
        while (!b) {
            System.out.println("\n *** Insira o seu username *** ");
            String username = is.nextLine();
            System.out.println("***************************************************************** ");
            System.out.println("\n *** Insira a sua password ***");
            String password = is.nextLine();
            if (workshop.getFuncionarios().containsKey(username)) {
                b = funcionarios.validafuncionariopassword(username, password);
            }
            if (b = false) {
                System.out.println("username ou password incorreta, tente novamente!");
            } else {
                funcionario = workshop.getFuncionarios().get(username);
            }
        }
        this.funcionario = funcionario;
        if (this.funcionario.getIsAdmin()){
            this.menu.setJ(2);
        }
        if (!this.funcionario.getEmTurno().isEmTurno()){
            this.menu.setJ(3);
        }
        if (this.funcionario.getEmTurno().isEmTurno()) {
            this.menu.setJ(4);
        }
    }

    private void IniciarTurno (){
        System.out.println("\n *** Insira o numero do posto de trabalho: *** ");
        int posto = is.nextInt();
        this.funcionarios.iniciarTurno(this.funcionario.getUsername(), workshop.getPosto().get(posto));
        this.menu.setJ(4);
    }

    private void TerminarTurno (){
        this.funcionarios.finalizarTurno(this.funcionario.getUsername());
        this.menu.setJ(3);
    }

    private void IniciarServico (){
        System.out.println("***** Selecione um serviço pra realizar *****");
        Map <String, Veiculo> veiculos = new HashMap<>();
        for(Map.Entry<Veiculo, List< Servico >> entry : funcionario.getEmTurno().getPosto().getServicosPorFazer().entrySet()){
            veiculos.put(entry.getKey().getMatricula(), entry.getKey());
            System.out.println("\n" + entry.getKey().getMarca() + " " + entry.getKey().getModelo() + " " + entry.getKey().getMatricula());
            for (Servico servico : entry.getValue()){
                System.out.println("\n" + servico.getId() + "-" + servico.getNome());
            }
        }
        System.out.println("*** Digite a matricula do veiculo ***");
        String matricula = is.nextLine();
        System.out.println("*** Digite o ID do serviço ***");
        int servico = is.nextInt();
        this.funcionarios.iniciarServico(veiculos.get(matricula), servicos.getServicos().get(servico), funcionario.getUsername());

    }
    private void TerminarServico(){
        this.funcionarios.finalizarServico(funcionario.getUsername());
    }

    private void PedirServico(){
        System.out.println("*** Digite o nif do cliente ***");
        String nif = is.nextLine();
        Cliente c = clientes.getClientes().get(nif);
        for(Map.Entry<String, Veiculo> entry : c.getVeiculos().entrySet()){
            System.out.println("\n" + entry.getValue().getMarca() + " " + entry.getValue().getModelo() + " " + entry.getValue().getMatricula());
        }
        System.out.println("*** Digite a matricula do veiculo ***");
        String matricula = is.nextLine();
        for (Map.Entry<Integer, Servico> entry : servicos.getServicos().entrySet()) {
            System.out.println(entry.getKey() + "- " + entry.getValue().getNome());
        }
        System.out.println("*** Digite o ID do servico ***");
        int id = is.nextInt();
        clientes.PedirServico(nif, matricula, servicos.getServicos().get(id));
        funcionarios.PedirServico(clientes.getClientes().get(nif).getVeiculos().get(matricula), servicos.getServicos().get(id));
    }

    public void AtualizarCleinte (){
        this.menu.setJ(6);
    }

    public void AtualizarMorada (){
        System.out.println("\n Insira o nif do cliente ");
        String nif = is.nextLine();
        System.out.println("\n Insira a nova morada do cliente ");
        String morada = is.nextLine();
        clientes.getClientes().get(nif).setMorada(morada);
    }
    public void AtualizarContacto (){
        System.out.println("\n Insira o nif do cliente ");
        String nif = is.nextLine();
        System.out.println("\n Insira o novo contacto do cliente ");
        String contacto = is.nextLine();
        clientes.getClientes().get(nif).setContacto(contacto);
    }
    public void AtualizarVeiculos (){
        System.out.println("\n Insira o nif do cliente ");
        String nif = is.nextLine();
        System.out.println("\n ******   REGISTAR NOVO VEICULO   ****** ");
        System.out.println("\n*****   Tipo de Combustivel    *****\n" +
                "     *  1- gasolina\n" +
                "     *  2- diesel\n" +
                "     *  3- eletrico\n" +
                "     *  4- hibrido gasolina\n" +
                "     *  5- hibrido diesel");
        int type = is.nextInt();
        System.out.println("\n Insira a matricula do veiculo ");
        String matricula = is.nextLine();
        System.out.println("\n insira a marca do veiculo ");
        String marca = is.nextLine();
        System.out.println("\n Insira o modelo do veiculo ");
        String modelo = is.nextLine();
        System.out.println("\n Insira a quilometragem do veiculo ");
        int quilometragem = is.nextInt();
        System.out.println("\n Insira o ano de fabrico do veiculo ");
        int ano = is.nextInt();
        System.out.println("\n Insira a data da ultima revisao do veiculo (formato yyyy-MM-ddTHH:mm:ss) ");
        String input = is.nextLine();
        LocalDateTime data_ultima_revisao = LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Veiculo veiculo = new Veiculo(new FichaVeiculo(), type, matricula, marca, modelo, quilometragem, ano, data_ultima_revisao);
        clientes.getClientes().get(nif).addVeiculo(matricula, veiculo);
    }
    public void RegistarCliente (){
        System.out.println("\n ******   REGISTAR CLIENTE   ****** ");
        System.out.println("\n ******   Escolha uma Estaçao de Serviço    ***** ");
        for(Map.Entry<Integer, Workshop> entry : workshops.getWorkshops().entrySet()){
            System.out.println(entry.getKey() + "- " + entry.getValue());
        }
        int n = is.nextInt();
        System.out.println("\n Insira o primeiro e ultimo nome do cliente ");
        String nome = is.nextLine();
        System.out.println("\n Insira o nif do cliente ");
        String nif = is.nextLine();
        System.out.println("\n Insira a morada do cliente ");
        String morada = is.nextLine();
        System.out.println("\n Insira o contacto do cliente ");
        String contacto = is.nextLine();
        System.out.println("\n Insira o numero de veiculos que ficarão associados ao cliente ");
        int num = is.nextInt();
        Map<String, Veiculo> veiculos = new HashMap<>();
        while (num > 0) {
            System.out.println("\n ******   REGISTAR NOVO VEICULO   ****** ");
            System.out.println("\n*****   Tipo de Combustivel    *****\n" +
                    "     *  1- gasolina\n" +
                    "     *  2- diesel\n" +
                    "     *  3- eletrico\n" +
                    "     *  4- hibrido gasolina\n" +
                    "     *  5- hibrido diesel");
            int type = is.nextInt();
            System.out.println("\n Insira a matricula do veiculo ");
            String matricula = is.nextLine();
            System.out.println("\n insira a marca do veiculo ");
            String marca = is.nextLine();
            System.out.println("\n Insira o modelo do veiculo ");
            String modelo = is.nextLine();
            System.out.println("\n Insira a quilometragem do veiculo ");
            int quilometragem = is.nextInt();
            System.out.println("\n Insira o ano de fabrico do veiculo ");
            int ano = is.nextInt();
            System.out.println("\n Insira a data da ultima revisao do veiculo (formato yyyy-MM-ddTHH:mm:ss) ");
            String input = is.nextLine();
            LocalDateTime data_ultima_revisao = LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Veiculo veiculo = new Veiculo(new FichaVeiculo(), type, matricula, marca, modelo, quilometragem, ano, data_ultima_revisao);
            veiculos.put(matricula, veiculo);
        }
        clientes.registacliente(nif, nome, morada, contacto, veiculos);
        workshops.registacliente(n, nif, nome, morada, contacto, veiculos);
        if (workshops.getWorkshops().get(n).getLocalidade().equals("Gualtar")){
            EmiteVoucher();
        }
    }

    public void run() {
        System.out.println("\n ******   BEM VINDO À ESIDEAL   ****** ");
        System.out.println("\n Insira o id correspondente á oficina: ");
        int id = is.nextInt();
        this.workshop = workshops.getWorkshops().get(id);
        this.menu.runW(this.workshop.getNome());
        System.out.println("Até breve!...");
    }
    
}