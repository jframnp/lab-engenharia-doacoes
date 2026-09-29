
package com.dpb.model;

public class Item {
    private String titulo;
    private String descricao;
    private String categoria;
    private String condicao; // "NOVO", "BOM", "REGULAR"
    private String status;   // "DISPONIVEL", "RESERVADO", "DOADO"

    public Item(String titulo, String descricao, String categoria, String condicao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.condicao = condicao;
        this.status = "DISPONIVEL"; // Status inicial padrão
    }

    // Método para validar se o item possui dados obrigatórios
    public boolean validar() {
        if (titulo == null || titulo.trim().isEmpty()) return false;
        if (descricao == null || descricao.trim().isEmpty()) return false;
        if (categoria == null || categoria.trim().isEmpty()) return false;
        
        // Valida se a condição é uma das permitidas
        if (condicao == null || (!condicao.equals("NOVO") && !condicao.equals("BOM") && !condicao.equals("REGULAR"))) {
            return false;
        }
        return true;
    }

    // Método para alterar o status do item (regra de negócio)
    public void alterarStatus(String novoStatus) {
        if (novoStatus.equals("RESERVADO")) {
            if (!this.status.equals("DISPONIVEL")) {
                throw new IllegalStateException("Apenas itens disponíveis podem ser reservados.");
            }
            this.status = "RESERVADO";
        } 
        else if (novoStatus.equals("DOADO")) {
            if (!this.status.equals("RESERVADO")) {
                throw new IllegalStateException("Apenas itens reservados podem ser doados.");
            }
            this.status = "DOADO";
        } 
        else if (novoStatus.equals("DISPONIVEL")) {
            if (this.status.equals("DOADO")) {
                throw new IllegalStateException("Itens já doados não podem voltar a ficar disponíveis.");
            }
            this.status = "DISPONIVEL";
        } 
        else {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
    }

    // Métodos auxiliares (Getters)
    public boolean isDisponivel() {
        return this.status.equals("DISPONIVEL");
    }

    public boolean isReservado() {
        return this.status.equals("RESERVADO");
    }

    public String getTitulo() { return titulo; }
    public String getStatus() { return status; }
    public String getCondicao() { return condicao; }
}