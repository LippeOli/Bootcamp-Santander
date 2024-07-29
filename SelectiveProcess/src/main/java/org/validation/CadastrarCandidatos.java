package org.validation;

import java.util.HashMap;
import java.util.Map;

public class CadastrarCandidatos {
    private Map<String, Candidato> candidatos;

    public CadastrarCandidatos() {
        candidatos = new HashMap<String, Candidato>();
    }

    public void adicionarCandidato(Candidato candidato) {
        candidatos.put(candidato.getNome(), candidato);
    }
    public void listarCandidatos() {
        for (Candidato candidato : candidatos.values()) {
            System.out.println(candidato);
        }
    }

}
