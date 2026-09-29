package com.dpb.model;
// testandooo!!!
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    private Item item;

    // Configuração inicial antes de cada teste
    @BeforeEach
    public void setUp() {
        item = new Item("Mesa de Jantar", "Mesa de madeira em bom estado", "Móveis", "BOM");
    }

    // --------------------------------------------------------
    // TESTE 1: Validação de campos obrigatórios
    // --------------------------------------------------------
    @Test
    public void testValidarItem() {
        // Caso de Teste 1: Item válido (deve retornar true)
        assertTrue(item.validar(), "Item com todos os dados deve ser válido");

        // Caso de Teste 2: Item com título vazio (deve retornar false)
        Item itemInvalido = new Item("", "Descrição", "Móveis", "BOM");
        assertFalse(itemInvalido.validar(), "Item sem título deve ser inválido");
        
        // Caso de Teste 3: Condição inválida (deve retornar false)
        Item itemCondicaoInvalida = new Item("Cadeira", "Cadeira de escritório", "Móveis", "QUEBRADO");
        assertFalse(itemCondicaoInvalida.validar(), "Condição inválida deve retornar false");
    }

    // --------------------------------------------------------
    // TESTE 2: Status inicial do item
    // --------------------------------------------------------
    @Test
    public void testStatusInicial() {
        // Caso de Teste 1: Um item recém-criado deve estar DISPONIVEL
        assertEquals("DISPONIVEL", item.getStatus(), "Status inicial deve ser DISPONIVEL");

        // Caso de Teste 2: Verificar se o método isDisponivel() retorna true
        assertTrue(item.isDisponivel(), "isDisponivel() deve retornar true para item novo");
    }

    // --------------------------------------------------------
    // TESTE 3: Transição de DISPONIVEL para RESERVADO
    // --------------------------------------------------------
    @Test
    public void testReservarItem() {
        // Caso de Teste 1: Alterar para RESERVADO com sucesso
        item.alterarStatus("RESERVADO");
        assertEquals("RESERVADO", item.getStatus(), "Status deve ser atualizado para RESERVADO");
        assertTrue(item.isReservado(), "isReservado() deve retornar true");

        // Caso de Teste 2: Tentar reservar um item que já está reservado (deve lançar exceção)
        assertThrows(IllegalStateException.class, () -> {
            item.alterarStatus("RESERVADO");
        }, "Não deve ser possível reservar um item já reservado");
    }

    // --------------------------------------------------------
    // TESTE 4: Transição de RESERVADO para DOADO
    // --------------------------------------------------------
    @Test
    public void testConcluirDoacao() {
        // Caso de Teste 1: Reservar e depois doar com sucesso
        item.alterarStatus("RESERVADO");
        item.alterarStatus("DOADO");
        assertEquals("DOADO", item.getStatus(), "Status deve ser atualizado para DOADO");

        // Caso de Teste 2: Tentar doar um item que ainda está DISPONIVEL (deve lançar exceção)
        Item novoItem = new Item("Livro", "Livro de Java", "Livros", "NOVO");
        assertThrows(IllegalStateException.class, () -> {
            novoItem.alterarStatus("DOADO");
        }, "Não é possível doar um item sem antes reservá-lo");
    }

    // --------------------------------------------------------
    // TESTE 5: Tentativas de transições inválidas
    // --------------------------------------------------------
    @Test
    public void testTransicoesInvalidas() {
        // Caso de Teste 1: Tentar alterar para um status que não existe
        assertThrows(IllegalArgumentException.class, () -> {
            item.alterarStatus("PERDIDO");
        }, "Status inválido deve lançar IllegalArgumentException");

        // Caso de Teste 2: Tentar reverter um item DOADO para DISPONIVEL
        item.alterarStatus("RESERVADO");
        item.alterarStatus("DOADO");
        assertThrows(IllegalStateException.class, () -> {
            item.alterarStatus("DISPONIVEL");
        }, "Itens já doados não podem voltar a ser disponíveis");
    }
}
