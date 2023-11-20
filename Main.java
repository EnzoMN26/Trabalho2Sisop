import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

class Main{

    class Nodo{
        public String programa;
        public boolean ocupado;
        public int tamanho;
        public Nodo(String programa, boolean ocupado, int tamanho){
            this.programa = programa;
            this.ocupado = ocupado;
            this.tamanho = tamanho;
        }
    }

    public static LinkedList<Nodo> memoria = new LinkedList<Nodo>();

    public static void main(String[] args){
        HashMap<Integer, String> instrucoes = new HashMap<Integer, String>(); //codigo do programa
        Scanner sc = new Scanner(System.in);

        System.out.println("Qual arquivo deseja executar?");
        String path = sc.nextLine();

        File file = new File(path);
        FileReader fr;
        int chave = 0;

        //LEITURA DO ARQUIVO ARMAZENANDO NO HASHMAP
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while((line = br.readLine()) != null && (line != "")){
                if(line.contains("IN") || line.contains("OUT")){
                    instrucoes.put(chave++, line.toLowerCase().trim());
                }
            }
        
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //EXECUCAO DE CADA INSTRUCAO NO HASHMAP
        for(int i = 0; i < instrucoes.size(); i++){
            System.out.println("Qual política de alocação deseja?\n1 - Best-Fit\n2 - Worst-Fit\n3 - Fisrt-Fit\n4 - Circular-Fit");
            int tipo = sc.nextInt();
            executaAcao(instrucoes.get(i), tipo);
        }

        sc.close();
    }

    public static void executaAcao(String instrucao, int tipo) {

        String[] acao = instrucao.split("(");

        switch (acao[0]) {
            case "in":
                switch (tipo){
                    case 1:
                        break; //ADICIONAR ALOCACAO Best-Fit
                    case 2:
                        break; //ADICIONAR ALOCACAO Worst-Fit
                    case 3:
                        break; //ADICIONAR ALOCACAO First-Fit
                    case 4:
                        break; //ADICIONAR ALOCACAO Circular-Fit
                }
            case "out":
                break;
        }
    }

    public static void concatenaEspacos(){
        for (int i = 0; i < memoria.size() - 1; i++) {
            Nodo aux1 = memoria.get(i);
            Nodo aux2 = memoria.get(i+1);

            if(!(aux1.ocupado) && !(aux2.ocupado)){
                aux1.tamanho += aux2.tamanho;
                memoria.remove(i+1);
            }
        }
    }
}