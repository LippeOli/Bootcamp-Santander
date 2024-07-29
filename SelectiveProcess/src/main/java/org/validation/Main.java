package org.validation;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        CadastrarCandidatos cadastro = new CadastrarCandidatos();

        //Inicio do processo
        Scanner sc = new Scanner(System.in);

        System.out.println("Valide o processo seletivo");

        System.out.println("Qual seu nome?");
        String nome = sc.nextLine();

        System.out.println("Qual seu cpf?");
        String cpf = sc.nextLine();

        System.out.println("Qual seu email?");
        String email = sc.nextLine();

        System.out.println("Sua Senha:");
        String senha = sc.nextLine();

        Candidato novoCandidato = new Candidato(nome, cpf, email, senha);
        cadastro.adicionarCandidato(novoCandidato);

        //listar todos
        cadastro.listarCandidatos();

    }
}