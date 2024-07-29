package org.manager;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Management management = new Management();

        while (true) {
            System.out.println("Escolha a opcao desejada:");
            System.out.println("1 - Adicionar Manga");
            System.out.println("2 - Listar Mangas");
            System.out.println("3 - Atualizar Manga");
            System.out.println("4 - Excluir Manga");
            System.out.println("5 - Buscar Manga por Titulo");
            System.out.println("6 - Sair");
            System.out.print("Opcao: ");

            String opcao = sc.nextLine();

            switch (opcao) {
                case "1":
                    int isbn = lerInteiro(sc, "ISBN: ");
                    System.out.print("Escreva o Titulo: ");
                    String titulo = sc.nextLine();
                    int anoIni = lerInteiro(sc, "Escreva o Ano de Inicio: ");
                    int anoFim = lerInteiro(sc, "Escreva o Ano de Fim: ");
                    System.out.print("Escreva o Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Escreva o Genero: ");
                    String genero = sc.nextLine();
                    System.out.print("Escreva a Revista: ");
                    String revista = sc.nextLine();
                    System.out.print("Escreva a Editora: ");
                    String editora = sc.nextLine();
                    int anoEdi = lerInteiro(sc, "Escreva o Ano da Edicao: ");
                    int quantVolAdq = lerInteiro(sc, "Escreva a Quantidade de Volumes Adquiridos: ");

                    Count novoCount = new Count(isbn, titulo, anoIni, anoFim, autor, genero, revista, editora, anoEdi, quantVolAdq);
                    management.adicionar(novoCount);
                    break;

                case "2":
                    List<Count> listaMangases = management.listar();
                    for (Count count : listaMangases) {
                        System.out.println(count);
                    }
                    break;

                case "3":
                    int isbnAtualizar = lerInteiro(sc, "ISBN do Manga a ser atualizado: ");
                    Count countExistente = management.buscarPorISBN(isbnAtualizar);

                    if (countExistente != null) {
                        System.out.print("Novo Titulo: ");
                        String novoTitulo = sc.nextLine();
                        int novoAnoIni = lerInteiro(sc, "Novo Ano de Inicio: ");
                        int novoAnoFim = lerInteiro(sc, "Novo Ano de Fim: ");
                        System.out.print("Novo Autor: ");
                        String novoAutor = sc.nextLine();
                        System.out.print("Novo Genero: ");
                        String novoGenero = sc.nextLine();
                        System.out.print("Nova Revista: ");
                        String novaRevista = sc.nextLine();
                        System.out.print("Nova Editora: ");
                        String novaEditora = sc.nextLine();
                        int novoAnoEdi = lerInteiro(sc, "Novo Ano da Edicao: ");
                        int novaQuantVolAdq = lerInteiro(sc, "Nova Quantidade de Volumes Adquiridos: ");

                        Count countAtualizado = new Count(isbnAtualizar, novoTitulo, novoAnoIni, novoAnoFim, novoAutor, novoGenero, novaRevista, novaEditora, novoAnoEdi, novaQuantVolAdq);
                        management.alterarManga(isbnAtualizar, countAtualizado);
                    } else {
                        System.out.println("Manga com ISBN " + isbnAtualizar + " não encontrado.");
                    }
                    break;

                case "4":
                    int idExcluir = lerInteiro(sc, "ISBN do Manga a ser excluído: ");
                    System.out.print("Tem certeza que deseja excluir esse manga? (Y/N): ");
                    String result = sc.nextLine();
                    if (result.equalsIgnoreCase("Y")) {
                        management.excluir(idExcluir);
                    }
                    break;

                case "5":
                    System.out.print("Titulo do Manga: ");
                    String tituloBuscar = sc.nextLine();
                    Count count = management.buscarPorTitulo(tituloBuscar);
                    if (count != null) {
                        System.out.println("Manga encontrado: " + count);
                    } else {
                        System.out.println("Manga não encontrado.");
                    }
                    break;

                case "6":
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private static int lerInteiro(Scanner sc, String mensagem) {
        int numero = -1;
        boolean isValid = false;

        while (!isValid) {
            System.out.print(mensagem);
            String input = sc.nextLine();

            try {
                numero = Integer.parseInt(input);
                isValid = true;  // Se a conversão for bem-sucedida, saia do loop
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            }
        }

        return numero;
    }
}

