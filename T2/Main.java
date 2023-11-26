import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

class Main{

    static class Nodo{

        public String programa;
        public boolean ocupado;
        public int tamanho;

        public Nodo(String programa, boolean ocupado, int tamanho){
            this.programa = programa;
            this.ocupado = ocupado;
            this.tamanho = tamanho;
        }
        
        public String toString(){
            return "|"+tamanho+"|";
        }
    }
    
    public static int memoriaSize = 32;
    public static LinkedList<Nodo> memorias = new LinkedList<Nodo>();

    public static void main(String[] args){
        // criaMemoria(tamanhoMemoria, tamanhoPaginaEMoldura);
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

        System.out.println("Qual política de alocação deseja?\n1 - Best-Fit\n2 - Worst-Fit\n3 - Fisrt-Fit\n4 - Circular-Fit");
        int tipo = sc.nextInt();
        // cria primeiro nodo com memoria total
        memorias.addFirst(new Nodo("M", false, memoriaSize));
        //EXECUCAO DE CADA INSTRUCAO NO HASHMAP
        for(int i = 0; i < instrucoes.size(); i++){
            executaAcao(instrucoes.get(i), tipo);
        }

        sc.close();
    }

    public static void executaAcao(String instrucao, int tipo) {

        String[] acao = instrucao.split("\\(");
        String stringMemorias = "";
        int melhorTamanhoNodo = 0;
        boolean semMemoria = false;
        switch (acao[0]) {
            case "in":
                String[] processoIn = acao[1].replace(")","").split(", ");
                switch (tipo){
                    //best-fit
                    case 1:
                        int distanciaTamanhoProcessoTamanhoMemoria = 1000;
                        Nodo memoriaEscolhida = null;
                        semMemoria = true;
                        int tamanhoProcesso = Integer.parseInt(processoIn[1]);
                        for(Nodo memoria : memorias){
                            //caso não ocupado, tamanho da memoria maior do que o do processo e seja o menor numero, seta como novo candidato a guardar memoria
                            if(!memoria.ocupado && memoria.tamanho > tamanhoProcesso && memoria.tamanho - tamanhoProcesso < distanciaTamanhoProcessoTamanhoMemoria){ 
                                                   //n deveria ser >=?
                                semMemoria = false;
                                melhorTamanhoNodo = memoria.tamanho;
                                distanciaTamanhoProcessoTamanhoMemoria = memoria.tamanho - tamanhoProcesso;
                                memoriaEscolhida = memoria;
                            }
                        }
                        //adiciona nodo antes da memoriaEscolhida, diminui tamanho do nodo escolhido e transforma nodos desocupados adjacentes em um so
                        if(!semMemoria){
                            memoriaEscolhida.tamanho = distanciaTamanhoProcessoTamanhoMemoria;
                            memorias.add(memorias.indexOf(memoriaEscolhida),new Nodo(processoIn[0],true,tamanhoProcesso));
                            concatenaEspacos();
                        }
                        break;
                    case 2:
                        break; //ADICIONAR ALOCACAO Worst-Fit
                    case 3:
                    //alteracao
                         Nodo memoriaEscolhidaFf = null;
                        semMemoria = true;
                        int tamanhoProcessoFf = Integer.parseInt(processoIn[1]);
                        int distanciaTamanhoProcessoTamanhoMemoriaFf = 0;
                        for(Nodo memoria :memorias){
                            if(!memoria.ocupado && memoria.tamanho >= tamanhoProcessoFf){
                                semMemoria = false;
                                melhorTamanhoNodo = memoria.tamanho;
                                distanciaTamanhoProcessoTamanhoMemoriaFf = memoria.tamanho - tamanhoProcessoFf;
                                memoriaEscolhidaFf = memoria;
                            }
                        }
                        if(!semMemoria){
                            memoriaEscolhidaFf.tamanho = distanciaTamanhoProcessoTamanhoMemoriaFf;
                            memorias.add(memorias.indexOf(memoriaEscolhidaFf),new Nodo(processoIn[0],true,tamanhoProcessoFf));
                            concatenaEspacos();
                        }
                        break; //ADICIONAR ALOCACAO First-Fit
                    case 4:
                        break; //ADICIONAR ALOCACAO Circular-Fit
                }
                break;
            case "out":
                String processoOut = acao[1].replace(")","");
                switch (tipo) {
                    case 1:
                        for(Nodo memoria : memorias){
                            //retira processo e seta como desocupado o nodo para poder se juntar a memoria desocupada
                            if(memoria.programa.equals(processoOut)){
                                memoria.programa = "M";
                                memoria.ocupado = false;
                            }
                        }
                        concatenaEspacos();
                        break;
                    case 2:
                        break; //ADICIONAR ALOCACAO Worst-Fit
                    case 3:
                    //alteracao
                    for(Nodo memoria : memorias){
                            //retira processo e seta como desocupado o nodo para poder se juntar a memoria desocupada
                            if(memoria.programa.equals(processoOut)){
                                memoria.programa = "M";
                                memoria.ocupado = false;
                            }
                        }
                     concatenaEspacos();
                        break; //ADICIONAR ALOCACAO First-Fit
                    case 4:
                        break; //ADICIONAR ALOCACAO Circular-Fit
                };
                break;
        }

        for(Nodo memoria : memorias){
            if(!memoria.ocupado)
                stringMemorias+=memoria.toString()+ " ";
        }
        // lista de nodos desocupados conforme mostrado nos exemplos do sor do T2
        System.out.println(stringMemorias);
        
        if(semMemoria){
            System.out.println("ESPAÇO INSUFICIENTE DE MEMÓRIA");
        }
    }

    public static void concatenaEspacos(){
        for (int i = memorias.size() - 2; i >= 0; i--) {
        //for (int i = 0; i < memorias.size() - 1; i++) { alteracao
            Nodo aux1 = memorias.get(i);
            Nodo aux2 = memorias.get(i+1);

            if(!(aux1.ocupado) && !(aux2.ocupado)){
                aux1.tamanho += aux2.tamanho;
                memorias.remove(i+1);
            }
        }
    }
}