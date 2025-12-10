package ui;

import business.funcionarios.Funcionario;
import business.workshop.Workshop;

import java.util.*;


public class Menu{

    private int j;
    private Map<Integer, List<Handler>> handlers;

    private static Scanner is = new Scanner(System.in);
    public interface Handler{
        void execute();
    }

    public void setJ(int j) {
        this.j = j;
    }

    public Menu(){

        this.j = 1;
        this.handlers = new Map<Integer, List<Handler>>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public List<Handler> get(Object key) {
                return null;
            }

            @Override
            public List<Handler> put(Integer key, List<Handler> value) {
                return null;
            }

            @Override
            public List<Handler> remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends Integer, ? extends List<Handler>> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<Integer> keySet() {
                return null;
            }

            @Override
            public Collection<List<Handler>> values() {
                return null;
            }

            @Override
            public Set<Entry<Integer, List<Handler>>> entrySet() {
                return null;
            }
        };
    }
    public void setHandler(int j, int i, Handler h) {
        if (this.handlers.containsKey(j)){
            List<Handler> handler = this.handlers.get(j);
            handler.set(i, h);
            this.handlers.putIfAbsent (j, handler);
        }
        else {
            List<Handler> handler = new ArrayList<>();
            handler.set(i, h);
            this.handlers.putIfAbsent (j, handler);
        }
    }

    public void runW(String workshop) {
        System.out.println("\n *** BEM VINDO À OFICINA" + workshop.toUpperCase() + " *** ");
        int op = 1;
        do {
            switch (this.j) {
                case 1:
                    displayOptions();
                    break;
                case 2:
                    displayOptions2();
                    break;
                case 3:
                    displayOptions3();
                    break;
                case 4:
                    displayOptions4();
                    break;
                case 5:
                    displayOptions5();
                    break;
                case 6:
                    displayOptions6();
                    break;
            }
            op = readOption();
            if (op == -1) {
                System.out.println("Opção indisponível! Tente novamente.");
            }
            else if (op-1 < this.handlers.get(this.j).size() && op != 0){
                this.handlers.get(this.j).get(op-1).execute();
            }

        }while (this.j != 0);
    }
    private void displayOptions(){
        System.out.print("*********************     OPÇOES     *********************: ");
        System.out.println("1- LOGIN");
        System.out.println("0- SAIR");
    }
    private void displayOptions2(){
    }

    private void displayOptions3(){
        System.out.print("*********************     OPÇOES     *********************: ");
        System.out.println("1- INICIAR TURNO");
        System.out.println("0- SAIR");
    }
    private void displayOptions4(){
        System.out.print("*********************     OPÇOES     *********************: ");
        System.out.println("1- TERMINAR TURNO");
        System.out.println("2- REGISTAR SERVIÇO");
        System.out.println("3- INICIAR SERVIÇO");
        System.out.println("4- REGISTAR CLIENTE");
        System.out.println("5- ATUALIZAR CLIENTE");
        System.out.println("0- SAIR");
    }
    private void displayOptions5(){
        System.out.print("*********************     OPÇOES     *********************: ");
        System.out.println("1- TERMINAR SERVICO");
        System.out.println("0- SAIR");
    }
    private void displayOptions6(){
        System.out.print("*********************     OPÇOES     *********************: ");
        System.out.println("1- ATUALIZAR MORADA");
        System.out.println("2- ATUALIZAR CONTACTO");
        System.out.println("3- ADICIONAR VEICULO");
        System.out.println("0- SAIR");
    }
    /** Ler uma opção válida */
    private int readOption() {
        int op;

        System.out.print("Opção: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        if (op<0) {
            op = -1;
        }
        if (op == 0){
            MudaJ();
        }
        return op;
    }

    private void MudaJ(){
        if (this.j == 1){
            this.j = 0;
        }
        if (this.j == 2 || this.j == 3 || this.j == 4 || this.j == 5){
            this.j = 1;
        }
        if (this.j == 6){
            this.j = 4;
        }
    }


}
