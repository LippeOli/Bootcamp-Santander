package org.manager;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Management {
    private static final String FILE_NAME = getFilePath("mangas.txt");
    private static final String INDEX_PRIMARIO = getFilePath("indicePrimario.txt");
    private static final String INDEX_SECUNDARIO = getFilePath("indiceSecundario.txt");

    private static final TreeMap<Integer, Long> primaryIndex = new TreeMap<>();
    private static final TreeMap<String, Integer> secondaryIndex = new TreeMap<>();

    private static String getFilePath(String fileName) {
        URL resource = Management.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            String resourcePath = "src/resources/" + fileName;
            try {
                File file = new File(resourcePath);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resourcePath;
        } else {
            return Paths.get(resource.getPath()).toString();
        }
    }


    public Management() {
        carregarIndices();
    }

    private void carregarIndices() {
        try (BufferedReader indexPrimReader = new BufferedReader(new FileReader(INDEX_PRIMARIO));
             BufferedReader indexSecReader = new BufferedReader(new FileReader(INDEX_SECUNDARIO))) {

            String linha;
            while ((linha = indexPrimReader.readLine()) != null) {
                if (!linha.trim().isEmpty()) { // Verifica se a linha não está vazia
                    String[] partes = linha.split(",");
                    if (partes.length == 2) { // Verifica se há duas partes após separar por ","
                        int isbn = Integer.parseInt(partes[0]);
                        long posicao = Long.parseLong(partes[1]);
                        primaryIndex.put(isbn, posicao);
                    } else {
                        System.out.println("Formato inválido na linha do índice primário: " + linha);
                    }
                }
            }

            while ((linha = indexSecReader.readLine()) != null) {
                if (!linha.trim().isEmpty()) { // Verifica se a linha não está vazia
                    String[] partes = linha.split(",");
                    if (partes.length == 2) { // Verifica se há duas partes após separar por ","
                        String titulo = partes[0];
                        int isbn = Integer.parseInt(partes[1]);
                        secondaryIndex.put(titulo, isbn);
                    } else {
                        System.out.println("Formato inválido na linha do índice secundário: " + linha);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Erro ao converter número na leitura dos índices.");
        }
    }

    public Count buscarPorTitulo(String titulo) {
        Integer isbn = secondaryIndex.get(titulo);
        if (isbn != null) {
            Long rrn = primaryIndex.get(isbn);
            if (rrn != null) {
                return buscarPorRRN(rrn);
            }
        }
        return null;
    }

    public Count buscarPorISBN(int isbn) {
        Long rrn = primaryIndex.get(isbn);
        if (rrn != null) {
            return buscarPorRRN(rrn);
        }
        return null;
    }

    private Count buscarPorRRN(long rrn) {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r")) {
            raf.seek(rrn);
            String linha = raf.readLine();
            if (linha != null) {
                return Count.fromString(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void adicionar (Count count) {
        if (buscarPorISBN(count.getIsbn()) != null) {
            System.out.println("ISBN já existe. Mangá não adicionado.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
             BufferedWriter indexPrimWriter = new BufferedWriter(new FileWriter(INDEX_PRIMARIO, true));
             BufferedWriter indexSecWriter = new BufferedWriter(new FileWriter(INDEX_SECUNDARIO, true))) {

            long offset = new File(FILE_NAME).length();
            long rrn = offset / 100;

            writer.write(count.toString());
            writer.newLine();

            indexPrimWriter.write(count.getIsbn() + "," + rrn);
            indexPrimWriter.newLine();

            indexSecWriter.write(count.getTitulo() + "," + count.getIsbn());
            indexSecWriter.newLine();

            primaryIndex.put(count.getIsbn(), rrn);
            secondaryIndex.put(count.getTitulo(), count.getIsbn());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Mangá adicionado com sucesso!");
    }


    public void alterarManga(int isbnAtualizar, Count countAtualizado) {
        Count countExistente = buscarPorISBN(isbnAtualizar);
        if (countExistente == null) {
            System.out.println("Mangá não encontrado para o ISBN fornecido.");
            return;
        }

        long rrn = primaryIndex.get(isbnAtualizar);

        try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw")) {
            raf.seek(rrn);
            raf.writeBytes(countAtualizado.toString());

            // Atualiza o índice secundário se o título foi alterado
            if (!countExistente.getTitulo().equals(countAtualizado.getTitulo())) {
                secondaryIndex.remove(countExistente.getTitulo());
                secondaryIndex.put(countAtualizado.getTitulo(), isbnAtualizar);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Mangá alterado com sucesso!");
    }

    public void excluir(int isbn) {
        // Lê todos os registros, exceto o que deve ser excluído
        List<Count> mangases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Count count = Count.fromString(linha);
                if (count.getIsbn() != isbn) {
                    mangases.add(count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Chama a função para compactar o arquivo e atualizar os índices
        compactarArquivo(mangases);
    }


    private void compactarArquivo(List<Count> mangases) {
        // Escreve os registros não excluídos em um arquivo temporário
        File arquivoTemporario = new File(FILE_NAME + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemporario));
             BufferedWriter indexPrimWriter = new BufferedWriter(new FileWriter(INDEX_PRIMARIO));
             BufferedWriter indexSecWriter = new BufferedWriter(new FileWriter(INDEX_SECUNDARIO))) {

            long offset = 0;
            for (Count count : mangases) {
                String mangaStr = count.toString();
                writer.write(mangaStr);
                writer.newLine();

                long RRN = offset;
                indexPrimWriter.write(count.getIsbn() + "," + RRN);
                indexPrimWriter.newLine();

                indexSecWriter.write(count.getTitulo() + "," + count.getIsbn());
                indexSecWriter.newLine();

                offset += mangaStr.length() + System.lineSeparator().length();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Substitui o arquivo original pelo arquivo temporário
        File arquivoOriginal = new File(FILE_NAME);
        if (arquivoOriginal.delete()) {
            if (arquivoTemporario.renameTo(arquivoOriginal)) {
                System.out.println("Arquivo compactado com sucesso!"); // Sucesso na compactação do arquivo
            } else {
                System.out.println("Falha ao renomear o arquivo temporário.");
            }
        } else {
            System.out.println("Falha ao excluir o arquivo original.");
        }
    }

    public List<Count> listar() {
        List<Count> mangases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Count count = Count.fromString(linha);
                mangases.add(count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mangases;
    }

}

