import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

class Main {

    static class Nodo {

        public String programa;
        public boolean ocupado;
        public int tamanho;

        public Nodo(String programa, boolean ocupado, int tamanho) {
            this.programa = programa;
            this.ocupado = ocupado;
            this.tamanho = tamanho;
        }

        public String toString() {
            return "|" + tamanho + "|";
        }
    }

    public static int memoriaSize = 64;
    public static LinkedList<Nodo> memorias = new LinkedList<Nodo>();

    public static void main(String[] args) {
        // criaMemoria(tamanhoMemoria, tamanhoPaginaEMoldura);
        HashMap<Integer, String> instrucoes = new HashMap<Integer, String>(); // codigo do programa
        Scanner sc = new Scanner(System.in);

        System.out.println("Qual arquivo deseja executar?");
        String path = sc.nextLine();

        File file = new File(path);
        FileReader fr;
        int chave = 0;

        // LEITURA DO ARQUIVO ARMAZENANDO NO HASHMAP
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while ((line = br.readLine()) != null && (line != "")) {
                if (line.contains("IN") || line.contains("OUT")) {
                    instrucoes.put(chave++, line.toLowerCase().trim());
                }
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(
                "Qual política de alocação deseja?\n1 - Best-Fit\n2 - Worst-Fit\n3 - First-Fit\n4 - Circular-Fit");
        int tipo = sc.nextInt();
        // cria primeiro nodo com memoria total
        memorias.addFirst(new Nodo("M", false, memoriaSize));
        int indexUltimoAlocado = 0;
        // EXECUCAO DE CADA INSTRUCAO NO HASHMAP
        for (int i = 0; i < instrucoes.size(); i++) {
            if (tipo == 4)
                indexUltimoAlocado = executaAcao(instrucoes.get(i), tipo, indexUltimoAlocado);
            else
                executaAcao(instrucoes.get(i), tipo, 0);

        }

        sc.close();
    }

    public static int executaAcao(String instrucao, int tipo, int indexUltimoAlocado) {

        String[] acao = instrucao.split("\\(");
        String stringMemorias = "";
        boolean semMemoria = false;
        int indexTipo4 = 0;
        switch (acao[0]) {
            case "in":
                String[] processoIn = acao[1].replace(")", "").split(", ");
                switch (tipo) {
                    // best-fit
                    case 1:
                        int distanciaTamanhoProcessoTamanhoMemoriaBf = 1000;
                        Nodo memoriaEscolhidaBf = null;
                        semMemoria = true;
                        int tamanhoProcessoBf = Integer.parseInt(processoIn[1]);
                        for (Nodo memoria : memorias) {
                            // caso não ocupado, tamanho da memoria maior do que o do processo e seja o
                            // menor numero, seta como novo candidato a guardar memoria
                            if (!memoria.ocupado && memoria.tamanho >= tamanhoProcessoBf
                                    && memoria.tamanho - tamanhoProcessoBf < distanciaTamanhoProcessoTamanhoMemoriaBf) {
                                // n deveria ser >=?
                                semMemoria = false;
                                distanciaTamanhoProcessoTamanhoMemoriaBf = memoria.tamanho - tamanhoProcessoBf;
                                memoriaEscolhidaBf = memoria;
                            }
                        }
                        // adiciona nodo antes da memoriaEscolhida, diminui tamanho do nodo escolhido e
                        // transforma nodos desocupados adjacentes em um so
                        if (!semMemoria) {
                            memoriaEscolhidaBf.tamanho = distanciaTamanhoProcessoTamanhoMemoriaBf;
                            memorias.add(memorias.indexOf(memoriaEscolhidaBf),
                                    new Nodo(processoIn[0], true, tamanhoProcessoBf));
                            concatenaEspacos();
                        }
                        break;
                    case 2:
                        // alteracao
                        Nodo memoriaEscolhidaWf = null;
                        semMemoria = true;
                        int tamanhoProcessoWf = Integer.parseInt(processoIn[1]);
                        int distanciaTamanhoProcessoTamanhoMemoriaWf = 0;
                        for (Nodo memoria : memorias) {
                            if (!memoria.ocupado && memoria.tamanho >= tamanhoProcessoWf
                                    && memoria.tamanho - tamanhoProcessoWf > distanciaTamanhoProcessoTamanhoMemoriaWf) {
                                semMemoria = false;
                                distanciaTamanhoProcessoTamanhoMemoriaWf = memoria.tamanho - tamanhoProcessoWf;
                                memoriaEscolhidaWf = memoria;
                            }
                        }
                        if (!semMemoria) {
                            memoriaEscolhidaWf.tamanho = distanciaTamanhoProcessoTamanhoMemoriaWf;
                            memorias.add(memorias.indexOf(memoriaEscolhidaWf),
                                    new Nodo(processoIn[0], true, tamanhoProcessoWf));
                            concatenaEspacos();
                        }
                        break; // ADICIONAR ALOCACAO Worst-Fit
                    case 3:
                        // alteracao
                        Nodo memoriaEscolhidaFf = null;
                        semMemoria = true;
                        boolean achouFirstFf = false;
                        int tamanhoProcessoFf = Integer.parseInt(processoIn[1]);
                        int distanciaTamanhoProcessoTamanhoMemoriaFf = 0;
                        for (Nodo memoria : memorias) {
                            if (!memoria.ocupado && memoria.tamanho >= tamanhoProcessoFf && !achouFirstFf) {
                                semMemoria = false;
                                achouFirstFf = true;
                                distanciaTamanhoProcessoTamanhoMemoriaFf = memoria.tamanho - tamanhoProcessoFf;
                                memoriaEscolhidaFf = memoria;
                            }
                        }
                        if (!semMemoria) {
                            memoriaEscolhidaFf.tamanho = distanciaTamanhoProcessoTamanhoMemoriaFf;
                            memorias.add(memorias.indexOf(memoriaEscolhidaFf),
                                    new Nodo(processoIn[0], true, tamanhoProcessoFf));
                            concatenaEspacos();
                        }
                        break; // ADICIONAR ALOCACAO First-Fit
                    case 4:
                        Nodo memoriaEscolhidaCf = null;
                        semMemoria = true;
                        boolean achouFirstCf = false;
                        int tamanhoProcessoCf = Integer.parseInt(processoIn[1]);
                        int distanciaTamanhoProcessoTamanhoMemoriaCf = 0;
                        for (int i = indexUltimoAlocado; i < memorias.size(); i++) {
                            if (!memorias.get(i).ocupado && memorias.get(i).tamanho >= tamanhoProcessoCf
                                    && !achouFirstCf) {
                                semMemoria = false;
                                achouFirstCf = true;
                                distanciaTamanhoProcessoTamanhoMemoriaCf = memorias.get(i).tamanho - tamanhoProcessoCf;
                                memoriaEscolhidaCf = memorias.get(i);
                            }
                        }
                        for (int i = 0; i < indexUltimoAlocado; i++) {
                            if (!memorias.get(i).ocupado && memorias.get(i).tamanho >= tamanhoProcessoCf
                                    && !achouFirstCf) {
                                semMemoria = false;
                                achouFirstCf = true;
                                distanciaTamanhoProcessoTamanhoMemoriaCf = memorias.get(i).tamanho - tamanhoProcessoCf;
                                memoriaEscolhidaCf = memorias.get(i);
                            }
                        }
                        if (!semMemoria) {
                            memoriaEscolhidaCf.tamanho = distanciaTamanhoProcessoTamanhoMemoriaCf;
                            memorias.add(memorias.indexOf(memoriaEscolhidaCf),
                                    new Nodo(processoIn[0], true, tamanhoProcessoCf));
                            indexTipo4 = memorias.lastIndexOf(memoriaEscolhidaCf);
                            concatenaEspacos();
                        }
                        break; // ADICIONAR ALOCACAO Circular-Fit
                }
                break;
            case "out":
                String processoOut = acao[1].replace(")", "");
                for (Nodo memoria : memorias) {
                    // retira processo e seta como desocupado o nodo para poder se juntar a memoria
                    // desocupada
                    if (memoria.programa.equals(processoOut)) {
                        memoria.programa = "M";
                        memoria.ocupado = false;
                    }
                }
                concatenaEspacos();
                break;
        }

        for (Nodo memoria : memorias) {
            if (!memoria.ocupado)
                stringMemorias += memoria.toString() + " ";
        }
        // lista de nodos desocupados conforme mostrado nos exemplos do sor do T2
        System.out.println(stringMemorias);

        if (semMemoria) {
            System.out.println("ESPAÇO INSUFICIENTE DE MEMÓRIA");
        }
        return indexTipo4 + 1;
    }

    public static void concatenaEspacos() {
        for (int i = memorias.size() - 2; i >= 0; i--) {
            // for (int i = 0; i < memorias.size() - 1; i++) { alteracao
            Nodo aux1 = memorias.get(i);
            Nodo aux2 = memorias.get(i + 1);

            if (!(aux1.ocupado) && !(aux2.ocupado)) {
                aux1.tamanho += aux2.tamanho;
                memorias.remove(i + 1);
            }
        }
    }
}